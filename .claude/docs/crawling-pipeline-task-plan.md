# InboundProduct 크롤링 파이프라인 작업 계획서

> 작성일: 2026-02-25
> 상태: Ready for Execution
> 상위 문서: [inbound-product-refactoring-design.md](./inbound-product-refactoring-design.md)
> 목표: 크롤링 파이프라인 완성 — 비동기 변환 전환 + 레거시 경로 분리

---

## 현재 상태 요약

### 완료된 작업 (코드 확인 완료)

| 작업 | 파일 | 상태 |
|------|------|------|
| ReceiveInboundProductService 등록 전용 전환 | `ReceiveInboundProductService.java` | ✅ 완료 |
| RetryPendingMappingService 직접 처리 방식 | `RetryPendingMappingService.java` | ✅ 완료 |
| InboundProductUpdateCoordinator 정리 | `InboundProductUpdateCoordinator.java` | ✅ 완료 |

### 현재 등록 흐름 (동기 변환 — 변경 대상)

```
API → ReceiveInboundProductService.execute()
  → InboundProductRegisterCoordinator.register()
    ① factory.create(command)              → InboundProduct(RECEIVED)
    ② mappingResolver.resolveMappingAndApply()  → MAPPED / PENDING_MAPPING
    ③ conversionCoordinator.convert()      → FullProductGroupRegistrationCoordinator.register() ← 동기!
       → markConverted(productGroupId)     → CONVERTED
    ④ commandManager.persist()
    ⑤ return InboundProductConversionResult
```

**문제점**:
- 변환(ProductGroup 등록)이 API 응답 경로에서 동기 실행
- 크롤링은 productGroupId가 즉시 필요하지 않음 → 비동기 가능
- LEGACY 소스 분기가 InboundProductRegisterCoordinator에 남아 있음 (크롤링 전용인데 불필요)

### 변경 후 등록 흐름 (비동기 변환)

```
API → ReceiveInboundProductService.execute()
  → InboundProductRegisterCoordinator.register()
    ① factory.create(command)                    → InboundProduct(RECEIVED)
    ② mappingResolver.resolveMappingAndApply()    → MAPPED / PENDING_MAPPING
    ③ markPendingConversion()                    → PENDING_CONVERSION (변환 대기)
    ④ commandManager.persist()
    ⑤ return InboundProductConversionResult(pendingConversion)

InboundConversionScheduler (비동기, 주기적 폴링):
    ① PENDING_CONVERSION 상태 InboundProduct N건 조회
    ② conversionCoordinator.convert(product, now)
       → FullProductGroupRegistrationCoordinator.register()
       → markConverted(productGroupId)
    ③ commandManager.persist(product)
```

---

## 작업 목록

### Task 1: InboundProductStatus에 PENDING_CONVERSION 추가

**레이어**: Domain
**파일**: `domain/src/main/java/.../inboundproduct/vo/InboundProductStatus.java`

**변경 내용**:
1. `PENDING_CONVERSION("변환 대기")` enum 상수 추가
2. `isPendingConversion()` 메서드 추가
3. `isReadyForConversion()` → `MAPPED || CONVERT_FAILED` (CONVERT_FAILED도 재변환 대상)
4. `canMarkPendingConversion()` 전이 가드 추가: `MAPPED` 상태에서만 허용
5. JavaDoc 상태 머신 다이어그램 업데이트

**상태 머신 변경**:
```
변경 전: RECEIVED → MAPPED → CONVERTED
변경 후: RECEIVED → MAPPED → PENDING_CONVERSION → CONVERTED
                                    ↑
                            CONVERT_FAILED → (재시도)
```

**의존**: 없음 (독립)

---

### Task 2: InboundProduct에 markPendingConversion() 추가

**레이어**: Domain
**파일**: `domain/src/main/java/.../inboundproduct/aggregate/InboundProduct.java`

**변경 내용**:
1. `markPendingConversion(Instant now)` 메서드 추가
   - 상태 가드: `status.canMarkPendingConversion()` 체크
   - `status = PENDING_CONVERSION`
   - `updatedAt = now`
2. `isPendingConversion()` 편의 메서드 추가

**의존**: Task 1

---

### Task 3: InboundProductRegisterCoordinator 비동기 전환

**레이어**: Application
**파일**: `application/src/main/java/.../inboundproduct/internal/InboundProductRegisterCoordinator.java`

**현재 코드** (변경 대상):
```java
if (mapping.isFullyMapped()) {
    ExternalSourceType sourceType = conversionCoordinator.convert(newProduct, now);  // ← 제거
    if (newProduct.status().isConverted() && sourceType == ExternalSourceType.LEGACY) {  // ← 제거
        newProduct.assignExternalProductCode(...);  // ← 제거
    }
}
```

**변경 내용**:
1. `conversionCoordinator` 의존성 제거
2. `conversionCoordinator.convert()` 직접 호출 → `newProduct.markPendingConversion(now)` 로 변경
3. LEGACY 소스 분기 제거 (InboundProduct는 크롤링 전용)
4. 반환값 변경: CONVERTED → `pendingConversion` 결과 반환

**변경 후 코드**:
```java
if (mapping.isFullyMapped()) {
    newProduct.markPendingConversion(now);
}
commandManager.persist(newProduct);

if (newProduct.status().isPendingConversion()) {
    return InboundProductConversionResult.pendingConversion(newProduct.idValue());
}
return InboundProductConversionResult.pendingMapping(newProduct.idValue());
```

**의존**: Task 2

---

### Task 4: InboundProductConversionResult에 pendingConversion 추가

**레이어**: Application
**파일**: `application/src/main/java/.../inboundproduct/dto/response/InboundProductConversionResult.java`

**변경 내용**:
1. `pendingConversion(Long inboundProductId)` 팩토리 메서드 추가
2. 기존 `created()` / `convertFailed()` 유지 (스케줄러에서 사용)

**의존**: 없음 (독립)

---

### Task 5: InboundConversionScheduler 구현 (핵심)

**레이어**: Application (Service) + Adapter-In (Scheduler)
**신규 파일**:
- `application/src/main/java/.../inboundproduct/port/in/command/ConvertPendingInboundProductUseCase.java`
- `application/src/main/java/.../inboundproduct/service/command/ConvertPendingInboundProductService.java`
- `adapter-in/scheduler/src/main/java/.../scheduler/inboundproduct/InboundConversionScheduler.java`

**변경 파일**:
- `application/src/main/java/.../inboundproduct/manager/InboundProductReadManager.java` — 조회 메서드 추가
- `application/src/main/java/.../inboundproduct/port/out/query/InboundProductQueryPort.java` — 조회 포트 추가

#### 5-1. UseCase 인터페이스

```java
public interface ConvertPendingInboundProductUseCase {
    int execute();
}
```

#### 5-2. Service 구현

```java
@Service
public class ConvertPendingInboundProductService implements ConvertPendingInboundProductUseCase {
    private static final int CONVERSION_BATCH_SIZE = 50;

    // 의존: readManager, conversionCoordinator, commandManager

    @Override
    public int execute() {
        List<InboundProduct> pendingProducts =
            readManager.findPendingConversionProducts(CONVERSION_BATCH_SIZE);

        int successCount = 0;
        for (InboundProduct product : pendingProducts) {
            try {
                conversionCoordinator.convert(product, Instant.now());
                commandManager.persist(product);
                successCount++;
            } catch (Exception e) {
                // product.markConvertFailed() 은 conversionCoordinator 내부에서 처리
                commandManager.persist(product);
            }
        }
        return successCount;
    }
}
```

#### 5-3. 스케줄러

```java
@Component
@ConditionalOnProperty(
    prefix = "scheduler.jobs.inbound-product-conversion",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true)
public class InboundConversionScheduler {

    private final ConvertPendingInboundProductUseCase convertUseCase;

    @Scheduled(cron = "${scheduler.jobs.inbound-product-conversion.cron:0 */5 * * * *}")
    public void convertPendingProducts() {
        int converted = convertUseCase.execute();
        if (converted > 0) {
            log.info("InboundProduct 비동기 변환 완료: count={}", converted);
        }
    }
}
```

**의존**: Task 1, Task 2, Task 6

---

### Task 6: Persistence 변경 — PENDING_CONVERSION 조회

**레이어**: Adapter-Out (Persistence)

**변경 파일**:
- `InboundProductQueryPort.java` — `findPendingConversionProducts(int limit)` 추가
- `InboundProductReadManager.java` — 위임 메서드 추가
- `InboundProductJpaRepository.java` — `findTop50ByStatusOrderByCreatedAtAsc(PENDING_CONVERSION)` 쿼리 추가
- `InboundProductQueryAdapter.java` — 포트 구현 추가

**의존**: Task 1

---

### Task 7: RetryPendingMappingService 업데이트

**레이어**: Application
**파일**: `application/src/main/java/.../inboundproduct/service/command/RetryPendingMappingService.java`

**현재 코드** (변경 대상):
```java
private void retryMapping(InboundProduct product) {
    InboundProductMappingResult mapping = mappingResolver.resolveMappingAndApply(product, now);
    if (mapping.isFullyMapped()) {
        conversionCoordinator.convert(product, now);  // ← 동기 변환
    }
    commandManager.persist(product);
}
```

**변경 내용**:
1. `conversionCoordinator` 의존성 제거
2. 매핑 성공 시 `product.markPendingConversion(now)` 호출
3. 변환은 InboundConversionScheduler가 처리

**변경 후 코드**:
```java
private void retryMapping(InboundProduct product) {
    InboundProductMappingResult mapping = mappingResolver.resolveMappingAndApply(product, now);
    if (mapping.isFullyMapped()) {
        product.markPendingConversion(now);  // ← 비동기 전환
    }
    commandManager.persist(product);
}
```

**의존**: Task 2

---

### Task 8: RetryConvertFailedService 업데이트

**레이어**: Application
**파일**: `application/src/main/java/.../inboundproduct/service/command/RetryConvertFailedService.java`

**현재 코드**:
```java
private void retryConversion(InboundProduct product) {
    conversionCoordinator.convert(product, now);  // ← 직접 변환
    commandManager.persist(product);
}
```

**변경 방향 검토**:

두 가지 선택지:
- **A: PENDING_CONVERSION으로 재설정** → InboundConversionScheduler가 처리 (통일된 경로)
- **B: 현재처럼 직접 변환 유지** → 실패 상품은 즉시 재시도 (스케줄러 대기 없음)

**권장: A** — 변환 경로를 InboundConversionScheduler로 통일하여 단일 책임.

**변경 내용**:
1. `conversionCoordinator` 의존성 제거
2. CONVERT_FAILED → PENDING_CONVERSION으로 상태 재설정
3. InboundConversionScheduler가 통합 처리

**변경 후 코드**:
```java
private void retryConversion(InboundProduct product) {
    product.markPendingConversion(Instant.now());  // CONVERT_FAILED → PENDING_CONVERSION
    commandManager.persist(product);
}
```

> 이를 위해 Task 1에서 `canMarkPendingConversion()`의 허용 상태에 `CONVERT_FAILED`도 추가 필요.

**의존**: Task 1, Task 2

---

### Task 9: InboundProductConversionCoordinator 정리

**레이어**: Application
**파일**: `application/src/main/java/.../inboundproduct/internal/InboundProductConversionCoordinator.java`

**변경 내용**:
- InboundConversionScheduler(ConvertPendingInboundProductService)에서만 호출됨
- **LEGACY 소스 특수 처리 없음** (크롤링 전용)
- ExternalSourceType 반환값 → void로 변경 가능 (호출부에서 LEGACY 분기가 사라졌으므로)

**검토**: convert() 메서드에서 ExternalSourceType을 반환하는 이유는 RegisterCoordinator에서 LEGACY 분기 때문이었음. LEGACY 분기 제거 후 반환 타입을 void로 단순화.

**의존**: Task 3 (RegisterCoordinator 변경 후)

---

### Task 10: 레거시 전용 코드 분리 정리

**레이어**: Domain + Application

**변경 내용**:

1. **InboundProductStatus에서 레거시 관련 메서드 검토**:
   - `isLegacyImported()` — 유지 (enum 값은 DB에 존재할 수 있으므로)
   - `canRouteToInternal()` — 크롤링에서는 사용하지 않음. 레거시 라우팅용이므로 유지
   - `requiresLegacyFallback()` — 크롤링에서는 사용하지 않음. 레거시 라우팅용이므로 유지

2. **InboundProduct.assignExternalProductCode()** — 레거시 등록 후 productGroupId 할당용.
   크롤링에서는 사용하지 않지만, 레거시 경로에서 여전히 필요할 수 있으므로 유지.

> 결론: 현 단계에서 삭제할 코드는 없음. Task 3에서 RegisterCoordinator의 LEGACY 분기만 제거하면 충분.

**의존**: Task 3

---

### Task 11: 스케줄러 설정

**레이어**: Bootstrap
**파일**: `bootstrap/bootstrap-web-api/src/main/resources/application.yml` (또는 worker)

**변경 내용**:
```yaml
scheduler:
  jobs:
    inbound-product-conversion:
      enabled: true
      cron: "0 */5 * * * *"    # 5분마다
```

> 기존 `inbound-product-retry` 스케줄러 설정 참고하여 동일 패턴으로 추가.

**의존**: Task 5

---

### Task 12: 테스트

**12-1. 도메인 단위 테스트**
- InboundProductStatus: PENDING_CONVERSION 상태 전이 검증
- InboundProduct: markPendingConversion() 성공/실패 케이스

**12-2. Application 단위 테스트**
- InboundProductRegisterCoordinator: 매핑 성공 시 PENDING_CONVERSION 반환 검증
- ConvertPendingInboundProductService: 배치 처리 + 성공/실패 검증
- RetryPendingMappingService: 매핑 성공 → PENDING_CONVERSION 확인
- RetryConvertFailedService: CONVERT_FAILED → PENDING_CONVERSION 재설정 확인

**12-3. 빌드 검증**
- `./gradlew build` 전체 통과 확인
- 기존 테스트 regression 확인

**의존**: Task 1~11 전체

---

## 실행 순서 (의존관계 반영)

```
Phase A: 도메인 변경 (독립)
  Task 1 ── InboundProductStatus PENDING_CONVERSION 추가
  Task 2 ── InboundProduct markPendingConversion() 추가  ← Task 1 이후
  Task 4 ── InboundProductConversionResult pendingConversion 추가 (독립)

Phase B: Persistence (Task 1 이후)
  Task 6 ── PENDING_CONVERSION 조회 쿼리 추가

Phase C: Application 변경 (Task 2 이후)
  Task 3 ── InboundProductRegisterCoordinator 비동기 전환
  Task 7 ── RetryPendingMappingService 업데이트
  Task 8 ── RetryConvertFailedService 업데이트
  Task 9 ── InboundProductConversionCoordinator 정리  ← Task 3 이후

Phase D: 스케줄러 (Task 6 이후)
  Task 5 ── InboundConversionScheduler 구현
  Task 11 ── 스케줄러 설정 추가

Phase E: 정리 + 테스트
  Task 10 ── 레거시 코드 분리 검토
  Task 12 ── 테스트 작성 + 빌드 검증
```

### 병렬 실행 가능 그룹

```
[Group 1 — 독립 작업]
  Task 1 + Task 4 (병렬 가능)

[Group 2 — Task 1 완료 후]
  Task 2 + Task 6 (병렬 가능)

[Group 3 — Task 2 완료 후]
  Task 3 + Task 7 + Task 8 (병렬 가능)

[Group 4 — Group 3 + Task 6 완료 후]
  Task 5 + Task 9 (병렬 가능)

[Group 5 — 최종]
  Task 10 + Task 11 + Task 12
```

---

## 리스크 및 주의사항

### 1. InboundProductConversionCoordinator.convert()의 CONVERT_FAILED 처리

현재 `convert()` 내부에서 예외 발생 시 `product.markConvertFailed(now)`를 호출한다.
InboundConversionScheduler에서 이를 호출할 때도 동일하게 동작하므로 변경 불필요.

### 2. 기존 RetryConvertFailedService와 InboundConversionScheduler의 역할 구분

| 서비스 | 대상 | 역할 |
|--------|------|------|
| InboundConversionScheduler | PENDING_CONVERSION | 정상 변환 대기 상품 처리 |
| RetryConvertFailedService | CONVERT_FAILED (retryCount < 3) | 실패 상품 → PENDING_CONVERSION 재설정 |

RetryConvertFailedService는 **상태 재설정만** 담당하고, 실제 변환은 InboundConversionScheduler가 통합 처리.

### 3. 트랜잭션 경계

InboundConversionScheduler의 각 상품 변환은 개별 트랜잭션으로 처리해야 함.
한 건 실패가 나머지에 영향을 주지 않도록 상품별 try-catch 필수.

### 4. canMarkPendingConversion() 허용 상태

`MAPPED`와 `CONVERT_FAILED` 두 상태에서 PENDING_CONVERSION으로 전이 가능해야 함:
- MAPPED → PENDING_CONVERSION: 정상 등록 흐름
- CONVERT_FAILED → PENDING_CONVERSION: 재시도 흐름

---

## 완료 기준

- [ ] PENDING_CONVERSION 상태가 InboundProductStatus에 추가됨
- [ ] 등록 API 호출 시 변환이 비동기로 처리됨 (PENDING_CONVERSION 반환)
- [ ] InboundConversionScheduler가 PENDING_CONVERSION 상품을 배치 변환
- [ ] RetryPendingMappingService가 매핑 성공 시 PENDING_CONVERSION으로 전이
- [ ] RetryConvertFailedService가 CONVERT_FAILED를 PENDING_CONVERSION으로 재설정
- [ ] 기존 모든 테스트 통과
- [ ] 전체 빌드 성공
