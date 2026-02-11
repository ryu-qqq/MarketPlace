# 상품 도메인 설계 문서

> **상태**: Draft (러프 설계)
> **작성일**: 2026-02-10
> **브랜치**: feature/product-catalog-structure

---

## 1. 개요

이커머스 마켓플레이스의 상품 도메인 설계.
상품 그룹 → 상품(SKU) 구조에 캐노니컬 옵션 시스템을 결합하여
셀러의 자유 입력 옵션을 정규화하고, 외부몰 연동을 자동화한다.

---

## 2. 핵심 설계 원칙

- **ProductGroup ↔ Product 분리**: 별도 Aggregate로 운영. 재고/가격 변경이 ProductGroup 잠금 없이 가능.
- **캐노니컬 옵션**: 시스템이 관리하는 read-only 마스터 데이터. 시드 데이터로 투입, 수정 불가.
- **3-Tier 옵션 매핑**: 셀러 입력 → 캐노니컬 → 채널별 매핑. 셀러는 캐노니컬 하나에만 매핑하면 모든 외부몰 자동 연동.
- **기존 패턴 재활용**: Notice(read-only 마스터), BrandPreset/Mapping(채널 매핑) 패턴 동일 적용.

---

## 3. Aggregate 구조

### 3.1 CanonicalOptionGroup (마스터 데이터, read-only)

시스템이 관리하는 정규화된 옵션 표준. NoticeCategory와 동일한 패턴.

```
CanonicalOptionGroup ── Aggregate Root
├── id (CanonicalOptionGroupId)
├── code: "COLOR", "SIZE_SHOES", "SIZE_CLOTHING", "SIZE_PANTS" ...
├── name: NoticeCategoryName 패턴 (nameKo, nameEn)
├── active
├── List<CanonicalOptionValue> ── child Entity
│   ├── id (CanonicalOptionValueId)
│   ├── code: "BLACK", "WHITE", "RED", "220", "S", "M" ...
│   ├── name
│   └── sortOrder
├── createdAt
└── updatedAt
```

**특징**:
- DB migration으로만 데이터 관리 (시드)
- Application 레이어는 조회 UseCase만 존재
- 수정 시 기존 매핑 전체가 꼬이므로 Command 없음

### 3.2 ProductGroup (Aggregate Root)

상품의 상위 개념. 공통 속성과 셀러 옵션 구조를 관리.

```
ProductGroup ── Aggregate Root
├── id (ProductGroupId)
├── sellerId (Long, 참조)
├── brandId (Long, 참조)
├── categoryId (Long, 참조)
├── shippingPolicyId (Long, 참조)
├── refundPolicyId (Long, 참조)
├── productGroupName
├── descriptionHtml (상세설명, HTML)
├── optionType: NONE | SINGLE | COMBINATION | FREE_INPUT
├── status: DRAFT | ACTIVE | INACTIVE | SOLDOUT | DELETED
│
├── List<ProductGroupImage> ── child Entity
│   ├── id
│   ├── imageUrl
│   ├── imageType: THUMBNAIL | DETAIL
│   └── sortOrder
│
├── List<SellerOptionGroup> ── child Entity
│   ├── id
│   ├── optionGroupName: "색상", "사이즈" (셀러 자유 입력)
│   ├── canonicalOptionGroupId (→ CanonicalOptionGroup 참조, nullable)
│   ├── sortOrder
│   └── List<SellerOptionValue> ── child Entity
│       ├── id
│       ├── optionValueName: "검정", "260" (셀러 자유 입력)
│       ├── canonicalOptionValueId (→ CanonicalOptionValue 참조, nullable)
│       └── sortOrder
│
├── createdAt
└── updatedAt
```

**optionType별 동작**:

| optionType | SellerOptionGroup | Product 수 | 비고 |
|---|---|---|---|
| NONE | 0개 | 1개 | 옵션 없는 단품 |
| SINGLE | 1개 | 옵션값 수만큼 | 예: 사이즈만 |
| COMBINATION | 2개 | 조합 수만큼 | 예: 컬러 × 사이즈 |
| FREE_INPUT | 0개 | 1개 | 구매 시 텍스트 입력 |

**외부 참조 (ID만 보유)**:

| 참조 대상 | 용도 |
|---|---|
| Seller | 상품 소유 셀러 |
| Brand | 상품 브랜드 |
| Category | 상품 카테고리 (→ CategoryGroup → NoticeCategory 고시정보 결정) |
| ShippingPolicy | 배송 정책 |
| RefundPolicy | 반품 정책 |

### 3.3 Product (별도 Aggregate)

SKU 단위. 실제 판매/재고 관리 대상.

```
Product ── Aggregate Root (별도)
├── id (ProductId)
├── productGroupId (Long, → ProductGroup 참조)
├── skuCode
├── salePrice
├── discountPrice
├── stockQuantity
├── status: ACTIVE | INACTIVE | SOLDOUT
├── sortOrder
│
├── List<ProductOptionMapping> ── child Entity
│   ├── id
│   └── sellerOptionValueId (→ ProductGroup의 SellerOptionValue 참조)
│
├── createdAt
└── updatedAt
```

**Product 분리 이유**:
- 재고/가격 변경이 빈번 → ProductGroup 잠금 없이 독립 수정
- 재고 변경 시 도메인 이벤트 발행 → 외부몰 동기화 트리거
- ProductGroup과는 productGroupId로 참조

---

## 4. 캐노니컬 옵션 매핑 체계

### 4.1 3-Tier 매핑 구조

```
Tier 1: 셀러 입력 (자유형)
  SellerOptionGroup: "색상"
  SellerOptionValue: "검정", "하양", "빨강"
        │
        ▼ (셀러가 1회 매핑)
Tier 2: 캐노니컬 (시스템 표준)
  CanonicalOptionGroup: COLOR
  CanonicalOptionValue: BLACK, WHITE, RED
        │
        ▼ (시스템이 관리)
Tier 3: 채널별 매핑
  네이버: BLACK → "10"
  쿠팡:   BLACK → "BK"
  11번가: BLACK → "블랙"
```

### 4.2 매핑 포인트

캐노니컬 매핑은 **셀러 옵션 레벨**에서 발생:

```
seller_option_group.canonical_option_group_id  → CanonicalOptionGroup
seller_option_value.canonical_option_value_id  → CanonicalOptionValue
```

Product는 캐노니컬을 직접 알 필요 없음.
외부몰 연동 시점에 체인을 따라감:

```
Product → SellerOptionValue → CanonicalOptionValue → ChannelOptionValue
```

### 4.3 채널 옵션 매핑 (BrandPreset/Mapping 패턴 동일)

```
ChannelOptionMapping
├── salesChannelId
├── canonicalOptionValueId
└── externalOptionCode
```

기존 BrandPreset → BrandMapping, CategoryPreset → CategoryMapping과 동일한 패턴.

---

## 5. DB 테이블 구조 (예상)

### 5.1 캐노니컬 옵션 (마스터)

```sql
canonical_option_group (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    code            VARCHAR(50) NOT NULL UNIQUE,
    name_ko         VARCHAR(100) NOT NULL,
    name_en         VARCHAR(100),
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

canonical_option_value (
    id                          BIGINT PRIMARY KEY AUTO_INCREMENT,
    canonical_option_group_id   BIGINT NOT NULL,
    code                        VARCHAR(50) NOT NULL,
    name_ko                     VARCHAR(100) NOT NULL,
    name_en                     VARCHAR(100),
    sort_order                  INT NOT NULL DEFAULT 0,
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (canonical_option_group_id) REFERENCES canonical_option_group(id),
    UNIQUE KEY uk_group_code (canonical_option_group_id, code)
);
```

### 5.2 상품 그룹

```sql
product_group (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id           BIGINT NOT NULL,
    brand_id            BIGINT NOT NULL,
    category_id         BIGINT NOT NULL,
    shipping_policy_id  BIGINT NOT NULL,
    refund_policy_id    BIGINT NOT NULL,
    name                VARCHAR(200) NOT NULL,
    description_html    TEXT,
    option_type         VARCHAR(20) NOT NULL,  -- NONE, SINGLE, COMBINATION, FREE_INPUT
    status              VARCHAR(20) NOT NULL,  -- DRAFT, ACTIVE, INACTIVE, SOLDOUT, DELETED
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

product_group_image (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_group_id    BIGINT NOT NULL,
    image_url           VARCHAR(500) NOT NULL,
    image_type          VARCHAR(20) NOT NULL,  -- THUMBNAIL, DETAIL
    sort_order          INT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_group_id) REFERENCES product_group(id)
);

seller_option_group (
    id                              BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_group_id                BIGINT NOT NULL,
    option_group_name               VARCHAR(100) NOT NULL,
    canonical_option_group_id       BIGINT,          -- nullable (미매핑 허용)
    sort_order                      INT NOT NULL DEFAULT 0,
    created_at                      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_group_id) REFERENCES product_group(id),
    FOREIGN KEY (canonical_option_group_id) REFERENCES canonical_option_group(id)
);

seller_option_value (
    id                              BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_option_group_id          BIGINT NOT NULL,
    option_value_name               VARCHAR(100) NOT NULL,
    canonical_option_value_id       BIGINT,          -- nullable (미매핑 허용)
    sort_order                      INT NOT NULL DEFAULT 0,
    created_at                      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_option_group_id) REFERENCES seller_option_group(id),
    FOREIGN KEY (canonical_option_value_id) REFERENCES canonical_option_value(id)
);
```

### 5.3 상품 (SKU)

```sql
product (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_group_id    BIGINT NOT NULL,
    sku_code            VARCHAR(100),
    sale_price          DECIMAL(15,2) NOT NULL,
    discount_price      DECIMAL(15,2),
    stock_quantity      INT NOT NULL DEFAULT 0,
    status              VARCHAR(20) NOT NULL,  -- ACTIVE, INACTIVE, SOLDOUT
    sort_order          INT NOT NULL DEFAULT 0,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_group_id) REFERENCES product_group(id)
);

product_option_mapping (
    id                      BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id              BIGINT NOT NULL,
    seller_option_value_id  BIGINT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (seller_option_value_id) REFERENCES seller_option_value(id),
    UNIQUE KEY uk_product_option (product_id, seller_option_value_id)
);
```

### 5.4 채널 옵션 매핑

```sql
channel_option_mapping (
    id                          BIGINT PRIMARY KEY AUTO_INCREMENT,
    sales_channel_id            BIGINT NOT NULL,
    canonical_option_value_id   BIGINT NOT NULL,
    external_option_code        VARCHAR(100) NOT NULL,
    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (canonical_option_value_id) REFERENCES canonical_option_value(id),
    UNIQUE KEY uk_channel_canonical (sales_channel_id, canonical_option_value_id)
);
```

---

## 6. 캐노니컬 옵션 그룹 상세 (확정)

> 원래 10개에서 INSEAM, BRACELET, HAT 추가로 **13개 그룹, 483개 값** 확정.
> DDL: V37, 시드 데이터: V38~V50

### 6.1 전체 현황

| 그룹ID | 코드 | 이름 | 값 수 | 마이그레이션 |
|--------|------|------|-------|-------------|
| 1 | COLOR | 컬러 | 20 | V38 |
| 2 | SIZE_CLOTHING | 의류 사이즈 | 52 | V39 |
| 3 | SIZE_SHOES | 신발 사이즈 | 178 | V40 |
| 4 | SIZE_PANTS | 바지 사이즈 | 19 | V41 |
| 5 | SIZE_INSEAM | 기장 사이즈 | 8 | V42 |
| 6 | SIZE_RING | 반지 사이즈 | 89 | V43 |
| 7 | SIZE_BRACELET | 팔찌 사이즈 | 9 | V44 |
| 8 | SIZE_HAT | 모자 사이즈 | 12 | V45 |
| 9 | SIZE_KIDS | 아동 사이즈 | 41 | V46 |
| 10 | SIZE_GENERAL | 범용 사이즈 | 9 | V47 |
| 11 | STORAGE | 저장용량 | 11 | V48 |
| 12 | VOLUME | 용량 | 19 | V49 |
| 13 | WEIGHT | 중량 | 16 | V50 |

### 6.2 코드 컨벤션

- **숫자 사이즈 충돌 방지**: 국가/체계 접두어 사용
  - `KR_` (한국), `EU_` (유럽), `US_` / `USM_` / `USW_` / `USC_` / `USY_` (미국), `UK_` / `UKC_` (영국)
- **반사이즈(0.5)**: `_5` 접미어 (예: `EU_35_5`, `USM_8_5`)
- **한국 신장 기반**: `H_` 접두어 (예: `H_100`)
- **단위 포함 코드**: `8GB`, `50ML`, `1KG`, `1_5KG`

### 6.3 주요 설계 결정

| 결정 사항 | 선택 | 이유 |
|-----------|------|------|
| SIZE_CLOTHING에 XXXS/XXS 추가 | O | 럭셔리 브랜드 대응 |
| SIZE_CLOTHING에 EU/US/UK 포함 | O | 글로벌 브랜드 대응 |
| SIZE_SHOES에 아동 범위 포함 | O | 신발은 아동~성인 연속 스펙트럼 |
| SIZE_PANTS + SIZE_INSEAM 분리 | O | 1그룹 = 1차원 원칙, COMBINATION 대응 |
| SIZE_KIDS에 신발 미포함 | O | 아동 신발은 SIZE_SHOES에서 처리 |
| SIZE_BRACELET, SIZE_HAT 추가 | O | 럭셔리 브랜드 악세사리 사이즈 필요 |

---

## 7. 주요 흐름

### 7.1 상품 등록

```
1. 셀러가 ProductGroup 생성
   - 기본 정보 입력 (이름, 상세설명, 브랜드, 카테고리 선택)
   - 배송/반품 정책 선택
   - optionType 선택

2. optionType에 따라:
   - NONE: Product 1개 자동 생성 (가격, 재고 입력)
   - SINGLE: SellerOptionGroup 1개 + SellerOptionValue N개 입력 → Product N개
   - COMBINATION: SellerOptionGroup 2개 입력 → Product 조합 수만큼
   - FREE_INPUT: Product 1개 (구매 시 텍스트 입력 필드)

3. 셀러가 SellerOptionValue → CanonicalOptionValue 매핑 (선택)
   - 매핑하면 외부몰 연동 시 자동 변환
   - 미매핑 시 수동 처리 필요
```

### 7.2 재고 변경 → 외부몰 동기화

```
1. Product.updateStock(newQuantity)
2. StockChangedEvent 도메인 이벤트 발행
3. EventHandler:
   a. productGroupId로 연동 대상 채널 목록 조회
   b. 해당 Product의 옵션 조합 조회
      Product → SellerOptionValue → CanonicalOptionValue
   c. 각 채널별 매핑 조회
      CanonicalOptionValue → ChannelOptionMapping → externalOptionCode
   d. 채널 API 호출 (재고 업데이트)
```

### 7.3 고시정보 연결

```
ProductGroup.categoryId
  → Category.categoryGroup (CLOTHING, SHOES, ...)
    → NoticeCategory (고시정보 카테고리)
      → NoticeField[] (제조국, 제조자, 소재 등)
        → 상품별 고시정보 값 입력 (별도 테이블: product_notice_value)
```

---

## 8. 개발 순서

| 단계 | 도메인 | 패턴 | 산출물 |
|------|--------|------|--------|
| 1 | CanonicalOption | Notice 패턴 (read-only 마스터) | Migration, Domain, Application(조회), Persistence, REST API |
| 2 | ProductGroup | 새 Aggregate | Migration, Domain, Application(CRUD), Persistence, REST API |
| 3 | Product | 별도 Aggregate | Migration, Domain, Application(CRUD + 이벤트), Persistence, REST API |
| 4 | ChannelOptionMapping | BrandPreset/Mapping 패턴 | Migration, Domain, Application, Persistence, REST API |
| 5 | ProductNoticeValue | 상품별 고시정보 | Migration, Domain, Application, Persistence |

---

## 9. 미결 사항 (디테일 잡을 것)

- [x] CanonicalOptionGroup 시드 데이터 범위 → **13개 그룹, 483개 값 확정** (V38~V50)
- [ ] ProductGroup.descriptionHtml 별도 테이블 분리 여부 (대용량 HTML)
- [ ] COMBINATION 옵션 시 전체 교차 조합 강제 vs 선택적 조합 허용
- [ ] FREE_INPUT 옵션의 구체적 구현 방식 (구매 시점 텍스트 입력 필드)
- [ ] Product 가격 정책 (salePrice, discountPrice 외 추가 필드?)
- [ ] 상품 상태 전이 규칙 (DRAFT → ACTIVE → INACTIVE / SOLDOUT → DELETED)
- [ ] StockChangedEvent 외 추가 도메인 이벤트 필요 여부
- [ ] product_notice_value 테이블 구조
- [ ] ChannelOptionMapping을 OptionPreset/OptionMapping 2단 구조로 갈지 단일 테이블로 갈지
- [ ] 이미지 업로드/저장 전략 (S3 등 외부 스토리지 연동)
