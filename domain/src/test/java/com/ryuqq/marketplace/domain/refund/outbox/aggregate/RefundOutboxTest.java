package com.ryuqq.marketplace.domain.refund.outbox.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.outbox.id.RefundOutboxId;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxStatus;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundOutbox Aggregate 단위 테스트")
class RefundOutboxTest {

    private static final Long DEFAULT_ORDER_ITEM_ID = 1001L;
    private static final String DEFAULT_PAYLOAD =
            "{\"orderItemId\":\"" + DEFAULT_ORDER_ITEM_ID + "\"}";

    @Nested
    @DisplayName("forNew() - 신규 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 아웃박스를 생성하면 PENDING 상태로 초기화된다")
        void createNewOutboxIsPending() {
            // given
            OrderItemId orderItemId = OrderItemId.of(DEFAULT_ORDER_ITEM_ID);
            Instant now = CommonVoFixtures.now();

            // when
            RefundOutbox outbox =
                    RefundOutbox.forNew(
                            orderItemId, RefundOutboxType.REQUEST, DEFAULT_PAYLOAD, now);

            // then
            assertThat(outbox).isNotNull();
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(0);
            assertThat(outbox.maxRetry()).isEqualTo(3);
            assertThat(outbox.processedAt()).isNull();
            assertThat(outbox.errorMessage()).isNull();
            assertThat(outbox.payload()).isEqualTo(DEFAULT_PAYLOAD);
            assertThat(outbox.outboxType()).isEqualTo(RefundOutboxType.REQUEST);
        }

        @Test
        @DisplayName("신규 생성 시 ID는 새 ID(value=null)이다")
        void createNewOutboxHasNewId() {
            // given
            OrderItemId orderItemId = OrderItemId.of(DEFAULT_ORDER_ITEM_ID);

            // when
            RefundOutbox outbox =
                    RefundOutbox.forNew(
                            orderItemId,
                            RefundOutboxType.APPROVE,
                            DEFAULT_PAYLOAD,
                            CommonVoFixtures.now());

            // then
            assertThat(outbox.isNew()).isTrue();
            assertThat(outbox.idValue()).isNull();
        }

        @Test
        @DisplayName("신규 생성 시 멱등성 키가 자동 생성된다")
        void createNewOutboxHasIdempotencyKey() {
            // given
            OrderItemId orderItemId = OrderItemId.of(DEFAULT_ORDER_ITEM_ID);

            // when
            RefundOutbox outbox =
                    RefundOutbox.forNew(
                            orderItemId,
                            RefundOutboxType.REQUEST,
                            DEFAULT_PAYLOAD,
                            CommonVoFixtures.now());

            // then
            assertThat(outbox.idempotencyKeyValue()).isNotBlank();
            assertThat(outbox.idempotencyKeyValue()).startsWith("ROBO:");
        }

        @Test
        @DisplayName("isPending()은 신규 생성 시 true이다")
        void newOutboxIsPending() {
            // given
            RefundOutbox outbox =
                    RefundOutbox.forNew(
                            OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                            RefundOutboxType.REQUEST,
                            DEFAULT_PAYLOAD,
                            CommonVoFixtures.now());

            // then
            assertThat(outbox.isPending()).isTrue();
            assertThat(outbox.isProcessing()).isFalse();
            assertThat(outbox.isCompleted()).isFalse();
            assertThat(outbox.isFailed()).isFalse();
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("PENDING 상태로 복원한다")
        void reconstitutePendingOutbox() {
            // when
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PENDING, 0, null);

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.PENDING);
            assertThat(outbox.isPending()).isTrue();
            assertThat(outbox.isNew()).isFalse();
        }

        @Test
        @DisplayName("PROCESSING 상태로 복원한다")
        void reconstituteProcessingOutbox() {
            // when
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PROCESSING, 0, null);

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.PROCESSING);
            assertThat(outbox.isProcessing()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태로 복원하면 isFailed()는 true이다")
        void reconstituteFailedOutbox() {
            // when
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.FAILED, 3, "외부 API 오류");

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.FAILED);
            assertThat(outbox.isFailed()).isTrue();
            assertThat(outbox.retryCount()).isEqualTo(3);
            assertThat(outbox.errorMessage()).isEqualTo("외부 API 오류");
        }
    }

    @Nested
    @DisplayName("startProcessing() - 처리 시작")
    class StartProcessingTest {

        @Test
        @DisplayName("PENDING 상태에서 처리를 시작하면 PROCESSING이 된다")
        void startProcessingFromPending() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PENDING, 0, null);
            Instant now = CommonVoFixtures.now();

            // when
            outbox.startProcessing(now);

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.PROCESSING);
            assertThat(outbox.isProcessing()).isTrue();
        }

        @Test
        @DisplayName("PENDING이 아닌 상태에서 처리 시작하면 예외가 발생한다")
        void startProcessingFromNonPending_ThrowsException() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PROCESSING, 0, null);

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDING 상태에서만 처리를 시작할 수 있습니다");
        }

        @Test
        @DisplayName("COMPLETED 상태에서 처리 시작하면 예외가 발생한다")
        void startProcessingFromCompleted_ThrowsException() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.COMPLETED, 0, null);

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("complete() - 처리 완료")
    class CompleteTest {

        @Test
        @DisplayName("PROCESSING 상태에서 완료 처리하면 COMPLETED가 된다")
        void completeFromProcessing() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PROCESSING, 0, null);
            Instant now = CommonVoFixtures.now();

            // when
            outbox.complete(now);

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.COMPLETED);
            assertThat(outbox.isCompleted()).isTrue();
            assertThat(outbox.processedAt()).isEqualTo(now);
            assertThat(outbox.errorMessage()).isNull();
        }

        @Test
        @DisplayName("PENDING 상태에서 완료 처리하면 예외가 발생한다")
        void completeFromPending_ThrowsException() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PENDING, 0, null);

            // when & then
            assertThatThrownBy(() -> outbox.complete(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING 상태에서만 완료할 수 있습니다");
        }
    }

    @Nested
    @DisplayName("failAndRetry() - 실패 후 재시도")
    class FailAndRetryTest {

        @Test
        @DisplayName("재시도 횟수가 maxRetry 미만이면 PENDING으로 돌아간다")
        void failAndRetryBelowMaxRetry() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PROCESSING, 0, null);
            String errorMsg = "네트워크 오류";

            // when
            outbox.failAndRetry(errorMsg, CommonVoFixtures.now());

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.errorMessage()).isEqualTo(errorMsg);
        }

        @Test
        @DisplayName("재시도 횟수가 maxRetry에 도달하면 FAILED 상태가 된다")
        void failAndRetryAtMaxRetry() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PROCESSING, 2, null);

            // when
            outbox.failAndRetry("최종 실패", CommonVoFixtures.now());

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isEqualTo(3);
            assertThat(outbox.isFailed()).isTrue();
        }

        @Test
        @DisplayName("PENDING 상태에서 failAndRetry 호출하면 예외가 발생한다")
        void failAndRetryFromPending_ThrowsException() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PENDING, 0, null);

            // when & then
            assertThatThrownBy(() -> outbox.failAndRetry("오류", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING 상태에서만 실패 처리할 수 있습니다");
        }
    }

    @Nested
    @DisplayName("fail() - 즉시 실패 처리")
    class FailTest {

        @Test
        @DisplayName("PROCESSING 상태에서 즉시 실패 처리하면 FAILED가 된다")
        void failFromProcessing() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PROCESSING, 0, null);
            String errorMsg = "복구 불가 오류";
            Instant now = CommonVoFixtures.now();

            // when
            outbox.fail(errorMsg, now);

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.FAILED);
            assertThat(outbox.isFailed()).isTrue();
            assertThat(outbox.errorMessage()).isEqualTo(errorMsg);
            assertThat(outbox.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PENDING 상태에서 fail 호출하면 예외가 발생한다")
        void failFromPending_ThrowsException() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PENDING, 0, null);

            // when & then
            assertThatThrownBy(() -> outbox.fail("오류", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("recordFailure() - 외부 API 실패 결과 반영")
    class RecordFailureTest {

        @Test
        @DisplayName("canRetry가 true이면 failAndRetry를 수행한다")
        void recordFailureWithRetry() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PROCESSING, 0, null);

            // when
            outbox.recordFailure(true, "일시적 오류", CommonVoFixtures.now());

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("canRetry가 false이면 fail을 수행한다")
        void recordFailureWithoutRetry() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PROCESSING, 0, null);

            // when
            outbox.recordFailure(false, "영구 오류", CommonVoFixtures.now());

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("retry() - 수동 재처리")
    class RetryTest {

        @Test
        @DisplayName("FAILED 상태에서 retry하면 PENDING 상태로 복구된다")
        void retryFromFailed() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.FAILED, 3, "이전 오류");
            Instant now = CommonVoFixtures.now();

            // when
            outbox.retry(now);

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(0);
            assertThat(outbox.errorMessage()).isNull();
        }

        @Test
        @DisplayName("PENDING 상태에서 retry 호출하면 예외가 발생한다")
        void retryFromPending_ThrowsException() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PENDING, 0, null);

            // when & then
            assertThatThrownBy(() -> outbox.retry(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("FAILED 상태에서만 재처리할 수 있습니다");
        }

        @Test
        @DisplayName("PROCESSING 상태에서 retry 호출하면 예외가 발생한다")
        void retryFromProcessing_ThrowsException() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PROCESSING, 1, null);

            // when & then
            assertThatThrownBy(() -> outbox.retry(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("recoverFromTimeout() - 타임아웃 복구")
    class RecoverFromTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 복구하면 PENDING이 된다")
        void recoverFromTimeoutInProcessing() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PROCESSING, 0, null);
            Instant now = CommonVoFixtures.now();

            // when
            outbox.recoverFromTimeout(now);

            // then
            assertThat(outbox.status()).isEqualTo(RefundOutboxStatus.PENDING);
            assertThat(outbox.errorMessage()).isEqualTo("타임아웃으로 인한 복구");
        }

        @Test
        @DisplayName("PENDING 상태에서 타임아웃 복구 호출하면 예외가 발생한다")
        void recoverFromTimeoutInPending_ThrowsException() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PENDING, 0, null);

            // when & then
            assertThatThrownBy(() -> outbox.recoverFromTimeout(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("타임아웃 복구는 PROCESSING 상태에서만 가능합니다");
        }
    }

    @Nested
    @DisplayName("canRetry() / shouldProcess() - 상태 조회 메서드")
    class StateCheckTest {

        @Test
        @DisplayName("PENDING 상태이고 retryCount가 maxRetry 미만이면 canRetry는 true이다")
        void canRetryIsTrueWhenPendingAndBelowMaxRetry() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PENDING, 0, null);

            // then
            assertThat(outbox.canRetry()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태이면 canRetry는 false이다")
        void canRetryIsFalseWhenFailed() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.FAILED, 3, "오류");

            // then
            assertThat(outbox.canRetry()).isFalse();
        }

        @Test
        @DisplayName("shouldProcess는 PENDING 상태에서만 true이다")
        void shouldProcessIsTrueOnlyForPending() {
            // given
            RefundOutbox pending = reconstitute(RefundOutboxStatus.PENDING, 0, null);
            RefundOutbox processing = reconstitute(RefundOutboxStatus.PROCESSING, 0, null);
            RefundOutbox completed = reconstitute(RefundOutboxStatus.COMPLETED, 0, null);
            RefundOutbox failed = reconstitute(RefundOutboxStatus.FAILED, 3, "오류");

            // then
            assertThat(pending.shouldProcess()).isTrue();
            assertThat(processing.shouldProcess()).isFalse();
            assertThat(completed.shouldProcess()).isFalse();
            assertThat(failed.shouldProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("version 관리 테스트")
    class VersionTest {

        @Test
        @DisplayName("refreshVersion()으로 버전을 갱신한다")
        void refreshVersionUpdatesVersion() {
            // given
            RefundOutbox outbox = reconstitute(RefundOutboxStatus.PENDING, 0, null);

            // when
            outbox.refreshVersion(5L);

            // then
            assertThat(outbox.version()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("전체 상태 전이 흐름 테스트")
    class FullFlowTest {

        @Test
        @DisplayName("PENDING → PROCESSING → COMPLETED 정상 흐름")
        void normalFlow() {
            // given
            RefundOutbox outbox =
                    RefundOutbox.forNew(
                            OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                            RefundOutboxType.APPROVE,
                            DEFAULT_PAYLOAD,
                            CommonVoFixtures.yesterday());

            // when
            outbox.startProcessing(CommonVoFixtures.now());
            assertThat(outbox.isProcessing()).isTrue();

            outbox.complete(CommonVoFixtures.now());

            // then
            assertThat(outbox.isCompleted()).isTrue();
            assertThat(outbox.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("PENDING → PROCESSING → FAILED → PENDING (수동 재처리) → PROCESSING → COMPLETED")
        void retryAndSuccessFlow() {
            // given
            RefundOutbox outbox =
                    RefundOutbox.forNew(
                            OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                            RefundOutboxType.COLLECT,
                            DEFAULT_PAYLOAD,
                            CommonVoFixtures.yesterday());

            // when: 최대 재시도 도달로 FAILED
            outbox.startProcessing(CommonVoFixtures.now());
            outbox.failAndRetry("1차 실패", CommonVoFixtures.now());
            outbox.startProcessing(CommonVoFixtures.now());
            outbox.failAndRetry("2차 실패", CommonVoFixtures.now());
            outbox.startProcessing(CommonVoFixtures.now());
            outbox.failAndRetry("3차 실패 - FAILED", CommonVoFixtures.now());

            assertThat(outbox.isFailed()).isTrue();

            // then: 수동 재처리 후 성공
            outbox.retry(CommonVoFixtures.now());
            assertThat(outbox.isPending()).isTrue();
            assertThat(outbox.retryCount()).isEqualTo(0);

            outbox.startProcessing(CommonVoFixtures.now());
            outbox.complete(CommonVoFixtures.now());
            assertThat(outbox.isCompleted()).isTrue();
        }
    }

    // ===== Helper =====

    private RefundOutbox reconstitute(
            RefundOutboxStatus status, int retryCount, String errorMessage) {
        Instant yesterday = CommonVoFixtures.yesterday();
        Instant processedAt = status.isTerminal() ? CommonVoFixtures.now() : null;
        return RefundOutbox.reconstitute(
                RefundOutboxId.of(1L),
                OrderItemId.of(DEFAULT_ORDER_ITEM_ID),
                RefundOutboxType.REQUEST,
                status,
                DEFAULT_PAYLOAD,
                retryCount,
                3,
                yesterday,
                yesterday,
                processedAt,
                errorMessage,
                0L,
                "ROBO:" + DEFAULT_ORDER_ITEM_ID + ":REQUEST:" + yesterday.toEpochMilli());
    }
}
