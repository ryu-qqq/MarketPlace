# AI Review Summary - PR #37 (Unified: Gemini + CodeRabbit)

## Review Statistics

- **PR**: [#37 - 셀러 스코프 접근 제어 + OutboundSync/LegacyConversion 모듈](https://github.com/ryu-qqq/MarketPlace/pull/37)
- **Bots Analyzed**: Gemini Code Assist + CodeRabbit AI (2 bots)
- **Total Comments**: 30 (Gemini: 2, CodeRabbit: 28)
- **After Deduplication**: 28 unique issues (2 duplicates merged)
- **Review Date**: 2026-02-28

### Priority Distribution (After Voting System)

| Priority | Count | Description |
|----------|-------|-------------|
| **Critical** | 4 | Outbox 패턴 위반, Null 안전성, 데이터 정합성 |
| **Major** | 11 | NPE, 입력 검증, 트랜잭션 경계, 예외 처리 |
| **Nitpick** | 9 | 코드 품질, 성능 최적화, 테스트 개선 |
| **Suggestion** | 4 | 선택적 개선사항 |

### 2-Bot Consensus (중복 이슈 - 우선순위 자동 상향)

| File:Line | Gemini | CodeRabbit | Consensus Priority |
|-----------|--------|------------|-------------------|
| `OutboundSyncRelayProcessor.java:67` | CRITICAL | NITPICK | **Critical (2-bot)** |
| `LegacyConversionSeederScheduler.java:40` | MAJOR | NITPICK | **Major (2-bot)** |

---

## Critical Issues (Must-Fix) - 4 items

### 1. Outbox 패턴 위반: SQS 발행 후 즉시 COMPLETED 처리 [2-bot consensus]
**Location**: `OutboundSyncRelayProcessor.java:67`
**Flagged by**: Gemini (CRITICAL) + CodeRabbit (NITPICK)

**Problem**:
- `relay()` 메서드가 SQS 발행 후 컨슈머 처리 전에 Outbox를 `COMPLETED`로 변경
- 컨슈머 실패 시 작업 실패를 추적/재시도할 수 없음
- Outbox 패턴의 핵심 원칙(데이터 정합성) 위배

**Impact**: 데이터 정합성 손실, 메시지 처리 실패 추적 불가

**Fix**:
- `OutboundSyncRelayProcessor`: `PROCESSING` 전환 + SQS 발행만 수행
- `ExecuteOutboundSyncService` (컨슈머): 실제 작업 완료 후 `COMPLETED` 전환

**Effort**: 30분

---

### 2. Null 안전성 검증 누락 (record/DTO)
**Location**: `OutboundSyncSqsMessage.java:17`
**Flagged by**: CodeRabbit (CRITICAL)

**Problem**: SQS 메시지 record의 필수 필드(outboxId, productGroupId 등) null 검증 없음

**Fix**: compact constructor에서 `Objects.requireNonNull` 적용

**Effort**: 5분

---

### 3. SeedLegacyConversionService 데이터 정합성 위험
**Location**: `SeedLegacyConversionService.java:48`
**Flagged by**: CodeRabbit (CRITICAL)

**Problem**: 시딩 로직에서 중복 검증 및 원자성 보장 미흡

**Fix**: existsPending 체크와 persist를 단일 트랜잭션으로 보장

**Effort**: 15분

---

### 4. ProductGroupActivationOutboxCoordinator 동시성 이슈
**Location**: `ProductGroupActivationOutboxCoordinator.java:121`
**Flagged by**: CodeRabbit (CRITICAL)

**Problem**: Outbox 생성 시 동시 요청에 대한 중복 방지 미흡

**Fix**: 멱등성 보장 로직 추가 (findPendingByProductGroupId 체크)

**Effort**: 15분

---

## Major Issues (Should-Fix) - 11 items

### 1. 다중 인스턴스 환경에서 lastCursor 상태 공유 불가 [2-bot consensus]
**Location**: `LegacyConversionSeederScheduler.java:40`
**Flagged by**: Gemini (MAJOR) + CodeRabbit (NITPICK)

**Problem**:
- `lastCursor`가 인스턴스 변수(volatile)로 선언
- 다중 인스턴스 환경에서 각 인스턴스가 서로 다른 커서 값 보유
- 분산 락은 동시 실행만 막고, 다음 실행 시 상태 손실

**Fix**: `lastCursor` 값을 Redis 또는 DB에 중앙 관리

**Effort**: 45분

---

### 2. NPE 방지: `supports()` null 안전성
**Location**:
- `DescriptionImageCompletionStrategy.java:39`
- `ProductGroupImageCompletionStrategy.java:40`
**Flagged by**: CodeRabbit (MAJOR)

**Problem**: `sourceType`가 null이면 NPE 발생

**Fix**:
```java
@Override
public boolean supports(ImageSourceType sourceType) {
    return sourceType != null && sourceType.isDescriptionImage();
}
```

**Effort**: 5분

---

### 3. 설정 플래그 미반영
**Location**: `OutboundSyncOutboxScheduler.java:29`
**Flagged by**: CodeRabbit (MAJOR)

**Problem**: `recover-timeout.enabled=false`여도 스케줄 계속 실행

**Fix**: `@ConditionalOnProperty`로 recover-timeout 개별 제어

**Effort**: 10분

---

### 4. 빈 컬렉션 단락 처리 필요
**Location**: `LegacyConversionOutboxQueryDslRepository.java:107`
**Flagged by**: CodeRabbit (MAJOR)

**Problem**: 빈 ID 컬렉션에도 DB 조회 실행

**Fix**: `ids == null || ids.isEmpty()` 시 즉시 `Collections.emptySet()` 반환

**Effort**: 5분

---

### 5. 입력 값 검증 누락
**Location**: Multiple files
- `OutboundSyncOutboxQueryAdapter.java:56` (outboxId null)
- `OutboundSyncOutboxQueryDslRepository.java:67` (batchSize 상한/하한)
- `SeedLegacyConversionCommand.java:15` (batchSize, maxTotal)
**Flagged by**: CodeRabbit (MAJOR)

**Problem**: 잘못된 입력값이 그대로 유입

**Fix**: 포트 경계에서 null/범위 검증 적용

**Effort**: 20분

---

### 6. 트랜잭션 경계 위반 (Zero-Tolerance)
**Location**: `ExecuteOutboundSyncService.java:48`
**Flagged by**: CodeRabbit (MAJOR)

**Problem**: `@Transactional` 메서드 내 TODO로 외부 연동 표시

**Fix**: 외부 연동을 트랜잭션 밖으로 분리

**Effort**: 30분

---

### 7. 예외 삼킴 (Silent Failure)
**Location**: `ExecuteOutboundSyncService.java:66`
**Flagged by**: CodeRabbit (MAJOR)

**Problem**: 실패를 로그만 남기고 상위에 전파하지 않음

**Fix**: 예외를 비즈니스 예외로 래핑하여 상위 전파

**Effort**: 10분

---

### 8. 트랜잭션 경계 내 부분 실패 처리
**Location**: `RecoverTimeoutOutboundSyncService.java:66`
**Flagged by**: CodeRabbit (MAJOR)

**Problem**: 배치 처리 중 일부 실패 시 전체 롤백 위험

**Fix**: 개별 트랜잭션으로 분리 또는 성공/실패 분리 처리

**Effort**: 30분

---

### 9. ImageTransformResponse `isTerminal()` null-safety 불일치
**Location**: `ImageTransformResponse.java:61`
**Flagged by**: CodeRabbit (MAJOR)

**Problem**: `status` null 시 `isTerminal()` 동작 불일치

**Fix**: null 방어 로직 추가

**Effort**: 5분

---

### 10. LegacyConversionOutboxConditionBuilder 조건 누락
**Location**: `LegacyConversionOutboxConditionBuilder.java:51`
**Flagged by**: CodeRabbit (MAJOR)

**Problem**: 특정 조건 조합 시 예상치 못한 쿼리 생성 가능

**Fix**: 조건 빌더 보완

**Effort**: 10분

---

### 11. OutboundSyncPublishAdapter `queueUrl` null 체크 부재
**Location**: `OutboundSyncPublishAdapter.java:23`
**Flagged by**: CodeRabbit (MAJOR)

**Problem**: Bean 생성 시 queueUrl null이면 런타임 에러

**Fix**: 생성자에서 `Objects.requireNonNull(queueUrl)` 적용

**Effort**: 5분

---

## Nitpick (Nice-to-Have) - 9 items

| # | Location | Issue | Bot | Effort |
|---|----------|-------|-----|--------|
| 1 | `scheduler-prod.yml:100` | 운영 환경 `max-total: 0`(무제한) 위험 | CodeRabbit | 15분 |
| 2 | `LegacyConversionSeederLockManager.java:65` | SeederLockKey record 매번 생성 → static final | CodeRabbit | 5분 |
| 3 | `SeedLegacyConversionService.java:76` | 로깅/메트릭 개선 | CodeRabbit | 5분 |
| 4 | `ExecuteOutboundSyncCommand.java:17` | syncType String → Enum 전환 | CodeRabbit | 10분 |
| 5 | `ProcessPendingOutboundSyncCommand.java:19` | batchSize 양수 검증 | CodeRabbit | 5분 |
| 6 | `RecoverTimeoutOutboundSyncCommand.java:19` | 입력 값 검증 | CodeRabbit | 5분 |
| 7 | `BatchChangeProductGroupStatusServiceTest.java:35` | 소유권 예외 경로 Outbox 무호출 검증 | CodeRabbit | 10분 |
| 8 | `SellerAddress.java:150` | "Tell, Don't Ask" - 좋은 사례 (Praise) | CodeRabbit | - |
| 9 | `SqsClientProperties.java:51` | 설정 클래스 개선 | CodeRabbit | 5분 |

---

## Suggestion (Optional) - 4 items

| # | Location | Issue | Bot | Effort |
|---|----------|-------|-----|--------|
| 1 | `ProductGroupActivationOutboxCoordinator.java:105` | N+1 쿼리 성능 최적화 (배치 조회) | CodeRabbit | 20분 |
| 2 | `OutboundSyncRelayProcessor.java:67` | `recordFailure(true, ...)` 하드코딩 → `canRetry()` | CodeRabbit | 10분 |
| 3 | `ImageTransformResponse.java:61` | isTerminal() null-safety | CodeRabbit | 5분 |
| 4 | `LegacyConversionOutboxConditionBuilder.java:51` | 조건 빌더 개선 | CodeRabbit | 10분 |

---

## TodoList 권장사항

### High Priority (Must-Fix Today)
1. Outbox 패턴 수정: SQS 발행 후 COMPLETED 제거 (30분) [2-bot]
2. `lastCursor` Redis/DB 중앙 관리 (45분) [2-bot]
3. Null 안전성 검증 - OutboundSyncSqsMessage (5분)
4. SeedLegacyConversionService 데이터 정합성 (15분)
5. ProductGroupActivationOutboxCoordinator 멱등성 (15분)
6. 트랜잭션 경계 위반 수정 (30분) [Zero-Tolerance]

**Total**: ~2.5시간

### Medium Priority (Should-Fix This Week)
1. NPE 방지: ImageCompletionStrategy 2건 (5분)
2. 설정 플래그 미반영 수정 (10분)
3. 빈 컬렉션 단락 처리 (5분)
4. 입력 값 검증 3건 (20분)
5. 예외 삼킴 수정 (10분)
6. 트랜잭션 부분 실패 처리 (30분)
7. OutboundSyncPublishAdapter queueUrl null 체크 (5분)
8. ImageTransformResponse null-safety (5분)
9. LegacyConversionOutboxConditionBuilder (10분)

**Total**: ~1.5시간

### Low Priority (Nice-to-Have)
- Nitpick 9건 + Suggestion 4건은 선택적 개선

---

## AI Review Integration

**Bot Coverage**: 2/3 (Gemini + CodeRabbit)
- Gemini Code Assist: 2개 인라인 코멘트 (CRITICAL 1, MAJOR 1)
- CodeRabbit AI: 28개 인라인 코멘트 (CRITICAL 3, MAJOR 10, NITPICK 11, SUGGESTION 4)
- Codex Connector: 리뷰 없음

**Voting System Applied**:
- 2-bot consensus: 2건 (자동 우선순위 상향)
  - `OutboundSyncRelayProcessor.java:67` → Critical 유지 (Gemini CRITICAL + CodeRabbit NITPICK)
  - `LegacyConversionSeederScheduler.java:40` → Major 유지 (Gemini MAJOR + CodeRabbit NITPICK)
- 단독 봇 의견: 26건 (원래 severity 유지)

**Zero-Tolerance 위반 자동 감지**:
- `ExecuteOutboundSyncService.java:48` → @Transactional 내 외부 연동 (Auto Critical)

---

Generated by `/ai-review` command with Claude Code (Unified: Gemini + CodeRabbit)
Date: 2026-02-28
