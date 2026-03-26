# Circuit Breaker 구현 계획서

## 1. 왜 Circuit Breaker를 쓰는가

외부 서비스(네이버 커머스, FileFlow)가 장애를 일으키면, 우리 서비스가 계속 요청을 보내면서 **응답을 기다리는 스레드가 쌓인다**.
이게 반복되면 스레드 풀이 고갈되고, 결국 외부 서비스와 무관한 내부 기능까지 멈추는 **장애 전파(Cascading Failure)** 가 발생한다.

Circuit Breaker는 이걸 막는 패턴이다:
- **외부가 죽었으면 빠르게 실패**시키고 (fail-fast)
- 스레드를 잡고 있지 않게 하고
- 일정 시간 후 복구됐는지 확인한다

---

## 2. 상태 머신 (State Machine)

```
CLOSED (정상)
  │  요청 통과, 성공/실패를 기록
  │  실패율 > threshold → OPEN
  ▼
OPEN (차단)
  │  모든 요청 즉시 거부 (CallNotPermittedException)
  │  waitDuration 경과 → HALF_OPEN
  ▼
HALF_OPEN (복구 확인)
  │  제한된 수의 요청만 통과
  │  성공 → CLOSED / 실패 → OPEN
```

### 왜 이 3개 상태인가?

- **CLOSED**: 정상 상태. 모든 요청이 통과하면서 결과를 sliding window에 기록한다.
- **OPEN**: 장애 감지됨. 요청을 보내봤자 실패할 거니까 보내지 않는다. `CallNotPermittedException`을 즉시 던져서 스레드를 해방한다.
- **HALF_OPEN**: "진짜 복구됐나?" 확인하는 상태. 소수의 요청만 통과시켜 성공하면 CLOSED로 복귀한다.

---

## 3. 핵심 설정값과 선택 근거

### 3.1 slidingWindowType: COUNT_BASED vs TIME_BASED

| 타입 | 설명 | 적합한 상황 |
|------|------|------------|
| COUNT_BASED | 최근 N건 기준 | 트래픽이 일정한 배치/스케줄러 |
| TIME_BASED | 최근 N초 기준 | 트래픽 변동이 큰 실시간 API |

**우리 선택: COUNT_BASED**
- 네이버 연동은 스케줄러/SQS Worker가 배치로 처리 → 트래픽 일정
- FileFlow도 이미지 업로드 스케줄러가 배치 처리

### 3.2 slidingWindowSize: 왜 특정 숫자인가?

**핵심 원칙**: 통계적으로 유의미한 샘플이면서, 판정이 너무 느리지 않아야 한다.

| 값 | 장점 | 단점 |
|----|------|------|
| 10 (현재 FileFlow) | 빠른 판정 | 1~2건 실패로 쉽게 OPEN → 오탐 |
| 50 | 균형 | - |
| 100 | 정확한 판정 | 판정까지 100건 필요 → 느림 |

**우리 선택**:
- **네이버**: `20` — SQS Worker가 동시에 여러 건 처리하므로 20건이면 판정에 충분. 너무 크면 429 폭탄 맞는 동안 OPEN 안 됨.
- **FileFlow**: `20` — 기존 10에서 상향. 10건은 너무 적어서 간헐적 네트워크 에러 2~3건에 오탐 발생 가능.

### 3.3 failureRateThreshold: 50%

**왜 50%?**
- 50% 미만(예: 30%): 너무 민감. 일시적 네트워크 지터에도 OPEN
- 50% 초과(예: 70%): 너무 둔감. 장애 상황인데 계속 요청 보냄
- 50%는 "반 이상 실패하면 장애로 본다"는 합리적 기준

### 3.4 slowCallDurationThreshold & slowCallRateThreshold

**이게 왜 필요한가?**

타임아웃까지 안 걸리지만 3~5초씩 느린 응답이 계속 오는 상황.
이때 실패는 아니지만, 스레드가 오래 잡히면서 풀이 고갈된다.
실패율만 보면 "정상"인데 실제로는 서비스가 느려지는 상태.

```
외부 API 응답: 200 OK (5초 걸림) → 성공이지만 slow call
외부 API 응답: 200 OK (5초 걸림) → 또 성공이지만 slow call
... 반복 → 스레드 풀 고갈 → 장애
```

**설정**:
- `slowCallDurationThreshold`: 3초 (네이버 API read timeout 30초의 1/10)
- `slowCallRateThreshold`: 80% (80% 이상이 slow이면 OPEN)

### 3.5 waitDurationInOpenState: 60초

OPEN 상태에서 HALF_OPEN으로 전이하기까지 대기 시간.

**왜 60초?**
- 네이버 Rate Limit: 보통 1분 내 해제됨
- FileFlow 서버 장애: 30초는 너무 짧음. 복구 중인데 HALF_OPEN 진입하면 또 실패
- 60초면 대부분의 일시적 장애가 해소되는 시간

### 3.6 permittedNumberOfCallsInHalfOpenState: 5

HALF_OPEN에서 복구 확인용으로 통과시킬 요청 수.

**왜 5?**
- 3건: 1건 실패로 다시 OPEN → 복구 판정이 불안정
- 10건: 복구 확인에 너무 많은 요청 소모
- 5건: "5건 중 과반수 성공하면 복구됐다"는 합리적 수준

### 3.7 minimumNumberOfCalls: 10

CB 판정 시작 전 최소 호출 수.

**왜 10?**
- slidingWindowSize(20)의 절반
- 서비스 시작 직후 1~2건 실패로 OPEN되는 콜드스타트 문제 방지

---

## 4. 예외 구분 전략

### 원칙: 외부 서비스 장애만 CB에 기록

```
recordExceptions → 외부 서비스 장애 (5xx, 429, 타임아웃)
ignoreExceptions → 클라이언트 잘못 (400, 404)
무시             → 내부 비즈니스 예외 (IllegalArgumentException 등)
```

### 왜 이렇게 구분하는가?

- **400 Bad Request**: 우리가 잘못된 데이터를 보낸 것. 외부 서비스는 정상. CB 기록하면 오탐.
- **404 Not Found**: 리소스가 없는 것. 서비스 장애 아님.
- **429 Rate Limit**: 외부 서비스가 요청 제한. 계속 보내면 더 악화됨. CB로 차단해야 함.
- **5xx Server Error**: 외부 서비스 장애. CB 기록 대상.
- **타임아웃**: 외부 서비스 응답 불가. CB 기록 대상.

### 네이버 예외 계층 설계

```
NaverCommerceException (base - RuntimeException)
├── NaverCommerceServerException      (5xx)  → recordExceptions ✓
├── NaverCommerceRateLimitException   (429)  → recordExceptions ✓
├── NaverCommerceBadRequestException  (400)  → ignoreExceptions ✓
└── NaverCommerceClientException      (기타 4xx) → ignoreExceptions ✓
```

### FileFlow 예외 (기존 SDK 제공 — 변경 불필요)

```
FileFlowException (base)
├── FileFlowServerException      (5xx)  → recordExceptions ✓ (기존)
└── FileFlowBadRequestException  (400)  → ignoreExceptions ✓ (기존)
```

---

## 5. Retry + Circuit Breaker 조합

### 조합 순서가 중요하다

```
요청 → [CircuitBreaker] → [Retry] → 외부 API
        (바깥)              (안쪽)
```

**왜 CB가 바깥이어야 하는가?**

Retry가 바깥이면:
```
Retry 1회차 → CB에 실패 기록
Retry 2회차 → CB에 실패 기록
Retry 3회차 → CB에 실패 기록
→ CB 입장에서는 3건 실패 (실제로는 1건에 대한 3회 재시도)
→ CB가 너무 빨리 OPEN됨
```

CB가 바깥이면:
```
CB → Retry 1회차 → 실패
     Retry 2회차 → 실패
     Retry 3회차 → 실패
→ CB 입장에서는 1건 실패 (Retry가 다 실패한 후 1건으로 기록)
→ CB 판정이 정확함
```

### 현재 프로젝트에서의 Retry

**네이버 OutboundSync**: Outbox 패턴이 Retry 역할을 대체한다.
- Outbox가 PENDING → PROCESSING → 실패 → PENDING (retryCount 증가)
- 스케줄러 주기마다 재시도 = Retry와 동일한 효과
- 따라서 **Adapter 레벨에서 별도 Retry는 넣지 않는다**

대신 CB OPEN 시 `ExternalServiceUnavailableException` → `deferRetry` (retryCount 안 깎음)

### 왜 deferRetry가 필요한가?

```
상황: 네이버 서버 장애 (5분간)
- deferRetry 없이: 매 주기 실패 → retryCount++ → 3회 소진 → FAILED (영구 실패)
- deferRetry 있으면: CB OPEN → retryCount 안 깎임 → 복구 후 정상 처리
```

**핵심**: CB OPEN은 "외부가 장애"라는 뜻이므로, 우리 Outbox의 retry 횟수를 깎으면 안 된다.

---

## 6. Fallback 전략

### 서비스 특성별 Fallback

| 상황 | Fallback | 이유 |
|------|----------|------|
| 네이버 상품 등록 (CB OPEN) | deferRetry → PENDING 복귀 | 등록은 반드시 성공해야 함. 캐시 응답 불가. |
| 네이버 상품 수정 (CB OPEN) | deferRetry → PENDING 복귀 | 수정도 반드시 반영되어야 함. |
| 네이버 상품 삭제 (CB OPEN) | deferRetry → PENDING 복귀 | 삭제 미반영은 판매 사고. |
| FileFlow 다운로드 (CB OPEN) | deferRetry → PENDING 복귀 | 이미지 업로드 지연은 허용 가능. |
| 네이버 상품 목록 조회 (CB OPEN) | 빈 목록 반환 + 로그 | 조회는 재시도 불필요. 다음 주기에 자연스럽게 재조회. |

### 우리 Fallback 패턴: Outbox + deferRetry

```
CB OPEN
  → ExternalServiceUnavailableException 발생
  → Outbox Processor가 catch
  → outbox.deferRetry(now)  // retryCount 안 깎음, PENDING 복귀
  → 다음 스케줄러 주기에 재처리
  → CB가 HALF_OPEN → CLOSED 되면 정상 처리
```

이것이 면접에서 말한 "Outbox 저장 → 복구 후 비동기 재처리" 패턴이다.

---

## 7. 메트릭 수집 (Micrometer + Prometheus + AMP + AMG)

### 왜 메트릭이 필요한가

CB가 OPEN/CLOSED를 오가는데 아무도 모르면, 장애 감지가 늦어진다.
메트릭을 수집해서:
- CB 상태 변화를 실시간 모니터링
- 실패율 추세를 파악
- OPEN 빈도가 높으면 외부 서비스 품질 이슈 확인

### 수집 구조

```
Spring App (Micrometer)
  → /actuator/prometheus (Prometheus 형식 메트릭 노출)
  → ADOT Collector (ECS sidecar, 이미 구성됨)
  → Amazon Managed Prometheus (AMP, remote write)
  → Amazon Managed Grafana (AMG, 대시보드)
```

### 수집할 메트릭

Resilience4j는 `resilience4j-micrometer` 모듈로 자동 메트릭을 제공한다:

| 메트릭 | 설명 | 용도 |
|--------|------|------|
| `resilience4j_circuitbreaker_state` | CB 상태 (0=CLOSED, 1=OPEN, 2=HALF_OPEN) | 상태 변화 감지 |
| `resilience4j_circuitbreaker_calls_seconds` | 호출당 소요 시간 (성공/실패/무시/차단) | 응답 시간 추세 |
| `resilience4j_circuitbreaker_failure_rate` | 현재 실패률 (%) | 장애 임계점 접근 감지 |
| `resilience4j_circuitbreaker_slow_call_rate` | Slow call 비율 (%) | 성능 저하 감지 |
| `resilience4j_circuitbreaker_not_permitted_calls_total` | OPEN 상태에서 차단된 호출 수 | 장애 영향 범위 파악 |
| `resilience4j_circuitbreaker_buffered_calls` | 슬라이딩 윈도우 내 호출 수 | 트래픽 추세 |

### Grafana 대시보드 패널 구성

1. **CB 상태 (Stat Panel)**: 현재 CLOSED/OPEN/HALF_OPEN
2. **실패율 추세 (Time Series)**: 시간대별 failure_rate, slow_call_rate
3. **호출 분포 (Stacked Bar)**: 성공/실패/무시/차단 비율
4. **응답 시간 히트맵 (Heatmap)**: P50, P95, P99

---

## 8. 구현 계획 (Task 분해)

### Phase 1: 네이버 예외 계층 + ResponseErrorHandler

| 순서 | 작업 | 모듈 |
|------|------|------|
| 1-1 | NaverCommerceException 예외 계층 생성 | naver-commerce-client |
| 1-2 | NaverCommerceResponseErrorHandler 구현 | naver-commerce-client |
| 1-3 | RestClient에 errorHandler 등록 | naver-commerce-client |

### Phase 2: Circuit Breaker 적용

| 순서 | 작업 | 모듈 |
|------|------|------|
| 2-1 | NaverCommerceCircuitBreakerConfig 생성 | naver-commerce-client |
| 2-2 | NaverCommerceProductClientAdapter에 CB 적용 | naver-commerce-client |
| 2-3 | NaverCommerceOrderClientAdapter에 CB 적용 | naver-commerce-client |
| 2-4 | NaverCommerceImageClientAdapter에 CB 적용 | naver-commerce-client |
| 2-5 | FileFlowCircuitBreakerConfig 설정값 조정 | fileflow-client |
| 2-6 | FileFlowCircuitBreakerConfig에 slowCall 설정 추가 | fileflow-client |

### Phase 3: Outbox deferRetry 패턴 적용

| 순서 | 작업 | 모듈 |
|------|------|------|
| 3-1 | OutboundSyncOutbox에 deferRetry() 메서드 추가 | domain |
| 3-2 | ExecuteOutboundSyncService에 ExternalServiceUnavailableException 처리 추가 | application |
| 3-3 | NaverXxxStrategy에서 CB 예외 → ExternalServiceUnavailableException 변환 | application |

### Phase 4: 메트릭 수집

| 순서 | 작업 | 모듈 |
|------|------|------|
| 4-1 | libs.versions.toml에 resilience4j-micrometer 의존성 추가 | gradle |
| 4-2 | naver-commerce-client, fileflow-client에 micrometer 의존성 추가 | build.gradle |
| 4-3 | CircuitBreakerConfig에 MeterRegistry 바인딩 | naver/fileflow config |
| 4-4 | application.yml에 resilience4j 메트릭 태그 설정 | bootstrap |

---

## 9. 설정값 요약

### 네이버 Circuit Breaker

```java
CircuitBreakerConfig.custom()
    .failureRateThreshold(50)
    .slowCallDurationThreshold(Duration.ofSeconds(3))
    .slowCallRateThreshold(80)
    .slidingWindowType(SlidingWindowType.COUNT_BASED)
    .slidingWindowSize(20)
    .minimumNumberOfCalls(10)
    .permittedNumberOfCallsInHalfOpenState(5)
    .waitDurationInOpenState(Duration.ofSeconds(60))
    .recordExceptions(
        NaverCommerceServerException.class,
        NaverCommerceRateLimitException.class)
    .ignoreExceptions(
        NaverCommerceBadRequestException.class,
        NaverCommerceClientException.class)
    .build();
```

### FileFlow Circuit Breaker (변경)

```java
// 변경 전                          // 변경 후
slidingWindowSize: 10        →     20
minimumNumberOfCalls: 5      →     10
permittedCallsInHalfOpen: 3  →     5
waitDurationInOpenState: 30s →     60s
// 추가
slowCallDurationThreshold: 3s
slowCallRateThreshold: 80%
```

---

## 10. 파일 변경 목록

### 새로 생성

| 파일 | 설명 |
|------|------|
| `naver/.../exception/NaverCommerceException.java` | 기본 예외 |
| `naver/.../exception/NaverCommerceServerException.java` | 5xx 예외 |
| `naver/.../exception/NaverCommerceRateLimitException.java` | 429 예외 |
| `naver/.../exception/NaverCommerceBadRequestException.java` | 400 예외 |
| `naver/.../exception/NaverCommerceClientException.java` | 기타 4xx 예외 |
| `naver/.../config/NaverCommerceResponseErrorHandler.java` | HTTP 상태별 예외 변환 |
| `naver/.../config/NaverCommerceCircuitBreakerConfig.java` | CB 설정 |

### 수정

| 파일 | 변경 내용 |
|------|----------|
| `naver/.../config/NaverCommerceClientConfig.java` | errorHandler 등록 |
| `naver/.../adapter/NaverCommerceProductClientAdapter.java` | CB 적용 |
| `naver/.../adapter/NaverCommerceOrderClientAdapter.java` | CB 적용 |
| `naver/.../adapter/NaverCommerceImageClientAdapter.java` | CB 적용 |
| `fileflow/.../config/FileFlowCircuitBreakerConfig.java` | 설정값 조정 + slowCall 추가 |
| `domain/.../outboundsync/aggregate/OutboundSyncOutbox.java` | deferRetry() 추가 |
| `application/.../outboundsync/service/command/ExecuteOutboundSyncService.java` | ExternalServiceUnavailableException 처리 |
| `gradle/libs.versions.toml` | resilience4j-micrometer 추가 |
| `naver-commerce-client/build.gradle` | resilience4j, micrometer 의존성 |
| `fileflow-client/build.gradle` | micrometer 의존성 추가 |
