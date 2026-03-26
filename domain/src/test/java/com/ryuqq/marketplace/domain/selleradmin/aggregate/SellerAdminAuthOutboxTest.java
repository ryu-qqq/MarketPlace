package com.ryuqq.marketplace.domain.selleradmin.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.selleradmin.SellerAdminFixtures;
import com.ryuqq.marketplace.domain.selleradmin.id.SellerAdminId;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminAuthOutboxStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAdminAuthOutbox Aggregate 단위 테스트")
class SellerAdminAuthOutboxTest {

    @Nested
    @DisplayName("forNew() - 신규 Outbox 생성")
    class ForNewTest {

        @Test
        @DisplayName("sellerAdminId와 payload로 Outbox를 생성한다")
        void createWithSellerAdminIdAndPayload() {
            SellerAdminId sellerAdminId =
                    SellerAdminId.of(SellerAdminFixtures.DEFAULT_SELLER_ADMIN_ID);
            String payload = SellerAdminFixtures.defaultAuthOutboxPayload();
            Instant now = CommonVoFixtures.now();

            SellerAdminAuthOutbox outbox =
                    SellerAdminAuthOutbox.forNew(sellerAdminId, payload, now);

            assertThat(outbox.sellerAdminId()).isEqualTo(sellerAdminId);
            assertThat(outbox.payload()).isEqualTo(payload);
            assertThat(outbox.status()).isEqualTo(SellerAdminAuthOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isZero();
            assertThat(outbox.maxRetry()).isEqualTo(3);
            assertThat(outbox.isNew()).isTrue();
        }

        @Test
        @DisplayName("sellerAdminId 없이 Outbox를 생성한다")
        void createWithoutSellerAdminId() {
            String payload = SellerAdminFixtures.defaultAuthOutboxPayload();
            Instant now = CommonVoFixtures.now();

            SellerAdminAuthOutbox outbox = SellerAdminAuthOutbox.forNew(payload, now);

            assertThat(outbox.sellerAdminId()).isNull();
            assertThat(outbox.sellerAdminIdValue()).isNull();
            assertThat(outbox.status()).isEqualTo(SellerAdminAuthOutboxStatus.PENDING);
            assertThat(outbox.idempotencyKeyValue()).startsWith("SAAO:unknown:");
        }

        @Test
        @DisplayName("최대 재시도 횟수를 지정해서 Outbox를 생성한다")
        void createWithMaxRetry() {
            SellerAdminId sellerAdminId =
                    SellerAdminId.of(SellerAdminFixtures.DEFAULT_SELLER_ADMIN_ID);
            int maxRetry = 5;

            SellerAdminAuthOutbox outbox =
                    SellerAdminAuthOutbox.forNew(
                            sellerAdminId,
                            SellerAdminFixtures.defaultAuthOutboxPayload(),
                            maxRetry,
                            CommonVoFixtures.now());

            assertThat(outbox.maxRetry()).isEqualTo(5);
        }

        @Test
        @DisplayName("생성된 Outbox의 idempotencyKey는 SAAO 접두어를 가진다")
        void createdOutboxHasSaaoPrefix() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.newSellerAdminAuthOutbox();

            assertThat(outbox.idempotencyKeyValue()).startsWith("SAAO:");
        }
    }

    @Nested
    @DisplayName("startProcessing() - 처리 시작")
    class StartProcessingTest {

        @Test
        @DisplayName("PENDING 상태에서 처리를 시작하면 PROCESSING이 된다")
        void pendingOutboxStartsProcessing() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.pendingSellerAdminAuthOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.startProcessing(now);

            assertThat(outbox.status()).isEqualTo(SellerAdminAuthOutboxStatus.PROCESSING);
            assertThat(outbox.isProcessing()).isTrue();
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PROCESSING 상태에서도 처리를 시작할 수 있다")
        void processingOutboxCanStartProcessingAgain() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.processingSellerAdminAuthOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.startProcessing(now);

            assertThat(outbox.status()).isEqualTo(SellerAdminAuthOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 처리를 시작하면 예외가 발생한다")
        void completedOutboxCannotStartProcessing() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.completedSellerAdminAuthOutbox();

            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("처리할 수 없는 상태");
        }

        @Test
        @DisplayName("FAILED 상태에서 처리를 시작하면 예외가 발생한다")
        void failedOutboxCannotStartProcessing() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.failedSellerAdminAuthOutbox();

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
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.processingSellerAdminAuthOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.complete(now);

            assertThat(outbox.status()).isEqualTo(SellerAdminAuthOutboxStatus.COMPLETED);
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
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.processingSellerAdminAuthOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.failAndRetry("연결 오류", now);

            assertThat(outbox.status()).isEqualTo(SellerAdminAuthOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.errorMessage()).isEqualTo("연결 오류");
        }

        @Test
        @DisplayName("재시도 횟수가 maxRetry에 도달하면 FAILED가 된다")
        void failAndRetryAtMaxRetryBecomesFailed() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.retriableSellerAdminAuthOutbox();
            Instant now = CommonVoFixtures.now();

            // retryCount가 1인 상태에서 2번 더 실패 → 총 3번(maxRetry) 도달
            outbox.startProcessing(now);
            outbox.failAndRetry("오류1", now);
            outbox.startProcessing(now);
            outbox.failAndRetry("최종 오류", now);

            assertThat(outbox.status()).isEqualTo(SellerAdminAuthOutboxStatus.FAILED);
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
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.processingSellerAdminAuthOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.fail("복구 불가한 오류", now);

            assertThat(outbox.status()).isEqualTo(SellerAdminAuthOutboxStatus.FAILED);
            assertThat(outbox.isFailed()).isTrue();
            assertThat(outbox.errorMessage()).isEqualTo("복구 불가한 오류");
            assertThat(outbox.retryCount()).isZero();
        }
    }

    @Nested
    @DisplayName("recordFailure() - 실패 결과 반영")
    class RecordFailureTest {

        @Test
        @DisplayName("canRetry=true이면 failAndRetry를 호출한다")
        void canRetryTrueCallsFailAndRetry() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.processingSellerAdminAuthOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.recordFailure(true, "일시적 오류", now);

            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.status()).isEqualTo(SellerAdminAuthOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("canRetry=false이면 즉시 실패 처리한다")
        void canRetryFalseCallsFail() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.processingSellerAdminAuthOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.recordFailure(false, "영구 오류", now);

            assertThat(outbox.status()).isEqualTo(SellerAdminAuthOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isZero();
        }
    }

    @Nested
    @DisplayName("recoverFromTimeout() - 타임아웃 복구")
    class RecoverFromTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 복구 시 PENDING으로 돌아간다")
        void processingOutboxRecoversToPending() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.processingSellerAdminAuthOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.recoverFromTimeout(now);

            assertThat(outbox.status()).isEqualTo(SellerAdminAuthOutboxStatus.PENDING);
            assertThat(outbox.errorMessage()).contains("타임아웃");
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태에서 타임아웃 복구 시 예외가 발생한다")
        void nonProcessingOutboxCannotRecover() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.pendingSellerAdminAuthOutbox();

            assertThatThrownBy(() -> outbox.recoverFromTimeout(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING 상태에서만");
        }
    }

    @Nested
    @DisplayName("isProcessingTimeout() - 타임아웃 여부 확인")
    class IsProcessingTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 기준 이전이면 타임아웃이다")
        void processingOutboxIsTimedOut() {
            long secondsAgo = 600L;
            SellerAdminAuthOutbox outbox =
                    SellerAdminFixtures.processingTimeoutSellerAdminAuthOutbox(secondsAgo);

            boolean isTimeout = outbox.isProcessingTimeout(CommonVoFixtures.now(), 300L);

            assertThat(isTimeout).isTrue();
        }

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 기준 이후이면 타임아웃이 아니다")
        void recentProcessingOutboxIsNotTimedOut() {
            long secondsAgo = 10L;
            SellerAdminAuthOutbox outbox =
                    SellerAdminFixtures.processingTimeoutSellerAdminAuthOutbox(secondsAgo);

            boolean isTimeout = outbox.isProcessingTimeout(CommonVoFixtures.now(), 300L);

            assertThat(isTimeout).isFalse();
        }

        @Test
        @DisplayName("PENDING 상태에서는 타임아웃 여부가 false다")
        void pendingOutboxIsNeverTimedOut() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.pendingSellerAdminAuthOutbox();

            boolean isTimeout = outbox.isProcessingTimeout(CommonVoFixtures.now(), 0L);

            assertThat(isTimeout).isFalse();
        }
    }

    @Nested
    @DisplayName("canRetry() 및 shouldProcess() 테스트")
    class CanRetryAndShouldProcessTest {

        @Test
        @DisplayName("재시도 횟수가 maxRetry 미만이고 처리 가능 상태이면 canRetry가 true다")
        void canRetryWhenBelowMaxAndProcessable() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.pendingSellerAdminAuthOutbox();

            assertThat(outbox.canRetry()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태이면 canRetry가 false다")
        void cannotRetryWhenFailed() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.failedSellerAdminAuthOutbox();

            assertThat(outbox.canRetry()).isFalse();
        }

        @Test
        @DisplayName("PENDING 상태이면 shouldProcess가 true다")
        void pendingOutboxShouldProcess() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.pendingSellerAdminAuthOutbox();

            assertThat(outbox.shouldProcess()).isTrue();
        }

        @Test
        @DisplayName("COMPLETED 상태이면 shouldProcess가 false다")
        void completedOutboxShouldNotProcess() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.completedSellerAdminAuthOutbox();

            assertThat(outbox.shouldProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("assignSellerAdminId() 및 refreshVersion() 테스트")
    class AssignAndVersionTest {

        @Test
        @DisplayName("sellerAdminId를 나중에 할당할 수 있다")
        void assignSellerAdminIdLater() {
            SellerAdminAuthOutbox outbox =
                    SellerAdminAuthOutbox.forNew(
                            SellerAdminFixtures.defaultAuthOutboxPayload(), CommonVoFixtures.now());
            SellerAdminId sellerAdminId =
                    SellerAdminId.of(SellerAdminFixtures.DEFAULT_SELLER_ADMIN_ID);

            outbox.assignSellerAdminId(sellerAdminId);

            assertThat(outbox.sellerAdminId()).isEqualTo(sellerAdminId);
        }

        @Test
        @DisplayName("refreshVersion으로 낙관적 락 버전을 갱신한다")
        void refreshVersionUpdatesVersion() {
            SellerAdminAuthOutbox outbox = SellerAdminFixtures.pendingSellerAdminAuthOutbox();

            outbox.refreshVersion(5L);

            assertThat(outbox.version()).isEqualTo(5L);
        }
    }
}
