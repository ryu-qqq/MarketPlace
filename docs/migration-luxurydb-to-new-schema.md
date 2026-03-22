# luxurydb → 신 스키마 전환 계획 (Step 2)

## 전제 조건

이 작업은 **Step 1(Shadow 검증 불일치율 0% + 운영 Gateway 라우팅 전환)이 완료된 후** 시작합니다.

Step 1 완료 시점의 상태:
- Gateway가 모든 레거시 셀러 어드민 트래픽을 MarketPlace로 라우팅
- MarketPlace가 luxurydb에 읽기/쓰기
- Outbox 스케줄러가 luxurydb → 신 스키마 동기화 중
- 레거시 서버는 제거 또는 fallback용 유지

## 현재 인프라

```
┌─────────────────────────────────────────────────┐
│ MarketPlace (운영)                                │
│                                                   │
│  rest-api-legacy (8081)                          │
│    │                                              │
│    ├── 읽기 → persistence-mysql-legacy → luxurydb │
│    ├── 쓰기 → persistence-mysql-legacy → luxurydb │
│    │                                              │
│    └── Outbox → 스케줄러 → persistence-mysql → 신 스키마 │
│                                                   │
│  ID 매핑 테이블: legacy PK ↔ new PK               │
│  레거시 ID 리졸버: legacy ID → new ID 변환         │
└─────────────────────────────────────────────────┘
```

## 전환 대상

| 도메인 | 우선순위 | Outbox 동기화 | ID 매핑 | 비고 |
|--------|---------|--------------|---------|------|
| 상품 (product/productGroup) | 1 | ✅ 있음 | ✅ 있음 | 가장 트래픽 많음 |
| 주문 (order) | 2 | ✅ 있음 | ✅ 있음 | 쓰기 빈도 높음 |
| 셀러 (seller) | 3 | - | - | 변경 빈도 낮아 후순위 |

---

## Phase 1: 읽기 전환

### 목표

GET 요청의 데이터 소스를 luxurydb → 신 스키마로 전환.
API 응답 형태(legacy 포맷)는 그대로 유지.

### 1-1. 준비 작업

**Feature Flag 추가:**

```yaml
# application.yml
migration:
  product:
    read-source: legacy    # legacy | new | shadow (비교 모드)
  order:
    read-source: legacy
```

**응답 매퍼 구현:**

신 스키마의 데이터 모델을 legacy API 응답 포맷으로 변환하는 매퍼가 필요합니다.

```
신 스키마 조회 결과 (ProductGroupDetail)
  → LegacyResponseMapper.toLegacyFormat()
  → legacy 응답 포맷 (LegacyProductGroupDetailResult)
  → 이때 new ID → legacy ID로 역변환 (ID 매핑 테이블 사용)
```

구현할 매퍼:
- `LegacyProductGroupResponseMapper` — 상품그룹 상세/목록
- `LegacyOrderResponseMapper` — 주문 상세/목록

### 1-2. Shadow 읽기 비교

Flag를 `shadow`로 설정하면, **luxurydb와 신 스키마 양쪽에서 동시 조회**하여 비교합니다.

```java
if ("shadow".equals(readSource)) {
    // 양쪽 조회
    LegacyProductGroupDetailResult legacyResult = legacyPort.findById(legacyId);

    long newId = idResolver.resolveProductGroupId(legacyId);
    ProductGroupDetail newResult = newPort.findById(newId);
    LegacyProductGroupDetailResult mappedResult = mapper.toLegacyFormat(newResult, legacyId);

    // 비교 (로그만, 응답은 legacyResult 반환)
    comparator.compare(legacyResult, mappedResult);

    return legacyResult;  // 셀러에게는 기존 데이터 반환
}
```

이 단계에서 발견할 수 있는 문제:
- Outbox 동기화 누락/지연
- 데이터 모델 매핑 오류
- ID 매핑 불일치

### 1-3. 읽기 전환

Shadow 비교에서 불일치율 0% 확인 후:

```yaml
migration:
  product:
    read-source: new      # 전환!
  order:
    read-source: new
```

문제 발생 시 즉시 `legacy`로 롤백 가능.

### 1-4. 도메인별 순차 전환

한 번에 다 바꾸지 않고, 도메인별로 순차 전환:

```
Week 1: 상품 읽기 → shadow 모드 (비교)
Week 2: 상품 읽기 → new (전환)
Week 3: 주문 읽기 → shadow 모드
Week 4: 주문 읽기 → new (전환)
```

---

## Phase 2: Dual Write

### 목표

POST/PUT/PATCH 요청을 luxurydb + 신 스키마 **양쪽에 동시 저장**.
이 단계에서 Outbox 스케줄러를 비활성화합니다 (Dual Write가 대체).

### 2-1. Dual Write 구현

```java
@Service
public class LegacyProductGroupFullRegisterService implements LegacyProductGroupFullRegisterUseCase {

    private final LegacyProductRegistrationCoordinator legacyCoordinator;  // luxurydb
    private final ProductGroupRegisterCoordinator newCoordinator;           // 신 스키마
    private final LegacyIdResolver idResolver;

    @Value("${migration.product.write-mode:legacy}")
    private String writeMode;  // legacy | dual | new

    @Override
    @Transactional
    public LegacyProductRegistrationResult execute(LegacyRegisterProductGroupCommand command) {
        // 1. luxurydb에 저장 (기존)
        LegacyProductRegistrationResult legacyResult = legacyCoordinator.register(command);

        if ("dual".equals(writeMode)) {
            try {
                // 2. 신 스키마에도 저장
                RegisterProductGroupCommand newCommand = commandMapper.toNewCommand(command);
                ProductGroupRegistrationResult newResult = newCoordinator.register(newCommand);

                // 3. ID 매핑 저장
                idResolver.saveMapping(legacyResult.productGroupId(), newResult.productGroupId());
            } catch (Exception e) {
                // Dual Write 실패 시 로그만 (luxurydb 저장은 유지)
                log.error("[DualWrite] 신 스키마 저장 실패. legacyId={}",
                         legacyResult.productGroupId(), e);
            }
        }

        return legacyResult;
    }
}
```

### 2-2. 트랜잭션 전략

luxurydb와 신 스키마는 **별도 DataSource**이므로 분산 트랜잭션 문제가 있습니다.

**권장 전략: 최종 일관성 (Eventually Consistent)**

```
1. luxurydb 저장 (메인 트랜잭션)
2. 신 스키마 저장 (별도 트랜잭션)
   - 실패 시: 보상 큐에 넣어서 재시도
   - 성공 시: ID 매핑 저장
```

luxurydb가 아직 Primary이므로, 신 스키마 저장 실패는 치명적이지 않습니다.
Outbox 스케줄러가 아직 동작 중이라면, 어차피 나중에 동기화됩니다.

### 2-3. Outbox 스케줄러 비활성화

Dual Write가 안정적으로 동작하면 Outbox 스케줄러를 비활성화:

```yaml
migration:
  product:
    write-mode: dual
    outbox-enabled: false    # Outbox 비활성화
  order:
    write-mode: dual
    outbox-enabled: false
```

### 2-4. 데이터 일치 검증

Dual Write 기간 동안 정기적으로 양쪽 DB 데이터를 비교:

```sql
-- 비교 쿼리 예시 (ID 매핑 테이블 기반)
SELECT m.legacy_id, m.new_id,
       l.price AS legacy_price, n.price AS new_price,
       l.display_yn AS legacy_display, n.display_yn AS new_display
FROM id_mapping m
JOIN luxurydb.product_group l ON l.product_group_id = m.legacy_id
JOIN new_schema.product_group n ON n.product_group_id = m.new_id
WHERE l.price != n.price OR l.display_yn != n.display_yn;
```

또는 기존 Shadow Runner(Python)의 YAML 테스트 케이스를 활용하여 자동 검증.

---

## Phase 3: 쓰기 전환

### 목표

luxurydb 쓰기를 제거하고, 신 스키마에만 저장.

### 3-1. 전환

```yaml
migration:
  product:
    write-mode: new        # luxurydb 쓰기 제거
    read-source: new
  order:
    write-mode: new
    read-source: new
```

이 시점에서:
- 쓰기: 신 스키마만
- 읽기: 신 스키마만
- luxurydb: 더 이상 사용하지 않음

### 3-2. luxurydb 읽기 전용 유지

전환 직후 바로 luxurydb를 제거하지 않고, **읽기 전용으로 유지**합니다.

```
기간: 전환 후 2~4주
용도: 문제 발생 시 데이터 확인/롤백 기반
접근: DBA만 직접 접근 가능
```

### 3-3. 모듈 정리

luxurydb 의존이 완전히 제거되면:

```
삭제 대상:
  - adapter-out/persistence-mysql-legacy (모듈 전체)
  - adapter-in/rest-api-legacy → rest-api로 통합 또는 유지
  - application/legacy*, legacyauth, legacyconversion, legacyseller, legacyshipment
  - bootstrap/bootstrap-legacy-api → bootstrap-web-api로 통합
  - adapter-out/client/legacy-auth-client

설정 정리:
  - persistence-legacy*.yml 제거
  - LegacyJpaConfig 제거
  - DMS 복제 중단
  - ID 매핑 테이블 아카이브
```

---

## 타임라인 (예상)

| 단계 | 기간 | 전제 |
|------|------|------|
| Step 1: Shadow 검증 + 운영 전환 | 현재 진행 중 | - |
| Phase 1: 읽기 전환 (shadow → new) | 2~3주 | Step 1 완료 |
| Phase 2: Dual Write | 2~3주 | Phase 1 안정 |
| Phase 3: 쓰기 전환 + luxurydb 제거 | 1~2주 | Phase 2 안정 |
| **전체** | **약 6~8주** (Step 1 완료 후) | - |

---

## 리스크 및 롤백 전략

| 리스크 | 대응 |
|--------|------|
| Phase 1 읽기 전환 후 응답 불일치 | Feature Flag로 즉시 `legacy`로 롤백 |
| Phase 2 Dual Write 시 신 스키마 저장 실패 | 로그 + 보상 큐 재시도. luxurydb는 정상 |
| Phase 2 데이터 불일치 발견 | Outbox 스케줄러 재활성화하여 보정 |
| Phase 3 전환 후 예상치 못한 문제 | luxurydb 읽기 전용 유지 중이므로 데이터 확인 가능. Flag로 `legacy`로 롤백 (2~4주 내) |

---

## 의존 관계

```
Step 1 (Shadow 검증)
  └── 불일치율 0%
       └── Gateway 운영 전환
            └── Phase 1 (읽기 전환)
                 ├── 응답 매퍼 구현
                 ├── Shadow 읽기 비교
                 └── Feature Flag 전환
                      └── Phase 2 (Dual Write)
                           ├── Command 매퍼 구현
                           ├── 분산 트랜잭션 처리
                           └── Outbox 비활성화
                                └── Phase 3 (쓰기 전환)
                                     ├── luxurydb 쓰기 제거
                                     ├── 모듈 정리
                                     └── DMS 중단
```
