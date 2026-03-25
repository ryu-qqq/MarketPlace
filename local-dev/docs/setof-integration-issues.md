# 세토프(setof-commerce) Admin API 연동 이슈 리포트

- 작성일: 2026-03-25
- 테스트 환경: Docker (`setof-web-api-admin`, 포트 48081) + Stage RDS
- 테스트 주체: MarketPlace E2E 스크립트 (`local-dev/scripts/setof-e2e-test.sh`)

---

## 전체 테스트 결과: 19건 중 13 성공 / 1 실패 / 5 SKIP

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
| **상품** | **PATCH /api/v2/admin/products/product-groups/{id}** | **❌ FAIL (400)** |
| 상품 | PUT /api/v2/admin/product-groups/{id}/images | ✅ PASS (204) |
| 상품 | PUT /api/v2/admin/product-groups/{id}/description | ✅ PASS (204) |
| 상품 | PUT /api/v2/admin/product-groups/{id}/notice | ✅ PASS (204) |
| 이미지 | PUT /api/v2/admin/image-variants/sync | ✅ PASS (200) |
| 개별상품 | PATCH /api/v2/admin/products/{id}/price | ⏭️ SKIP (상품 ID 조회 불가) |
| 개별상품 | PATCH /api/v2/admin/products/{id}/stock | ⏭️ SKIP (상품 ID 조회 불가) |
| 주문 | POST /api/v2/orders/{id}/confirm | ⏭️ SKIP (데이터 없음) |
| 주문 | POST /api/v2/orders/{id}/ready-to-ship | ⏭️ SKIP (데이터 없음) |
| 취소 | POST /api/v2/cancels/{id}/approve | ⏭️ SKIP (데이터 없음) |

---

## 🔴 BUG-001: 옵션/상품 부분 수정 시 DELETED 상품 상태 전이 에러

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

## 🟡 INFO-001: 셀러 등록/수정 Admin API 미존재

- MarketPlace에서 `POST /api/v2/admin/sellers`, `PUT /api/v2/admin/sellers/{sellerId}` 호출 시 **405 Method Not Allowed**
- 세토프 Admin 모듈에 `SellerCommandController`가 없고 `SellerQueryController`(조회)만 존재
- MarketPlace 측에서 `@Deprecated` 처리 완료
- **판단 필요**: 셀러 정보를 MarketPlace에서 세토프로 동기화해야 하면 API 추가 필요

---

## 🟡 INFO-002: 셀러 주소 Admin API 미존재

- MarketPlace에서 `POST/PUT/DELETE /api/v2/admin/seller-addresses/sellers/{sellerId}` 호출 시 **404 Not Found**
- 세토프 Admin 모듈에 `SellerAddressController` 자체가 없음
- MarketPlace 측에서 `@Deprecated` 처리 완료
- **판단 필요**: 셀러 주소를 세토프에 동기화할 필요가 있는지

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
