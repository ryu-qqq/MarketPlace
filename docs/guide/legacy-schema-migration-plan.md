# Legacy Schema Migration Plan

> Legacy DB(luxurydb) 의존 제거 및 새 스키마 직접 저장 전환 계획

## 1. 현재 상태

### 데이터 흐름

```
클라이언트 (외부 회사, 수정 불가)
    ↓ legacy productGroupId 사용
Legacy API Controller
    ↓
Legacy UseCase → luxurydb 저장 → LegacyConversionOutbox(PENDING) 생성
                                        ↓ (5초 스케줄러)
                                  LegacyConversionCoordinator
                                        ↓
                                  PreResolver (셀러/브랜드/카테고리 매핑)
                                        ↓
                                  FullProductGroupRegistrationCoordinator
                                        ↓
                                  새 스키마 저장 (internal ID 생성)
                                        ↓
                                  legacy_product_id_mappings 기록
```

### Legacy API 엔드포인트 목록

| Method | Endpoint | 기능 | UseCase |
|--------|----------|------|---------|
| POST | `/api/v1/legacy/product/group` | 상품그룹 등록 | `LegacyProductGroupFullRegisterUseCase` |
| GET | `/api/v1/legacy/product/group/{id}` | 상품그룹 조회 | `LegacyProductQueryUseCase` |
| PUT | `/api/v1/legacy/product/group/{id}` | 상품그룹 전체 수정 | `LegacyProductGroupFullUpdateUseCase` |
| PATCH | `/api/v1/legacy/product/group/{id}/display-yn` | 전시 상태 변경 | `LegacyProductUpdateDisplayStatusUseCase` |
| PATCH | `/api/v1/legacy/product/group/{id}/out-stock` | 품절 처리 | `LegacyProductMarkOutOfStockUseCase` |
| PATCH | `/api/v1/legacy/product/group/{id}/images` | 이미지 수정 | `LegacyProductUpdateImagesUseCase` |
| PATCH | `/api/v1/legacy/product/group/{id}/notice` | 고시정보 수정 | `LegacyProductUpdateNoticeUseCase` |
| PATCH | `/api/v1/legacy/product/group/{id}/detailDescription` | 상세설명 수정 | `LegacyProductUpdateDescriptionUseCase` |
| POST | `/api/v1/legacy/auth/authentication` | 인증 | `LegacyLoginUseCase` |
| GET | `/api/v1/legacy/image/presigned` | 이미지 업로드 URL | `LegacyGetPresignedUrlUseCase` |
| GET | `/api/v1/legacy/seller/me` | 셀러 정보 조회 | `LegacyGetCurrentSellerUseCase` |
| GET | `/api/v1/legacy/shipment/company-codes` | 택배사 코드 | `LegacyGetShipmentCompanyCodesUseCase` |

### ID 체계

| 구분 | 테이블 | PK 전략 | 비고 |
|------|--------|---------|------|
| Legacy | `luxurydb.product_group` | AUTO_INCREMENT | 클라이언트가 사용하는 ID |
| Legacy | `luxurydb.product` | AUTO_INCREMENT | 클라이언트가 사용하는 SKU ID |
| Internal | `market.product_groups` | AUTO_INCREMENT | 내부 시스템 ID |
| Internal | `market.products` | AUTO_INCREMENT | 내부 시스템 SKU ID |
| Mapping | `market.legacy_product_id_mappings` | - | 양방향 매핑 |

### 새 스키마에서 product_group_id를 참조하는 테이블

| 테이블 | FK 컬럼 | 용도 |
|--------|---------|------|
| `products` | `product_group_id` | SKU |
| `product_group_descriptions` | `product_group_id` | 상세설명 |
| `product_group_images` | `product_group_id` | 이미지 |
| `product_notices` | `product_group_id` | 고시정보 |
| `seller_option_groups` | `product_group_id` | 옵션 |
| `product_profiles` | `product_group_id` | AI 분석 프로필 |
| `outbound_products` | `product_group_id` | 외부채널 상품 매핑 |
| `outbound_sync_outboxes` | `product_group_id` | 외부채널 동기화 |
| `order_items` | `product_group_id`, `product_id` | 주문 |
| `product_option_mappings` | `product_id` | 옵션-SKU 매핑 |

---

## 2. 제약 조건

- **클라이언트 수정 불가** - 외부 회사, 변경 시 비용 발생
- **클라이언트는 legacy productGroupId/productId로 요청** - 이 계약은 유지해야 함
- **외부채널(SetOf, Naver)은 internal ID 사용** - 영향 없음
- **주말 전환 가능** - 등록/수정이 없는 시간대

---

## 3. 전환 전략

### 핵심 아이디어

Legacy API를 **영구적인 번역 레이어**로 전환한다.

```
전환 후:

클라이언트 → Legacy API Controller
                  ↓
            ID 번역 레이어 (매핑 테이블)
                  ↓
            새 스키마 UseCase 직접 호출
                  ↓
            응답 시 legacy ID로 재변환
```

### 기존 상품 vs 신규 상품

```
[기존 상품] 클라이언트: "1234번 조회"
  → 매핑 테이블: legacy 1234 → internal 5678
  → 새 스키마에서 5678 조회
  → 응답에 productGroupId=1234 반환

[신규 상품] 클라이언트: "상품 등록"
  → 새 스키마에 직접 저장 → internal ID = 90001
  → 매핑 불필요 (legacy=internal=90001)
  → 응답에 productGroupId=90001 반환
  → 이후 클라이언트는 90001로 요청
```

### Auto-Increment 충돌 방지

```sql
-- 전환 시점에 실행
-- legacy max product_group_id 이상으로 설정하여 ID 범위 충돌 방지
ALTER TABLE product_groups AUTO_INCREMENT = {legacy_max_id + 10000};
ALTER TABLE products AUTO_INCREMENT = {legacy_max_product_id + 10000};
```

---

## 4. 작업 단계

### Phase 0: 사전 준비 (평일 가능)

#### 0-1. ID 범위 조사
```sql
-- legacy DB
SELECT MAX(product_group_id) FROM luxurydb.product_group;
SELECT MAX(product_id) FROM luxurydb.product;

-- new schema
SELECT MAX(id) FROM market.product_groups;
SELECT MAX(id) FROM market.products;

-- 매핑 완료율 확인
SELECT COUNT(*) FROM market.legacy_product_id_mappings;
SELECT COUNT(DISTINCT legacy_product_group_id) FROM market.legacy_product_id_mappings;
```

#### 0-2. 미변환 상품 확인
```sql
-- PENDING/FAILED 아웃박스 확인 (전환 전 0건이어야 함)
SELECT status, COUNT(*)
FROM legacy_conversion_outboxes
GROUP BY status;
```

#### 0-3. ID 번역 서비스 구현

새로운 컴포넌트:

```
LegacyIdTranslator (Application Layer)
├── translateProductGroupId(legacyId) → internalId
├── translateProductId(legacyId) → internalId
├── reverseTranslateProductGroupId(internalId) → legacyId
├── reverseTranslateProductId(internalId) → legacyId
├── isLegacyId(id) → boolean  (매핑 테이블에 존재하면 legacy)
└── 캐시: Redis 또는 Local Cache (78K건, 변경 없음)
```

#### 0-4. 새로운 Legacy UseCase 구현

기존 Legacy UseCase를 래핑하는 Bridge UseCase:

```
LegacyBridgeProductGroupRegisterUseCase
  → PreResolver + BundleFactory + FullProductGroupRegistrationCoordinator 동기 호출
  → luxurydb 저장 없음, outbox 생성 없음

LegacyBridgeProductGroupQueryUseCase
  → legacyId 번역 → 새 스키마 조회 → legacy 응답 포맷으로 변환

LegacyBridgeProductGroupUpdateUseCase
  → legacyId 번역 → FullProductGroupUpdateCoordinator 동기 호출
```

#### 0-5. Feature Flag 추가

```yaml
legacy:
  write-mode: LEGACY  # LEGACY | NEW
```

- **LEGACY**: 현재 동작 (luxurydb 저장 + outbox)
- **NEW**: 새 스키마 직접 저장 (Bridge UseCase 사용)

#### 0-6. 레거시 컨트롤러에 Flag 기반 분기 적용

```java
// 예시: LegacyProductGroupCommandController
if (writeMode == NEW) {
    return bridgeRegisterUseCase.execute(command);  // 새 스키마 직접
} else {
    return legacyRegisterUseCase.execute(command);  // 기존 luxurydb
}
```

### Phase 1: 검증 (평일, Stage 환경)

#### 1-1. Stage에서 write-mode=NEW 테스트
- 상품 등록 → 새 스키마에 직접 저장 확인
- 상품 조회 → legacy ID로 조회 가능 확인
- 상품 수정 → legacy ID로 수정 → 새 스키마 반영 확인
- 외부채널 동기화 정상 확인

#### 1-2. 응답 포맷 검증
- 기존 상품: legacy ID 반환 확인
- 신규 상품: 새 ID 반환 확인
- 클라이언트 API 스펙과 응답 일치 확인

### Phase 2: 전환 (주말)

#### 2-1. 전환 전 체크리스트
- [ ] 모든 outbox COMPLETED 확인 (PENDING/PROCESSING = 0)
- [ ] legacy_product_id_mappings 데이터 정합성 확인
- [ ] ID 범위 충돌 없음 확인
- [ ] Stage 테스트 완료

#### 2-2. 전환 실행 (주말, 서비스 중지 불필요)

```bash
# 1. Auto-increment 조정
ALTER TABLE product_groups AUTO_INCREMENT = {legacy_max + 10000};
ALTER TABLE products AUTO_INCREMENT = {legacy_max + 10000};

# 2. Feature flag 전환
legacy.write-mode=NEW

# 3. 스케줄러 비활성화 (outbox 더 이상 불필요)
scheduler.jobs.legacy-conversion.enabled=false
scheduler.jobs.legacy-conversion-seeder.enabled=false
scheduler.jobs.legacy-conversion-timeout.enabled=false

# 4. 애플리케이션 재시작 (rolling restart)
```

#### 2-3. 전환 후 모니터링
- Legacy API 정상 응답 확인
- 상품 등록/조회/수정 E2E 테스트
- 외부채널 동기화 정상 확인
- 에러 로그 모니터링

### Phase 3: 안정화 (전환 후 1-2주)

#### 3-1. Rollback 준비
- Feature flag를 LEGACY로 되돌리면 즉시 원복 가능
- luxurydb는 읽기 전용으로 유지 (안전망)

#### 3-2. Legacy DB 읽기 전용 전환
```sql
-- luxurydb 사용자 권한을 READ ONLY로 변경
GRANT SELECT ON luxurydb.* TO 'marketplace'@'%';
REVOKE INSERT, UPDATE, DELETE ON luxurydb.* FROM 'marketplace'@'%';
```

### Phase 4: 정리 (안정화 확인 후)

#### 4-1. 코드 정리
- 기존 Legacy UseCase 구현체 제거 (luxurydb 쓰기 로직)
- LegacyConversionOutbox 관련 코드 제거
- LegacyConversionSeeder/Scheduler 제거
- Feature flag 분기 제거 (NEW만 남김)
- `persistence-mysql-legacy` 모듈 제거 (luxurydb 의존성)

#### 4-2. 인프라 정리
- luxurydb RDS 인스턴스 축소 또는 중지
- Legacy Flyway migration 제거

#### 4-3. 유지되는 것
- Legacy API Controller (클라이언트 계약 유지)
- legacy_product_id_mappings 테이블 (기존 상품 ID 번역용, 영구)
- LegacyIdTranslator (번역 레이어, 영구)
- LegacyConversionPreResolver의 셀러 매핑 (기존 상품 조회용)

---

## 5. 리스크 및 대응

### 리스크 1: ID 범위 충돌
- **원인**: 새 스키마 auto-increment가 legacy ID 범위 내에서 생성
- **대응**: Phase 2에서 auto-increment를 legacy max + 10000 이상으로 설정
- **검증**: Phase 0-1에서 ID 범위 사전 조사

### 리스크 2: 매핑 누락
- **원인**: 일부 legacy 상품이 conversion 실패로 매핑 테이블에 없음
- **대응**: Phase 2-1 체크리스트에서 FAILED outbox 확인 및 재처리
- **검증**: legacy 상품 수 vs 매핑 수 일치 확인

### 리스크 3: 응답 포맷 차이
- **원인**: 새 스키마 조회 결과를 legacy 포맷으로 변환 시 필드 누락
- **대응**: Phase 1에서 기존 응답과 신규 응답 diff 비교
- **검증**: 클라이언트 API 스펙 문서 기준 필드 매핑 검증

### 리스크 4: 전환 중 데이터 유실
- **원인**: flag 전환 시점에 진행 중이던 요청
- **대응**: 주말 전환 (요청 없는 시간), rolling restart
- **완화**: 즉시 rollback 가능 (flag → LEGACY)

---

## 6. 타임라인

```
Week 1: Phase 0 (사전 준비)
  - ID 범위 조사
  - LegacyIdTranslator 구현
  - Bridge UseCase 구현
  - Feature flag 추가

Week 2: Phase 1 (Stage 검증)
  - Stage에서 NEW 모드 테스트
  - 응답 포맷 검증
  - E2E 테스트

Week 2 주말: Phase 2 (전환)
  - Auto-increment 조정
  - Flag 전환
  - 모니터링

Week 3-4: Phase 3 (안정화)
  - 모니터링 지속
  - Legacy DB 읽기 전용 전환

Week 5+: Phase 4 (정리)
  - 코드/인프라 정리
```

---

## 7. 변환 대상 컴포넌트 매트릭스

### 영향받는 컴포넌트

| 레이어 | 컴포넌트 | 변경 내용 |
|--------|---------|----------|
| **Adapter-In** | Legacy Controllers (11개) | Flag 기반 분기 추가 |
| **Application** | Bridge UseCases (신규) | 새 스키마 직접 저장 로직 |
| **Application** | LegacyIdTranslator (신규) | ID 번역 서비스 |
| **Application** | 기존 Legacy UseCases | Phase 4에서 제거 |
| **Application** | LegacyConversion* | Phase 4에서 제거 |
| **Adapter-Out** | persistence-mysql-legacy | Phase 4에서 제거 |
| **Bootstrap** | LegacyModuleConfig | Phase 4에서 제거 |
| **Scheduler** | LegacyConversion*Scheduler | Phase 2에서 비활성화, Phase 4에서 제거 |
| **Config** | application.yml | Feature flag 추가 |
| **DB** | product_groups, products | Auto-increment 조정 |

### 영향 없는 컴포넌트

| 컴포넌트 | 이유 |
|---------|------|
| 새 API Controllers | legacy API와 독립 |
| 외부채널 (SetOf, Naver) | internal ID 사용, 영향 없음 |
| Outbound Sync | internal ID 기반, 영향 없음 |
| 주문 시스템 | internal ID 기반, 영향 없음 |

---

## 8. 쿼리 참고

### 전환 전 정합성 확인 쿼리

```sql
-- 1. legacy 상품 수
SELECT COUNT(DISTINCT product_group_id) as legacy_groups,
       COUNT(product_id) as legacy_products
FROM luxurydb.product
WHERE delete_yn = 'N';

-- 2. 매핑 완료 수
SELECT COUNT(DISTINCT legacy_product_group_id) as mapped_groups,
       COUNT(*) as mapped_products
FROM market.legacy_product_id_mappings;

-- 3. 미변환 아웃박스
SELECT status, COUNT(*), MAX(created_at)
FROM market.legacy_conversion_outboxes
WHERE status != 'COMPLETED'
GROUP BY status;

-- 4. ID 범위 확인
SELECT 'legacy_group' as type, MAX(product_group_id) as max_id FROM luxurydb.product_group
UNION ALL
SELECT 'legacy_product', MAX(product_id) FROM luxurydb.product
UNION ALL
SELECT 'internal_group', MAX(id) FROM market.product_groups
UNION ALL
SELECT 'internal_product', MAX(id) FROM market.products;
```
