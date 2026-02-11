# Seller Domain & Policy Specification

> 셀러 도메인 + 배송정책/환불정책 재설계 기능 정의서
>
> 브랜치: `feature/seller-policy-redesign`
> 작성일: 2026-01-26
> 벤치마킹: 쿠팡 마켓플레이스, 네이버 스마트스토어

---

## 1. 개요

### 1.1 목적

셀러 도메인을 재설계하고, 배송/환불 정책을 분리하여 유연하게 관리할 수 있는 시스템 구축

### 1.2 핵심 요구사항

| 구분         | 요구사항                             | 우선순위 |
| ------------ | ------------------------------------ | -------- |
| 셀러 재설계  | 핵심 정보만 정리, 단순 상태 관리     | P0       |
| 다중 정책    | 셀러당 배송/환불 정책 N개 보유       | P0       |
| Fallback     | 정책 미지정 시 시스템 기본 정책 적용 | P0       |
| 출고지/반품지 | 배송정책과 연결된 주소 관리          | P0       |

### 1.3 정책 적용 우선순위 (Fallback Chain)

```text
1순위: 상품에 직접 지정된 정책
    ↓ (없으면)
2순위: 셀러의 기본(is_default=true) 정책
    ↓ (없으면)
3순위: 시스템 기본 정책 (DB 관리)
```

### 1.4 결정 사항

| 항목             | 결정                                    |
| ---------------- | --------------------------------------- |
| 셀러 상태        | 활성/비활성만 (상태 이력 관리 안함)     |
| 약관동의         | 제거 (불필요)                           |
| 기본정책 참조    | seller 테이블에서 제거 (is_default로 관리) |
| 정산정보         | 추후 정산 설계 시 별도 진행             |
| FK 정책          | FK 제거, INDEX로 대체 (운영 편의)       |
| 삭제 정책        | Soft Delete (deleted_at)                |

---

## 2. 신규 스키마 설계

### 2.1 전체 ERD

```text
┌─────────────────────────────────────────────────────────────┐
│                         seller                              │
│  셀러 핵심 정보 (단순화)                                    │
└─────────────────────────────────────────────────────────────┘
        │
        ├── 1:1 ──→ seller_business_info (사업자 + CS)
        │
        ├── 1:N ──→ seller_address (출고지/반품지)
        │                   │
        │                   └──→ shipping_policy에서 참조
        │
        ├── 1:N ──→ shipping_policy (배송정책)
        │
        └── 1:N ──→ refund_policy (환불정책)

┌─────────────────────────────────────────────────────────────┐
│                      system_policy                          │
│  시스템 기본 정책 (Fallback용)                              │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 seller (셀러 기본 정보)

```sql
CREATE TABLE seller (
    seller_id           BIGINT PRIMARY KEY AUTO_INCREMENT,

    -- 기본 정보
    seller_name         VARCHAR(100) NOT NULL,       -- 셀러명 (내부 관리용)
    display_name        VARCHAR(100) NOT NULL,       -- 노출명 (고객 노출용)
    logo_url            VARCHAR(500),
    description         TEXT,

    -- 상태 (단순화: 활성/비활성만)
    is_active           TINYINT(1) NOT NULL DEFAULT 1,

    -- 감사
    created_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at          DATETIME,

    INDEX idx_seller_name (seller_name),
    INDEX idx_seller_display_name (display_name),
    INDEX idx_seller_active (is_active)
);
```

### 2.3 seller_business_info (사업자 + CS 정보)

```sql
CREATE TABLE seller_business_info (
    seller_business_info_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id                BIGINT NOT NULL,

    -- 사업자 정보
    registration_number      VARCHAR(20) NOT NULL,       -- 사업자등록번호
    company_name             VARCHAR(100) NOT NULL,      -- 상호명
    representative           VARCHAR(50) NOT NULL,       -- 대표자명
    sale_report_number       VARCHAR(50),                -- 통신판매업 신고번호

    -- 사업장 주소
    business_address_zipcode VARCHAR(10),
    business_address_line1   VARCHAR(200),
    business_address_line2   VARCHAR(200),

    -- CS 정보
    cs_phone                 VARCHAR(20),                -- CS 대표번호
    cs_mobile                VARCHAR(20),                -- CS 휴대폰
    cs_email                 VARCHAR(100),               -- CS 이메일

    -- 감사
    created_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at               DATETIME,

    INDEX idx_business_info_seller (seller_id)
);
```

### 2.4 seller_address (출고지/반품지 통합)

```sql
CREATE TABLE seller_address (
    seller_address_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id            BIGINT NOT NULL,

    -- 주소 유형
    address_type         VARCHAR(20) NOT NULL,           -- SHIPPING(출고지), RETURN(반품지)

    -- 주소 정보
    address_name         VARCHAR(50),                    -- 주소 별칭 (예: "본사 창고", "물류센터")
    zipcode              VARCHAR(10) NOT NULL,
    address_line1        VARCHAR(200) NOT NULL,
    address_line2        VARCHAR(200),

    -- 담당자/수령인
    contact_name         VARCHAR(50) NOT NULL,
    contact_phone        VARCHAR(20) NOT NULL,

    -- 기본 여부 (유형별로 하나만 기본)
    is_default           TINYINT(1) NOT NULL DEFAULT 0,

    -- 감사
    created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at           DATETIME,

    INDEX idx_address_seller (seller_id),
    INDEX idx_address_type (seller_id, address_type),
    INDEX idx_address_default (seller_id, address_type, is_default)
);
```

**address_type 값:**
- `SHIPPING`: 출고지 (상품 발송 주소)
- `RETURN`: 반품지 (반품 수령 주소)

### 2.5 shipping_policy (배송 정책)

```sql
CREATE TABLE shipping_policy (
    shipping_policy_id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id                BIGINT NOT NULL,

    -- 기본 정보
    policy_name              VARCHAR(100) NOT NULL,
    is_default               TINYINT(1) NOT NULL DEFAULT 0,
    is_active                TINYINT(1) NOT NULL DEFAULT 1,

    -- 출고지/반품지 연결
    shipping_address_id      BIGINT,                       -- 출고지 (seller_address FK)
    return_address_id        BIGINT,                       -- 반품지 (seller_address FK)

    -- 배송비 유형
    shipping_fee_type        VARCHAR(20) NOT NULL,
    -- FREE, PAID, CONDITIONAL_FREE, QUANTITY_BASED

    -- 배송비 설정
    base_fee                 INT NOT NULL DEFAULT 0,              -- 기본 배송비
    free_threshold           INT,                                  -- 무료배송 기준금액

    -- 도서산간 추가배송비
    jeju_extra_fee           INT NOT NULL DEFAULT 0,
    island_extra_fee         INT NOT NULL DEFAULT 0,

    -- 반품/교환 배송비
    return_fee               INT NOT NULL DEFAULT 0,              -- 반품 배송비 (편도)
    exchange_fee             INT NOT NULL DEFAULT 0,              -- 교환 배송비 (왕복)

    -- 출고 정보
    lead_time_days           INT NOT NULL DEFAULT 1,              -- 출고 리드타임 (영업일)
    cutoff_time              TIME,                                 -- 당일출고 마감시간

    -- 감사
    created_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at               DATETIME,

    INDEX idx_shipping_policy_seller (seller_id),
    INDEX idx_shipping_policy_default (seller_id, is_default),
    INDEX idx_shipping_policy_shipping_address (shipping_address_id),
    INDEX idx_shipping_policy_return_address (return_address_id)
);
```

**배송정책 ↔ 주소 연결:**
- `shipping_address_id`: 이 정책의 출고지
- `return_address_id`: 이 정책의 반품지
- NULL이면 셀러의 기본(is_default) 주소 사용

### 2.6 refund_policy (환불 정책)

```sql
CREATE TABLE refund_policy (
    refund_policy_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id                BIGINT NOT NULL,

    -- 기본 정보
    policy_name              VARCHAR(100) NOT NULL,
    is_default               TINYINT(1) NOT NULL DEFAULT 0,
    is_active                TINYINT(1) NOT NULL DEFAULT 1,

    -- 반품/교환 기간
    return_period_days       INT NOT NULL DEFAULT 7,
    exchange_period_days     INT NOT NULL DEFAULT 7,

    -- 반품 불가 조건 (JSON)
    non_returnable_conditions JSON,
    -- ["포장 훼손", "태그 제거", "사용 흔적"]

    -- 자동환불 설정
    auto_refund_enabled      TINYINT(1) NOT NULL DEFAULT 0,
    auto_refund_hours        INT,                                  -- 자동환불 시간
    auto_refund_max_amount   INT,                                  -- 자동환불 최대금액

    -- 부분환불
    partial_refund_enabled   TINYINT(1) NOT NULL DEFAULT 1,

    -- 반품 검수
    inspection_required      TINYINT(1) NOT NULL DEFAULT 1,
    inspection_period_days   INT NOT NULL DEFAULT 3,

    -- 추가 안내
    additional_info          TEXT,

    -- 감사
    created_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at               DATETIME,

    INDEX idx_refund_policy_seller (seller_id),
    INDEX idx_refund_policy_default (seller_id, is_default)
);
```

### 2.7 system_policy (시스템 기본 정책)

```sql
CREATE TABLE system_policy (
    system_policy_id     BIGINT PRIMARY KEY AUTO_INCREMENT,

    -- 정책 유형
    policy_type          VARCHAR(20) NOT NULL UNIQUE,
    -- SHIPPING, REFUND

    -- 정책 데이터 (JSON)
    policy_data          JSON NOT NULL,

    -- 감사
    created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at           DATETIME
);

-- 초기 데이터
INSERT INTO system_policy (policy_type, policy_data) VALUES
('SHIPPING', '{
    "shippingFeeType": "CONDITIONAL_FREE",
    "baseFee": 3000,
    "freeThreshold": 50000,
    "jejuExtraFee": 3000,
    "islandExtraFee": 5000,
    "returnFee": 3000,
    "exchangeFee": 6000,
    "leadTimeDays": 2,
    "cutoffTime": "14:00"
}'),
('REFUND', '{
    "returnPeriodDays": 7,
    "exchangePeriodDays": 7,
    "nonReturnableConditions": ["포장 훼손으로 상품 가치 감소", "사용/소비로 상품 가치 감소"],
    "autoRefundEnabled": false,
    "partialRefundEnabled": true,
    "inspectionRequired": true,
    "inspectionPeriodDays": 3
}');
```

---

## 3. 테이블 요약

| 테이블                | 설명                 | 관계          |
| --------------------- | -------------------- | ------------- |
| seller                | 셀러 기본 정보       | Root          |
| seller_business_info  | 사업자 + CS 정보     | seller 1:1    |
| seller_address        | 출고지/반품지        | seller 1:N    |
| shipping_policy       | 배송 정책            | seller 1:N    |
| refund_policy         | 환불 정책            | seller 1:N    |
| system_policy         | 시스템 기본 정책     | 독립          |

### 공통 규칙

- **FK 없음**: INDEX로 대체 (운영 편의)
- **Soft Delete**: 모든 테이블에 `deleted_at` 포함
- **Audit**: `created_at`, `updated_at`, `deleted_at`

---

## 4. 주소 ↔ 배송정책 연결 구조

```text
┌─────────────────────────────────────────────────────────────┐
│                    seller_address                           │
│  address_type = 'SHIPPING' (출고지)                         │
│  address_type = 'RETURN' (반품지)                           │
└─────────────────────────────────────────────────────────────┘
                     ▲                    ▲
                     │                    │
         shipping_address_id      return_address_id
                     │                    │
┌─────────────────────────────────────────────────────────────┐
│                   shipping_policy                           │
│  - shipping_address_id (출고지 참조)                        │
│  - return_address_id (반품지 참조)                          │
│  - NULL이면 셀러의 기본(is_default) 주소 사용               │
└─────────────────────────────────────────────────────────────┘
```

**조회 로직:**
```
1. shipping_policy.shipping_address_id 있으면 → 해당 주소 사용
2. 없으면 → seller_address에서 is_default=true AND address_type='SHIPPING' 조회
3. 그것도 없으면 → 에러 또는 시스템 기본값
```

---

## 5. 도메인 모델 설계

### 5.1 Aggregate 구조

```text
┌─────────────────────────────────────────────────────────────┐
│                     Seller Aggregate                        │
│  - SellerId (ID VO)                                         │
│  - SellerName, DisplayName (VO)                             │
│  - LogoUrl, Description (VO)                                │
│  - isActive (boolean)                                       │
└─────────────────────────────────────────────────────────────┘
        │
        │ 별도 Aggregate (Long FK 참조)
        ▼
┌─────────────────────────────────────────────────────────────┐
│  SellerBusinessInfo  │  사업자 + CS 정보                    │
│  SellerAddress       │  출고지/반품지 (N개)                 │
│  ShippingPolicy      │  배송 정책 (N개)                     │
│  RefundPolicy        │  환불 정책 (N개)                     │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    SystemPolicy                             │
│  - 시스템 기본 배송/환불 정책 (Fallback)                    │
│  - 독립 Aggregate, 관리자만 수정                            │
└─────────────────────────────────────────────────────────────┘
```

### 5.2 Value Objects

```yaml
# ID VOs
SellerId: Long
SellerAddressId: Long
ShippingPolicyId: Long
RefundPolicyId: Long

# Seller VOs
SellerName: String (1-100자)
DisplayName: String (1-100자)
LogoUrl: String (URL 형식, nullable)
Description: String (nullable)

# Business VOs
RegistrationNumber: String (사업자번호 형식 검증)
CompanyName: String
Representative: String
SaleReportNumber: String (nullable)
Address: zipcode, line1, line2
CsContact: phone, mobile, email

# Address VOs
AddressType: Enum (SHIPPING, RETURN)
ContactInfo: name, phone

# Policy VOs
Money: Integer (원 단위)
ShippingFeeType: Enum (FREE, PAID, CONDITIONAL_FREE, QUANTITY_BASED)
```

---

## 6. API 설계

### 6.1 셀러 API

| Method | Endpoint              | 설명           |
| ------ | --------------------- | -------------- |
| POST   | `/sellers`            | 셀러 등록      |
| GET    | `/sellers/{sellerId}` | 셀러 상세 조회 |
| PUT    | `/sellers/{sellerId}` | 셀러 정보 수정 |
| PATCH  | `/sellers/{sellerId}/activate`   | 셀러 활성화   |
| PATCH  | `/sellers/{sellerId}/deactivate` | 셀러 비활성화 |
| DELETE | `/sellers/{sellerId}` | 셀러 삭제 (soft) |

### 6.2 사업자 정보 API

| Method | Endpoint                            | 설명           |
| ------ | ----------------------------------- | -------------- |
| GET    | `/sellers/{sellerId}/business-info` | 사업자정보 조회 |
| PUT    | `/sellers/{sellerId}/business-info` | 사업자정보 수정 |

### 6.3 주소 API (출고지/반품지)

| Method | Endpoint                                          | 설명           |
| ------ | ------------------------------------------------- | -------------- |
| POST   | `/sellers/{sellerId}/addresses`                   | 주소 등록      |
| GET    | `/sellers/{sellerId}/addresses`                   | 주소 목록      |
| GET    | `/sellers/{sellerId}/addresses?type=SHIPPING`     | 출고지 목록    |
| GET    | `/sellers/{sellerId}/addresses?type=RETURN`       | 반품지 목록    |
| GET    | `/sellers/{sellerId}/addresses/{addressId}`       | 주소 상세      |
| PUT    | `/sellers/{sellerId}/addresses/{addressId}`       | 주소 수정      |
| DELETE | `/sellers/{sellerId}/addresses/{addressId}`       | 주소 삭제      |
| PATCH  | `/sellers/{sellerId}/addresses/{addressId}/default` | 기본주소 지정 |

### 6.4 배송정책 API

| Method | Endpoint                                                   | 설명              |
| ------ | ---------------------------------------------------------- | ----------------- |
| POST   | `/sellers/{sellerId}/shipping-policies`                    | 배송정책 생성     |
| GET    | `/sellers/{sellerId}/shipping-policies`                    | 배송정책 목록     |
| GET    | `/sellers/{sellerId}/shipping-policies/{policyId}`         | 배송정책 상세     |
| PUT    | `/sellers/{sellerId}/shipping-policies/{policyId}`         | 배송정책 수정     |
| DELETE | `/sellers/{sellerId}/shipping-policies/{policyId}`         | 배송정책 삭제     |
| PATCH  | `/sellers/{sellerId}/shipping-policies/{policyId}/default` | 기본정책 지정     |
| PATCH  | `/sellers/{sellerId}/shipping-policies/{policyId}/activate`   | 정책 활성화    |
| PATCH  | `/sellers/{sellerId}/shipping-policies/{policyId}/deactivate` | 정책 비활성화  |

### 6.5 환불정책 API

| Method | Endpoint                                                  | 설명              |
| ------ | --------------------------------------------------------- | ----------------- |
| POST   | `/sellers/{sellerId}/refund-policies`                     | 환불정책 생성     |
| GET    | `/sellers/{sellerId}/refund-policies`                     | 환불정책 목록     |
| GET    | `/sellers/{sellerId}/refund-policies/{policyId}`          | 환불정책 상세     |
| PUT    | `/sellers/{sellerId}/refund-policies/{policyId}`          | 환불정책 수정     |
| DELETE | `/sellers/{sellerId}/refund-policies/{policyId}`          | 환불정책 삭제     |
| PATCH  | `/sellers/{sellerId}/refund-policies/{policyId}/default`  | 기본정책 지정     |
| PATCH  | `/sellers/{sellerId}/refund-policies/{policyId}/activate`   | 정책 활성화     |
| PATCH  | `/sellers/{sellerId}/refund-policies/{policyId}/deactivate` | 정책 비활성화   |

### 6.6 시스템 정책 API (관리자)

| Method | Endpoint                          | 설명                 |
| ------ | --------------------------------- | -------------------- |
| GET    | `/admin/system-policies/shipping` | 시스템 배송정책 조회 |
| PUT    | `/admin/system-policies/shipping` | 시스템 배송정책 수정 |
| GET    | `/admin/system-policies/refund`   | 시스템 환불정책 조회 |
| PUT    | `/admin/system-policies/refund`   | 시스템 환불정책 수정 |

---

## 7. 구현 계획

### Phase 1: Domain Layer

- [ ] Seller Aggregate + VOs
- [ ] SellerBusinessInfo Aggregate
- [ ] SellerAddress Aggregate
- [ ] ShippingPolicy Aggregate
- [ ] RefundPolicy Aggregate
- [ ] SystemPolicy Aggregate
- [ ] Domain Events

### Phase 2: Application Layer

- [ ] Seller UseCases
- [ ] SellerAddress UseCases
- [ ] ShippingPolicy UseCases
- [ ] RefundPolicy UseCases
- [ ] PolicyResolver 구현

### Phase 3: Persistence Layer

- [ ] Entity 구현
- [ ] Repository/Adapter 구현
- [ ] QueryDsl Repository

### Phase 4: REST API Layer

- [ ] Controllers
- [ ] DTOs
- [ ] Mappers
- [ ] 통합 테스트

### Phase 5: Migration

- [ ] DDL 스크립트 작성
- [ ] 데이터 마이그레이션 스크립트
- [ ] 시스템 기본 정책 초기 데이터

---

## 8. 참고 자료

### Sources

- [쿠팡 마켓플레이스 판매 방식](https://www.windly.cc/blog/coupang-selling-methods)
- [쿠팡윙 반품 정책 총정리](https://www.windly.cc/blog/coupangwing-return-management-seller-guide)
- [네이버 스마트스토어 배송비 설정](https://faq.bizesm.com/wordpress/?p=4371)
- [반품안심케어 이용 가이드](https://guide-kr.fassto.ai/nfa/return)
