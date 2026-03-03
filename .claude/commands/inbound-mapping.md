---
allowed-tools: Bash(mysql:*), Bash(kill:*), Read, Grep, Glob
argument-hint: [--env stage|prod] [--source mustit|setof] [--dry-run]
description: 인바운드 상품 매핑 누락 진단 및 자동 수정. 브랜드/카테고리 매핑 추가 + DB 업데이트.
---

# Inbound Product Mapping Resolver

## 목적
인바운드 상품(inbound_products)의 브랜드/카테고리 매핑 누락을 진단하고 자동으로 수정합니다.

## 인수 파싱
- `--env`: 대상 환경 (기본값: stage)
  - `stage` → localhost:13308 (포트포워딩 필요)
  - `prod` → localhost:13307 (포트포워딩 필요)
- `--source`: 인바운드 소스 필터 (기본값: 전체)
  - `mustit` → inbound_source_id = 1
  - `setof` → inbound_source_id = 2
- `--dry-run`: 분석만 수행, DB 수정 없음

## DB 접속 정보
```
Stage: -h 127.0.0.1 -P 13308 -u admin -p'7N}ZQ)cIixn:[FtTWZ0>VZ8Zja]2+NyD'
Prod: -h 127.0.0.1 -P 13307 -u admin -p (비밀번호는 사용자에게 확인)
```
MySQL 바이너리: `/usr/local/mysql/bin/mysql`

## 실행 순서

### Phase 1: 포트포워딩 확인
1. `localhost:{port}` 접속 가능한지 확인
2. 안 되면 사용자에게 `local-dev/scripts/aws-port-forward-stage.sh` 실행 안내

### Phase 2: 매핑 누락 진단
다음 SQL로 RECEIVED 또는 PENDING_MAPPING 상태의 인바운드 상품을 조회:
```sql
-- market 스키마
SELECT ip.id, ip.inbound_source_id, ip.external_product_code,
       ip.external_brand_code, ip.external_category_code,
       ip.internal_brand_id, ip.internal_category_id, ip.status
FROM inbound_products ip
WHERE ip.status IN ('RECEIVED', 'PENDING_MAPPING')
ORDER BY ip.inbound_source_id, ip.id;
```

### Phase 3: 브랜드 매핑 분석
누락된 브랜드 코드 찾기:
```sql
-- 매핑되지 않은 브랜드 코드 조회
SELECT DISTINCT ip.inbound_source_id, ip.external_brand_code
FROM inbound_products ip
WHERE ip.status IN ('RECEIVED', 'PENDING_MAPPING')
  AND NOT EXISTS (
    SELECT 1 FROM inbound_brand_mapping ibm
    WHERE ibm.inbound_source_id = ip.inbound_source_id
      AND ibm.external_brand_code = ip.external_brand_code
      AND ibm.active = 1
  );
```

누락된 브랜드에 대해:
1. `crawler` 스키마의 `crawled_product` 테이블에서 brand_name 확인 (deleted_at IS NULL)
2. `market.brand` 테이블에서 name_ko 또는 name_en으로 내부 브랜드 검색
3. 내부 브랜드가 없으면: 신규 브랜드 등록 필요 → 사용자에게 확인 후 INSERT
4. 내부 브랜드가 있으면: `inbound_brand_mapping`에 매핑 INSERT

### Phase 4: 카테고리 매핑 분석
누락된 카테고리 코드 찾기:
```sql
-- 매핑되지 않은 카테고리 코드 조회
SELECT DISTINCT ip.inbound_source_id, ip.external_category_code
FROM inbound_products ip
WHERE ip.status IN ('RECEIVED', 'PENDING_MAPPING')
  AND NOT EXISTS (
    SELECT 1 FROM inbound_category_mapping icm
    WHERE icm.inbound_source_id = ip.inbound_source_id
      AND icm.external_category_code = ip.external_category_code
      AND icm.active = 1
  );
```

누락된 카테고리에 대해:
1. 외부 카테고리 코드 패턴 분석 (예: `L23r03r02` → L=대분류, r03=중분류, r02=소분류)
2. `market.category` 테이블에서 적합한 내부 카테고리 검색
3. 매칭되면 `inbound_category_mapping`에 INSERT
4. 매칭 안 되면 사용자에게 수동 매핑 요청

### Phase 5: 결과 리포트
```
=== 인바운드 매핑 진단 결과 ===

📊 대상 상품: N건 (RECEIVED: X건, PENDING_MAPPING: Y건)

🔴 브랜드 매핑 누락: N건
  - brand_code=1561 (Wild Donkey) → brand_id=123 ✅ 매핑 추가
  - brand_code=9999 (Unknown) → ❌ 내부 브랜드 없음 → 신규 등록 필요

🔴 카테고리 매핑 누락: N건
  - category_code=L23r03r02 → category_id=924 ✅ 매핑 추가
  - category_code=UNKNOWN → ❌ 매칭 불가 → 수동 매핑 필요

✅ 매핑 완료: N건 추가됨
⚠️ 미해결: N건 (수동 처리 필요)
```

## 주의사항

### Collation 이슈
crawler와 market 스키마의 collation이 다를 수 있음:
- market: `utf8mb4_unicode_ci`
- crawler: `utf8mb4_0900_ai_ci`
JOIN 시 반드시 `COLLATE utf8mb4_unicode_ci` 추가

### 소스별 ID
| 소스 | inbound_source_id |
|------|-------------------|
| MUSTIT | 1 |
| SETOF | 2 |

### brand_code=0 이슈
크롤러에서 brand_code가 0인 경우가 있음 (FootJoy/Titleist 등 코드 미부여).
이 경우 brand_name으로 매칭 시도.

### inbound_products 직접 수정 금지
매핑 테이블만 수정합니다. inbound_products의 status나 internal_*_id는 건드리지 않습니다.
크롤러가 재수신하면 애플리케이션 레벨에서 자동으로 매핑 → 변환됩니다.

## --dry-run 모드
`--dry-run` 플래그가 있으면:
- Phase 2~4의 진단 쿼리만 실행
- INSERT/UPDATE 쿼리는 실행하지 않고 실행할 SQL만 출력
- 사용자가 확인 후 직접 실행하거나 `--dry-run` 없이 재실행
