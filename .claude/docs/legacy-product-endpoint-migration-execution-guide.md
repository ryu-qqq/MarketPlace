# 레거시 상품 엔드포인트 전환 실행 가이드

> 작성일: 2026-02-24
> 최종 수정: 2026-02-25
> 상태: In Progress
> 상위 문서: [product-integration-migration-plan.md](./product-integration-migration-plan.md)
> 대상: `LegacyProductCommandController` 기반 상품 엔드포인트 11개

---

## 1. 목적

레거시 상품 엔드포인트를 **무중단으로** MarketPlace 내부 상품 모델로 전환한다.

- 외부 OMS(사방넷/셀릭)가 사용하는 PK는 변경하지 않는다
- 상태(CONVERTED/LEGACY_IMPORTED)에 따라 처리 경로를 분기한다
- CONVERTED 경로에서 상품 수정 시 OutboundSyncOutbox를 발행하여 외부 채널 동기화
- LEGACY_IMPORTED 경로에서는 persistence-mysql-legacy 모듈로 세토프 DB 직접 처리 (폴백)
- 전환 중에도 API 계약(요청/응답 shape)은 유지한다

---

## 2. 핵심 원칙

1. **Strangler Fig 패턴 적용**
   - Legacy endpoint는 유지, 내부 처리 경로만 상태 기반으로 점진 전환

2. **외부 식별자 불변**
   - 외부 OMS가 받은 `productGroupId`/`productId`는 이후에도 동일하게 사용 가능해야 한다

3. **내부 PK 비노출**
   - 외부 응답에서 MarketPlace internal PK를 직접 반환하지 않는다

4. **레거시 전용 매핑 전략 (2026-02-25 결정)**
   - 레거시 경로는 `LegacyConversionOutbox`(그룹 레벨) + `legacy_product_id_mapping`(SKU 레벨)을 매핑 기준으로 사용
   - `InboundProduct`는 크롤링 전용이므로 레거시 라우팅에 사용하지 않는다

5. **수정 시 채널 동기화**
   - CONVERTED 경로에서 상품 수정 시 OutboundSyncOutbox 이벤트를 발행한다
   - 세토프 DB 동기화 + 기타 연결된 세일즈채널 동기화를 보장한다

---

## 3. 현재 갭 요약

### 3.1 구현 완료

- `InboundProduct` 기반 productGroupId 매핑
- 레거시 상품 엔드포인트의 command 분리 구조 (10개+ UseCase/Service/Coordinator)
- **`InboundProductItem` 전체 스택 (2026-02-24 완료)**
  - Domain: `InboundProductItem` aggregate + `InboundProductItemId` ID VO + `InboundProductItemStatus` enum
  - Persistence: `V82__create_inbound_product_items_table.sql` + JPA Entity + Mapper + JpaRepo + QueryDslRepo + ConditionBuilder
  - Adapter: CommandAdapter + QueryAdapter (CQRS 분리)
  - Application: CommandPort + QueryPort + CommandManager + ReadManager
- **`InboundProductStatus.LEGACY_IMPORTED` 추가 (2026-02-24 완료)**
  - `isLegacyImported()`, `canRouteToInternal()`, `requiresLegacyFallback()` 메서드 추가
- **`InboundProductErrorCode` 확장 (2026-02-24 완료)**
  - `IBP-006` (아이템 미발견), `IBP-007` (미매핑 외부 ID) 추가
- **productId 양방향 변환 로직 (2026-02-24 완료)**
  - `LegacyProductItemIdResolver`: external↔internal 변환
  - `LegacyProductItemMappingUseCase/Service`: 응답 역매핑 서비스
  - `LegacyOptionUpdateCoordinator`: productId 변환 적용
  - `LegacyStockUpdateCoordinator`: productId 변환 적용
  - `LegacyProductCommandApiMapper`: 역매핑 포함 응답 변환
  - `LegacyProductCommandController`: 역매핑 적용 (option/stock/out-stock)

### 3.2 구현 완료 — 추가 (2026-02-25)

- **persistence-mysql-legacy 모듈**: Entity/Repository/CommandAdapter/CommandEntityMapper 전체 스택
  - Product: 8개 CommandAdapter (ProductGroup, Product, Stock, Option, Notice, Delivery, Image, Description)
  - Option: 2개 CommandAdapter (OptionGroup, OptionDetail)
  - Mapper: LegacyProductCommandEntityMapper, LegacyOptionCommandEntityMapper
- **Legacy 상품 등록 파이프라인**:
  - Bundle/Factory/Coordinator/Service/UseCase 전체 흐름
  - Manager 패턴 (LegacyProductGroupPersistManager, LegacyProductPersistManager, LegacyOptionPersistManager)
  - LegacyOptionResolver: 배치 내 옵션 중복 제거
  - LegacyProductRegistrationService → luxurydb INSERT → setof PK 반환

### 3.3 미구현

| 항목 | 우선순위 | 의존 |
|------|---------|------|
| SetofProductGroupCommandPort 와이어링 (8개 업데이트 → application 연결) | P0 | persistence-mysql-legacy |
| `LegacyConversionOutbox` 도메인 + Persistence | P0 | - |
| `legacy_product_id_mapping` 도메인 + Persistence | P0 | - |
| `LegacyConversionScheduler` (변환 → legacy_product_id_mapping 생성) | P0 | LegacyConversionOutbox |
| `LegacyCommandResolver` 상태 분기 (LegacyConversionOutbox 기반) | P0 | LegacyConversionOutbox |
| productId 변환 → legacy_product_id_mapping 기반 전환 | P1 | legacy_product_id_mapping |
| 수정 시 OutboundSyncOutbox 이벤트 발행 | P1 | Phase 2 OutboundSync 처리기 |
| 응답 PK 정책 강제 (내부 PK 노출 방지 자동화) | P2 | - |

---

## 4. 전환 아키텍처 (2026-02-25 업데이트)

> **InboundProduct는 크롤링 전용**. 레거시 라우팅은 LegacyConversionOutbox 기반.

```
Legacy API
  → LegacyProductCommandController
    → LegacyConversionOutbox 조회 (legacy_product_group_id)
      → 상태 분기:
         ┌─ COMPLETED: MarketPlace 내부 경로
         │   → outbox.internalProductGroupId로 내부 상품 식별
         │   → legacy_product_id_mapping으로 productId external↔internal 변환
         │   → 내부 UseCase 실행 (MarketPlace DB 수정)
         │   → OutboundSyncOutbox 발행 (세토프 DB + 기타 채널 동기화)
         │   → 응답 productId/internal 값 역매핑 후 반환
         │
         └─ PENDING / 미존재: luxurydb 직접 경로
             → persistence-mysql-legacy로 luxurydb 직접 CRUD
             → 응답: 세토프 데이터 그대로 반환
```

---

## 5. 데이터 모델 (2026-02-25 업데이트)

### 5.1 레거시 매핑 테이블

> InboundProduct/Item은 크롤링 전용. 레거시 경로는 아래 전용 테이블 사용.

#### LegacyConversionOutbox (그룹 레벨 — 변환 추적 겸 매핑)

- 용도: ProductGroup 레벨 변환 추적 + legacy↔internal 매핑
- 핵심 컬럼:
  - `legacy_product_group_id` → `internal_product_group_id`
  - `status` (PENDING → PROCESSING → COMPLETED / FAILED)
- 스키마: 상위 문서 §2.2 참조

#### legacy_product_id_mapping (SKU 레벨 — 순수 매핑)

- 용도: SKU(productId) 레벨 legacy↔internal 매핑
- 핵심 컬럼:
  - `legacy_product_id` (PK)
  - `internal_product_id`
  - `legacy_product_group_id` (그룹 참조)
- 스키마: 상위 문서 §3.2 참조

### 5.2 인덱스/제약

- legacy_product_conversion_outbox:
  - `(status, created_at)` — 스케줄러 폴링용
  - `(legacy_product_group_id)` — 라우팅 조회용
- legacy_product_id_mapping:
  - PK: `(legacy_product_id)`
  - `(legacy_product_group_id)` — 그룹별 SKU 조회용
  - `(internal_product_id)` — 역매핑 조회용

---

## 6. 엔드포인트별 전환 규칙

### 6.1 POST `/product/group` (등록)

- **동기 처리** (즉시 응답):
  - `LegacyProductRegistrationCoordinator` → luxurydb INSERT
  - setof productGroupId + productIds 즉시 반환
  - `LegacyConversionOutbox` 생성 (PENDING, reference=legacy_product_group_id)
- **비동기 처리** (스케줄러):
  - `LegacyConversionScheduler` → PENDING 폴링
  - `legacy_product_group_id`로 luxurydb에서 **최신 데이터** 조회 → Bundle 변환
  - `FullProductGroupRegistrationCoordinator.register()` → MarketPlace 내부 상품 생성
  - outbox COMPLETED + `legacy_product_id_mapping` 레코드 생성 (SKU별)
  - 내부 상품 생성 완료 후 → AI 분석 → OutboundSyncOutbox 생성 (연결된 채널별)
- **Reference-based 이점**:
  - 등록 후 변환 전에 수정(PUT/PATCH)이 들어와도 luxurydb가 갱신됨
  - 변환 스케줄러가 실행 시점에 최신 데이터를 읽으므로 수정 내용이 자동 반영

### 6.2 수정 엔드포인트 공통: 변환 상태별 라우팅 (2026-02-25 결정)

레거시 수정 엔드포인트(PUT/PATCH)는 해당 상품의 **변환 상태**에 따라 처리 경로가 달라진다.

```
외부 OMS → PUT/PATCH 요청 (legacy_product_group_id 기반)
  │
  ├─ Case A: 미변환 (LegacyConversionOutbox = PENDING)
  │   → luxurydb UPDATE만 수행
  │   → LegacyConversionOutbox는 PENDING 유지 (별도 처리 불필요)
  │   → 변환 스케줄러 실행 시 luxurydb에서 최신 데이터 조회 → 수정 내용 자동 반영
  │   → 응답: 세토프 데이터 그대로 반환
  │
  └─ Case B: 변환 완료 (COMPLETED)
      → outbox.internalProductGroupId로 내부 상품 식별
      → SKU 레벨: legacy_product_id_mapping으로 productId 변환
      → MarketPlace 내부 상품 수정
      → OutboundSyncOutbox 발행 (UPDATE 타입) → luxurydb + 기타 채널 동기화
      → 응답: internal→external 역변환 후 반환 (legacy_product_id_mapping 기반)
```

**Case A가 단순한 이유**: Reference-based 아웃박스이므로 luxurydb UPDATE만 하면 된다.
변환 스케줄러가 payload가 아닌 luxurydb 최신 데이터를 읽으므로, 별도의 아웃박스 갱신이 불필요하다.

**라우팅 판별 방법**:
1. `legacy_product_group_id`로 `LegacyConversionOutbox` 조회
2. status = COMPLETED → Case B (outbox.internalProductGroupId + legacy_product_id_mapping 경유)
3. status = PENDING/PROCESSING → Case A (luxurydb 직접)
4. 아웃박스 레코드 없음 → luxurydb 직접 처리 (Phase 0 단계의 기존 상품)

### 6.3 PUT `/{id}` (전체 수정)

**CONVERTED 경로** (Case B):
- InboundProduct.internalProductGroupId로 내부 상품 수정
- payload 내 `productOptions[].productId`를 external→internal 변환
- 수정 완료 후 OutboundSyncOutbox 발행 (UPDATE 타입)
- 응답: internal→external 역변환

**미변환 경로** (Case A):
- persistence-mysql-legacy로 luxurydb 직접 수정
- 변환 스케줄러가 최신 데이터 기반 변환
- 응답: 세토프 데이터 그대로 반환

**LEGACY_IMPORTED 경로** (벌크 적재 상품):
- persistence-mysql-legacy로 세토프 DB 직접 수정
- 응답: 세토프 데이터 그대로 반환

### 6.4 PUT `/{id}/option`

> 라우팅 기본 정책은 §6.2 참조. 아래는 각 경로의 상세 처리.

**CONVERTED 경로** (Case B):
- 요청 `productId` external→internal 변환 필수
- option group/value는 이름 기반 해석 + ID 힌트(있을 경우)
- 수정 완료 후 OutboundSyncOutbox 발행
- 응답 productId 역매핑

**미변환 경로** (Case A):
- luxurydb 직접 수정 → 변환 시 최신 옵션 반영

### 6.5 PATCH `/{id}/stock`

**CONVERTED 경로** (Case B):
- 요청 `productId` external→internal 변환
- 재고 업데이트 후 OutboundSyncOutbox 발행
- 응답 productId 역매핑

**미변환 경로** (Case A):
- luxurydb 직접 수정 → 변환 시 최신 재고 반영

### 6.6 PATCH `/{id}/out-stock`

**CONVERTED 경로** (Case B):
- group 상태 변경은 내부 경로
- OutboundSyncOutbox 발행
- 응답 productId 역매핑

**미변환 경로** (Case A):
- luxurydb 직접 수정

### 6.7 notice/images/description/price/display-yn

**CONVERTED 경로** (Case B):
- productGroupId 매핑만 정확하면 내부 경로 전환 가능
- 수정 완료 후 OutboundSyncOutbox 발행
- 응답에 SKU 목록 포함 시 역매핑 정책 준수

**미변환 경로** (Case A):
- luxurydb 직접 수정 → 변환 시 최신 데이터 반영

---

## 7. 상태 기반 라우팅 정책

### 7.1 기준 상태 (LegacyConversionOutbox 기반)

| 상태 | 의미 | 처리 |
|------|------|------|
| `COMPLETED` | 내부 상품 변환 완료, MarketPlace DB에 상품 존재 | MarketPlace 내부 경로 강제 |
| `PENDING` / 미존재 | 미변환, luxurydb에만 상품 존재 | luxurydb 직접 CRUD |

### 7.2 정책

- `COMPLETED`:
  - MarketPlace 내부 경로 강제
  - outbox.internalProductGroupId + legacy_product_id_mapping 기반 매핑
  - 매핑 실패 시 명시적 오류 반환 (조용한 fallback 금지)
  - 수정 성공 시 OutboundSyncOutbox 발행

- `PENDING / 미존재`:
  - persistence-mysql-legacy로 luxurydb 직접 처리
  - Reference-based이므로 변환 스케줄러가 최신 데이터 자동 반영
  - 전환률 지표 모니터링 후 단계적 제거

---

## 8. 변환/해석 규칙

### 8.1 Product ID

- 입력: external productId (= legacy_product_id)
- 처리 전: `legacy_product_id_mapping`으로 internal productId 해석
- 처리 후 응답: internal→legacy 역해석 (legacy_product_id_mapping 기반)

### 8.2 Option Group / Option Value

- 기본 판단 기준: 이름(`optionGroupName`, `optionValueName`)
- 보조 힌트: 외부 optionGroupId/optionDetailId (존재 시)
- 충돌 시 정책:
  - 자동 병합 금지
  - 명시적 예외 + 에러코드 반환

---

## 9. 실패 정책

1. **external productId 미매핑**
   - `COMPLETED` 상태: legacy_product_id_mapping에 없으면 4xx(도메인 오류) 반환
   - 미변환 상태: luxurydb에서 직접 처리 (productId 변환 불필요)

2. **부분 매핑 성공(일부 SKU만 해석)**
   - 전체 실패(트랜잭션 롤백)

3. **응답 역매핑 실패**
   - 내부 PK 노출 대신 명시적 서버 오류 처리

4. **조용한 fallback 금지**
   - outbox가 `COMPLETED`인데 fallback(luxurydb 직접) 경로를 타지 않도록 방어

5. **OutboundSyncOutbox 발행 실패**
   - 상품 수정 자체는 성공으로 처리
   - 아웃박스 발행 실패는 별도 모니터링으로 감지
   - 수동 재발행 또는 보정 스케줄러로 대응

---

## 10. 롤아웃 계획 (작업 순서)

### Step 1: Legacy CRUD 프록시 (3-Phase 레거시 Phase 1)

| # | 작업 | 설명 |
|---|------|------|
| 1-1 | persistence-mysql-legacy 모듈 생성 | ✅ 완료. luxurydb DataSource + Entity + Repository |
| 1-2 | SetofProductGroupCommandPort 와이어링 | 8개 업데이트 메서드를 application 레이어에 연결 |
| 1-3 | LegacyProductCommandController → luxurydb 직접 CRUD | 모든 레거시 엔드포인트가 luxurydb로 CRUD 수행 |
| 1-4 | Auto-increment 조정 SQL | PK 충돌 방지 |

### Step 2: 변환 파이프라인 + 상태 기반 라우팅 (3-Phase 레거시 Phase 2/3)

| # | 작업 | 설명 |
|---|------|------|
| 2-1 | LegacyConversionOutbox 도메인 + Persistence | 변환 추적 + 그룹 매핑 인프라 |
| 2-2 | legacy_product_id_mapping 도메인 + Persistence | SKU 레벨 매핑 인프라 |
| 2-3 | LegacyConversionScheduler 구현 | PENDING 폴링 → 변환 → COMPLETED + mapping 생성 |
| 2-4 | LegacyCommandResolver 상태 분기 | outbox COMPLETED → 내부 경로, 미변환 → luxurydb 직접 |
| 2-5 | productId 변환 로직 전환 | InboundProductItem → legacy_product_id_mapping 기반 |
| 2-6 | 엔드포인트별 라우팅 적용 | option → stock → out-stock → 나머지 |

### Step 3: 채널 동기화 연동

| # | 작업 | 설명 |
|---|------|------|
| 3-1 | 수정 시 OutboundSyncOutbox 발행 | COMPLETED 경로 수정 완료 후 아웃박스 이벤트 생성 |
| 3-2 | 세토프 채널 OutboundSync 처리기 | 세토프 DB 동기화 (persistence-mysql-legacy 활용) |
| 3-3 | 네이버 채널 OutboundSync 처리기 | 네이버 커머스 API 연동 |
| 3-4 | 통합 검증 | 수정 → 아웃박스 → 채널 동기화 전체 흐름 확인 |

### Step 4: 벌크 전환 + 폴백 제거

| # | 작업 | 설명 |
|---|------|------|
| 4-1 | 벌크 아웃박스 생성 | 기존 세토프 상품 → LegacyConversionOutbox PENDING 벌크 INSERT |
| 4-2 | 배치 전환 | LegacyConversionScheduler가 순차 변환 처리 |
| 4-3 | 전환율 모니터링 | outbox PENDING 0건 달성 확인 |
| 4-4 | luxurydb 직접 CRUD 경로 제거 | 폴백 분기문 정리 |
| 4-5 | persistence-mysql-legacy 모듈 제거 | 세토프 DB 직접 연결 종료 |
| 4-6 | Legacy 매핑 테이블 제거 | outbox + legacy_product_id_mapping DROP |

---

## 11. 구현 파일 맵

### InboundProduct (크롤링 전용 — 구현 완료)

```
Domain (inboundproduct 패키지):
  aggregate/InboundProduct.java           ← 크롤링 수신/변환 추적 (레거시에는 사용 안 함)
  vo/InboundProductStatus.java            ← PENDING_CONVERSION 추가 예정
```

> **참고**: InboundProductItem은 크롤링 경로에서만 활용.
> 레거시 SKU 매핑은 `legacy_product_id_mapping`이 담당.

### Legacy 매핑 인프라 (미구현 — P0)

```
Domain (legacyconversion 패키지 — 신규):
  aggregate/LegacyConversionOutbox.java       ← 그룹 매핑 + 변환 추적
  vo/LegacyConversionStatus.java              ← PENDING → COMPLETED / FAILED
  vo/LegacyProductIdMapping.java              ← SKU 매핑 VO

Persistence (legacyconversion 패키지 — 신규):
  legacy_product_conversion_outbox 테이블
  legacy_product_id_mapping 테이블
  각각 Entity + Repository + Adapter

Application (legacyconversion 패키지 — 신규):
  LegacyConversionScheduler.java              ← PENDING 폴링 → 변환 → mapping 생성
```

### Legacy Product 라우팅 (구현 완료, outbox 기반 분기 전환 필요)

```
Application (legacyproduct 패키지):
  internal/LegacyProductIdResolver.java         ← setofPK → internal (outbox 기반으로 전환 필요)
  internal/LegacyProductItemIdResolver.java      ← productId 변환 (legacy_product_id_mapping 기반으로 전환 필요)
  internal/LegacyCommandResolver.java            ← outbox 기반 상태 분기 완성 필요
  internal/LegacyOptionUpdateCoordinator.java    ← productId 변환 적용 완료
  internal/LegacyStockUpdateCoordinator.java     ← productId 변환 적용 완료
  internal/LegacyOutOfStockCoordinator.java      ← productId 변환 불필요 (응답만)
  internal/LegacyProductUpdateCoordinator.java   ← 추상 클래스

Adapter-In (rest-api):
  controller/LegacyProductCommandController.java  ← 역매핑 적용 완료
  mapper/LegacyProductCommandApiMapper.java        ← 역매핑 포함 응답 변환
  facade/LegacyProductCommandFacade.java
```

### OutboundSync (도메인/Persistence 완료, 처리기 미구현)

```
Domain (outboundsync 패키지):
  aggregate/OutboundSyncOutbox.java        ← PENDING → PROCESSING → COMPLETED/FAILED
  id/OutboundSyncOutboxId.java
  vo/OutboundSyncStatus.java
  vo/SyncType.java

Domain (outboundproduct 패키지):
  aggregate/OutboundProduct.java           ← PENDING_REGISTRATION → REGISTERED
  id/OutboundProductId.java
  vo/OutboundProductStatus.java

Persistence: 양쪽 모두 전체 스택 구현됨

Application: Outbox 생성 흐름 구현됨 (PostAnalysisProductGroupCoordinator)
  → 처리 스케줄러 미구현
```

### 미구현 모듈

```
persistence-mysql-legacy 추가 와이어링:
  ✅ 모듈 자체 구현 완료 (Entity/Repo/Adapter/Mapper)
  ✅ SetofProductGroupCommandPort 8개 업데이트 메서드 (adapter 레벨)
  ❌ Application Service에서 CommandPort 호출 연결 (와이어링)
  ❌ SetofProductSyncAdapter (SalesChannelProductSyncPort 구현)

Legacy 변환 인프라 (전 레이어 신규):
  domain/legacyconversion/
    LegacyConversionOutbox.java + LegacyConversionStatus.java
    LegacyProductIdMapping.java
  persistence-mysql/legacyconversion/
    LegacyConversionOutboxJpaEntity + Repository + Adapter
    LegacyProductIdMappingJpaEntity + Repository + Adapter
  application/legacyconversion/
    LegacyConversionScheduler.java

adapter-out/client/naver-commerce-client/  ← 상품 등록/수정 확장 (신규)
  adapter/NaverCommerceProductClientAdapter.java  ← SalesChannelProductSyncPort 구현
  dto/NaverCommerceProductRequest.java
  dto/NaverCommerceProductResponse.java
  mapper/NaverCommerceProductMapper.java
```

---

## 12. 관측/검증 항목

- endpoint별 매핑 성공률
- unmapped external productId 발생 건수
- `LEGACY_IMPORTED` 폴백 호출 비율
- 내부 PK 노출 건수 (목표: 0)
- option/stock 업데이트 정합성 샘플링 결과
- OutboundSyncOutbox 처리 성공률
- 세토프 DB 동기화 지연 시간
- 네이버 API 호출 성공률

---

## 13. 완료 기준

아래를 모두 만족하면 레거시 상품 엔드포인트 전환 완료로 간주한다.

- [ ] 11개 엔드포인트가 LegacyConversionOutbox 기반 상태 라우팅(COMPLETED/미변환)으로 동작
- [ ] COMPLETED 경로에서 legacy_product_id_mapping 기반 productId 양방향 변환 정상 동작
- [ ] 미변환 경로에서 luxurydb 직접 CRUD가 에러 없이 동작
- [ ] COMPLETED 경로 수정 시 OutboundSyncOutbox 발행 → 채널 동기화 성공
- [ ] 외부 응답 PK가 항상 legacy(세토프) 기준으로 유지
- [ ] `legacy_product_id_mapping` 기반 양방향 매핑이 운영 데이터에서 안정화
- [ ] 외부 OMS(사방넷/셀릭) 에러율 < 0.1%
- [ ] 회귀 테스트 및 운영 지표 이상 없음

---

## 14. 관련 문서

- [상품 통합 마이그레이션 계획서](./product-integration-migration-plan.md) ← 마스터 문서
- [게이트웨이 라우팅 마이그레이션 명세서](./gateway-routing-migration-spec.md)
- [레거시 엔드포인트 트래픽 분석](./legacy-endpoint-traffic-analysis.md)
- [InboundProduct 리팩토링 설계서](./inbound-product-refactoring-design.md)
