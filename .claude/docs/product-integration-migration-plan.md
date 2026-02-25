# 상품 통합 마이그레이션 계획서

> 작성일: 2026-02-23
> 최종 수정: 2026-02-25
> 상태: In Progress
> 관련 브랜치: feature/product-integration

---

## 1. 배경 및 목표

### 1.1 현재 상황

- **세토프(Setof)**: 기존 자사몰 + 커머스 플랫폼. 어드민 서버가 OMS 역할과 세일즈채널 게이트웨이 역할을 겸함
- **MarketPlace OMS**: 세토프 어드민을 대체하는 신규 OMS. **모든 세일즈채널의 허브** 역할
- **외부 OMS(사방넷, 셀릭)**: 세토프 어드민 API를 호출하여 상품 등록/수정/재고 관리 수행 중
- **인프라**: 세토프 DB(luxurydb)와 MarketPlace DB가 **동일 RDS** 내 별도 스키마로 운영

### 1.2 핵심 문제

| 문제 | 상세 |
|------|------|
| OMS + 채널 겸임 | 세토프 어드민이 OMS 기능과 세일즈채널 게이트웨이를 겸하고 있음 |
| PK 충돌 | MarketPlace product_groups/products 테이블의 auto_increment가 세토프 PK 범위와 겹칠 수 있음 |
| PK 변경 불가 | 외부 OMS(사방넷, 셀릭)는 기존 세토프 PK를 변경할 수 없음 |
| 스키마 차이 | MarketPlace 내부 상품 스키마 ≠ 세토프 상품 스키마 |
| 이중 경로 | 기존 세토프 상품과 신규 MarketPlace 상품이 동시 운영되어야 함 |
| 세토프 웹서버 의존 | 세토프 웹서버(고객용)가 세토프 DB를 직접 읽고 있어 동기화 필수 |

### 1.3 목표 아키텍처

```
                  ┌───────────────────────────┐
                  │    MarketPlace (OMS)       │
                  │  상품/주문/정산 통합 관리     │
                  │  MarketPlace DB = SOT      │
                  └──┬────┬────┬────┬────┬───┘
                     │    │    │    │    │
                     │    │    │    │    │  세일즈채널 연동
                     ▼    ▼    ▼    ▼    ▼
                  ┌────┐┌────┐┌────┐┌────┐┌─────┐
                  │세토프││네이버││쿠팡 ││11번가││ ... │
                  │자사몰││스토어││마켓 ││     ││     │
                  └────┘└────┘└────┘└────┘└─────┘
```

**MarketPlace가 모든 세일즈채널의 허브**이며, 세토프는 그중 하나의 채널일 뿐.

### 1.4 이번 페이즈 목표

1. **상품 등록/변환 파이프라인 완성** — InboundProduct → 내부 상품 → OutboundProduct
2. **세일즈채널 아웃박스 처리기 구현** — OutboundSyncOutbox PENDING → PROCESSING → COMPLETED
3. **외부 세일즈채널 연동 성공** — 세토프(자사몰) + 네이버 스마트스토어
4. **레거시 엔드포인트 무중단 전환** — 외부 OMS(사방넷/셀릭) 에러 없이 MarketPlace로 라우팅

---

## 2. 전략: Strangler Fig + InboundProduct + 분리 아웃박스 + OutboundSync

### 2.1 핵심 개념

```
[Inbound - 크롤링] 외부 → InboundProduct(수신+매핑) → PENDING_CONVERSION → 내부 상품
[Inbound - 레거시] 외부 OMS → luxurydb INSERT → LegacyConversionOutbox → 내부 상품
[Outbound] 내부 상품 변경 → OutboundSyncOutbox → 세일즈채널 어댑터 → 외부 채널 반영
```

> **⚠️ InboundProduct 역할 한정 (2026-02-25 결정)**
>
> `InboundProduct`는 **크롤링 경로 전용**이다. 레거시(세토프) 상품에는 사용하지 않는다.
> - 크롤링: InboundProduct로 수신 → 매핑 → 변환 추적
> - 레거시: LegacyConversionOutbox + `legacy_product_id_mapping`으로 변환/매핑 추적
> - 세토프 마이그레이션 완료 시 Legacy 매핑 테이블만 삭제, InboundProduct는 영구 운영

### 2.2 내부 상품 변환: 분리 아웃박스 전략 (2026-02-25 결정)

#### 의사결정 배경

내부 MarketPlace 상품으로의 변환이 필요한 경로가 두 가지:

1. **InboundProduct 경로** (크롤링): InboundProduct 수신 → 매핑 → **내부 상품 변환**
2. **Legacy 경로** (세토프 OMS): luxurydb INSERT → **내부 상품 변환**

두 경로 모두 최종적으로 `FullProductGroupRegistrationCoordinator.register()`를 호출하여
MarketPlace 내부 상품을 생성한다. 이 변환 단계를 비동기 아웃박스로 분리하되,
**공유 아웃박스가 아닌 분리 아웃박스**를 채택한다.

#### 공유 vs 분리 비교

| 기준 | 공유 아웃박스 | 분리 아웃박스 (채택) |
|------|------------|-------------------|
| 생명주기 | 영구+임시 혼재 | 영구(InboundProduct) / 임시(Legacy) 분리 |
| 페이로드 | 두 가지 구조 혼재 | 각 경로별 최적 구조 |
| 제거 용이성 | Legacy 제거 시 공유 테이블 정리 필요 | Legacy 테이블+코드만 삭제 |
| 구현 복잡도 | Strategy 패턴 필요 | 각각 단순 구현 |
| 모니터링 | 단일 | 2개 (경로별 독립) |

#### 분리 아웃박스 채택 이유

1. **생명주기가 다름**: InboundProduct 변환은 영구 기능(크롤링 계속 운영), Legacy 변환은 임시 기능(세토프 마이그레이션 완료 시 제거)
2. **페이로드가 근본적으로 다름**: InboundProduct은 rawPayloadJson + 매핑 정보 기반, Legacy는 구조화된 Bundle 기반
3. **InboundProduct 상태 머신 확장이 자연스러움**: `PENDING_CONVERSION` 상태 추가로 별도 아웃박스 테이블 불필요
4. **Legacy 제거 용이**: 세토프 마이그레이션 완료 시 `legacy_product_conversion_outbox` 테이블과 관련 코드만 삭제

#### 분리 아웃박스 아키텍처

```
[InboundProduct 경로 — 영구]
  InboundProduct 수신 → 매핑(MAPPED)
    → markPendingConversion() → InboundProduct 상태 = PENDING_CONVERSION
    → InboundConversionScheduler (폴링)
      → PENDING_CONVERSION 조회
      → ConversionCoordinator.convert()
      → FullProductGroupRegistrationCoordinator.register()
      → markConverted(productGroupId)
      → 실패 시: markConvertFailed() + retry

[Legacy 경로 — 임시 (세토프 마이그레이션 완료 시 제거)]
  LegacyProductRegistrationCoordinator.register(bundle)
    → luxurydb INSERT (기존, 동기)
    → LegacyConversionOutbox 생성 (PENDING, reference=legacy_product_group_id)
    → return setof PKs (기존, 즉시 응답)
  LegacyConversionScheduler (폴링)
    → PENDING 조회
    → legacy_product_group_id로 luxurydb에서 최신 데이터 조회
    → ProductGroupRegistrationBundle 변환
    → FullProductGroupRegistrationCoordinator.register()
    → 성공: COMPLETED + internalProductGroupId 기록
    → 실패: retry 또는 FAILED
```

#### InboundProduct 상태 머신 변경

```
변경 전: RECEIVED → PENDING_MAPPING → MAPPED → CONVERTED
변경 후: RECEIVED → PENDING_MAPPING → MAPPED → PENDING_CONVERSION → CONVERTED
                                                      ↑ 비동기 변환 대기
```

- `PENDING_CONVERSION`: 매핑 완료, 내부 상품 변환 대기 중
- InboundProduct 테이블 자체가 아웃박스 역할 (별도 테이블 불필요)
- `InboundConversionScheduler`가 `PENDING_CONVERSION` 상태를 폴링하여 처리

#### Legacy 변환 아웃박스 스키마

```sql
CREATE TABLE legacy_product_conversion_outbox (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    legacy_product_group_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    internal_product_group_id BIGINT,
    retry_count INT NOT NULL DEFAULT 0,
    max_retry INT NOT NULL DEFAULT 3,
    last_error_message TEXT,
    created_at TIMESTAMP(6) NOT NULL,
    updated_at TIMESTAMP(6) NOT NULL,
    INDEX idx_status_created (status, created_at),
    INDEX idx_legacy_pg_id (legacy_product_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

- **Reference-based 설계**: payload를 저장하지 않고 `legacy_product_group_id`만 참조
- 변환 시점에 luxurydb에서 최신 데이터를 조회하여 Bundle 생성
- `status`: PENDING → PROCESSING → COMPLETED / FAILED
- `internal_product_group_id`: 변환 성공 시 생성된 MarketPlace 내부 PK
- 세토프 마이그레이션 완료 시 테이블 자체를 DROP

#### Reference-based 아웃박스 채택 이유

| 기준 | Payload 직렬화 방식 | Reference 방식 (채택) |
|------|-------------------|---------------------|
| 등록 후 수정 | 아웃박스 payload 갱신 필요 | luxurydb에서 최신 데이터 조회 (자동 반영) |
| 데이터 정합성 | 등록 시점 스냅샷 (stale 가능) | 변환 시점 최신 데이터 보장 |
| 아웃박스 크기 | payload_json LONGTEXT (수 KB~수십 KB) | reference ID만 (8 bytes) |
| 구현 복잡도 | 직렬화/역직렬화 + 스키마 변경 시 호환성 관리 | 기존 luxurydb 조회 로직 재사용 |
| 업데이트 핸들링 | 미변환 상태에서 수정 시 아웃박스 payload도 갱신 필요 | luxurydb에 반영된 최신 데이터를 자동으로 읽음 |

**핵심 이점**: 등록 후 변환 전에 수정 요청이 들어와도 luxurydb가 갱신되므로,
변환 스케줄러가 실행될 때 자연스럽게 최신 데이터를 기반으로 내부 상품을 생성한다.

#### 재처리 정책

| 구분 | InboundProduct | Legacy |
|------|---------------|--------|
| 상태 관리 | InboundProduct 도메인 상태 | 아웃박스 테이블 status |
| 재처리 트리거 | `PENDING_CONVERSION` 폴링 | `PENDING` 폴링 |
| 실패 처리 | `markConvertFailed()` → CONVERT_FAILED | retry_count++ → max 초과 시 FAILED |
| 수동 재처리 | 상태를 PENDING_CONVERSION으로 재설정 | 상태를 PENDING으로 재설정 |
| 모니터링 | InboundProduct status별 카운트 | 아웃박스 status별 카운트 |

### 2.3 업데이트 플로우 (2026-02-25 결정)

등록 후 업데이트 요청이 들어올 때, **변환 상태**에 따라 처리 경로가 달라진다.

#### Legacy 업데이트 라우팅

```
외부 OMS → PUT/PATCH 요청 (legacy_product_group_id 기반)
  │
  ├─ Case A: 미변환 (LegacyConversionOutbox = PENDING)
  │   → luxurydb UPDATE만 수행
  │   → 아웃박스는 그대로 PENDING 유지
  │   → LegacyConversionScheduler가 실행될 때 luxurydb에서 최신 데이터 조회
  │   → 변환 결과에 수정 내용이 자연스럽게 반영됨
  │
  └─ Case B: 변환 완료 (COMPLETED, internal_product_group_id 존재)
      → outbox.internalProductGroupId로 내부 상품 식별
      → SKU 레벨: legacy_product_id_mapping으로 productId 변환
      → 내부 상품 수정 (MarketPlace DB)
      → OutboundSyncOutbox 발행 (UPDATE 타입)
      → OutboundSyncScheduler → luxurydb UPDATE + 기타 채널 동기화
```

**Case A의 핵심**: Reference-based 아웃박스이므로 별도 처리가 불필요하다.
luxurydb를 직접 UPDATE하면 변환 스케줄러가 실행 시점에 최신 데이터를 읽는다.

**Case B의 핵심**: CONVERTED 이후에는 MarketPlace 내부 상품이 SOT(Source of Truth).
내부 수정 → OutboundSync로 luxurydb에 역동기화한다.

#### InboundProduct 업데이트 라우팅

```
크롤링 서버 → PUT/PATCH 요청 (inbound_product_id 기반)
  │
  ├─ Case A: 변환 대기 (PENDING_CONVERSION)
  │   → InboundProduct 데이터만 갱신
  │   → InboundConversionScheduler가 실행될 때 갱신된 InboundProduct 데이터 기반 변환
  │   → 수정 내용이 자연스럽게 반영됨
  │
  └─ Case B: 변환 완료 (CONVERTED)
      → InboundProduct 데이터 갱신 + 재매핑 (필요 시)
      → 내부 상품 동기 수정 (MarketPlace DB)
      → OutboundSyncOutbox 발행 (UPDATE 타입)
      → 채널 동기화
```

#### 업데이트 상태 전이 요약

| 상태 | Legacy 수정 시 | InboundProduct 수정 시 |
|------|--------------|---------------------|
| **미변환** (PENDING / PENDING_CONVERSION) | luxurydb UPDATE만 | InboundProduct UPDATE만 |
| **변환 완료** (COMPLETED / CONVERTED) | 내부 수정 → OutboundSync → luxurydb | InboundProduct + 내부 수정 → OutboundSync |

**공통 원칙**: 미변환 상태에서는 소스 데이터(luxurydb / InboundProduct)만 수정하고,
변환 스케줄러가 최신 데이터를 기반으로 내부 상품을 생성하게 한다.
변환 완료 후에는 MarketPlace 내부 상품이 SOT이므로 내부 수정 후 외부 동기화한다.

### 2.4 전환기 아키텍처

세토프 웹서버가 아직 세토프 DB를 직접 읽으므로, 전환기에는 **듀얼 경로** 필요.

> 참고: 레거시 엔드포인트의 업데이트 라우팅 상세는 §2.3 참조.

```
[전환기]
 MarketPlace ──CRUD──▶ MarketPlace DB (SOT)
      │
      ├──OutboundSync──▶ 세토프 DB (luxurydb)  ← 세토프 웹서버가 직접 읽음
      └──OutboundSync──▶ 네이버 API             ← HTTP 연동

[최종]
 MarketPlace ──CRUD──▶ MarketPlace DB (SOT)
      │
      ├──HTTP API──▶ 세토프 상품서버  ← 세토프도 API 수신으로 전환
      └──HTTP API──▶ 네이버 API
```

### 2.5 전환기 세토프 동기화 방식

| 구분 | 처리 방향 | 동기화 방법 |
|------|----------|-----------|
| **등록** | MarketPlace DB 먼저 → 세토프 DB | OutboundSyncOutbox → 세토프 DB INSERT |
| **수정** | MarketPlace DB 먼저 → 세토프 DB | OutboundSyncOutbox → 세토프 DB UPDATE |
| **조회 (레거시)** | MarketPlace DB 우선 → 세토프 DB 폴백 | CONVERTED: MarketPlace / LEGACY_IMPORTED: 세토프 DB |
| **조회 (세토프 웹)** | 세토프 DB 직접 | 아웃박스가 세토프 DB를 최신으로 유지 |

---

## 3. PK 충돌 방지

### 3.1 Auto-increment 버퍼링

```sql
-- 배포 전 세토프 실제 max 값 확인 필수
SELECT MAX(id) FROM luxurydb.product_groups;
SELECT MAX(id) FROM luxurydb.products;

-- MarketPlace 테이블 auto_increment 조정
ALTER TABLE marketplace.product_groups AUTO_INCREMENT = {세토프_max + 50000};
ALTER TABLE marketplace.products AUTO_INCREMENT = {세토프_max + 100000};
```

### 3.2 매핑 전략 (경로별 분리, 2026-02-25 결정)

> InboundProduct는 **크롤링 전용**, 레거시는 **전용 매핑 테이블** 사용.

#### 크롤링 경로 — InboundProduct

| 테이블 | 역할 | 핵심 컬럼 |
|--------|------|----------|
| `inbound_products` | 크롤링 상품의 ProductGroup 레벨 수신/변환 추적 | `external_product_code` ↔ `internal_product_group_id` |

- InboundProduct 상태 머신(`RECEIVED → MAPPED → PENDING_CONVERSION → CONVERTED`)으로 변환 추적
- 크롤링 채널별 `inbound_source_id`로 소스 구분

#### 레거시 경로 — LegacyConversionOutbox + legacy_product_id_mapping

| 테이블 | 역할 | 핵심 컬럼 |
|--------|------|----------|
| `legacy_product_conversion_outbox` | ProductGroup 레벨 매핑 (변환 추적 겸용) | `legacy_product_group_id` ↔ `internal_product_group_id` |
| `legacy_product_id_mapping` | Product(SKU) 레벨 매핑 | `legacy_product_id` ↔ `internal_product_id` |

- **LegacyConversionOutbox**가 그룹 레벨 변환 추적 + 매핑을 겸함 (별도 그룹 매핑 테이블 불필요)
- **legacy_product_id_mapping**이 SKU 레벨 매핑 전담
- LegacyConversionScheduler가 변환 성공 시 두 테이블에 매핑 기록
- 세토프 마이그레이션 완료 시 두 테이블 + 관련 코드 전체 삭제

#### legacy_product_id_mapping 스키마

```sql
CREATE TABLE legacy_product_id_mapping (
    legacy_product_id BIGINT NOT NULL PRIMARY KEY,
    internal_product_id BIGINT NOT NULL,
    legacy_product_group_id BIGINT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    INDEX idx_legacy_pg_id (legacy_product_group_id),
    INDEX idx_internal_product_id (internal_product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 3.3 응답 PK 규칙

**외부 OMS가 한번 받은 PK는 절대 바뀌지 않는다.**

- 세토프에서 받았으면 세토프 PK를 계속 반환
- MarketPlace에서 받았으면 MarketPlace PK를 계속 반환
- 크롤링: InboundProduct가 양방향 매핑 허브
- 레거시: LegacyConversionOutbox(그룹) + legacy_product_id_mapping(SKU)이 양방향 매핑 허브

---

## 4. 실행 단계

### Phase 0: 인프라 준비

> 목표: 전환에 필요한 인프라 기반 구축

#### 0-1. persistence-mysql-legacy 모듈 생성

세토프 DB(luxurydb)에 직접 연결하여 조회/수정하는 어댑터 모듈.

```
adapter-out/persistence-mysql-legacy/
  ├── config/
  │   └── LegacyDataSourceConfig.java     ← luxurydb DataSource + EntityManager
  ├── entity/
  │   ├── SetofProductGroupEntity.java    ← 세토프 product_groups 매핑
  │   └── SetofProductEntity.java         ← 세토프 products 매핑
  ├── repository/
  │   ├── SetofProductGroupJpaRepository.java
  │   └── SetofProductQueryDslRepository.java
  ├── adapter/
  │   ├── SetofProductQueryAdapter.java   ← 조회 폴백용
  │   └── SetofProductCommandAdapter.java ← 동기화 쓰기용
  └── mapper/
      └── SetofProductEntityMapper.java
```

**용도**:
- LEGACY_IMPORTED 경로의 폴백 조회/수정
- OutboundSync 세토프 채널의 실제 DB 쓰기 (전환기)
- 벌크 이관 스케줄러의 데이터 소스

#### 0-2. Legacy CRUD 와이어링 (3-Phase 레거시 접근법의 Phase 1)

persistence-mysql-legacy 모듈에 이미 구현된 `SetofProductGroupCommandPort`의 8개 업데이트 메서드를
application 레이어에 연결하여, MarketPlace가 luxurydb의 **CRUD 프록시** 역할을 수행한다.

```
구현 완료 (adapter 레벨):
  ✅ SetofProductGroupCommandAdapter.updatePrice()
  ✅ SetofProductGroupCommandAdapter.updateDisplayYn()
  ✅ SetofProductGroupCommandAdapter.updateSoldOutYn()
  ✅ SetofProductGroupCommandAdapter.updateNotice()
  ✅ SetofProductGroupCommandAdapter.replaceImages()
  ✅ SetofProductGroupCommandAdapter.updateDetailDescription()
  ✅ SetofProductGroupCommandAdapter.updateStock()
  ✅ SetofProductGroupCommandAdapter.markProductSoldOut()

와이어링 필요 (application → adapter):
  ❌ 각 메서드를 호출하는 Application Service / UseCase
  ❌ LegacyProductCommandController에서 직접 호출 경로 구성
```

이 단계 완료 시 MarketPlace는 **모든 레거시 상품 CRUD를 luxurydb에 대해 수행**할 수 있다.
외부 OMS(사방넷/셀릭) 입장에서는 기존 세토프 어드민과 동일한 동작.

#### 0-3. Auto-increment 조정

```sql
-- 배포 전 세토프 실제 max 값 확인 필수
SELECT MAX(id) FROM luxurydb.product_groups;
SELECT MAX(id) FROM luxurydb.products;

ALTER TABLE marketplace.product_groups AUTO_INCREMENT = {세토프_max + 50000};
ALTER TABLE marketplace.products AUTO_INCREMENT = {세토프_max + 100000};
```

---

### Phase 1: 레거시 변환 파이프라인 + 엔드포인트 라우팅

> 목표: Legacy 변환 인프라 구축 + 외부 OMS 요청을 변환 상태 기반으로 처리

#### 1-1. LegacyConversionOutbox + legacy_product_id_mapping 구현

3-Phase 레거시 접근법의 **Phase 2**에 해당하는 변환 인프라:

```
[Domain]
  LegacyConversionOutbox aggregate (상태 머신, 재시도)
  LegacyConversionStatus enum (PENDING → PROCESSING → COMPLETED / FAILED)
  LegacyProductIdMapping VO (legacy_product_id ↔ internal_product_id)

[Persistence]
  legacy_product_conversion_outbox 테이블 (§2.2 스키마)
  legacy_product_id_mapping 테이블 (§3.2 스키마)
  각각 Entity + Repository + Adapter

[Application]
  LegacyConversionScheduler (PENDING 폴링 → 변환 → COMPLETED)
    → luxurydb에서 최신 데이터 조회
    → ProductGroupRegistrationBundle 변환
    → FullProductGroupRegistrationCoordinator.register()
    → 성공: COMPLETED + internal_product_group_id 기록
    → legacy_product_id_mapping 레코드 생성 (SKU별)
```

#### 1-2. 상태 기반 라우팅 (LegacyConversionOutbox 기반)

```
외부 OMS → LegacyProductCommandController
  → LegacyConversionOutbox 조회 (legacy_product_group_id)
    ├─ COMPLETED → MarketPlace 내부 경로
    │   ├─ productGroupId: outbox.internalProductGroupId
    │   ├─ productId: legacy_product_id_mapping으로 변환
    │   ├─ 처리: MarketPlace 내부 상품 수정
    │   └─ 동기화: OutboundSyncOutbox → 세토프 DB + 기타 채널
    │
    └─ PENDING / 미존재 → luxurydb 직접 경로
        ├─ persistence-mysql-legacy로 세토프 DB 직접 CRUD
        └─ 응답: 세토프 데이터 그대로 반환
```

> **InboundProduct를 거치지 않는다.** 레거시 라우팅은 LegacyConversionOutbox 상태만으로 판별.

#### 1-3. 엔드포인트별 전환 상세

**ProductGroup 레벨 (productGroupId만 사용)**

| 엔드포인트 | COMPLETED 경로 | 미변환(PENDING/미존재) 경로 |
|-----------|---------------|--------------------------|
| PUT /{id} (전체수정) | outbox.internalProductGroupId → 내부 수정 | luxurydb 직접 수정 |
| PUT /{id}/notice | outbox → 내부 수정 | luxurydb 직접 수정 |
| PUT /{id}/images | outbox → 내부 수정 | luxurydb 직접 수정 |
| PUT /{id}/detailDescription | outbox → 내부 수정 | luxurydb 직접 수정 |
| PATCH /{id}/price | outbox → 내부 수정 | luxurydb 직접 수정 |
| PATCH /{id}/display-yn | outbox → 내부 수정 | luxurydb 직접 수정 |

**Product(SKU) 레벨 (productId 사용)**

| 엔드포인트 | COMPLETED 경로 | 미변환 경로 |
|-----------|---------------|-----------|
| PUT /{id}/option | legacy_product_id_mapping으로 productId 변환 + 내부 수정 | luxurydb 직접 수정 |
| PATCH /{id}/stock | legacy_product_id_mapping으로 productId 변환 + 내부 수정 | luxurydb 직접 수정 |
| PATCH /{id}/out-stock | productId 역매핑 + 내부 수정 | luxurydb 직접 수정 |

**등록/조회**

| 엔드포인트 | 처리 |
|-----------|------|
| POST /product/group | luxurydb INSERT + LegacyConversionOutbox 생성 (비동기 변환) |
| GET /{id} | MarketPlace DB 우선 (outbox COMPLETED 시) → 없으면 luxurydb 직접 조회 |

#### 1-4. 구현 순서

| 순서 | 작업 | 사유 |
|------|------|------|
| 1 | LegacyConversionOutbox 도메인 + Persistence | 변환 추적의 핵심 인프라 |
| 2 | legacy_product_id_mapping 도메인 + Persistence | SKU 레벨 매핑의 핵심 인프라 |
| 3 | LegacyConversionScheduler 구현 | PENDING → 변환 → COMPLETED 파이프라인 |
| 4 | LegacyCommandResolver 상태 분기 완성 | outbox 기반 COMPLETED vs 미변환 라우팅 |
| 5 | productId 양방향 변환 → legacy_product_id_mapping 기반 전환 | SKU 매핑을 InboundProductItem → legacy_product_id_mapping으로 |
| 6 | 수정 → OutboundSyncOutbox 연동 | COMPLETED 경로에서 수정 시 아웃박스 이벤트 발행 |

---

### Phase 2: OutboundSync 처리 파이프라인

> 목표: OutboundSyncOutbox를 실제로 처리하여 외부 채널에 상품 등록/수정

#### 2-1. 현재 상태

```
구현 완료:
  ✅ OutboundSyncOutbox 도메인 모델 (상태 머신, 재시도, 낙관적 락)
  ✅ OutboundProduct 도메인 모델 (외부 상품 매핑)
  ✅ Outbox 생성 흐름 (PostAnalysisProductGroupCoordinator → 채널별 PENDING 생성)
  ✅ Persistence 스택 (Entity, Repository, Adapter)

미구현:
  ❌ OutboundSync 처리 스케줄러 (PENDING → PROCESSING → COMPLETED)
  ❌ 채널별 상품 등록/수정 클라이언트
  ❌ 세토프 상품 동기화 어댑터
  ❌ 네이버 상품 등록 API 클라이언트
```

#### 2-2. OutboundSync 처리 아키텍처

```
OutboundSyncScheduler (주기적 폴링 또는 이벤트)
  │
  ├─ PENDING Outbox 조회 (batch)
  │
  ├─ 채널별 분기:
  │   ├─ salesChannelId = 세토프 → SetofProductSyncAdapter
  │   │   ├─ 전환기: persistence-mysql-legacy로 세토프 DB 직접 쓰기
  │   │   └─ 최종: HTTP API → 세토프 상품서버
  │   │
  │   ├─ salesChannelId = 네이버 → NaverCommerceProductAdapter
  │   │   └─ HTTP API → 네이버 커머스 API
  │   │
  │   └─ salesChannelId = 기타 → 해당 채널 어댑터
  │
  ├─ 성공 → OutboundSyncOutbox.complete()
  │         OutboundProduct.registerExternalProduct(externalId)
  │
  └─ 실패 → OutboundSyncOutbox.failAndRetry()
             최대 재시도 초과 시 → FAILED + 알림
```

#### 2-3. 채널별 어댑터 인터페이스

```java
// application 레이어 Port
public interface SalesChannelProductSyncPort {
    SyncResult registerProduct(SalesChannelId channelId, ProductSyncPayload payload);
    SyncResult updateProduct(SalesChannelId channelId, String externalProductId, ProductSyncPayload payload);
}

// 각 채널별 Adapter-Out 구현
// adapter-out/persistence-mysql-legacy → SetofProductSyncAdapter (전환기)
// adapter-out/client/naver-commerce-client → NaverCommerceProductSyncAdapter
// adapter-out/client/setof-client → SetofProductSyncAdapter (최종, HTTP)
```

#### 2-4. 세토프 채널 어댑터 (전환기)

```
adapter-out/persistence-mysql-legacy/
  └── adapter/
      └── SetofProductSyncAdapter.java
          ├─ registerProduct(): 세토프 DB에 상품 INSERT
          ├─ updateProduct(): 세토프 DB에 상품 UPDATE
          └─ 세토프 스키마 매핑 (MarketPlace 상품 → 세토프 테이블 구조)
```

#### 2-5. 네이버 커머스 어댑터

```
adapter-out/client/naver-commerce-client/
  ├── 기존 구현:
  │   ├── NaverCommerceCategoryClientAdapter.java   ← 카테고리 동기화
  │   └── NaverCommerceBrandClientAdapter.java       ← 브랜드 검색
  │
  └── 신규 구현:
      ├── NaverCommerceProductClientAdapter.java     ← 상품 등록/수정
      │   ├─ registerProduct(): POST /v2/products
      │   ├─ updateProduct(): PUT /v2/products/{id}
      │   └─ 응답에서 externalProductId 추출
      ├── dto/
      │   ├── NaverCommerceProductRequest.java
      │   └── NaverCommerceProductResponse.java
      └── mapper/
          └── NaverCommerceProductMapper.java        ← 내부 상품 → 네이버 포맷
```

---

### Phase 3: 배치 전환 (기존 세토프 상품 → MarketPlace 내부 상품)

> 목표: 세토프 기존 상품을 점진적으로 MarketPlace 내부 상품으로 전환

#### 3-1. 배치 전환 프로세스

기존 세토프 상품에 대한 LegacyConversionOutbox 레코드를 벌크 생성한 뒤,
LegacyConversionScheduler가 순차적으로 변환 처리.

```
[벌크 아웃박스 생성]
INSERT INTO legacy_product_conversion_outbox (
    legacy_product_group_id, seller_id, status, created_at, updated_at
)
SELECT pg.id, pg.seller_id, 'PENDING', NOW(6), NOW(6)
FROM luxurydb.product_groups pg
WHERE pg.delete_yn = 'N'
  AND NOT EXISTS (
    SELECT 1 FROM legacy_product_conversion_outbox o
    WHERE o.legacy_product_group_id = pg.id
  );

[LegacyConversionScheduler — 기존과 동일한 로직]
  ① PENDING 폴링
  ② luxurydb에서 최신 데이터 조회 (SetofProductGroupComposite)
  ③ ProductGroupRegistrationBundle 변환
  ④ FullProductGroupRegistrationCoordinator.register()
  ⑤ 성공:
     → outbox.complete(internalProductGroupId)
     → legacy_product_id_mapping 벌크 INSERT (SKU별)
     → OutboundProduct + OutboundSyncOutbox 생성 (채널 동기화)
  ⑥ 실패: retry or FAILED
```

#### 3-2. 전환율 모니터링

```sql
SELECT
    status,
    COUNT(*) AS cnt,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER(), 1) AS pct
FROM legacy_product_conversion_outbox
GROUP BY status;

-- 목표: PENDING = 0, COMPLETED = 100%
```

---

### Phase 4: 세토프 DB 의존 완전 제거

> 목표: persistence-mysql-legacy 모듈 제거, 세토프를 HTTP API 세일즈채널로 전환

#### 4-1. 제거 조건 (모두 충족 시)

- [ ] legacy_product_conversion_outbox 전체가 COMPLETED (PENDING = 0건)
- [ ] 레거시 폴백(luxurydb 직접 CRUD) 호출 빈도 0 (모니터링 확인)
- [ ] OutboundSync 세토프 채널 정상 운영 확인
- [ ] 외부 OMS(사방넷/셀릭) 정상 동작 확인 (최소 2주 모니터링)

#### 4-2. 제거 대상

```
- adapter-out/persistence-mysql-legacy/ 모듈 전체
- legacy_product_conversion_outbox 테이블
- legacy_product_id_mapping 테이블
- Legacy 변환/라우팅 관련 코드 전체
- 세토프 DB DataSource 설정
```

#### 4-3. 교체 대상

```
persistence-mysql-legacy (세토프 DB 직접 쓰기)
  → adapter-out/client/setof-client/ (HTTP API 연동)

세토프 웹서버가 API를 통해 상품 데이터를 수신받는 구조로 전환되면
setof-client 모듈에서 HTTP API로 상품 등록/수정을 처리.
```

---

## 5. 코드 현황 매핑

### 5.1 구현 완료

| 영역 | 컴포넌트 | 상태 |
|------|---------|------|
| Domain | InboundProduct (크롤링 전용) + 상태 enum | ✅ |
| Domain | OutboundProduct + OutboundSyncOutbox | ✅ |
| Domain | SalesChannel + SellerSalesChannel | ✅ |
| Persistence | InboundProduct 전체 스택 | ✅ |
| Persistence | OutboundProduct/SyncOutbox 전체 스택 | ✅ |
| Application | InboundProduct 파이프라인 (수신→매핑→변환) | ✅ |
| Application | OutboundSync Outbox 생성 흐름 | ✅ |
| Adapter-In | LegacyProductCommandController (11개 엔드포인트) | ✅ |
| Adapter-Out | naver-commerce-client (카테고리/브랜드 동기화) | ✅ |
| Adapter-Out | persistence-mysql-legacy 모듈 (Entity/Repo/Adapter/Mapper) | ✅ |
| Adapter-Out | persistence-mysql-legacy: SetofProductGroupCommandPort 8개 업데이트 메서드 (adapter 레벨) | ✅ |
| Application | Legacy 상품 등록 파이프라인 (Bundle→Coordinator→luxurydb) | ✅ |

### 5.2 미구현 (이번 페이즈 대상)

| 영역 | 컴포넌트 | 우선순위 |
|------|---------|---------|
| Application | **SetofProductGroupCommandPort 와이어링** (8개 업데이트 → application 연결) | P0 |
| Domain | **LegacyConversionOutbox 도메인 모델** (상태 머신, 재시도) | P0 |
| Domain+Persistence | **legacy_product_id_mapping** (SKU 레벨 매핑) | P0 |
| Domain | **InboundProductStatus.PENDING_CONVERSION** 상태 추가 | P0 |
| Application | **LegacyConversionScheduler** (PENDING 폴링 → 변환 → legacy_product_id_mapping 생성) | P0 |
| Application | **InboundConversionScheduler** (PENDING_CONVERSION 폴링 → 내부 상품 변환) | P0 |
| Application | **OutboundSyncOutbox 처리 스케줄러** | P0 |
| Adapter-Out | **세토프 상품 동기화 어댑터** (전환기: DB 직접 / 최종: HTTP) | P0 |
| Adapter-Out | **네이버 커머스 상품 등록/수정 클라이언트** | P0 |
| Application | **LegacyCommandResolver 상태 분기** (LegacyConversionOutbox 기반) | P1 |
| Application | **productId 변환 → legacy_product_id_mapping 기반 전환** | P1 |
| Application | **수정 → OutboundSyncOutbox 이벤트 발행** | P1 |
| Application | **벌크 아웃박스 생성 + 배치 전환 스케줄러** | P2 |

### 5.3 전체 흐름 (목표 상태 — 분리 아웃박스 반영)

```
[상품 등록 - 크롤링 경로]
InboundProduct 수신 → 매핑(MAPPED) → markPendingConversion()
  → InboundConversionScheduler (비동기)
    → FullProductGroupRegistrationCoordinator.register() → 내부 상품 생성
    → markConverted(productGroupId)
  → AI 분석 (IntelligenceOutbox, 비동기)
  → AUTO_APPROVED 판정
  → PostAnalysisProductGroupCoordinator
    → OutboundProduct + OutboundSyncOutbox 생성 (채널별, PENDING)

[상품 등록 - 레거시 경로]
외부 OMS POST → LegacyProductRegistrationCoordinator
  → luxurydb INSERT (동기, setof PK 즉시 반환)
  → LegacyConversionOutbox 생성 (PENDING, reference=legacy_product_group_id)
  → LegacyConversionScheduler (비동기)
    → legacy_product_group_id로 luxurydb에서 최신 데이터 조회
    → ProductGroupRegistrationBundle 변환
    → FullProductGroupRegistrationCoordinator.register()
    → 성공: COMPLETED + internalProductGroupId 기록
    → legacy_product_id_mapping 레코드 생성 (SKU별)
  → AI 분석 + OutboundSync (내부 상품 생성 완료 후)

[아웃박스 처리 - OutboundSync]
OutboundSyncScheduler (주기적 폴링)
  → PENDING Outbox 조회
  → 채널별 분기:
    → SetofProductSyncAdapter.registerProduct()  ← 세토프 DB INSERT (전환기)
    → NaverCommerceProductAdapter.registerProduct() ← 네이버 API 호출
  → 성공: COMPLETED + OutboundProduct에 externalProductId 저장
  → 실패: FAILED + 재시도

[상품 수정 - Legacy 경로 (미변환: PENDING)]
외부 OMS PUT/PATCH → luxurydb UPDATE만 수행
  → LegacyConversionOutbox는 PENDING 유지
  → LegacyConversionScheduler 실행 시 luxurydb에서 최신 데이터 조회
  → 수정 내용이 변환 결과에 자연스럽게 반영

[상품 수정 - Legacy 경로 (변환 완료: COMPLETED)]
외부 OMS PUT/PATCH → LegacyConversionOutbox 조회 (COMPLETED)
  → outbox.internalProductGroupId로 내부 상품 식별
  → SKU 레벨: legacy_product_id_mapping으로 productId 변환
  → MarketPlace 내부 상품 수정
  → OutboundSyncOutbox 생성 (UPDATE 타입, PENDING)
  → OutboundSyncScheduler가 처리
    → SetofProductSyncAdapter.updateProduct()  ← luxurydb UPDATE
    → NaverCommerceProductAdapter.updateProduct() ← 네이버 API 호출

[상품 수정 - InboundProduct 경로 (미변환: PENDING_CONVERSION)]
크롤링 서버 PUT/PATCH → InboundProduct 데이터만 갱신
  → InboundConversionScheduler 실행 시 갱신된 데이터 기반 변환

[상품 수정 - InboundProduct 경로 (변환 완료: CONVERTED)]
크롤링 서버 PUT/PATCH → InboundProduct 갱신 + 내부 상품 동기 수정
  → OutboundSyncOutbox 생성 (UPDATE 타입, PENDING)
  → OutboundSyncScheduler가 처리
    → SetofProductSyncAdapter.updateProduct()  ← luxurydb UPDATE
    → NaverCommerceProductAdapter.updateProduct() ← 네이버 API 호출
```

---

## 6. 외부 OMS 트래픽 (7일 기준)

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

## 7. 리스크 및 대응

| 리스크 | 영향 | 대응 |
|--------|------|------|
| 벌크 적재 중 세토프 신규 상품 추가 | 매핑 누락 | 선 적재 후 delta sync (차이분 보정) |
| 배치 전환 중 외부 OMS 동시 호출 | 동시성 이슈 | 낙관적 락 또는 상품별 동기화 |
| 세토프 DB 스키마와 MarketPlace 매핑 불일치 | 데이터 오류 | setof-commerce persistence-mysql-legacy 참조 |
| 네이버 API Rate Limit | 등록 지연 | 배치 크기 조절 + 재시도 |
| 옵션 구조 불일치로 SKU 매칭 실패 | Product 매핑 오류 | 수동 매핑 어드민 도구 제공 |
| auto_increment 버퍼 부족 | PK 충돌 | 충분한 여유 확보 (세토프 max * 2 이상) |

---

## 8. 모니터링 지표

| 지표 | 확인 방법 | 정상 기준 |
|------|----------|----------|
| 전환율 | outbox COMPLETED / 전체 outbox | 점진적 증가 → 100% |
| 레거시 폴백 비율 | luxurydb 직접 CRUD 횟수 / 전체 | 점진적 감소 → 0% |
| 외부 OMS 에러율 | 4xx/5xx 응답 비율 | < 0.1% |
| OutboundSync 성공률 | COMPLETED / 전체 | > 99% |
| OutboundSync 지연 | PENDING 생성 → COMPLETED 시간 | < 5분 |
| 채널별 동기화 상태 | 채널별 FAILED 건수 | 0에 수렴 |

---

## 9. 롤백 전략

### Phase별 롤백

```
Phase 0 (인프라 + Legacy CRUD):
  → auto_increment 원복
  → Legacy CRUD 와이어링 제거 (luxurydb 직접 접근만 유지)
  → persistence-mysql-legacy 모듈 비활성화

Phase 1 (변환 + 라우팅):
  → LegacyConversionScheduler 비활성화
  → 라우팅 로직 제거 → 모든 요청 luxurydb 직접 처리로 원복
  → legacy_product_conversion_outbox / legacy_product_id_mapping 데이터 유지 (롤백 불필요)

Phase 2 (OutboundSync):
  → 스케줄러 비활성화
  → PENDING 아웃박스 수동 처리 또는 삭제

Phase 3 (배치 전환):
  → 배치 중지, COMPLETED된 건은 유지 (롤백 불필요)

Phase 4 (의존 제거):
  → Phase 3 100% + Phase 2 안정화 후에만 진행
```

---

## 10. 완료 기준

### 이번 페이즈 Exit Criteria

- [ ] 상품 등록 시 InboundProduct → 내부 상품 → OutboundProduct → OutboundSyncOutbox 전체 흐름 동작
- [ ] OutboundSyncScheduler가 PENDING 아웃박스를 처리하여 세토프 DB 동기화 성공
- [ ] OutboundSyncScheduler가 네이버 스마트스토어 상품 등록 API 호출 성공
- [ ] 레거시 엔드포인트 11개가 상태 기반 라우팅으로 동작 (CONVERTED/LEGACY_IMPORTED)
- [ ] 외부 OMS(사방넷/셀릭)가 에러 없이 MarketPlace 경유 동작 확인
- [ ] 상품 수정 시 OutboundSyncOutbox 이벤트 발행 → 채널 동기화 성공

### 전체 마이그레이션 Exit Criteria

- [ ] legacy_product_conversion_outbox PENDING = 0건 (모든 상품 COMPLETED)
- [ ] 레거시 폴백(luxurydb 직접 CRUD) 트래픽 0%
- [ ] persistence-mysql-legacy 모듈 제거
- [ ] legacy_product_conversion_outbox + legacy_product_id_mapping 테이블 제거
- [ ] 세토프 연동이 HTTP API 기반으로 전환

---

## 11. 관련 문서

- [레거시 엔드포인트 전환 실행 가이드](./legacy-product-endpoint-migration-execution-guide.md)
- [게이트웨이 라우팅 마이그레이션 명세서](./gateway-routing-migration-spec.md)
- [레거시 엔드포인트 트래픽 분석](./legacy-endpoint-traffic-analysis.md)
- [InboundProduct 리팩토링 설계서](./inbound-product-refactoring-design.md)
