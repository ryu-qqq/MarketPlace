# Legacy Endpoint Traffic Analysis

> 분석일: 2026-02-21
> 분석 기간: 7일
> 데이터 소스: AWS CloudWatch `/aws/ecs/gateway-prod/application`
> 쿼리 필터: OMS IP 기반 (외부 OMS가 호출하는 엔드포인트만 구현 대상)

---

## IP-소스 매핑

| IP | 소스 | 역할 |
|----|------|------|
| `3.34.54.99` | **어드민** | MarketPlace 어드민 서버 (구현 제외) |
| `27.102.150.130` | **사방넷 OMS** | 외부 OMS 연동 |
| `112.175.152.87` | **셀릭 OMS** | 외부 OMS 연동 |
| `119.205.195.140` | **셀릭 OMS** | 외부 OMS 연동 |
| `121.140.112.177` | **셀릭 OMS** | 외부 OMS 연동 |
| `121.140.112.202` | **셀릭 OMS** | 외부 OMS 연동 |

---

## 게이트웨이 라우팅 구성

| 호스트 | 대상 서비스 |
|--------|------------|
| `admin-server.set-of.net/**` | `setof-commerce-legacy-admin-prod:8089` |
| `server.set-of.net/**` | `setof-commerce-legacy-api-prod:8088` (프론트) |
| `*/api/v1/market/**` | `marketplace-web-api-prod:9090` |

---

## 1. Product 엔드포인트 (OMS 호출)

### 사방넷 OMS (`27.102.150.130`)

| Method | Endpoint | 호출수 |
|--------|----------|--------|
| GET | `/api/v1/product/group/{id}` | 1,968 |
| PUT | `/api/v1/product/group/{id}/option` | 1,648 |
| PUT | `/api/v1/product/group/{id}/notice` | 320 |
| PUT | `/api/v1/product/group/{id}` | 296 |
| PATCH | `/api/v1/product/group/{id}/price` | 226 |
| PATCH | `/api/v1/product/group/{id}/display-yn` | 118 |
| PATCH | `/api/v1/product/group/{id}/out-stock` | 100 |
| PUT | `/api/v1/product/group/{id}/images` | 86 |
| POST | `/api/v1/product/group` | 78 |
| PUT | `/api/v1/product/group/{id}/detailDescription` | 8 |

### 셀릭 OMS (`112.175.152.87`)

| Method | Endpoint | 호출수 |
|--------|----------|--------|
| GET | `/api/v1/product/group/{id}` | 9,420 |
| PATCH | `/api/v1/product/group/{id}/stock` | 2,968 |
| PUT | `/api/v1/product/group/{id}` | 2,800 |

### LegacyProductController 최종 (11개)

| # | Method | Path (prefix: `/api/v1/legacy/product/group`) | 주요 호출 소스 |
|---|--------|-----------------------------------------------|---------------|
| 1 | GET | `/{productGroupId}` | 셀릭(9,420), 사방넷(1,968) |
| 2 | POST | (base) | 사방넷(78) |
| 3 | PUT | `/{productGroupId}` | 셀릭(2,800), 사방넷(296) |
| 4 | PUT | `/{productGroupId}/notice` | 사방넷(320) |
| 5 | PUT | `/{productGroupId}/images` | 사방넷(86) |
| 6 | PUT | `/{productGroupId}/detailDescription` | 사방넷(8) |
| 7 | PUT | `/{productGroupId}/option` | 사방넷(1,648) |
| 8 | PATCH | `/{productGroupId}/price` | 사방넷(226) |
| 9 | PATCH | `/{productGroupId}/display-yn` | 사방넷(118) |
| 10 | PATCH | `/{productGroupId}/out-stock` | 사방넷(100) |
| 11 | PATCH | `/{productGroupId}/stock` | 셀릭(2,968) |

---

## 2. 비상품 엔드포인트 (OMS 호출)

### Auth

| 소스 | Method | Endpoint | 호출수 | 설명 |
|------|--------|----------|--------|------|
| 셀릭 | POST | `/api/v1/auth/authentication` | 12,088 | 로그인 (인증) |
| 사방넷 | POST | `/api/v1/auth/authentication` | 6,754 | 로그인 (인증) |

> 패턴: `POST /auth/authentication` → `GET /seller` 순서로 항상 쌍으로 호출.
> OMS가 API 호출 전 매번 인증 + 셀러정보 확인하는 흐름.

### Seller

| 소스 | Method | Endpoint | 호출수 | 설명 |
|------|--------|----------|--------|------|
| 셀릭 | GET | `/api/v1/seller` | 12,088 | 현재 인증된 셀러 정보 조회 |
| 사방넷 | GET | `/api/v1/seller` | 398 | 현재 인증된 셀러 정보 조회 |

> 쿼리 파라미터 없음. 토큰 기반으로 "내 셀러 정보" 반환.

### Order

| 소스 | Method | Endpoint | 호출수 | 설명 |
|------|--------|----------|--------|------|
| 셀릭 | GET | `/api/v1/orders` | 13,372 | 주문 목록 조회 |
| 사방넷 | GET | `/api/v1/orders` | 7,670 | 주문 목록 조회 |
| 사방넷 | PUT | `/api/v1/order` | 882 | 주문 수정 |
| 사방넷 | GET | `/api/v1/order/{id}` | 744 | 주문 상세 조회 |
| 셀릭 | GET | `/api/v1/order/{id}` | 8 | 주문 상세 조회 |

### QnA

| 소스 | Method | Endpoint | 호출수 | 설명 |
|------|--------|----------|--------|------|
| 사방넷 | GET | `/api/v1/qnas` | 4,316 | 문의 목록 조회 |

### Shipment

| 소스 | Method | Endpoint | 호출수 | 설명 |
|------|--------|----------|--------|------|
| 사방넷 | GET | `/api/v1/shipment/company-codes` | 372 | 택배사 코드 목록 조회 |

---

## 3. 전체 구현 대상 요약

### OMS가 호출하는 레거시 엔드포인트 (총 18개)

| 도메인 | 엔드포인트 수 | 구현 상태 |
|--------|-------------|----------|
| **Product** | 11개 | LegacyProductController ✅ |
| **Auth** | 1개 (`POST /auth/authentication`) | TODO |
| **Seller** | 1개 (`GET /seller`) | TODO |
| **Order** | 3개 (`GET /orders`, `GET /order/{id}`, `PUT /order`) | TODO |
| **QnA** | 1개 (`GET /qnas`) | TODO |
| **Shipment** | 1개 (`GET /shipment/company-codes`) | TODO |

### 어드민 전용 (구현 제외 → MarketPlace 자체 기능으로 대체)

| Endpoint | 호출수 | 사유 |
|----------|--------|------|
| `GET /product/group/uuid/{uuid}` | 4,014 | 내부 어드민 전용 |
| `GET /products/group` (리스트) | 252 | 내부 어드민 전용 |

### 기존 정의됐으나 OMS 미사용 (구현 제외)

| 도메인 | 미사용 엔드포인트 |
|--------|------------------|
| Auth | `GET /auth/{authId}`, `POST /auth/admin-validation`, `GET /auth/{sellerId}`, `PATCH /auth/approval-status` |
| Brand | `GET /brands`, `GET /brand/external/{siteId}/mapping` |
| Category | `GET /category/{id}`, `GET /category/parent/{id}`, `GET /category/parents`, `GET /category/page`, `GET /category/external/{siteId}/mapping` |
| Order | `GET /settlements`, `GET /order/history/{id}`, `GET /shipment/order/{id}`, `GET /order/today-dashboard`, `GET /order/date-dashboard` |

---

## 구현 원칙

- **외부 OMS(사방넷, 셀릭)가 호출하는 엔드포인트만 레거시 컨트롤러로 구현**
- **어드민 전용 엔드포인트는 구현하지 않음** → MarketPlace 자체 기능으로 대체
- 게이트웨이에서 `admin-server.set-of.net` 트래픽을 MarketPlace로 라우팅 예정

## 비고

- 게이트웨이 로그에는 `http_host` 필드 없음 → `http_client_ip`로 소스 구분
- 레거시 컨트롤러 경로: `/api/v1/legacy/...` (기존 세토프 `/api/v1/...`과 구분)
- CloudWatch 로그 그룹: `/aws/ecs/gateway-prod/application`
- OMS 호출 패턴: `POST /auth/authentication` → `GET /seller` → 실제 API 호출
