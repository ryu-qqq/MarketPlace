package com.ryuqq.marketplace.domain.selleradmin.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.selleradmin.SellerAdminFixtures;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminEmailOutboxStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAdminEmailOutbox Aggregate 단위 테스트")
class SellerAdminEmailOutboxTest {

    @Nested
    @DisplayName("forNew() - 신규 이메일 Outbox 생성")
    class ForNewTest {

        @Test
        @DisplayName("sellerId와 payload로 이메일 Outbox를 생성한다")
        void createWithSellerIdAndPayload() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.newSellerAdminEmailOutbox();

            assertThat(outbox.sellerId()).isNotNull();
            assertThat(outbox.payload()).isEqualTo(SellerAdminFixtures.defaultEmailOutboxPayload());
            assertThat(outbox.status()).isEqualTo(SellerAdminEmailOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isZero();
            assertThat(outbox.maxRetry()).isEqualTo(3);
            assertThat(outbox.isNew()).isTrue();
        }

        @Test
        @DisplayName("생성된 이메일 Outbox의 idempotencyKey는 SAEO 접두어를 가진다")
        void createdOutboxHasSaeoPrefix() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.newSellerAdminEmailOutbox();

            assertThat(outbox.idempotencyKeyValue()).startsWith("SAEO:");
        }

        @Test
        @DisplayName("생성 직후 isPending이 true다")
        void newOutboxIsPending() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.newSellerAdminEmailOutbox();

            assertThat(outbox.isPending()).isTrue();
            assertThat(outbox.shouldProcess()).isTrue();
        }
    }

    @Nested
    @DisplayName("startProcessing() - 처리 시작")
    class StartProcessingTest {

        @Test
        @DisplayName("PENDING 상태에서 처리를 시작하면 PROCESSING이 된다")
        void pendingOutboxStartsProcessing() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.pendingSellerAdminEmailOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.startProcessing(now);

            assertThat(outbox.status()).isEqualTo(SellerAdminEmailOutboxStatus.PROCESSING);
            assertThat(outbox.isProcessing()).isTrue();
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 처리를 시작하면 예외가 발생한다")
        void completedOutboxCannotStartProcessing() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.completedSellerAdminEmailOutbox();

            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("처리할 수 없는 상태");
        }

        @Test
        @DisplayName("FAILED 상태에서 처리를 시작하면 예외가 발생한다")
        void failedOutboxCannotStartProcessing() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.failedSellerAdminEmailOutbox();

            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("처리할 수 없는 상태");
        }
    }

    @Nested
    @DisplayName("complete() - 처리 완료")
    class CompleteTest {

        @Test
        @DisplayName("처리 완료 시 COMPLETED 상태가 된다")
        void completeChangesStatusToCompleted() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.processingSellerAdminEmailOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.complete(now);

            assertThat(outbox.status()).isEqualTo(SellerAdminEmailOutboxStatus.COMPLETED);
            assertThat(outbox.isCompleted()).isTrue();
            assertThat(outbox.processedAt()).isEqualTo(now);
            assertThat(outbox.errorMessage()).isNull();
        }
    }

    @Nested
    @DisplayName("failAndRetry() - 실패 및 재시도")
    class FailAndRetryTest {

        @Test
        @DisplayName("재시도 횟수가 maxRetry 미만이면 PENDING으로 돌아간다")
        void failAndRetryBelowMaxRetryReturnsToPending() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.processingSellerAdminEmailOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.failAndRetry("이메일 서버 오류", now);

            assertThat(outbox.status()).isEqualTo(SellerAdminEmailOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.errorMessage()).isEqualTo("이메일 서버 오류");
        }

        @Test
        @DisplayName("재시도 횟수가 maxRetry에 도달하면 FAILED가 된다")
        void failAndRetryAtMaxRetryBecomesFailed() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.retriableSellerAdminEmailOutbox();
            Instant now = CommonVoFixtures.now();

            // retryCount가 1인 상태에서 2번 더 실패 → 총 3번(maxRetry) 도달
            outbox.startProcessing(now);
            outbox.failAndRetry("오류1", now);
            outbox.startProcessing(now);
            outbox.failAndRetry("최종 오류", now);

            assertThat(outbox.status()).isEqualTo(SellerAdminEmailOutboxStatus.FAILED);
            assertThat(outbox.isFailed()).isTrue();
            assertThat(outbox.retryCount()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("fail() - 즉시 실패")
    class FailTest {

        @Test
        @DisplayName("즉시 실패 처리 시 FAILED 상태가 된다")
        void immediateFailChangesStatusToFailed() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.processingSellerAdminEmailOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.fail("SES 인증 실패", now);

            assertThat(outbox.status()).isEqualTo(SellerAdminEmailOutboxStatus.FAILED);
            assertThat(outbox.isFailed()).isTrue();
            assertThat(outbox.errorMessage()).isEqualTo("SES 인증 실패");
            assertThat(outbox.retryCount()).isZero();
        }
    }

    @Nested
    @DisplayName("recordFailure() - 실패 결과 반영")
    class RecordFailureTest {

        @Test
        @DisplayName("canRetry=true이면 failAndRetry를 호출한다")
        void canRetryTrueCallsFailAndRetry() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.processingSellerAdminEmailOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.recordFailure(true, "일시적 오류", now);

            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.status()).isEqualTo(SellerAdminEmailOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("canRetry=false이면 즉시 실패 처리한다")
        void canRetryFalseCallsFail() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.processingSellerAdminEmailOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.recordFailure(false, "이메일 주소 오류", now);

            assertThat(outbox.status()).isEqualTo(SellerAdminEmailOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isZero();
        }
    }

    @Nested
    @DisplayName("recoverFromTimeout() - 타임아웃 복구")
    class RecoverFromTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 복구 시 PENDING으로 돌아간다")
        void processingOutboxRecoversToPending() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.processingSellerAdminEmailOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.recoverFromTimeout(now);

            assertThat(outbox.status()).isEqualTo(SellerAdminEmailOutboxStatus.PENDING);
            assertThat(outbox.errorMessage()).contains("타임아웃");
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태에서 타임아웃 복구 시 예외가 발생한다")
        void nonProcessingOutboxCannotRecover() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.pendingSellerAdminEmailOutbox();

            assertThatThrownBy(() -> outbox.recoverFromTimeout(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING 상태에서만");
        }
    }

    @Nested
    @DisplayName("isProcessingTimeout() - 타임아웃 여부 확인")
    class IsProcessingTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 기준을 넘으면 true를 반환한다")
        void processingOutboxIsTimedOut() {
            SellerAdminEmailOutbox outbox =
                    SellerAdminFixtures.processingTimeoutSellerAdminEmailOutbox(600L);

            boolean isTimeout = outbox.isProcessingTimeout(CommonVoFixtures.now(), 300L);

            assertThat(isTimeout).isTrue();
        }

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 기준 이내이면 false를 반환한다")
        void recentProcessingOutboxIsNotTimedOut() {
            SellerAdminEmailOutbox outbox =
                    SellerAdminFixtures.processingTimeoutSellerAdminEmailOutbox(10L);

            boolean isTimeout = outbox.isProcessingTimeout(CommonVoFixtures.now(), 300L);

            assertThat(isTimeout).isFalse();
        }

        @Test
        @DisplayName("PENDING 상태에서는 타임아웃 여부가 false다")
        void pendingOutboxIsNeverTimedOut() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.pendingSellerAdminEmailOutbox();

            boolean isTimeout = outbox.isProcessingTimeout(CommonVoFixtures.now(), 0L);

            assertThat(isTimeout).isFalse();
        }
    }

    @Nested
    @DisplayName("refreshVersion() 테스트")
    class RefreshVersionTest {

        @Test
        @DisplayName("refreshVersion으로 낙관적 락 버전을 갱신한다")
        void refreshVersionUpdatesVersion() {
            SellerAdminEmailOutbox outbox = SellerAdminFixtures.pendingSellerAdminEmailOutbox();

            outbox.refreshVersion(3L);

            assertThat(outbox.version()).isEqualTo(3L);
        }
    }
}
