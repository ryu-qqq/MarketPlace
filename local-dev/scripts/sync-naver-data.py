#!/usr/bin/env python3
"""
네이버 커머스 카테고리/브랜드 데이터를 Stage DB에 동기화하는 스크립트.

사전 준비:
    pip install bcrypt requests pymysql

사용법:
    1. 포트포워딩 실행:
       bash local-dev/scripts/aws-port-forward-stage.sh

    2. 환경변수 설정:
       export NAVER_COMMERCE_CLIENT_ID="..."
       export NAVER_COMMERCE_CLIENT_SECRET="..."
       export STAGE_DB_PASSWORD="..."
       export STAGE_DB_NAME="marketplace"    # DB 이름

    3. 실행:
       python3 local-dev/scripts/sync-naver-data.py

    옵션:
       --categories-only    카테고리만 동기화
       --brands-only        브랜드만 동기화
       --dry-run            실제 DB에 저장하지 않고 조회만
"""

import os
import sys
import time
import base64
import argparse
from collections import defaultdict

try:
    import bcrypt
except ImportError:
    print("❌ bcrypt 패키지가 필요합니다: pip install bcrypt")
    sys.exit(1)

try:
    import requests
except ImportError:
    print("❌ requests 패키지가 필요합니다: pip install requests")
    sys.exit(1)

try:
    import pymysql
except ImportError:
    print("❌ pymysql 패키지가 필요합니다: pip install pymysql")
    sys.exit(1)


# ── Config ──────────────────────────────────────────────────────
NAVER_BASE_URL = "https://api.commerce.naver.com/external"
NAVER_CLIENT_ID = os.environ.get("NAVER_COMMERCE_CLIENT_ID", "")
NAVER_CLIENT_SECRET = os.environ.get("NAVER_COMMERCE_CLIENT_SECRET", "")

DB_HOST = os.environ.get("STAGE_DB_HOST", "127.0.0.1")
DB_PORT = int(os.environ.get("STAGE_DB_PORT", "13308"))
DB_USER = os.environ.get("STAGE_DB_USER", "admin")
DB_PASSWORD = os.environ.get("STAGE_DB_PASSWORD", "")
DB_NAME = os.environ.get("STAGE_DB_NAME", "")

SALES_CHANNEL_ID = 2  # 네이버 스마트스토어


# ── Naver Commerce Auth ────────────────────────────────────────
def get_naver_token():
    """네이버 커머스 OAuth2 토큰 발급 (BCrypt 서명 기반)"""
    timestamp = int(time.time() * 1000)
    message = f"{NAVER_CLIENT_ID}_{timestamp}"

    # clientSecret은 Base64 인코딩된 BCrypt salt
    salt_str = base64.b64decode(NAVER_CLIENT_SECRET).decode("utf-8")
    hashed = bcrypt.hashpw(message.encode("utf-8"), salt_str.encode("utf-8"))
    signature = base64.b64encode(hashed).decode("utf-8")

    resp = requests.post(
        f"{NAVER_BASE_URL}/v1/oauth2/token",
        data={
            "client_id": NAVER_CLIENT_ID,
            "timestamp": str(timestamp),
            "grant_type": "client_credentials",
            "client_secret_sign": signature,
            "type": "SELF",
        },
        timeout=30,
    )
    resp.raise_for_status()
    data = resp.json()

    if "access_token" not in data:
        print(f"❌ 토큰 발급 실패: {data}")
        sys.exit(1)

    return data["access_token"]


# ── Naver API Calls ────────────────────────────────────────────
def fetch_all_categories(token):
    """GET /v1/categories → 전체 카테고리 목록"""
    resp = requests.get(
        f"{NAVER_BASE_URL}/v1/categories",
        headers={"Authorization": f"Bearer {token}"},
        timeout=60,
    )
    resp.raise_for_status()
    return resp.json()


def search_brands(token, name, max_retries=3):
    """GET /v1/product-brands?name={name} → 브랜드 검색 (429 retry 포함)"""
    for attempt in range(max_retries):
        resp = requests.get(
            f"{NAVER_BASE_URL}/v1/product-brands",
            params={"name": name},
            headers={"Authorization": f"Bearer {token}"},
            timeout=30,
        )
        if resp.status_code == 429:
            wait = 1.0 * (attempt + 1)
            time.sleep(wait)
            continue
        resp.raise_for_status()
        return resp.json()
    # 최종 시도 후에도 429면 빈 리스트 반환
    return []


def fetch_all_brands(token):
    """
    브랜드 전체 수집.
    검색 API만 제공되므로 한글 초성, 영문, 숫자 등으로 반복 검색하여 중복 제거.
    """
    seen = {}

    # 검색 키워드 목록
    prefixes = []
    # 한글 초성 대표 음절 (가, 나, 다, ...)
    korean_starts = list("가나다라마바사아자차카타파하")
    prefixes.extend(korean_starts)
    # 영문 A-Z
    prefixes.extend([chr(c) for c in range(ord("A"), ord("Z") + 1)])
    # 숫자 0-9
    prefixes.extend([str(i) for i in range(10)])

    total = len(prefixes)
    for i, prefix in enumerate(prefixes):
        sys.stdout.write(f"\r  브랜드 검색 [{i + 1}/{total}] '{prefix}'")
        sys.stdout.flush()
        try:
            brands = search_brands(token, prefix)
            if brands:
                for b in brands:
                    bid = str(b["id"])
                    if bid not in seen:
                        seen[bid] = b
        except requests.exceptions.HTTPError as e:
            if e.response is not None and e.response.status_code == 400:
                pass  # name이 짧으면 400 에러 가능
            else:
                print(f"\n  ⚠️  '{prefix}' 검색 실패: {e}")
        except Exception as e:
            print(f"\n  ⚠️  '{prefix}' 검색 실패: {e}")
        time.sleep(0.5)  # rate limit (429 방지)

    print(f"\n  총 수집된 고유 브랜드: {len(seen)}건")
    return list(seen.values())


# ── DB Operations ──────────────────────────────────────────────
def get_db_connection():
    return pymysql.connect(
        host=DB_HOST,
        port=DB_PORT,
        user=DB_USER,
        password=DB_PASSWORD,
        database=DB_NAME,
        charset="utf8mb4",
        cursorclass=pymysql.cursors.DictCursor,
        autocommit=False,
    )


def save_categories(conn, categories):
    """
    카테고리를 depth 순서대로 저장.
    wholeCategoryName에서 트리 구조를 파싱하여 parent_id, depth, path를 설정.
    """
    print(f"\n{'='*50}")
    print(f"카테고리 저장 시작 ({len(categories)}건)")
    print(f"{'='*50}")

    # 1. depth별로 분류
    by_depth = defaultdict(list)
    whole_to_code = {}

    for cat in categories:
        whole = cat.get("wholeCategoryName", "") or cat.get("name", "")
        parts = whole.split(">") if ">" in whole else [whole]
        cat["_depth"] = len(parts) - 1
        cat["_parts"] = parts
        by_depth[cat["_depth"]].append(cat)
        whole_to_code[whole] = str(cat["id"])

    max_depth = max(by_depth.keys()) if by_depth else 0
    print(f"  depth 범위: 0 ~ {max_depth}")
    for d in sorted(by_depth.keys()):
        print(f"    depth {d}: {len(by_depth[d])}건")

    cursor = conn.cursor()

    # 2. 기존 데이터 확인
    cursor.execute(
        "SELECT external_category_code, id FROM sales_channel_category WHERE sales_channel_id = %s",
        (SALES_CHANNEL_ID,),
    )
    existing = {row["external_category_code"]: row["id"] for row in cursor.fetchall()}
    print(f"  기존 데이터: {len(existing)}건")

    # 3. depth 순서대로 삽입
    code_to_db_id = {}
    # 기존 데이터도 맵에 등록
    for code, db_id in existing.items():
        code_to_db_id[code] = db_id

    inserted = 0
    skipped = 0

    for depth in sorted(by_depth.keys()):
        cats_at_depth = sorted(by_depth[depth], key=lambda c: c.get("wholeCategoryName", ""))
        sort_order = 0

        for cat in cats_at_depth:
            ext_code = str(cat["id"])
            ext_name = cat["name"]
            whole_name = cat.get("wholeCategoryName", ext_name)
            is_leaf = 1 if cat.get("last", False) else 0

            # 이미 존재하면 스킵
            if ext_code in existing:
                code_to_db_id[ext_code] = existing[ext_code]
                skipped += 1
                continue

            # 부모 찾기
            parent_db_id = None
            if depth > 0:
                parent_parts = cat["_parts"][:-1]
                parent_whole = ">".join(parent_parts)
                parent_code = whole_to_code.get(parent_whole)
                if parent_code:
                    parent_db_id = code_to_db_id.get(parent_code)

            # INSERT (path는 임시값, 나중에 UPDATE)
            cursor.execute(
                """INSERT INTO sales_channel_category
                   (sales_channel_id, external_category_code, external_category_name,
                    parent_id, depth, path, sort_order, leaf, status, display_path)
                   VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)""",
                (
                    SALES_CHANNEL_ID,
                    ext_code,
                    ext_name,
                    parent_db_id,
                    depth,
                    "0",  # 임시 path
                    sort_order,
                    is_leaf,
                    "ACTIVE",
                    whole_name,
                ),
            )
            db_id = cursor.lastrowid
            code_to_db_id[ext_code] = db_id
            inserted += 1
            sort_order += 1

        # depth별 커밋
        conn.commit()
        if inserted > 0 or skipped > 0:
            sys.stdout.write(f"\r  depth {depth} 처리 완료")
            sys.stdout.flush()

    print(f"\n  삽입: {inserted}건, 스킵(이미존재): {skipped}건")

    # 4. path 업데이트
    if inserted > 0:
        print("  path 업데이트 중...")

        # depth 0: path = 자신의 id
        cursor.execute(
            """UPDATE sales_channel_category
               SET path = CAST(id AS CHAR)
               WHERE sales_channel_id = %s AND depth = 0 AND path = '0'""",
            (SALES_CHANNEL_ID,),
        )

        # depth 1+: path = 부모path + "/" + 자신id
        for d in range(1, max_depth + 1):
            cursor.execute(
                """UPDATE sales_channel_category c
                   JOIN sales_channel_category p ON c.parent_id = p.id
                   SET c.path = CONCAT(p.path, '/', CAST(c.id AS CHAR))
                   WHERE c.sales_channel_id = %s AND c.depth = %s AND c.path = '0'""",
                (SALES_CHANNEL_ID, d),
            )

        conn.commit()
        print("  path 업데이트 완료")

    print(f"✅ 카테고리 저장 완료!")


def save_brands(conn, brands):
    """브랜드를 sales_channel_brand에 저장"""
    print(f"\n{'='*50}")
    print(f"브랜드 저장 시작 ({len(brands)}건)")
    print(f"{'='*50}")

    cursor = conn.cursor()

    # 기존 데이터 확인
    cursor.execute(
        "SELECT external_brand_code FROM sales_channel_brand WHERE sales_channel_id = %s",
        (SALES_CHANNEL_ID,),
    )
    existing = {row["external_brand_code"] for row in cursor.fetchall()}
    print(f"  기존 데이터: {len(existing)}건")

    inserted = 0
    skipped = 0
    batch = []
    batch_size = 500

    for brand in brands:
        ext_code = str(brand["id"])
        ext_name = brand["name"]

        if ext_code in existing:
            skipped += 1
            continue

        batch.append((SALES_CHANNEL_ID, ext_code, ext_name, "ACTIVE"))
        existing.add(ext_code)  # 중복 방지

        if len(batch) >= batch_size:
            cursor.executemany(
                """INSERT IGNORE INTO sales_channel_brand
                   (sales_channel_id, external_brand_code, external_brand_name, status)
                   VALUES (%s, %s, %s, %s)""",
                batch,
            )
            conn.commit()
            inserted += len(batch)
            batch = []
            sys.stdout.write(f"\r  {inserted}건 삽입 중...")
            sys.stdout.flush()

    # 남은 배치 처리
    if batch:
        cursor.executemany(
            """INSERT IGNORE INTO sales_channel_brand
               (sales_channel_id, external_brand_code, external_brand_name, status)
               VALUES (%s, %s, %s, %s)""",
            batch,
        )
        conn.commit()
        inserted += len(batch)

    print(f"\n  삽입: {inserted}건, 스킵(이미존재): {skipped}건")
    print(f"✅ 브랜드 저장 완료!")


# ── Main ───────────────────────────────────────────────────────
def main():
    parser = argparse.ArgumentParser(description="네이버 커머스 데이터 Stage DB 동기화")
    parser.add_argument("--categories-only", action="store_true", help="카테고리만 동기화")
    parser.add_argument("--brands-only", action="store_true", help="브랜드만 동기화")
    parser.add_argument("--dry-run", action="store_true", help="API 조회만 (DB 저장 안함)")
    args = parser.parse_args()

    do_categories = not args.brands_only
    do_brands = not args.categories_only

    print("=" * 60)
    print("네이버 커머스 데이터 동기화 → Stage DB")
    print("=" * 60)
    print(f"  DB: {DB_HOST}:{DB_PORT}/{DB_NAME}")
    print(f"  Sales Channel ID: {SALES_CHANNEL_ID} (네이버 스마트스토어)")
    print(f"  대상: {'카테고리' if do_categories else ''} {'브랜드' if do_brands else ''}")
    print(f"  Dry Run: {args.dry_run}")
    print()

    # 환경변수 검증
    if not NAVER_CLIENT_ID or not NAVER_CLIENT_SECRET:
        print("❌ 환경변수를 설정하세요:")
        print("   export NAVER_COMMERCE_CLIENT_ID='...'")
        print("   export NAVER_COMMERCE_CLIENT_SECRET='...'")
        sys.exit(1)
    if not args.dry_run and not DB_PASSWORD:
        print("❌ DB 비밀번호를 설정하세요:")
        print("   export STAGE_DB_PASSWORD='...'")
        sys.exit(1)
    if not args.dry_run and not DB_NAME:
        print("❌ DB 이름을 설정하세요:")
        print("   export STAGE_DB_NAME='marketplace'")
        sys.exit(1)

    # 1. 네이버 토큰 발급
    print("1. 네이버 커머스 토큰 발급...")
    token = get_naver_token()
    print("   ✅ 토큰 발급 성공")

    categories = []
    brands = []

    # 2. 카테고리 조회
    if do_categories:
        print("\n2. 네이버 카테고리 전체 조회...")
        categories = fetch_all_categories(token)
        print(f"   ✅ {len(categories)}건 조회 완료")

        # depth 분포 출력
        depth_count = defaultdict(int)
        for cat in categories:
            whole = cat.get("wholeCategoryName", "")
            d = whole.count(">")
            depth_count[d] += 1
        for d in sorted(depth_count.keys()):
            print(f"      depth {d}: {depth_count[d]}건")

    # 3. 브랜드 조회
    if do_brands:
        print(f"\n{'3' if do_categories else '2'}. 네이버 브랜드 전체 수집...")
        brands = fetch_all_brands(token)

    if args.dry_run:
        print("\n🔍 Dry Run 모드 - DB 저장 생략")
        if categories:
            print(f"\n카테고리 샘플 (상위 5건):")
            for cat in categories[:5]:
                print(f"  [{cat['id']}] {cat['wholeCategoryName']} (leaf={cat.get('last', False)})")
        if brands:
            print(f"\n브랜드 샘플 (상위 10건):")
            for b in brands[:10]:
                print(f"  [{b['id']}] {b['name']}")
        return

    # 4. DB 저장
    step = "4" if (do_categories and do_brands) else "3"
    print(f"\n{step}. DB 저장 시작...")
    conn = get_db_connection()
    try:
        if do_categories and categories:
            save_categories(conn, categories)
        if do_brands and brands:
            save_brands(conn, brands)
    except Exception as e:
        conn.rollback()
        print(f"\n❌ DB 저장 실패: {e}")
        raise
    finally:
        conn.close()

    print(f"\n{'='*60}")
    print("✅ 동기화 완료!")
    print(f"{'='*60}")


if __name__ == "__main__":
    main()
