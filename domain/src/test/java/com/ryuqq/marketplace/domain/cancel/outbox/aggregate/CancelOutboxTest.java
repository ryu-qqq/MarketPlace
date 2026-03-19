package com.ryuqq.marketplace.domain.cancel.outbox.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxStatus;
import com.ryuqq.marketplace.domain.cancel.outbox.vo.CancelOutboxType;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelOutbox Aggregate 단위 테스트")
class CancelOutboxTest {

    @Nested
    @DisplayName("forNew() - 신규 Outbox 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 Outbox는 PENDING 상태로 생성된다")
        void forNewCreatesPendingOutbox() {
            CancelOutbox outbox = CancelFixtures.newCancelOutbox();

            assertThat(outbox.status()).isEqualTo(CancelOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("신규 Outbox는 retryCount가 0이다")
        void forNewHasZeroRetryCount() {
            CancelOutbox outbox = CancelFixtures.newCancelOutbox();

            assertThat(outbox.retryCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("신규 Outbox는 maxRetry가 3이다")
        void forNewHasDefaultMaxRetry() {
            CancelOutbox outbox = CancelFixtures.newCancelOutbox();

            assertThat(outbox.maxRetry()).isEqualTo(3);
        }

        @Test
        @DisplayName("신규 Outbox는 isNew()가 true이다")
        void forNewOutboxIsNew() {
            CancelOutbox outbox = CancelFixtures.newCancelOutbox();

            assertThat(outbox.isNew()).isTrue();
        }

        @Test
        @DisplayName("신규 Outbox는 processedAt이 null이다")
        void forNewOutboxHasNullProcessedAt() {
            CancelOutbox outbox = CancelFixtures.newCancelOutbox();

            assertThat(outbox.processedAt()).isNull();
        }

        @Test
        @DisplayName("신규 Outbox는 errorMessage가 null이다")
        void forNewOutboxHasNullErrorMessage() {
            CancelOutbox outbox = CancelFixtures.newCancelOutbox();

            assertThat(outbox.errorMessage()).isNull();
        }

        @Test
        @DisplayName("신규 Outbox의 멱등키는 COBO: 접두사를 가진다")
        void forNewOutboxHasValidIdempotencyKey() {
            CancelOutbox outbox = CancelFixtures.newCancelOutbox();

            assertThat(outbox.idempotencyKeyValue()).startsWith("COBO:");
        }

        @Test
        @DisplayName("SELLER_CANCEL 타입의 Outbox를 생성한다")
        void forNewWithSellerCancelType() {
            CancelOutbox outbox = CancelFixtures.newCancelOutbox(CancelOutboxType.SELLER_CANCEL);

            assertThat(outbox.outboxType()).isEqualTo(CancelOutboxType.SELLER_CANCEL);
        }
    }

    @Nested
    @DisplayName("startProcessing() - 처리 시작")
    class StartProcessingTest {

        @Test
        @DisplayName("PENDING 상태에서 처리를 시작하면 PROCESSING 상태가 된다")
        void startProcessingFromPending() {
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.startProcessing(now);

            assertThat(outbox.status()).isEqualTo(CancelOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("PENDING이 아닌 상태에서 처리를 시작하면 예외가 발생한다")
        void startProcessingFromNonPending_ThrowsException() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();

            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDING");
        }
    }

    @Nested
    @DisplayName("complete() - 처리 완료")
    class CompleteTest {

        @Test
        @DisplayName("PROCESSING 상태에서 완료하면 COMPLETED 상태가 된다")
        void completeFromProcessing() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.complete(now);

            assertThat(outbox.status()).isEqualTo(CancelOutboxStatus.COMPLETED);
        }

        @Test
        @DisplayName("완료 후 processedAt이 설정된다")
        void completeSetProcessedAt() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.complete(now);

            assertThat(outbox.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("완료 후 errorMessage가 null로 초기화된다")
        void completeResetsErrorMessage() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();

            outbox.complete(CommonVoFixtures.now());

            assertThat(outbox.errorMessage()).isNull();
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태에서 완료하면 예외가 발생한다")
        void completeFromNonProcessing_ThrowsException() {
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();

            assertThatThrownBy(() -> outbox.complete(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING");
        }
    }

    @Nested
    @DisplayName("failAndRetry() - 실패 및 재시도")
    class FailAndRetryTest {

        @Test
        @DisplayName("재시도 횟수가 maxRetry 미만이면 PENDING으로 되돌아간다")
        void failAndRetryBelowMaxRetry() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.failAndRetry("네트워크 오류", now);

            assertThat(outbox.status()).isEqualTo(CancelOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("재시도 횟수가 maxRetry에 도달하면 FAILED 상태가 된다")
        void failAndRetryAtMaxRetry_BecomesFailed() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.failAndRetry("오류1", now);
            outbox.startProcessing(now);
            outbox.failAndRetry("오류2", now);
            outbox.startProcessing(now);
            outbox.failAndRetry("오류3", now);

            assertThat(outbox.status()).isEqualTo(CancelOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("실패 시 errorMessage가 저장된다")
        void failAndRetrySavesErrorMessage() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();
            String errorMsg = "타임아웃 오류";

            outbox.failAndRetry(errorMsg, CommonVoFixtures.now());

            assertThat(outbox.errorMessage()).isEqualTo(errorMsg);
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태에서 failAndRetry하면 예외가 발생한다")
        void failAndRetryFromNonProcessing_ThrowsException() {
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();

            assertThatThrownBy(() -> outbox.failAndRetry("오류", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING");
        }
    }

    @Nested
    @DisplayName("fail() - 즉시 실패 처리")
    class FailTest {

        @Test
        @DisplayName("PROCESSING 상태에서 즉시 실패하면 FAILED 상태가 된다")
        void failFromProcessing() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();

            outbox.fail("치명적 오류", CommonVoFixtures.now());

            assertThat(outbox.status()).isEqualTo(CancelOutboxStatus.FAILED);
        }

        @Test
        @DisplayName("즉시 실패 시 processedAt이 설정된다")
        void failSetsProcessedAt() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.fail("치명적 오류", now);

            assertThat(outbox.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태에서 fail하면 예외가 발생한다")
        void failFromNonProcessing_ThrowsException() {
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();

            assertThatThrownBy(() -> outbox.fail("오류", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING");
        }
    }

    @Nested
    @DisplayName("recordFailure() - 외부 API 실패 반영")
    class RecordFailureTest {

        @Test
        @DisplayName("canRetry가 true이면 failAndRetry를 호출한다")
        void recordFailureWithRetry() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();

            outbox.recordFailure(true, "재시도 가능 오류", CommonVoFixtures.now());

            assertThat(outbox.status()).isEqualTo(CancelOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("canRetry가 false이면 즉시 실패 처리한다")
        void recordFailureWithoutRetry() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();

            outbox.recordFailure(false, "재시도 불가 오류", CommonVoFixtures.now());

            assertThat(outbox.status()).isEqualTo(CancelOutboxStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("retry() - 수동 재처리")
    class RetryTest {

        @Test
        @DisplayName("FAILED 상태에서 retry하면 PENDING 상태가 된다")
        void retryFromFailed() {
            CancelOutbox outbox = CancelFixtures.failedCancelOutbox();

            outbox.retry(CommonVoFixtures.now());

            assertThat(outbox.status()).isEqualTo(CancelOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("retry 후 retryCount가 0으로 초기화된다")
        void retryResetsRetryCount() {
            CancelOutbox outbox = CancelFixtures.failedCancelOutbox();

            outbox.retry(CommonVoFixtures.now());

            assertThat(outbox.retryCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("retry 후 errorMessage가 null로 초기화된다")
        void retryResetsErrorMessage() {
            CancelOutbox outbox = CancelFixtures.failedCancelOutbox();

            outbox.retry(CommonVoFixtures.now());

            assertThat(outbox.errorMessage()).isNull();
        }

        @Test
        @DisplayName("FAILED가 아닌 상태에서 retry하면 예외가 발생한다")
        void retryFromNonFailed_ThrowsException() {
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();

            assertThatThrownBy(() -> outbox.retry(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("FAILED");
        }
    }

    @Nested
    @DisplayName("recoverFromTimeout() - 타임아웃 복구")
    class RecoverFromTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 복구하면 PENDING 상태가 된다")
        void recoverFromTimeoutFromProcessing() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();

            outbox.recoverFromTimeout(CommonVoFixtures.now());

            assertThat(outbox.status()).isEqualTo(CancelOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("타임아웃 복구 후 errorMessage에 복구 메시지가 저장된다")
        void recoverFromTimeoutSetsErrorMessage() {
            CancelOutbox outbox = CancelFixtures.processingCancelOutbox();

            outbox.recoverFromTimeout(CommonVoFixtures.now());

            assertThat(outbox.errorMessage()).contains("타임아웃");
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태에서 타임아웃 복구하면 예외가 발생한다")
        void recoverFromTimeoutFromNonProcessing_ThrowsException() {
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();

            assertThatThrownBy(() -> outbox.recoverFromTimeout(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING");
        }
    }

    @Nested
    @DisplayName("canRetry() / shouldProcess() 상태 조회")
    class StatusQueryTest {

        @Test
        @DisplayName("PENDING이고 retryCount < maxRetry이면 canRetry()가 true이다")
        void canRetryWhenPendingAndBelowMax() {
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();

            assertThat(outbox.canRetry()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태이면 canRetry()가 false이다")
        void cannotRetryWhenFailed() {
            CancelOutbox outbox = CancelFixtures.failedCancelOutbox();

            assertThat(outbox.canRetry()).isFalse();
        }

        @Test
        @DisplayName("PENDING 상태이면 shouldProcess()가 true이다")
        void shouldProcessWhenPending() {
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();

            assertThat(outbox.shouldProcess()).isTrue();
        }

        @Test
        @DisplayName("COMPLETED 상태이면 shouldProcess()가 false이다")
        void shouldNotProcessWhenCompleted() {
            CancelOutbox outbox = CancelFixtures.completedCancelOutbox();

            assertThat(outbox.shouldProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isPending() / isProcessing() / isCompleted() / isFailed() 상태 확인")
    class StateCheckTest {

        @Test
        @DisplayName("PENDING 상태 확인")
        void pendingStateCheck() {
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();

            assertThat(outbox.isPending()).isTrue();
            assertThat(outbox.isProcessing()).isFalse();
            assertThat(outbox.isCompleted()).isFalse();
            assertThat(outbox.isFailed()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED 상태 확인")
        void completedStateCheck() {
            CancelOutbox outbox = CancelFixtures.completedCancelOutbox();

            assertThat(outbox.isPending()).isFalse();
            assertThat(outbox.isProcessing()).isFalse();
            assertThat(outbox.isCompleted()).isTrue();
            assertThat(outbox.isFailed()).isFalse();
        }

        @Test
        @DisplayName("FAILED 상태 확인")
        void failedStateCheck() {
            CancelOutbox outbox = CancelFixtures.failedCancelOutbox();

            assertThat(outbox.isPending()).isFalse();
            assertThat(outbox.isProcessing()).isFalse();
            assertThat(outbox.isCompleted()).isFalse();
            assertThat(outbox.isFailed()).isTrue();
        }
    }

    @Nested
    @DisplayName("refreshVersion() - 버전 갱신")
    class RefreshVersionTest {

        @Test
        @DisplayName("version을 갱신할 수 있다")
        void refreshVersionUpdatesVersion() {
            CancelOutbox outbox = CancelFixtures.pendingCancelOutbox();

            outbox.refreshVersion(5L);

            assertThat(outbox.version()).isEqualTo(5L);
        }
    }
}
