# 상품 통합 마이그레이션 계획서

> 작성일: 2026-02-23
> 상태: Draft
> 관련 브랜치: feature/product-integration

---

## 1. 배경 및 목표

### 1.1 현재 상황

- **세토프(Setof)**: 기존 자사몰 겸 커머스 플랫폼. 어드민 서버가 외부몰 허브 역할 수행
- **MarketPlace OMS**: 세토프를 대체하는 신규 OMS. 세토프는 OMS가 관리하는 하나의 외부몰로 전환
- **외부 OMS(사방넷, 셀릭)**: 세토프 어드민 API를 호출하여 상품 등록/수정/재고 관리 수행 중
- **인프라**: 세토프 DB와 OMS DB가 **동일 RDS** 내 별도 스키마로 운영

### 1.2 핵심 문제

| 문제 | 상세 |
|------|------|
| PK 충돌 | OMS product_groups/products 테이블의 auto_increment가 세토프 PK 범위와 겹칠 수 있음 |
| PK 변경 불가 | 외부 OMS(사방넷, 셀릭)는 기존 세토프 PK를 변경할 수 없음 |
| 스키마 차이 | OMS 내부 상품 스키마 ≠ 세토프 상품 스키마 (컬럼, 관계 테이블 상이) |
| 이중 경로 | 기존 세토프 상품과 신규 OMS 상품이 동시 운영되어야 함 |
| SKU 레벨 PK | 외부 OMS가 productId(SKU PK)를 사용하는 엔드포인트 5개 존재 |

### 1.3 목표

1. 외부 OMS가 **에러 없이** 기존 세토프 PK로 계속 호출 가능
2. 신규 상품은 **OMS PK 체계**로 관리
3. **Strangler Fig 패턴**으로 점진적으로 세토프 의존 제거
4. 인바운드 프로덕트 파이프라인의 **비동기 분리** 및 안정성 개선
5. 최종적으로 세토프 ExternalProduct 전송 파이프라인까지 완성

---

## 2. 전략: Strangler Fig + InboundProduct 파이프라인

### 2.1 핵심 개념

```
모든 외부 OMS 요청
  → InboundProduct(매핑 허브)에서 라우팅
    ├─ CONVERTED: OMS 내부 상품으로 처리
    └─ LEGACY_IMPORTED: 세토프 스키마 fallback → 점진적 전환
```

### 2.2 PK 버퍼링

세토프 PK와 OMS PK가 절대 충돌하지 않도록 auto_increment 오프셋 설정.

| 테이블 | 세토프 max (확인 필요) | OMS auto_increment 시작 | 비고 |
|--------|----------------------|------------------------|------|
| `product_groups` | 조회 필요 | **세토프 max + 50,000 이상** | productGroupId |
| `products` | 조회 필요 | **세토프 max + 100,000 이상** | productId (SKU) |

```sql
-- 배포 전 세토프 실제 max 값 확인 필수
SELECT MAX(id) FROM setof_db.product_groups;
SELECT MAX(id) FROM setof_db.products;

-- OMS 테이블 auto_increment 조정
ALTER TABLE marketplace_db.product_groups AUTO_INCREMENT = {세토프_max + 버퍼};
ALTER TABLE marketplace_db.products AUTO_INCREMENT = {세토프_max + 버퍼};
```

### 2.3 신규 테이블: inbound_product_items

Product(SKU) 레벨 PK 매핑을 위한 테이블 추가.

```sql
CREATE TABLE inbound_product_items (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    inbound_product_id    BIGINT NOT NULL,               -- FK → inbound_products.id
    external_product_id   BIGINT NOT NULL,               -- 세토프 product PK
    internal_product_id   BIGINT,                        -- OMS product PK (전환 후 채움)
    status                VARCHAR(50) NOT NULL,           -- LEGACY_IMPORTED / CONVERTED
    created_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_external_product_id (external_product_id),
    INDEX idx_inbound_product_id (inbound_product_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 2.4 상태 확장: InboundProductStatus

```
기존:
  RECEIVED → PENDING_MAPPING → MAPPED → CONVERTED → CONVERT_FAILED

추가:
  LEGACY_IMPORTED    세토프에서 벌크 임포트됨. 아직 OMS 내부 상품 미생성.
                     레거시 스키마로 fallback 처리 중.
```

---

## 3. 실행 단계

### Phase 0: 선 적재 (벌크 마이그레이션)

> 목표: 세토프 기존 상품을 InboundProduct + InboundProductItems에 적재

#### 0-1. InboundProduct 벌크 적재

```sql
INSERT INTO inbound_products (
    inbound_source_id,
    external_product_code,
    product_name,
    external_brand_code,
    external_category_code,
    seller_id,
    regular_price,
    current_price,
    option_type,
    status,
    created_at,
    updated_at
)
SELECT
    1,                              -- LEGACY inbound source
    CAST(pg.id AS CHAR),           -- 세토프 productGroupId → externalProductCode
    pg.product_group_name,
    CAST(pg.brand_id AS CHAR),
    CAST(pg.category_id AS CHAR),
    pg.seller_id,
    pg.regular_price,
    pg.current_price,
    pg.option_type,
    'LEGACY_IMPORTED',
    NOW(),
    NOW()
FROM setof_db.product_groups pg
WHERE pg.status != 'DELETED';      -- 삭제된 상품 제외
```

#### 0-2. InboundProductItems 벌크 적재

```sql
INSERT INTO inbound_product_items (
    inbound_product_id,
    external_product_id,
    internal_product_id,
    status,
    created_at,
    updated_at
)
SELECT
    ip.id,
    p.id,                          -- 세토프 product PK
    NULL,                          -- OMS product PK (아직 미전환)
    'LEGACY_IMPORTED',
    NOW(),
    NOW()
FROM setof_db.products p
JOIN inbound_products ip
    ON ip.external_product_code = CAST(p.product_group_id AS CHAR)
    AND ip.inbound_source_id = 1;
```

#### 0-3. Auto-increment 조정

```sql
ALTER TABLE marketplace_db.product_groups AUTO_INCREMENT = {계산된 값};
ALTER TABLE marketplace_db.products AUTO_INCREMENT = {계산된 값};
```

#### 0-4. 검증

```sql
-- 적재 건수 확인
SELECT COUNT(*) FROM inbound_products WHERE status = 'LEGACY_IMPORTED';
SELECT COUNT(*) FROM inbound_product_items WHERE status = 'LEGACY_IMPORTED';

-- 세토프 원본과 비교
SELECT COUNT(*) FROM setof_db.product_groups WHERE status != 'DELETED';
SELECT COUNT(*) FROM setof_db.products p
    JOIN setof_db.product_groups pg ON p.product_group_id = pg.id
    WHERE pg.status != 'DELETED';
```

---

### Phase 1: 레거시 컨트롤러 라우팅 레이어

> 목표: 모든 레거시 엔드포인트가 InboundProduct를 통해 라우팅되도록 변경

#### 1-1. 라우팅 흐름

```
외부 OMS → LegacyProductCommandController
  │
  ▼
InboundProduct 조회 (externalProductCode = productGroupId)
  │
  ├─ 없음 (완전 신규) ─────────────────────────────────┐
  │   → InboundProduct 생성 (RECEIVED)                  │
  │   → 기존 파이프라인: 매핑 → 변환 → OMS 상품 등록      │
  │   → 응답: OMS PK (100001)                           │
  │                                                      │
  ├─ 있음 + CONVERTED ──────────────────────────────────┐
  │   → OMS 내부 상품으로 직접 처리                       │
  │   → 응답: externalProductCode (세토프 PK) 반환        │
  │                                                      │
  └─ 있음 + LEGACY_IMPORTED ────────────────────────────┐
      → 세토프 스키마에서 처리 (기존 로직)                 │
      → 응답: externalProductCode (세토프 PK) 반환        │
      → (비동기) OMS 전환 시도                            │
```

#### 1-2. 엔드포인트별 라우팅 상세

**ProductGroup 레벨 (productGroupId만 사용)**

| 엔드포인트 | 라우팅 방식 |
|-----------|------------|
| POST (등록) | 신규 → InboundProduct 파이프라인 → OMS PK 반환 |
| GET /{id} (조회) | InboundProduct 상태 기반 분기 |
| PUT /{id} (수정) | InboundProduct 상태 기반 분기 |
| PUT /{id}/notice | InboundProduct 상태 기반 분기 |
| PUT /{id}/images | InboundProduct 상태 기반 분기 |
| PUT /{id}/detailDescription | InboundProduct 상태 기반 분기 |
| PATCH /{id}/price | InboundProduct 상태 기반 분기 (ProductGroup 레벨만) |
| PATCH /{id}/display-yn | InboundProduct 상태 기반 분기 |

**Product(SKU) 레벨 (productId 사용)**

| 엔드포인트 | 라우팅 방식 |
|-----------|------------|
| PUT /{id}/option | InboundProduct 분기 + InboundProductItems로 productId 매핑 |
| PATCH /{id}/stock | InboundProduct 분기 + InboundProductItems로 productId 매핑 |
| PATCH /{id}/out-stock | InboundProduct 분기 (응답에만 productId) |

**Product PK 매핑 흐름 (option, stock 엔드포인트)**

```
사방넷 → PATCH /{productGroupId}/stock
  Body: [{ productId: 50001, quantity: 30 }, { productId: 50002, quantity: 25 }]
  │
  ▼
① InboundProduct 조회 (externalProductCode: "{productGroupId}")
  │
  ├─ CONVERTED:
  │   ② InboundProductItems 조회 (external_product_id IN [50001, 50002])
  │   ③ internal_product_id [300001, 300002]로 변환
  │   ④ OMS 내부 재고 업데이트
  │   ⑤ 응답에서 internal → external PK 역매핑
  │   → 응답: [{ productId: 50001, ... }, { productId: 50002, ... }]
  │
  └─ LEGACY_IMPORTED:
      ② 세토프 스키마에서 직접 처리 (productId 그대로 사용)
      → 응답: [{ productId: 50001, ... }, { productId: 50002, ... }]
```

#### 1-3. 응답 PK 규칙

```
LEGACY_IMPORTED 상태:
  productGroupId → externalProductCode (세토프 PK) 반환
  productId      → external_product_id (세토프 PK) 반환

CONVERTED 상태:
  productGroupId → externalProductCode (세토프 PK) 반환  ← OMS 내부 PK가 아님!
  productId      → external_product_id (세토프 PK) 반환  ← OMS 내부 PK가 아님!

신규 등록:
  productGroupId → OMS PK 반환 (100001~)
  productId      → OMS PK 반환 (300001~)
  * InboundProduct에 externalProductCode = OMS PK 저장
  * InboundProductItems에 external_product_id = OMS PK 저장
```

핵심: **외부 OMS가 한번 받은 PK는 절대 바뀌지 않는다.**
- 세토프에서 받았으면 세토프 PK를 계속 사용
- OMS에서 받았으면 OMS PK를 계속 사용
- InboundProduct/Items가 양방향 매핑 허브 역할

---

### Phase 2: 인바운드 프로덕트 비동기 분리

> 목표: 수신(RECEIVED)만 동기 처리, 매핑/변환은 비동기로 분리

#### 2-1. 현재 문제

```
현재 (동기):
  요청 → 수신 → 매핑 → 변환 → 내부 상품 등록 → 응답
                                              ↑ 여기까지 한 트랜잭션

  문제: 크롤링으로 대량 호출 시 응답 지연, 한 건 실패 시 전체 롤백
```

#### 2-2. 개선 후

```
변경 (비동기):
  요청 → 수신(RECEIVED) → 응답 (즉시)        ← 동기
         ↓
  [비동기 이벤트 또는 스케줄러]
  RECEIVED → 매핑 → MAPPED → 변환 → CONVERTED  ← 비동기
```

#### 2-3. 구현 방식

```
선택지 A: Spring ApplicationEvent + @Async
  - InboundProduct 저장 후 InboundProductReceivedEvent 발행
  - @EventListener + @Async로 매핑/변환 비동기 처리
  - 장점: 구현 단순
  - 단점: 서버 재시작 시 이벤트 유실 가능

선택지 B: SQS 기반
  - InboundProduct 저장 후 SQS 메시지 발행
  - SQS Consumer가 매핑/변환 처리
  - 장점: 메시지 유실 없음, 재시도 내장, DLQ 지원
  - 단점: 인프라 추가 필요 (이미 SQS 사용 중이면 추가 비용 적음)

선택지 C: 배치 스케줄러
  - @Scheduled로 주기적으로 RECEIVED 상태 상품 처리
  - 장점: 가장 단순, 장애 시 자동 재시도
  - 단점: 실시간성 부족 (폴링 주기만큼 지연)
```

#### 2-4. PENDING_MAPPING 재처리 스케줄러

```
@Scheduled(fixedDelay = 300_000)  // 5분 간격
void retryPendingMapping() {
    1. PENDING_MAPPING 상태 상품 조회 (limit 100)
    2. 매핑 재시도 (브랜드/카테고리 매핑 테이블 재조회)
    3. 매핑 성공 → MAPPED → 변환 시도
    4. 매핑 실패 → PENDING_MAPPING 유지 (재시도 횟수 증가)
    5. 재시도 횟수 초과 시 알림 (관리자 개입 필요)
}
```

#### 2-5. CONVERT_FAILED 재처리

```
@Scheduled(fixedDelay = 600_000)  // 10분 간격
void retryConvertFailed() {
    1. CONVERT_FAILED 상태 상품 조회 (limit 50)
    2. 변환 재시도
    3. 성공 → CONVERTED
    4. 실패 → 재시도 횟수 증가, 임계값 초과 시 알림
}
```

---

### Phase 3: 배치 전환 (LEGACY_IMPORTED → CONVERTED)

> 목표: 세토프 의존 상품을 점진적으로 OMS 내부 상품으로 전환

#### 3-1. 배치 전환 프로세스

```
@Scheduled 또는 수동 트리거
void convertLegacyImported() {
    // 1. LEGACY_IMPORTED 상태 상품 N건 조회
    List<InboundProduct> targets = findByStatus(LEGACY_IMPORTED, limit);

    for (InboundProduct ip : targets) {
        // 2. 세토프 스키마에서 전체 데이터 읽기
        SetofProductData setofData = setofSchemaReader.read(ip.externalProductCode());

        // 3. rawPayloadJson 채우기
        ip.updateRawPayload(serialize(setofData));

        // 4. 매핑 시도 (브랜드/카테고리)
        MappingResult mapping = mappingResolver.resolveMappingAndApply(ip, now);

        if (mapping.isFullyMapped()) {
            // 5. OMS ProductGroup + Product 생성
            ProductGroupRegistrationBundle bundle = conversionFactory.toBundle(ip, LEGACY);
            Long productGroupId = registrationCoordinator.register(bundle);

            // 6. InboundProduct 상태 업데이트
            ip.markConverted(productGroupId, now);

            // 7. InboundProductItems 매핑 업데이트
            List<Product> omsProducts = productQueryPort.findByGroupId(productGroupId);
            updateProductItemMappings(ip.id(), setofData.products(), omsProducts);
        }

        commandManager.persist(ip);
    }
}
```

#### 3-2. Product(SKU) 매핑 업데이트

```
세토프 Product 매칭 기준:
  - 옵션 조합 (optionName + optionValue) 기준 매칭
  - 또는 세토프 product 순서 기반 순차 매칭 (옵션 구조가 동일한 경우)

InboundProductItems 업데이트:
  external_product_id: 50001 → internal_product_id: 300001
  external_product_id: 50002 → internal_product_id: 300002
  status: LEGACY_IMPORTED → CONVERTED
```

#### 3-3. 전환율 모니터링

```sql
SELECT
    status,
    COUNT(*) AS cnt,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 1) AS pct
FROM inbound_products
WHERE inbound_source_id = 1
GROUP BY status
ORDER BY FIELD(status, 'LEGACY_IMPORTED', 'PENDING_MAPPING', 'MAPPED', 'CONVERTED', 'CONVERT_FAILED');

-- 목표:
-- LEGACY_IMPORTED | 0    | 0%     ← 전환 완료 목표
-- CONVERTED       | N    | 100%
```

---

### Phase 4: ExternalProduct 전송 파이프라인

> 목표: CONVERTED된 인바운드 상품 중 세토프 대상 상품을 ExternalProduct로 전송

#### 4-1. 전송 대상 필터링

```
세토프 전송 대상: 명품 관련 상품만
  - 카테고리 기반 필터링 (명품 카테고리 ID 목록)
  - 또는 브랜드 기반 필터링 (명품 브랜드 ID 목록)
  - 또는 수동 지정 (어드민에서 세토프 전송 플래그 설정)
```

#### 4-2. 전송 흐름

```
CONVERTED 상태의 InboundProduct
  ↓
[전송 대상 여부 판단]
  ├─ 세토프 전송 대상:
  │   → OMS 내부 ProductGroup → ExternalProduct 변환
  │   → SalesChannel(세토프) 연결
  │   → 세토프 사이트로 상품 데이터 전송
  │
  └─ 비대상:
      → 전송 없음. OMS 내부에서만 관리.
```

#### 4-3. 기존 파이프라인 활용

```
이미 존재하는 외부몰 전송 구조:
  ProductGroup → ExternalProduct → SalesChannel → 외부몰 API

세토프도 동일한 구조로 처리:
  ProductGroup → ExternalProduct(site: SETOF) → SalesChannel(SETOF) → 세토프 반영
```

---

### Phase 5: 세토프 DB 의존 완전 제거

> 목표: 세토프 스키마 조회/수정 코드 전체 제거

#### 5-1. 제거 조건 (모두 충족 시)

```
□ inbound_products 전체가 CONVERTED (LEGACY_IMPORTED = 0건)
□ inbound_product_items 전체가 CONVERTED
□ 레거시 fallback 호출 빈도 0 (모니터링 확인)
□ 세토프 전송 파이프라인 정상 운영 확인
□ 외부 OMS(사방넷/셀릭) 정상 동작 확인 (최소 2주 모니터링)
```

#### 5-2. 제거 대상

```
- 세토프 스키마 cross-schema 조회 코드
- LEGACY_IMPORTED 상태 처리 로직
- 레거시 fallback 분기문
- InboundProductStatus.LEGACY_IMPORTED enum 값 (deprecated → 제거)
- 세토프 전용 데이터 변환 코드
```

#### 5-3. 최종 상태

```
외부 OMS → 레거시 컨트롤러 → InboundProduct(매핑)
  → OMS 내부 상품으로 100% 처리
  → 세토프 전송 대상만 ExternalProduct로 변환/전송
  → 세토프 DB 의존 없음
```

---

## 4. 외부 OMS 엔드포인트 영향 분석

### 4.1 엔드포인트별 변경 범위

| # | 엔드포인트 | 현재 | Phase 1 이후 | SKU PK 매핑 필요 |
|---|-----------|------|-------------|:---:|
| 1 | POST /product/group | InboundProduct 파이프라인 | 유지 + OMS PK 반환 | O |
| 2 | GET /{id} | 세토프 직접 조회 | InboundProduct 라우팅 | O (응답) |
| 3 | PUT /{id} | 세토프 직접 수정 | InboundProduct 라우팅 | X |
| 4 | PUT /{id}/notice | 세토프 직접 수정 | InboundProduct 라우팅 | X |
| 5 | PUT /{id}/images | 세토프 직접 수정 | InboundProduct 라우팅 | X |
| 6 | PUT /{id}/detailDescription | 세토프 직접 수정 | InboundProduct 라우팅 | X |
| 7 | PUT /{id}/option | 세토프 직접 수정 | InboundProduct 라우팅 | O (요청+응답) |
| 8 | PATCH /{id}/price | 세토프 직접 수정 | InboundProduct 라우팅 | X |
| 9 | PATCH /{id}/display-yn | 세토프 직접 수정 | InboundProduct 라우팅 | X |
| 10 | PATCH /{id}/out-stock | 세토프 직접 수정 | InboundProduct 라우팅 | O (응답) |
| 11 | PATCH /{id}/stock | 세토프 직접 수정 | InboundProduct 라우팅 | O (요청+응답) |

### 4.2 외부 OMS 트래픽 (7일 기준)

| 엔드포인트 | 사방넷 | 셀릭 | 합계 |
|-----------|--------|------|------|
| GET /{id} | 1,968 | 9,420 | 11,388 |
| PATCH /stock | - | 2,968 | 2,968 |
| PUT /{id} | 296 | 2,800 | 3,096 |
| PUT /option | 1,648 | - | 1,648 |
| PUT /notice | 320 | - | 320 |
| PATCH /price | 226 | - | 226 |
| PATCH /display-yn | 118 | - | 118 |
| PATCH /out-stock | 100 | - | 100 |
| PUT /images | 86 | - | 86 |
| POST (등록) | 78 | - | 78 |
| PUT /detailDescription | 8 | - | 8 |

---

## 5. 인바운드 프로덕트 코드 개선사항

Phase 1~2와 병행하여 기존 코드의 개선도 수행.

### 5.1 OptionType 매핑 중복 제거

```
현재:
  LegacyInboundApiMapper.mapLegacyOptionTypeToInternal()  ← 1차 매핑
  LegacyPayloadParser.resolveOptionType()                  ← 2차 매핑 (동일 로직)

개선:
  OptionTypeConverter 단일 유틸로 통합
```

### 5.2 에러 처리 강화

```
현재:
  catch (Exception e) → markConvertFailed() → 끝

개선:
  - 재시도 횟수 필드 추가 (retry_count)
  - 최대 재시도 횟수 설정 (예: 3회)
  - 초과 시 알림 발송
  - DLQ 또는 별도 실패 테이블로 이동
```

### 5.3 LegacyPayloadParser.toUpdateBundle() 구현

```
현재:
  return Optional.empty();  // 세토프 업데이트 미구현

개선:
  세토프 상품 수정 시 OMS 내부 ProductGroup도 함께 업데이트되도록 구현
```

---

## 6. 도메인 모델 변경사항

### 6.1 InboundProductStatus 확장

```java
public enum InboundProductStatus {
    RECEIVED,           // 신규 수신
    PENDING_MAPPING,    // 매핑 실패, 관리자 개입 필요
    MAPPED,             // 매핑 완료, 변환 대기
    CONVERTED,          // 내부 상품 변환 완료
    CONVERT_FAILED,     // 변환 실패
    LEGACY_IMPORTED;    // 세토프 벌크 임포트 (신규)
}
```

### 6.2 InboundProductItem 도메인 모델 (신규)

```java
public class InboundProductItem {
    private final InboundProductItemId id;
    private final InboundProductId inboundProductId;
    private final Long externalProductId;       // 세토프 product PK
    private Long internalProductId;              // OMS product PK (전환 후)
    private InboundProductItemStatus status;     // LEGACY_IMPORTED / CONVERTED
    private final Instant createdAt;
    private Instant updatedAt;
}
```

### 6.3 InboundProduct 변경

```java
// 기존 필드에 추가
public class InboundProduct {
    // ... 기존 필드

    private int retryCount;                      // 재시도 횟수 (신규)

    public void incrementRetry(Instant now) { ... }
    public boolean isRetryExhausted(int maxRetry) { ... }
}
```

---

## 7. 일정 및 우선순위

| Phase | 내용 | 선행 조건 | 예상 범위 |
|-------|------|----------|----------|
| **0** | 선 적재 + Auto-increment 조정 | 세토프 max PK 확인 | SQL 스크립트 |
| **1** | 레거시 컨트롤러 라우팅 레이어 | Phase 0 완료 | InboundProduct 라우팅 + InboundProductItem |
| **2** | 비동기 분리 + 재처리 스케줄러 | Phase 1 안정화 | 이벤트/배치 인프라 |
| **3** | 배치 전환 (LEGACY → CONVERTED) | Phase 1, 2 완료 | 배치 스케줄러 + 모니터링 |
| **4** | ExternalProduct 전송 파이프라인 | Phase 3 진행 중 | 세토프 전용 전송 로직 |
| **5** | 세토프 DB 의존 제거 | Phase 3 100% + Phase 4 안정 | 코드 정리 |

### 병행 가능 작업

```
Phase 0 + 1: 순차 (선 적재 후 라우팅)
Phase 2: Phase 1과 병행 가능 (신규 상품 비동기 처리)
Phase 3: Phase 1 완료 후 독립 실행
Phase 4: Phase 3과 병행 가능
Phase 5: Phase 3, 4 완료 후
```

---

## 8. 리스크 및 대응

| 리스크 | 영향 | 대응 |
|--------|------|------|
| 선 적재 중 세토프에서 신규 상품 추가 | 매핑 누락 | 선 적재 후 delta sync (차이분 보정) |
| 배치 전환 중 외부 OMS 동시 호출 | 동시성 이슈 | 낙관적 락 또는 상품별 동기화 |
| 세토프 스키마 변경 | 데이터 불일치 | 세토프 어드민 닫는 일정과 동기화 |
| 옵션 구조 불일치로 SKU 매칭 실패 | Product 매핑 오류 | 수동 매핑 어드민 도구 제공 |
| auto_increment 버퍼 부족 | PK 충돌 | 충분한 여유 확보 (세토프 max * 2 이상) |

---

## 9. 모니터링 지표

| 지표 | 확인 방법 | 정상 기준 |
|------|----------|----------|
| 전환율 | CONVERTED / 전체 LEGACY_IMPORTED | 점진적 증가 → 100% |
| 레거시 fallback 비율 | LEGACY_IMPORTED 라우팅 횟수 / 전체 | 점진적 감소 → 0% |
| 외부 OMS 에러율 | 4xx/5xx 응답 비율 | < 0.1% |
| 매핑 실패 건수 | PENDING_MAPPING 누적 | 감소 추세 |
| 변환 실패 건수 | CONVERT_FAILED 누적 | 0에 수렴 |
| API 응답 시간 | p95 latency | Phase 2 이후 개선 |

---

## 10. 롤백 전략

### Phase별 롤백

```
Phase 0 (선 적재):
  → inbound_products, inbound_product_items에서 LEGACY_IMPORTED 삭제
  → auto_increment 원복

Phase 1 (라우팅):
  → 라우팅 로직 제거, 기존 세토프 직접 처리로 원복
  → 게이트웨이 라우팅을 세토프 어드민으로 되돌리면 즉시 복구

Phase 2 (비동기):
  → 비동기 처리 비활성화, 동기 처리로 원복

Phase 3 (배치 전환):
  → 배치 중지, CONVERTED된 건은 유지 (롤백 불필요)

Phase 4 (전송 파이프라인):
  → 세토프 전송 비활성화

Phase 5 (의존 제거):
  → 이 단계는 Phase 3, 4 안정화 후에만 진행하므로 롤백 상황이 발생하면 안 됨
```
