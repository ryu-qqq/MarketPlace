package com.ryuqq.marketplace.integration.oms;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.OutboundSyncOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.entity.OutboundSyncOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundsync.repository.OutboundSyncOutboxJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * retrySyncHistory API E2E 통합 테스트.
 *
 * <p>테스트 대상: POST /oms/sync-history/{outboxId}/retry
 *
 * <p>기능 흐름: RetryOutboundSyncService → Outbox 조회(getById) → retry() → persist → 200 OK + ACCEPTED
 * 응답
 *
 * <p>StubExternalClientConfig에서 OutboundSyncOutboxQueryPort와 OutboundSyncOutboxCommandPort가 실제 DB
 * 어댑터로 등록되어 있으므로, 별도의 override 없이 실제 DB 동작을 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("oms")
@Tag("command")
@DisplayName("retrySyncHistory API E2E 테스트")
class RetrySyncHistoryE2ETest extends E2ETestBase {

    private static final String RETRY_URL = "/oms/sync-history/{outboxId}/retry";

    @Autowired private OutboundSyncOutboxJpaRepository syncOutboxRepository;

    @BeforeEach
    void setUp() {
        syncOutboxRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        syncOutboxRepository.deleteAll();
    }

    // ========================================================================
    // 1. POST /oms/sync-history/{outboxId}/retry - 정상 케이스
    // ========================================================================

    @Nested
    @DisplayName("POST /oms/sync-history/{outboxId}/retry - 정상 케이스")
    class RetrySyncHistorySuccessTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1-S01] FAILED 상태 Outbox를 retry 하면 200 OK와 ACCEPTED 응답을 반환한다")
        void retrySyncHistory_failedOutbox_returns200WithAccepted() {
            // given: FAILED 상태의 Outbox를 DB에 저장
            OutboundSyncOutboxJpaEntity failedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity());
            long outboxId = failedEntity.getId();

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.outboxId", equalTo((int) outboxId))
                    .body("data.status", equalTo("ACCEPTED"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-S02] retry 후 DB에서 상태가 PENDING으로 변경되어야 한다")
        void retrySyncHistory_failedOutbox_statusChangesToPendingInDb() {
            // given: FAILED 상태의 Outbox를 DB에 저장
            OutboundSyncOutboxJpaEntity failedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity());
            long outboxId = failedEntity.getId();

            // when: retry 호출
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // then: DB에서 상태가 PENDING으로 변경되었는지 확인
            var updated =
                    syncOutboxRepository
                            .findById(outboxId)
                            .orElseThrow(
                                    () ->
                                            new AssertionError(
                                                    "Outbox를 찾을 수 없습니다. outboxId=" + outboxId));
            assertThat(updated.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-S03] retry 후 DB에서 retryCount가 0으로 초기화되어야 한다")
        void retrySyncHistory_failedOutbox_retryCountResetToZeroInDb() {
            // given: FAILED 상태의 Outbox (retryCount = maxRetry = 3)
            OutboundSyncOutboxJpaEntity failedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity());
            long outboxId = failedEntity.getId();

            // 저장 전 retryCount 확인
            assertThat(failedEntity.getRetryCount()).isEqualTo(failedEntity.getMaxRetry());

            // when: retry 호출
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // then: retryCount가 0으로 초기화되었는지 확인
            var updated =
                    syncOutboxRepository
                            .findById(outboxId)
                            .orElseThrow(
                                    () ->
                                            new AssertionError(
                                                    "Outbox를 찾을 수 없습니다. outboxId=" + outboxId));
            assertThat(updated.getRetryCount()).isEqualTo(0);
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-S04] retry 후 DB에서 errorMessage가 null로 초기화되어야 한다")
        void retrySyncHistory_failedOutbox_errorMessageClearedInDb() {
            // given: FAILED 상태의 Outbox (errorMessage 있음)
            OutboundSyncOutboxJpaEntity failedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity());
            long outboxId = failedEntity.getId();

            // 저장 전 errorMessage 확인
            assertThat(failedEntity.getErrorMessage()).isNotNull();

            // when: retry 호출
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // then: errorMessage가 null로 초기화되었는지 확인
            var updated =
                    syncOutboxRepository
                            .findById(outboxId)
                            .orElseThrow(
                                    () ->
                                            new AssertionError(
                                                    "Outbox를 찾을 수 없습니다. outboxId=" + outboxId));
            assertThat(updated.getErrorMessage()).isNull();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-S05] 응답 본문에 outboxId와 status 필드가 포함되어야 한다")
        void retrySyncHistory_failedOutbox_responseContainsRequiredFields() {
            // given
            OutboundSyncOutboxJpaEntity failedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity());
            long outboxId = failedEntity.getId();

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.outboxId", notNullValue())
                    .body("data.status", notNullValue())
                    .body("data.outboxId", equalTo((int) outboxId))
                    .body("data.status", equalTo("ACCEPTED"));
        }
    }

    // ========================================================================
    // 2. POST /oms/sync-history/{outboxId}/retry - 실패 케이스: 비정상 상태
    // ========================================================================

    @Nested
    @DisplayName("POST /oms/sync-history/{outboxId}/retry - 비정상 상태 Outbox")
    class RetrySyncHistoryInvalidStatusTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2-F01] PENDING 상태 Outbox를 retry 하면 에러 응답을 반환한다")
        void retrySyncHistory_pendingOutbox_returnsError() {
            // given: PENDING 상태의 Outbox 저장
            OutboundSyncOutboxJpaEntity pendingEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntity());
            long outboxId = pendingEntity.getId();

            // when & then: PENDING 상태는 retry 불가 (도메인 규칙: isFailed() 검사)
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(not(equalTo(HttpStatus.OK.value())));
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-F02] PROCESSING 상태 Outbox를 retry 하면 에러 응답을 반환한다")
        void retrySyncHistory_processingOutbox_returnsError() {
            // given: PROCESSING 상태의 Outbox 저장
            OutboundSyncOutboxJpaEntity processingEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newProcessingEntity());
            long outboxId = processingEntity.getId();

            // when & then: PROCESSING 상태는 retry 불가 (도메인 규칙: isFailed() 검사)
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(not(equalTo(HttpStatus.OK.value())));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C2-F03] COMPLETED 상태 Outbox를 retry 하면 에러 응답을 반환한다")
        void retrySyncHistory_completedOutbox_returnsError() {
            // given: COMPLETED 상태의 Outbox 저장
            OutboundSyncOutboxJpaEntity completedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newCompletedEntity());
            long outboxId = completedEntity.getId();

            // when & then: COMPLETED 상태는 retry 불가 (도메인 규칙: isFailed() 검사)
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(not(equalTo(HttpStatus.OK.value())));
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-F04] PENDING 상태 retry 시도 후 DB 상태가 변경되지 않아야 한다")
        void retrySyncHistory_pendingOutbox_dbStateUnchanged() {
            // given: PENDING 상태의 Outbox 저장
            OutboundSyncOutboxJpaEntity pendingEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntity());
            long outboxId = pendingEntity.getId();

            // when: 에러 발생하는 retry 호출
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(not(equalTo(HttpStatus.OK.value())));

            // then: DB 상태가 여전히 PENDING이어야 한다
            var unchanged =
                    syncOutboxRepository
                            .findById(outboxId)
                            .orElseThrow(
                                    () ->
                                            new AssertionError(
                                                    "Outbox를 찾을 수 없습니다. outboxId=" + outboxId));
            assertThat(unchanged.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.PENDING);
        }
    }

    // ========================================================================
    // 3. POST /oms/sync-history/{outboxId}/retry - 실패 케이스: 존재하지 않는 ID
    // ========================================================================

    @Nested
    @DisplayName("POST /oms/sync-history/{outboxId}/retry - 존재하지 않는 outboxId")
    class RetrySyncHistoryNotFoundTest {

        @Test
        @Tag("P0")
        @DisplayName("[C3-F01] 존재하지 않는 outboxId로 retry 하면 에러 응답을 반환한다")
        void retrySyncHistory_nonExistingOutboxId_returnsError() {
            // given: DB에 데이터 없음
            long nonExistingOutboxId = 99999L;

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, nonExistingOutboxId)
                    .then()
                    .statusCode(not(equalTo(HttpStatus.OK.value())));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C3-F02] 존재하지 않는 outboxId retry 시 DB에 변경이 없어야 한다")
        void retrySyncHistory_nonExistingOutboxId_dbUnchanged() {
            // given: DB에 다른 FAILED Outbox 저장 (영향을 받지 않아야 함)
            OutboundSyncOutboxJpaEntity otherFailedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity());
            long nonExistingOutboxId = 99999L;
            long otherOutboxId = otherFailedEntity.getId();

            // when: 존재하지 않는 ID로 retry 호출
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, nonExistingOutboxId)
                    .then()
                    .statusCode(not(equalTo(HttpStatus.OK.value())));

            // then: 다른 Outbox는 FAILED 상태 그대로여야 한다
            var unchanged =
                    syncOutboxRepository
                            .findById(otherOutboxId)
                            .orElseThrow(
                                    () ->
                                            new AssertionError(
                                                    "Outbox를 찾을 수 없습니다. outboxId="
                                                            + otherOutboxId));
            assertThat(unchanged.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.FAILED);
        }
    }

    // ========================================================================
    // 4. POST /oms/sync-history/{outboxId}/retry - 인증 테스트
    // ========================================================================

    @Nested
    @DisplayName("POST /oms/sync-history/{outboxId}/retry - 인증 테스트")
    class RetrySyncHistoryAuthTest {

        @Test
        @Tag("P0")
        @DisplayName("[C4-A01] 비인증 요청 시 401 반환")
        void retrySyncHistory_unauthenticated_returns401() {
            // given
            OutboundSyncOutboxJpaEntity failedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity());
            long outboxId = failedEntity.getId();

            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C4-A02] 인증된 사용자는 retry를 호출할 수 있다")
        void retrySyncHistory_authenticatedUser_canCallRetry() {
            // given
            OutboundSyncOutboxJpaEntity failedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity());
            long outboxId = failedEntity.getId();

            // when & then: authenticated() 통과 → 200 OK
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }

    // ========================================================================
    // 5. 전체 플로우 시나리오
    // ========================================================================

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] FAILED 상태 Outbox를 retry 후 DB에서 PENDING 상태로 변경되는 전체 플로우")
        void fullFlow_failedOutbox_retryThenVerifyPendingInDb() {
            // Step 1: FAILED 상태 Outbox 저장
            OutboundSyncOutboxJpaEntity failedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity());
            long outboxId = failedEntity.getId();

            // 초기 상태 검증
            assertThat(failedEntity.getStatus())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.Status.FAILED);
            assertThat(failedEntity.getRetryCount()).isEqualTo(failedEntity.getMaxRetry());
            assertThat(failedEntity.getErrorMessage()).isNotNull();

            // Step 2: retry API 호출
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, outboxId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.outboxId", equalTo((int) outboxId))
                    .body("data.status", equalTo("ACCEPTED"));

            // Step 3: DB 최종 상태 검증
            var finalState =
                    syncOutboxRepository
                            .findById(outboxId)
                            .orElseThrow(
                                    () ->
                                            new AssertionError(
                                                    "retry 후 Outbox를 찾을 수 없습니다. outboxId="
                                                            + outboxId));

            assertThat(finalState.getStatus())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.Status.PENDING);
            assertThat(finalState.getRetryCount()).isEqualTo(0);
            assertThat(finalState.getErrorMessage()).isNull();
        }

        @Test
        @Tag("P1")
        @DisplayName("[F2] 여러 Outbox 중 FAILED 상태만 retry 가능한 플로우")
        void fullFlow_multipleOutboxes_onlyFailedCanRetry() {
            // Step 1: 다양한 상태의 Outbox 저장
            OutboundSyncOutboxJpaEntity failedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newFailedEntity());
            OutboundSyncOutboxJpaEntity pendingEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newPendingEntity());
            OutboundSyncOutboxJpaEntity completedEntity =
                    syncOutboxRepository.save(
                            OutboundSyncOutboxJpaEntityFixtures.newCompletedEntity());

            // Step 2: FAILED Outbox retry → 성공 (200 OK)
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, failedEntity.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", equalTo("ACCEPTED"));

            // Step 3: PENDING Outbox retry → 실패 (에러)
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, pendingEntity.getId())
                    .then()
                    .statusCode(not(equalTo(HttpStatus.OK.value())));

            // Step 4: COMPLETED Outbox retry → 실패 (에러)
            given().spec(givenAuthenticatedUser())
                    .when()
                    .post(RETRY_URL, completedEntity.getId())
                    .then()
                    .statusCode(not(equalTo(HttpStatus.OK.value())));

            // Step 5: DB 최종 상태 검증 - FAILED → PENDING, 나머지는 변경 없음
            var retried = syncOutboxRepository.findById(failedEntity.getId()).orElseThrow();
            var untouchedPending =
                    syncOutboxRepository.findById(pendingEntity.getId()).orElseThrow();
            var untouchedCompleted =
                    syncOutboxRepository.findById(completedEntity.getId()).orElseThrow();

            assertThat(retried.getStatus()).isEqualTo(OutboundSyncOutboxJpaEntity.Status.PENDING);
            assertThat(untouchedPending.getStatus())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.Status.PENDING);
            assertThat(untouchedCompleted.getStatus())
                    .isEqualTo(OutboundSyncOutboxJpaEntity.Status.COMPLETED);
        }
    }
}
