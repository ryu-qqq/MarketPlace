#!/usr/bin/env python3
"""SET_OF 카테고리 → 내부 카테고리 시맨틱 매핑 스크립트."""
import pymysql

DB_CONFIG = {
    'host': '127.0.0.1',
    'port': 13308,
    'user': 'admin',
    'password': '7N}ZQ)cIixn:[FtTWZ0>VZ8Zja]2+NyD',
    'database': 'market',
    'charset': 'utf8mb4',
}

# SET_OF depth 2 → Internal depth 1 매핑 (성별+유형 → 성별유형)
DEPTH2_MAPPING = {
    # 여성
    6: 12,    # 여성 > 의류 → 여성의류
    7: 16,    # 여성 > 슈즈 → 여성신발
    8: 19,    # 여성 > 가방 → 여성가방
    # 9: 액세서리 → 하위에서 분기
    # 남성
    10: 14,   # 남성 > 의류 → 남성의류
    11: 17,   # 남성 > 슈즈 → 남성신발
    12: 20,   # 남성 > 가방 → 남성가방
    # 13: 액세서리 → 하위에서 분기
}

# SET_OF depth 3 → Internal category 매핑
# 여성 > 의류 > ...
DEPTH3_MAPPING = {
    # 여성 > 의류
    19: 249,   # 탑 → 여성의류 > 티셔츠
    20: 247,   # 스웨터 → 여성의류 > 니트
    21: 250,   # 블라우스/셔츠 → 여성의류 > 블라우스/셔츠
    22: 248,   # 원피스 → 여성의류 > 원피스
    23: 253,   # 팬츠 → 여성의류 > 바지
    24: 255,   # 스커트 → 여성의류 > 스커트
    25: 251,   # 코트/자켓 → 여성의류 > 아우터
    26: 267,   # 언더웨어/라운지웨어 → 여성언더웨어/잠옷 > 잠옷/홈웨어
    27: 257,   # 스포츠웨어/아웃도어 → 여성의류 > 트레이닝복
    # 여성 > 슈즈
    28: 298,   # 스니커즈 → 여성신발 > 운동화
    29: 301,   # 샌들/슬리퍼 → 여성신발 > 샌들
    30: 296,   # 로퍼 → 여성신발 > 단화
    31: 296,   # 플랫 → 여성신발 > 단화
    32: 297,   # 힐 → 여성신발 > 힐/펌프스
    33: 296,   # 드레스슈즈 → 여성신발 > 단화
    34: 293,   # 부츠/워커 → 여성신발 > 부츠
    # 여성 > 가방
    35: 324,   # 크로스백 → 여성가방 > 크로스백
    36: 329,   # 클러치/파우치 → 여성가방 > 클러치백
    37: 326,   # 토트백 → 여성가방 > 토트백
    38: 325,   # 숄더백 → 여성가방 > 숄더백
    39: 323,   # 백팩 → 여성가방 > 백팩
    40: 327,   # 더플백 → 여성가방 > 에코백 (가장 가까운)
    41: 342,   # 러기지백 → 여행용가방/소품 > 기내용 캐리어
    42: 328,   # 기타 → 여성가방 > 힙색/슬링백
    # 여성 > 액세서리
    43: 354,   # 지갑 → 지갑 > 여성지갑
    44: 417,   # 시계 → 시계 > 패션시계
    45: 362,   # 벨트 → 벨트 > 여성벨트
    46: 394,   # 목도리/스카프 → 패션소품 > 스카프
    47: 24,    # 모자 → 모자
    48: 378,   # 장갑 → 장갑 > 여성장갑
    49: 32,    # 주얼리 → 주얼리
    50: 407,   # 키링 → 패션소품 > 키홀더
    51: 27,    # 아이웨어 → 선글라스/안경테
    52: 28,    # 헤어액세서리 → 헤어액세서리
    53: 395,   # 손수건 → 패션소품 > 손수건
    54: 381,   # 양말/스타킹 → 양말 > 여성양말

    # 남성 > 의류
    55: 275,   # 탑 → 남성의류 > 티셔츠
    56: 274,   # 스웨터 → 남성의류 > 니트
    57: 276,   # 셔츠 → 남성의류 > 셔츠/남방
    58: 280,   # 팬츠 → 남성의류 > 바지
    59: 277,   # 코트/자켓 → 남성의류 > 아우터
    60: 281,   # 수트 → 남성의류 > 정장세트
    61: 290,   # 언더웨어/라운지웨어 → 남성언더웨어/잠옷 > 잠옷/홈웨어
    62: 282,   # 스포츠웨어/아웃도어 → 남성의류 > 트레이닝복
    # 남성 > 슈즈
    63: 303,   # 스니커즈 → 남성신발 > 운동화
    64: 314,   # 샌들/슬리퍼 → 남성신발 > 샌들
    65: 312,   # 로퍼 → 남성신발 > 슬립온
    66: 308,   # 드레스슈즈 → 남성신발 > 구두
    67: 304,   # 부츠/워커 → 남성신발 > 부츠
    # 남성 > 가방
    68: 334,   # 크로스백 → 남성가방 > 크로스백
    69: 339,   # 클러치/파우치 → 남성가방 > 클러치백
    70: 336,   # 토트백 → 남성가방 > 토트백
    71: 338,   # 브리프케이스 → 남성가방 > 브리프케이스
    72: 333,   # 백팩 → 남성가방 > 백팩
    73: 337,   # 더플백 → 남성가방 > 에코백
    74: 342,   # 러기지백 → 여행용가방/소품 > 기내용 캐리어
    75: 340,   # 기타 → 남성가방 > 힙색/슬링백
    # 남성 > 액세서리
    76: 355,   # 지갑 → 지갑 > 남성지갑
    77: 417,   # 시계 → 시계 > 패션시계
    78: 363,   # 벨트/서스펜더 → 벨트 > 남성벨트
    79: 394,   # 목도리/스카프 → 패션소품 > 스카프
    80: 24,    # 모자 → 모자
    81: 379,   # 장갑 → 장갑 > 남성장갑
    82: 32,    # 주얼리 → 주얼리
    83: 407,   # 키링 → 패션소품 > 키홀더
    84: 27,    # 아이웨어 → 선글라스/안경테
    85: 396,   # 넥타이 → 패션소품 > 넥타이
    86: 395,   # 손수건 → 패션소품 > 손수건
    87: 382,   # 양말 → 양말 > 남성양말

    # 키즈 > 의류 (내부에 키즈 카테고리는 없지만 출산/육아(6) 하위에 있을 수 있음)
    # 키즈는 일단 스킵 - 사용자가 의류/잡화만 보면 된다고 함

    # 스포츠 > 골프 - 스포츠/레저(8) 하위로 매핑 가능하지만 복잡하므로 스킵
}

# SET_OF depth 4 (leaf) → 부모의 internal mapping을 상속
# depth 4 leaf들은 부모 depth 3의 매핑을 따름


def main():
    conn = pymysql.connect(**DB_CONFIG)
    cur = conn.cursor(pymysql.cursors.DictCursor)

    # 1. SET_OF 카테고리 전체 로드
    cur.execute("""
        SELECT id, external_category_code, external_category_name, depth, path, leaf, parent_id
        FROM sales_channel_category
        WHERE sales_channel_id = 1
        ORDER BY depth, id
    """)
    setof_categories = cur.fetchall()
    setof_by_id = {c['id']: c for c in setof_categories}

    # 2. 매핑 가능한 카테고리 수집
    mappings = []  # (setof_cat_id, internal_cat_id)

    for cat in setof_categories:
        scid = cat['id']
        depth = cat['depth']

        # depth 1: 성별 카테고리 (여성, 남성 등) → 매핑 안 함 (너무 상위)
        if depth == 1:
            # 패션의류(1) 또는 패션잡화(2) 레벨로 매핑
            if scid == 1:  # 여성
                mappings.append((scid, 1))   # → 패션의류
            elif scid == 2:  # 남성
                mappings.append((scid, 1))   # → 패션의류
            elif scid == 3:  # 키즈 - 스킵
                continue
            elif scid == 4:  # 스포츠 - 스킵
                continue
            elif scid == 5:  # 미전시 - 스킵
                continue

        # depth 2: 유형 카테고리 (의류, 슈즈 등)
        elif depth == 2:
            if scid in DEPTH2_MAPPING:
                mappings.append((scid, DEPTH2_MAPPING[scid]))
            elif scid == 9:  # 여성 > 액세서리 → 패션잡화
                mappings.append((scid, 2))
            elif scid == 13:  # 남성 > 액세서리 → 패션잡화
                mappings.append((scid, 2))
            # 키즈(14,15,16), 골프(17), 아웃도어(18) → 스킵

        # depth 3: 세부 카테고리
        elif depth == 3:
            if scid in DEPTH3_MAPPING:
                mappings.append((scid, DEPTH3_MAPPING[scid]))

        # depth 4: leaf → 부모의 매핑을 따름
        elif depth == 4:
            parent_id = cat['parent_id']
            if parent_id in DEPTH3_MAPPING:
                mappings.append((scid, DEPTH3_MAPPING[parent_id]))

    print(f"총 매핑 가능: {len(mappings)}건")

    # 3. category_preset 삽입
    preset_count = 0
    mapping_count = 0

    for scb_id, internal_id in mappings:
        cat = setof_by_id[scb_id]

        # Insert preset
        cur.execute("""
            INSERT INTO category_preset (shop_id, sales_channel_category_id, preset_name, status)
            VALUES (1, %s, %s, 'ACTIVE')
        """, (scb_id, cat['external_category_name']))
        preset_id = cur.lastrowid
        preset_count += 1

        # Insert mapping
        cur.execute("""
            INSERT INTO category_mapping (preset_id, sales_channel_category_id, internal_category_id, status)
            VALUES (%s, %s, %s, 'ACTIVE')
        """, (preset_id, scb_id, internal_id))
        mapping_count += 1

    conn.commit()
    print(f"category_preset 삽입: {preset_count}건")
    print(f"category_mapping 삽입: {mapping_count}건")

    # 4. 매핑되지 않은 카테고리 출력
    mapped_ids = {m[0] for m in mappings}
    unmapped = [c for c in setof_categories if c['id'] not in mapped_ids]
    if unmapped:
        print(f"\n매핑 미처리 카테고리: {len(unmapped)}건")
        for c in unmapped:
            parent = setof_by_id.get(c.get('parent_id'))
            parent_name = parent['external_category_name'] if parent else ''
            print(f"  [{c['depth']}] {parent_name} > {c['external_category_name']} (id={c['id']})")

    cur.close()
    conn.close()


if __name__ == '__main__':
    main()
