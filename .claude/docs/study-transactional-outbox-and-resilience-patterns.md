# Transactional Outbox 패턴 & 메시지 복원력 — 면접 대비 학습 자료

> 작성일: 2026-03-04
> 출처: 리디, 강남언니, 29CM, 카카오페이, Uber 기술블로그

---

## 목차

1. [왜 메시지 발행이 어려운가 — 5가지 문제](#1-왜-메시지-발행이-어려운가--5가지-문제)
2. [Transactional Outbox 패턴](#2-transactional-outbox-패턴)
3. [리디 — Polling Publisher 구현과 운영](#3-리디--polling-publisher-구현과-운영)
4. [29CM — 컨슈머 멱등성 구현](#4-29cm--컨슈머-멱등성-구현)
5. [강남언니 — 이벤트 저장소의 이중 역할](#5-강남언니--이벤트-저장소의-이중-역할)
6. [카카오페이 — 이벤트 드리븐 적재적소](#6-카카오페이--이벤트-드리븐-적재적소)
7. [Uber — Retry Topic + DLQ 분리](#7-uber--retry-topic--dlq-분리)
8. [DLQ 처리 전략 비교](#8-dlq-처리-전략-비교)
9. [서킷 브레이커와의 관계](#9-서킷-브레이커와의-관계)
10. [회사별 패턴 비교표](#10-회사별-패턴-비교표)
11. [면접 예상 질문과 답변](#11-면접-예상-질문과-답변)

---

## 1. 왜 메시지 발행이 어려운가 — 5가지 문제

> 출처: 강남언니 기술블로그

분산 시스템에서 DB와 메시지 브로커는 서로 다른 저장소이므로 **원자적 처리가 불가능**합니다.

### 문제 1: 발행되어야 할 메시지가 누락

```
상품 저장 → DB 커밋 ✅ → 메시지 발행 ❌ (네트워크 오류)
→ 검색 시스템에 상품 미반영
```

### 문제 2: 발행되면 안 될 메시지가 발행

```
메시지 발행 ✅ → DB 커밋 ❌ (롤백)
→ 존재하지 않는 상품의 메시지가 검색 시스템에 전달
```

### 문제 3: 커밋 지연으로 인한 불일치

```
메시지 발행 → 컨슈머가 상품 조회 → 아직 커밋 안 됨 → 조회 실패
```

### 문제 4: 순서 보장 실패

```
상태 변경: ON → OFF → ON
메시지 도착 순서: ON, ON, OFF (네트워크 지연으로 역전)
→ 최종 상태 OFF (실제로는 ON이어야 함)
```

### 문제 5: 배치 보상 처리의 한계

```
전체 데이터를 주기적으로 동기화 → 자원 낭비, 실시간성 상실
```

**핵심 인사이트**: 메시지를 트랜잭션 안에 넣으면 문제 2가 발생하고, 밖에 넣으면 문제 1이 발생합니다. 이걸 해결하는 것이 Transactional Outbox 패턴입니다.

---

## 2. Transactional Outbox 패턴

### 3가지 원칙 조합

```
① DB 트랜잭션 원자성 → 비즈니스 로직 + 메시지 저장을 한 트랜잭션으로
② At-Least Once Delivery → 성공할 때까지 발행 재시도
③ Idempotent Consumer → 중복 수신해도 결과 동일
```

### Producer/Consumer 역할 분리

```
Producer 책임: 메시지가 최소 1번 전달됨을 보장 (At-Least Once)
Consumer 책임: 중복 수신 시 멱등하게 처리 (Exactly-Once Processing)
```

### 기본 코드 패턴

```java
// Producer 쪽 — 같은 트랜잭션
@Transactional
public void registerProduct(RegisterProduct command) {
    var product = Product.create(command);
    repository.save(product);                         // 비즈니스 데이터
    outbox.save(ProductRegistered.from(product));      // 같은 트랜잭션
}

// 스케줄러 — 폴링으로 발행
@Scheduled(fixedRate = 1000)
public void publishMessages() {
    var messages = outbox.read(10);
    publisher.publish(messages);
    outbox.delete(messages);
}
```

### 구현 방식 2가지

| 방식 | 원리 | 장점 | 단점 |
|------|------|------|------|
| **Polling Publisher** | Outbox 테이블을 주기적으로 SELECT | 구현 단순, 빠른 도입 | DB 폴링 부하, 락 경합 |
| **Transaction Log Tailing** | MySQL binlog CDC (Debezium) | 폴링 없음, 실시간 | CDC 도구 학습/운영 비용 |

---

## 3. 리디 — Polling Publisher 구현과 운영

> 출처: https://ridicorp.com/story/transactional-outbox-pattern-ridi/

### 도입 배경

기존에 DB 트랜잭션 완료 후 Kafka에 메시지를 발행하는 구조. 발행 실패 시 `dead_letter_queue` DB 테이블에 저장하고 배치로 재시도 → 지연과 순서 보장 문제.

### 스키마 설계 — 락 경합 해결

```sql
-- 발행 대기 메시지
CREATE TABLE message (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  topic VARCHAR NOT NULL,
  payload LONGTEXT NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 처리 완료 기록 (DELETE 대신 INSERT로 락 경합 회피)
CREATE TABLE processed_message (
  id BIGINT PRIMARY KEY  -- message.id 참조
);
```

**설계 진화**:
- 초기: message 테이블에서 직접 UPDATE/DELETE → INSERT와 DELETE가 같은 id 범위에서 **deadlock** 발생
- 개선: `processed_message` 테이블 분리 → INSERT/DELETE를 물리적으로 분리

### 동시성 제어 — 이중 락

```
Redis 분산 락 (redlock) → 노드 간 중복 폴링 방지
    ↓
MySQL 레코드 락 (SELECT FOR UPDATE) → 레코드 수준 보호
```

```typescript
const relayMessages = async () => {
  const lock = await redlock.lock(redisLock.key, redisLock.ttlInMs);
  if (!lock) return { messages: [] };

  return await db.transaction(async (trx) => {
    const messages = await queries.findMessagesToPublish(BATCH_SIZE);
    // ... 발행 로직
    await queries.insertProcessedMessages(processedMessageIds).transacting(trx);
    return { messages, processedMessageIds };
  });
};
```

### 메시지 3가지 분류

| 결과 | 처리 | 예시 |
|------|------|------|
| 성공 | `processed_message`에 INSERT | Kafka 발행 성공 |
| 재시도 가능 | 다음 폴링에서 재시도 | 일시적 Kafka 장애 |
| 스킵 (영구 실패) | `processed_message`에 INSERT | 잘못된 JSON, 없는 토픽 |

### 삭제 최적화 — id 마진

```typescript
const maxIdToDelete = Math.max(...processedIds) - MAX_ID_MARGIN_TO_DELETE;
// 최근 1000개는 남기고 그 이전 것만 삭제 → INSERT/DELETE 락 경합 완화
```

### 모니터링

```typescript
// 메시지 지연 감지 (60초 이상이면 경고)
const elapsed = now - message.created_at;
if (elapsed >= 60_000) {
  logError(new Error('message-relay processing elapsed.'));
}
```

- Datadog APM으로 처리량, 에러율, 레이턴시 추적
- processed_message 테이블 행 수 모니터링
- 임계치 초과 시 Slack 알림

### 안전한 종료 (Graceful Shutdown)

```typescript
let shouldTerminate = false;
let jobOngoing = false;

const terminate = async () => {
  shouldTerminate = true;
  while (jobOngoing) {
    await delay(1000);  // 현재 폴링 루프 완료 대기
  }
  process.exit(1);
};
```

배포 시 현재 폴링 루프가 완료될 때까지 대기 → 메시지 손실 방지.

### 핵심 교훈

- Polling Publisher는 구현이 단순하지만 **운영에서 성능 튜닝이 많이 필요**
- 초기 배포 후 deadlock, lock wait 다수 발생 → processed_message 분리, id 마진으로 개선
- CDC(Debezium) 대비 장단점 트레이드오프를 명확히 인지하고 선택

---

## 4. 29CM — 컨슈머 멱등성 구현

> 출처: https://medium.com/@greg.shiny82/트랜잭셔널-아웃박스-패턴의-실제-구현-사례-29cm-0f822fc23edb

### Producer 쪽 (Outbox)

```
비즈니스 로직 + Outbox 저장 → 같은 트랜잭션
Outbox 폴러가 published=false인 메시지를 Kafka에 발행
발행 성공 → published=true
배치로 오래된 published=false 재시도
```

### Consumer 쪽 — CheckDuplicatedMessageService

```java
@KafkaListener(topics = "point-cancel")
public void onMessage(PointCancelEvent event) {
    checkDuplicatedMessageService.check(event);  // 중복 체크
    pointService.cancel(event);                  // 비즈니스 로직
}
```

```java
@Service
public class CheckDuplicatedMessageService {
    private final PointEventOutboxRepository repository;

    public void check(PointCancelEvent event) {
        if (repository.existsByEventId(event.getEventId())) {
            throw new DuplicatedMessageException();  // 이미 처리된 메시지
        }
    }
}
```

### 트랜잭션으로 멱등성 보장

```
비즈니스 로직 실행 + PROCESSED_MESSAGE INSERT → 같은 트랜잭션
PROCESSED_MESSAGE.event_id에 UNIQUE INDEX
→ 중복 메시지가 오면 UNIQUE 제약조건 위반 → 롤백
```

### 핵심 원칙

```
Producer = At-Least Once Delivery (최소 1회 전달 보장)
Consumer = Idempotent Processing (멱등 처리로 중복 안전)
→ 결과적으로 Exactly-Once Processing 달성
```

---

## 5. 강남언니 — 이벤트 저장소의 이중 역할

> 출처: https://blog.gangnamunni.com/post/transactional-outbox

### 도메인 이벤트 저장소 = Event Store + Outbox

```java
@Transactional
public void update(UUID id, Consumer<Campaign> modifier) {
    Campaign campaign = dataModel.toEntity();
    modifier.accept(campaign);               // 상태 변경
    dataModel.update(campaign);              // DB 반영
    eventStore.store(campaign.getEvents());  // 이벤트 저장 + 발행 대기
}
```

이벤트 저장소가 두 가지 역할을 동시에 수행:
1. **기록 (Event Store)**: 비즈니스 사건을 영구 기록
2. **발신함 (Outbox)**: 미발행 이벤트를 주기적으로 발행

### At-Least Once vs Exactly-Once

| 전략 | 발행자 | 소비자 | 처리량 |
|------|--------|--------|--------|
| **At-Most Once** | 1번만 시도, 실패 시 포기 | 단순 | 높음 |
| **At-Least Once** | 성공할 때까지 재시도 | 멱등 처리 필요 | 높음 |
| **Exactly-Once** | 2PC 등 복잡한 프로토콜 | 단순 | 낮음 |

> "Transactional Outbox 패턴을 사용하지 않고 발행 시스템에 Exactly-Once Delivery를 구현할 수도 있으나, 시간당 처리량이 감소합니다."

### 모든 메시지에 적용할 필요 없음

- 메시지 누락이 문제없는 경우 → At-Most Once로 자원 절약
- 강한 일관성이 필요한 경우 → 동기 호출이 나을 수 있음

---

## 6. 카카오페이 — 이벤트 드리븐 적재적소

> 출처: https://tech.kakaopay.com/post/event-driven-architecture/

이 글은 Outbox 패턴이 아니라 **이벤트 드리븐을 언제 써야 하고 언제 쓰면 안 되는지**에 대한 글입니다.

### 장점 (실전 경험)

- **쉬운 비동기 전환**: `@Async` 하나로 전체 핸들러 비동기화
- **의존성 분리**: 상위→하위 이벤트 전파에서 의존관계 미발생
- **확장성**: 메시지 큐 기반 분산 시스템으로 전환 용이

### 단점 (실전 경험)

**로직 추적 어려움**:
> "이벤트 트리의 부모에서 자식 방향으로 전파되는 과정을 따라가는 것이 쉽지 않다"

**트랜잭션 관리 복잡**:
> "과도한 의존성 Decoupling과 비동기 동작으로 인해 트랜잭션을 관리하는 것이 어려워진다"

**네이밍 혼란**:
> "PostSlackMessage와 같이 진행형 능동태로 작성하면 직관적이지 않아 클래스명을 자주 잊어버린다"

### 적용 판단 기준

| 상황 | 이벤트 드리븐 | 직접 호출 |
|------|-------------|---------|
| 구성요소 5개 이상 | O | |
| 비동기 처리 필요 | O | |
| 팀원 학습 비용 고려 | | O |
| 단순한 구조 | | O |
| 분산 시스템 전환 예정 | O | |

### 핵심 메시지

이벤트 드리븐은 만능이 아닙니다. 디버깅 어려움과 트랜잭션 관리 복잡도가 대가이며, "적재적소"에 사용하는 판단이 더 중요합니다.

---

## 7. Uber — Retry Topic + DLQ 분리

> 출처: https://www.uber.com/blog/reliable-reprocessing/

### 문제 — 실패 메시지가 배치를 막는다

```
메시지 A (실패) → 재시도 → 재시도 → 재시도...
메시지 B, C, D (정상) → 대기 중 (A가 끝날 때까지 처리 불가)
```

클라이언트 레벨 재시도는 **하나의 실패 메시지가 전체 파이프라인을 블로킹**합니다.

### 해결 — Retry Topic 계층 구조

```
Original Topic → Consumer
                   ├─ 성공 → 완료
                   └─ 실패 → Retry Topic 1로 발행 + 원본 오프셋 커밋
                              ├─ 성공 → 완료
                              └─ 실패 → Retry Topic 2로 발행
                                          └─ 실패 → DLQ Topic
```

핵심: 실패 메시지를 **별도 토픽으로 이동** + 원본 토픽에서 **오프셋 커밋** → 정상 메시지 블로킹 없음.

### Exponential Backoff — Leaky Bucket

Kafka에는 delayed delivery 기능이 없으므로, **컨슈머가 메시지를 꺼낸 후 blocking wait**:

```
Retry Topic 1: 즉시 처리
Retry Topic 2: 처리 전 N초 대기
Retry Topic 3: 처리 전 N*2초 대기
→ DLQ: 최종 포기
```

### 에러 분류 — Transient vs Permanent

| 에러 유형 | 예시 | 처리 |
|----------|------|------|
| 일시적 (Transient) | 네트워크 타임아웃, 서비스 일시 장애 | Retry Topic으로 이동 |
| 영구적 (Permanent) | NullPointerException, 코드 버그 | 즉시 DLQ로 (재시도 무의미) |

### DLQ 3가지 연산

| 연산 | 설명 |
|------|------|
| **Listing** | DLQ 내용 조회 (CLI 도구, 오프셋 추적) |
| **Purging** | 처리 불필요한 메시지 삭제 |
| **Merging** | DLQ → Retry Topic 1로 재발행 (수정 후 재처리) |

### 트레이드오프

| 장점 | 단점 |
|------|------|
| 정상 메시지 블로킹 없음 | 데이터 타입 많으면 토픽 수 폭발 |
| 장애 격리 | 각 retry topic 컨슈머 그룹 관리 |
| 관찰성 높음 (토픽별 추적) | blocking wait으로 컨슈머 자원 점유 |

**대안**: 토픽 수가 너무 많아지면 retry 메타데이터를 이벤트 필드에 포함 → 하나의 retry 토픽으로 통합.

---

## 8. DLQ 처리 전략 비교

### DB 테이블 기반 DLQ (리디 방식)

```
실패 메시지 → dead_letter_queue DB 테이블에 INSERT
배치 프로세스가 주기적으로 재시도
```

| 장점 | 단점 |
|------|------|
| 이력 추적 용이 (SQL 조회) | DB 폴링 부하 |
| 구현 단순 | 실시간성 부족 |
| 메시지 브로커 독립적 | 테이블 크기 관리 필요 |

### 메시지 큐 기반 DLQ (Uber, SQS 방식)

```
실패 메시지 → DLQ 토픽/큐로 자동 이동
DLQ 컨슈머 또는 수동 재드라이브로 처리
```

| 장점 | 단점 |
|------|------|
| 자동 이동 (코드 불필요) | 이력 추적 어려움 |
| 실시간 알림 (CloudWatch) | 재드라이브 도구 필요 |
| 메인 큐 오염 없음 | 멱등성 이중 관리 문제 |

### 애플리케이션 레벨 Outbox (현재 MarketPlace 방식)

```
실패 → Outbox 상태를 FAILED로 변경 (retryable 플래그로 분기)
수동 retry() API로 FAILED → PENDING 전환
```

| 장점 | 단점 |
|------|------|
| 별도 DLQ 인프라 불필요 | FAILED 건 수동 재처리 |
| retryable 플래그로 세밀한 분기 | 스케줄러 주기에 의존 |
| 서킷 브레이커와 호환 (deferRetry) | |
| 전체 이력 DB에 보존 | |

---

## 9. 서킷 브레이커와의 관계

### SQS DLQ + 서킷 브레이커 = 충돌 가능

```
서킷 브레이커 OPEN (30초간)
  → SQS visibility timeout 만료 → 메시지 재시도
  → 서킷 여전히 OPEN → 실패
  → maxReceiveCount 소진 → DLQ로 이동
  → 실제로는 복구 가능했는데 DLQ로 빠짐
```

### Outbox + 서킷 브레이커 = 호환

```
서킷 브레이커 OPEN
  → ExternalServiceUnavailableException 발생
  → outbox.deferRetry(now) — retryCount 소진 안 함, PENDING 복귀
  → 서킷 CLOSED 후 다음 스케줄러 주기에서 재시도
```

서킷 브레이커와 조합할 때는 **retryCount를 소진하지 않는 deferRetry** 메커니즘이 핵심입니다.

---

## 10. 회사별 패턴 비교표

| | 리디 | 강남언니 | 29CM | 카카오페이 | Uber |
|---|------|---------|------|----------|------|
| **메시지 브로커** | Kafka | 미명시 | Kafka | Spring Event | Kafka |
| **Outbox 방식** | Polling Publisher | Polling Publisher | Polling Publisher | 이벤트 저장소 | Retry Topic |
| **DLQ 위치** | DB 테이블 | 미명시 | 미명시 | 미적용 | Kafka Topic |
| **멱등성** | Redis+DB 이중 락 | 컨슈머 멱등 처리 | PROCESSED_MESSAGE 테이블 | 미명시 | 컨슈머 멱등성 요구 |
| **동시성 제어** | Redis+MySQL 이중 락 | 미명시 | 미명시 | @Async | 파티션 기반 |
| **실패 분류** | 성공/재시도/스킵 | At-Least Once | At-Least Once | 미적용 | Transient/Permanent |
| **핵심 교훈** | 락 경합 해결 | 5가지 문제 정의 | 컨슈머 멱등성 | 적재적소 판단 | 블로킹 방지 |

---

## 11. 면접 예상 질문과 답변

### Q1: "Transactional Outbox 패턴이 뭔가요?"

> "DB 트랜잭션과 메시지 발행의 원자성을 보장하는 패턴입니다. 비즈니스 로직과 메시지를 같은 트랜잭션으로 Outbox 테이블에 저장하고, 별도 폴러가 At-Least Once로 발행합니다. 컨슈머는 멱등하게 구현해서 중복 수신을 안전하게 처리합니다."

### Q2: "왜 필요한가요? 그냥 트랜잭션 후에 메시지 보내면 안 되나요?"

> "5가지 문제가 있습니다. 첫째 발행 실패 시 메시지 누락, 둘째 발행 후 롤백 시 잘못된 메시지 전달, 셋째 커밋 지연으로 인한 불일치, 넷째 순서 역전, 다섯째 배치 보상의 비효율입니다. Outbox 패턴은 DB 원자성을 활용해서 이 문제들을 해결합니다."

### Q3: "Polling Publisher vs CDC(Debezium) 중 뭘 선택하나요?"

> "리디는 Polling Publisher를 선택했습니다. 구현이 단순하고 빠르게 도입할 수 있지만, DB 폴링 부하와 락 경합이 있습니다. CDC는 폴링 없이 실시간이지만 Debezium 같은 도구의 학습/운영 비용이 있습니다. 팀 규모와 운영 역량에 따라 결정해야 합니다."

### Q4: "멱등성은 어떻게 보장하나요?"

> "29CM 사례가 좋은 예입니다. PROCESSED_MESSAGE 테이블에 유니크 인덱스를 걸고, 비즈니스 로직 실행과 메시지 처리 기록을 같은 트랜잭션으로 묶습니다. 중복 메시지가 오면 유니크 제약조건에 걸려서 롤백됩니다. 리디는 여기에 Redis 분산 락까지 추가해서 폴러 자체의 중복 실행도 방지했습니다."

### Q5: "DLQ는 어떻게 처리하나요?"

> "크게 세 가지 접근이 있습니다. 리디는 DB 테이블 기반 DLQ를 썼고, Uber는 Kafka DLQ 토픽을 분리했습니다. 저희 프로젝트는 Outbox 상태 머신(FAILED 상태)으로 DLQ 역할을 대체합니다. DB 기반은 이력 추적이 쉽고, 토픽 기반은 실시간 알림이 쉽지만 재드라이브 도구가 필요합니다."

### Q6: "SQS DLQ를 안 쓰는 이유가 있나요?"

> "외부 API 호출 시 서킷 브레이커와의 호환 문제가 있습니다. 서킷이 OPEN인 동안 SQS가 maxReceiveCount를 소진해서 DLQ로 빠질 수 있습니다. 실제로는 복구 가능한 상황인데요. 저희는 deferRetry로 retryCount를 소진하지 않고 PENDING으로 복귀시켜서 이 문제를 해결합니다. 또한 Outbox와 SQS 양쪽에서 상태를 관리하면 멱등성 이중 관리 문제도 발생합니다."

### Q7: "모든 메시지에 이 패턴을 적용해야 하나요?"

> "아닙니다. 카카오페이 사례처럼 '적재적소'가 중요합니다. 메시지 누락이 괜찮으면 At-Most Once로 충분하고, 강한 일관성이 필요하면 동기 호출이 나을 수 있습니다. Outbox 패턴은 '최종적 일관성(Eventual Consistency)'이 허용되고 메시지 유실이 허용되지 않는 경우에 적합합니다."

### Q8: "Uber의 Retry Topic 방식은 뭐가 다른가요?"

> "클라이언트 레벨 재시도는 실패 메시지가 정상 메시지를 블로킹합니다. Uber는 실패 메시지를 별도 Kafka retry topic으로 이동시키고 원본 오프셋을 커밋합니다. 영구적 에러(코드 버그)와 일시적 에러(네트워크)를 구분해서, 영구적 에러는 즉시 DLQ로 보내 재시도 낭비를 방지합니다. 단점은 데이터 타입이 많으면 토픽 수가 폭발한다는 것입니다."

### Q9: "At-Least Once와 Exactly-Once의 차이는?"

> "At-Least Once는 메시지가 최소 1번 전달됨을 보장합니다. 네트워크 오류 등으로 같은 메시지가 여러 번 올 수 있습니다. Exactly-Once Delivery는 정확히 1번 전달이지만 2PC 같은 복잡한 프로토콜이 필요하고 처리량이 떨어집니다. 실무에서는 At-Least Once + Idempotent Consumer 조합으로 'Exactly-Once Processing'을 달성하는 것이 표준입니다."

### Q10: "실제 프로젝트에서 어떻게 적용했나요?"

> "외부 커머스 API(네이버, 세토프) 상품 동기화에 Outbox 패턴을 사용합니다. 스케줄러가 3분마다 PENDING 건을 SQS로 발행하고, Strategy 패턴으로 채널별 실행을 분기합니다. DomainException(매핑 없음 등)은 retryable=false로 즉시 FAILED, 인프라 오류는 retryable=true로 3회 재시도합니다. 서킷 브레이커 OPEN 시에는 deferRetry로 retryCount를 소진하지 않습니다."

---

## 참고 링크

- [리디 - Transactional Outbox 패턴으로 메시지 발행 보장하기](https://ridicorp.com/story/transactional-outbox-pattern-ridi/)
- [강남언니 - 분산 시스템에서 메시지 안전하게 다루기](https://blog.gangnamunni.com/post/transactional-outbox)
- [29CM - 트랜잭셔널 아웃박스 패턴의 실제 구현 사례](https://medium.com/@greg.shiny82/트랜잭셔널-아웃박스-패턴의-실제-구현-사례-29cm-0f822fc23edb)
- [카카오페이 - 이벤트 드리븐 적재적소에 사용하기](https://tech.kakaopay.com/post/event-driven-architecture/)
- [Uber - Building Reliable Reprocessing and Dead Letter Queues with Kafka](https://www.uber.com/blog/reliable-reprocessing/)
