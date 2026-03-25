# 세토프(setof-commerce) Admin API 연동 이슈 리포트

- 작성일: 2026-03-25
- 테스트 환경: Docker (`setof-web-api-admin`, 포트 48081) + Stage RDS
- 테스트 주체: MarketPlace E2E 스크립트 (`local-dev/scripts/setof-e2e-test.sh`)

---

## 전체 테스트 결과: 24건 중 18 성공 / 1 실패 / 5 SKIP

| Phase | API | 결과 |
|---|---|---|
| 인증 | POST /api/admin/v1/auth/seller-token | ✅ PASS |
| 배송정책 | POST /api/v2/shipping-policies | ✅ PASS (201) |
| 배송정책 | PUT /api/v2/shipping-policies/{policyId} | ✅ PASS (204) |
| 환불정책 | POST /api/v2/refund-policies | ✅ PASS (201) |
| 환불정책 | PUT /api/v2/refund-policies/{policyId} | ✅ PASS (204) |
| 상품 | POST /api/v2/admin/product-groups | ✅ PASS (201) |
| 상품 | GET /api/v2/admin/product-groups/{id} | ✅ PASS (200) |
| 상품 | PUT /api/v2/admin/product-groups/{id} | ✅ PASS (204) |
| 상품 | PATCH /api/v2/admin/product-groups/{id}/basic-info | ✅ PASS (204) |
| 상품 | PATCH /api/v2/admin/products/product-groups/{id} | ✅ PASS (204) — BUG-001 수정 확인 |
| 상품 | PUT /api/v2/admin/product-groups/{id}/images | ✅ PASS (204) |
| 상품 | PUT /api/v2/admin/product-groups/{id}/description | ✅ PASS (204) |
| 상품 | PUT /api/v2/admin/product-groups/{id}/notice | ✅ PASS (204) |
| 이미지 | PUT /api/v2/admin/image-variants/sync | ✅ PASS (200) |
| 셀러 | POST /api/v2/admin/sellers | ✅ PASS (201) |
| **셀러** | **PUT /api/v2/admin/sellers/{id}** | **❌ FAIL (404) — BUG-002** |
| 셀러주소 | POST /api/v2/admin/seller-addresses/sellers/{sellerId} | ✅ PASS (201) |
| 셀러주소 | PUT /api/v2/admin/seller-addresses/sellers/{sellerId}/{addressId} | ✅ PASS (204) |
| 셀러주소 | DELETE /api/v2/admin/seller-addresses/sellers/{sellerId}/{addressId} | ✅ PASS (204) |
| 개별상품 | PATCH /api/v2/admin/products/{id}/price | ⏭️ SKIP (상품 ID 조회 불가) |
| 개별상품 | PATCH /api/v2/admin/products/{id}/stock | ⏭️ SKIP (상품 ID 조회 불가) |
| 주문 | POST /api/v2/orders/{id}/confirm | ⏭️ SKIP (데이터 없음) |
| 주문 | POST /api/v2/orders/{id}/ready-to-ship | ⏭️ SKIP (데이터 없음) |
| 취소 | POST /api/v2/cancels/{id}/approve | ⏭️ SKIP (데이터 없음) |

---

## ✅ BUG-001: 옵션/상품 부분 수정 시 DELETED 상품 상태 전이 에러 — **수정 완료 (b8a8ba7a)**

### 재현 시나리오

1. 상품 등록 (POST) → 상품 2개 (M, L) 생성
2. 상품 전체 수정 (PUT) → 기존 상품 DELETED 처리 + 새 상품 2개 (M, L) 재생성
3. 옵션/상품 부분 수정 (PATCH) → **400 에러 발생**

### 요청

```
PATCH /api/v2/admin/products/product-groups/517469
X-Seller-Token: eyJhbGciOiJIUzM4NCJ9...
Content-Type: application/json
```

```json
{
    "optionGroups": [
        {
            "optionGroupName": "사이즈",
            "sortOrder": 0,
            "optionValues": [
                {"optionValueName": "M", "sortOrder": 0},
                {"optionValueName": "L", "sortOrder": 1},
                {"optionValueName": "XL", "sortOrder": 2}
            ]
        }
    ],
    "products": [
        {
            "skuCode": "TEST-M",
            "regularPrice": 55000,
            "currentPrice": 42900,
            "stockQuantity": 150,
            "sortOrder": 0,
            "selectedOptions": [{"optionGroupName": "사이즈", "optionValueName": "M"}]
        },
        {
            "skuCode": "TEST-L",
            "regularPrice": 55000,
            "currentPrice": 42900,
            "stockQuantity": 80,
            "sortOrder": 1,
            "selectedOptions": [{"optionGroupName": "사이즈", "optionValueName": "L"}]
        },
        {
            "skuCode": "TEST-XL",
            "regularPrice": 55000,
            "currentPrice": 42900,
            "stockQuantity": 30,
            "sortOrder": 2,
            "selectedOptions": [{"optionGroupName": "사이즈", "optionValueName": "XL"}]
        }
    ]
}
```

### 응답 (400)

```json
{
    "type": "about:blank",
    "title": "Bad Request",
    "status": 400,
    "detail": "상태 DELETED에서 DELETED로 전이할 수 없습니다",
    "instance": "/api/v2/admin/products/product-groups/517469",
    "properties": {
        "timestamp": "2026-03-25T06:26:24.046015843Z",
        "code": "PRD-003",
        "args": {
            "to": "DELETED",
            "from": "DELETED"
        }
    }
}
```

### 서버 로그 (Docker: setof-web-api-admin)

```
WARN  GlobalExceptionHandler - DomainException (Client Error):
  code=PRD-003, status=400,
  detail=상태 DELETED에서 DELETED로 전이할 수 없습니다,
  args={to=DELETED, from=DELETED}
```

로그 직전 SQL:
```sql
-- DELETED 포함 전체 상품 조회 (product_option_mappings JOIN)
SELECT pomje1_0.id, pomje1_0.deleted, pomje1_0.deleted_at,
       pomje1_0.product_id, pomje1_0.seller_option_value_id
FROM product_option_mappings pomje1_0
WHERE pomje1_0.product_id IN (?, ?, ?, ?)
-- ↑ 4개 중 2개는 PUT 전체수정에서 DELETED 처리된 상품
```

### 원인 분석

1. `PUT /api/v2/admin/product-groups/{id}` (전체수정)이 기존 상품(M, L)을 **soft delete** (status=DELETED) 처리하고, 새 상품(M, L)을 재생성
2. `PATCH /api/v2/admin/products/product-groups/{id}` (부분수정)이 해당 productGroupId의 **모든 상품을 조회** — DELETED 상태 상품도 포함
3. DELETED 상품에 대해 상태 전이(delete → delete)를 시도하면서 `PRD-003` 에러 발생

### 수정 제안

products 테이블에서 productGroupId로 조회할 때 `WHERE deleted = false` 또는 `WHERE status != 'DELETED'` 조건 추가. 부분 수정 시에는 활성(active) 상태 상품만 대상으로 처리해야 함.

---

## ✅ INFO-001: 셀러 등록/수정 Admin API — **추가 완료 (d12c8d23)**

- POST /api/v2/admin/sellers: ✅ 201 성공
- PUT /api/v2/admin/sellers/{sellerId}: ❌ 404 → **BUG-002 참조**

---

## ✅ INFO-002: 셀러 주소 Admin API — **추가 완료 (d12c8d23)**

- POST /api/v2/admin/seller-addresses/sellers/{sellerId}: ✅ 201 성공
- PUT /api/v2/admin/seller-addresses/sellers/{sellerId}/{addressId}: ✅ 204 성공
- DELETE /api/v2/admin/seller-addresses/sellers/{sellerId}/{addressId}: ✅ 204 성공

---

## 🔴 BUG-002: 셀러 등록 시 CS 정보(seller_cs) 미생성 → 수정 시 404

### 재현 시나리오

1. 셀러 등록 (POST /api/v2/admin/sellers) — `businessInfo.csContact` 포함하여 호출
2. 셀러 수정 (PUT /api/v2/admin/sellers/{sellerId}) — `csInfo` 포함하여 호출 → **404**

### 요청

```
PUT /api/v2/admin/sellers/76
Content-Type: application/json
```

```json
{
    "sellerName": "E2E 수정된 셀러",
    "displayName": "E2E수정",
    "logoUrl": "https://example.com/logo-updated.png",
    "description": "수정된 셀러 설명",
    "csInfo": {
        "phone": "02-9876-5432",
        "email": "updated@e2etest.com",
        "mobile": "010-9876-5432"
    },
    "businessInfo": {
        "registrationNumber": "123-45-67890",
        "companyName": "수정된주식회사",
        "representative": "김수정",
        "saleReportNumber": "2026-서울강남-99999",
        "businessAddress": {
            "zipCode": "06235",
            "line1": "서울시 강남구 역삼로 100",
            "line2": "20층"
        }
    }
}
```

### 응답 (404)

```json
{
    "type": "/errors/seller/sel-300",
    "title": "Seller Error",
    "status": 404,
    "detail": "셀러 ID 76에 해당하는 CS 정보를 찾을 수 없습니다",
    "instance": "/api/v2/admin/sellers/76",
    "properties": {
        "timestamp": "2026-03-25T07:29:02.704389430Z",
        "code": "SEL-300"
    }
}
```

### DB 검증

```sql
-- sellers 테이블: 정상 등록됨
SELECT * FROM sellers WHERE id = 76;
-- ✅ 데이터 있음

-- seller_business_infos 테이블: 정상 등록됨
SELECT * FROM seller_business_infos WHERE seller_id = 76;
-- ✅ 데이터 있음

-- seller_cs 테이블: 데이터 없음!
SELECT * FROM seller_cs WHERE seller_id = 76;
-- ❌ 0건 — 등록 시 csContact을 보냈지만 seller_cs에 INSERT 안 됨
```

### 원인 분석

셀러 등록 API(`RegisterSellerApiRequest`)에서 `businessInfo.csContact`으로 CS 정보를 전달하고 있으나, 세토프 서버의 셀러 등록 로직에서 `seller_cs` 테이블에 INSERT하는 부분이 빠져있음. `sellers`, `seller_business_infos`는 정상 생성되지만 `seller_cs`만 누락.

### 수정 제안

셀러 등록 서비스에서 `csContact`이 전달되면 `seller_cs` 테이블에도 INSERT 처리 필요. 또는 셀러 수정 시 `seller_cs`가 없으면 신규 생성(upsert)하도록 처리.

---

## 🟢 MarketPlace 측 수정 완료 사항 (2026-03-25)

### 인증 체계 변경
- 글로벌 `X-Service-Token` → Shop별 `X-Seller-Token` 동적 헤더
- `shop.apiKey()` + `shop.apiSecret()` → 토큰 발급 API → JWT accessToken 획득
- `SetofSellerTokenProvider` 인메모리 캐싱 + 401 시 자동 재발급

### URL 수정
| Before | After |
|---|---|
| `/api/v2/admin/sellers/{sellerId}/shipping-policies` | `/api/v2/shipping-policies` |
| `/api/v2/admin/sellers/{sellerId}/refund-policies` | `/api/v2/refund-policies` |

### DTO 수정
- `SetofProductGroupRegistrationRequest`: sellerId 필드 제거
- `SetofProductGroupDetailResponse`: `{ "data": ... }` 래핑 + `optionProductMatrix` 구조 반영
- `SetofShippingPolicySyncRequest`: `leadTime` 중첩 객체 구조로 변경 + 불필요 필드 제거
- `SetofSellerCreateRequest`: `sellerInfo` + `businessInfo` 중첩 구조 신규 생성

### 포트 변경
- 모든 Sync 포트(배송/클레임/셀러정책)에 `Shop shop` 파라미터 추가
- 각 Service에서 Shop 조회 → 어댑터에 전달

---

## 재현 방법

```bash
# 1. 세토프 Docker 실행
cd /Users/ryu-qqq/Documents/ryu-qqq/setof-commerce
./gradlew :bootstrap:bootstrap-web-api-admin:bootJar -x test -x jacocoTestCoverageVerification
cd local-dev
docker compose -f docker-compose.aws.yml --profile web-api-admin up -d --build

# 2. 포트포워딩 (Stage RDS)
bash /Users/ryu-qqq/Documents/ryu-qqq/MarketPlace/local-dev/scripts/aws-port-forward-stage.sh &

# 3. E2E 테스트 실행
bash /Users/ryu-qqq/Documents/ryu-qqq/MarketPlace/local-dev/scripts/setof-e2e-test.sh

# 4. 서버 로그 확인
docker logs setof-web-api-admin 2>&1 | grep "PRD-003"
```
