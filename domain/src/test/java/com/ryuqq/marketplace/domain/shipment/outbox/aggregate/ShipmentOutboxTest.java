package com.ryuqq.marketplace.domain.shipment.outbox.aggregate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.ShipmentOutboxFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxStatus;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentOutbox Aggregate 단위 테스트")
class ShipmentOutboxTest {

    @Nested
    @DisplayName("forNew() - 신규 아웃박스 생성")
    class ForNewTest {

        @Test
        @DisplayName("신규 아웃박스는 PENDING 상태로 생성된다")
        void createNewOutboxWithPendingStatus() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("신규 아웃박스는 재시도 횟수가 0이다")
        void createNewOutboxWithZeroRetryCount() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();

            assertThat(outbox.retryCount()).isZero();
        }

        @Test
        @DisplayName("신규 아웃박스는 최대 재시도 횟수가 3이다")
        void createNewOutboxWithDefaultMaxRetry() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();

            assertThat(outbox.maxRetry()).isEqualTo(3);
        }

        @Test
        @DisplayName("신규 아웃박스는 isNew()가 true이다")
        void createNewOutboxIsNew() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();

            assertThat(outbox.isNew()).isTrue();
        }

        @Test
        @DisplayName("신규 아웃박스의 processedAt은 null이다")
        void createNewOutboxHasNullProcessedAt() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();

            assertThat(outbox.processedAt()).isNull();
        }

        @Test
        @DisplayName("신규 아웃박스의 errorMessage는 null이다")
        void createNewOutboxHasNullErrorMessage() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();

            assertThat(outbox.errorMessage()).isNull();
        }

        @Test
        @DisplayName("신규 아웃박스는 멱등키가 자동 생성된다")
        void createNewOutboxHasIdempotencyKey() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();

            assertThat(outbox.idempotencyKey()).isNotNull();
            assertThat(outbox.idempotencyKeyValue()).startsWith("SHPO:");
        }

        @Test
        @DisplayName("forNew() 생성 시 기본 필드가 올바르게 설정된다")
        void newOutboxFieldsSetCorrectly() {
            Instant now = CommonVoFixtures.now();

            ShipmentOutbox outbox =
                    ShipmentOutbox.forNew(
                            ShipmentFixtures.defaultOrderItemId(),
                            ShipmentOutboxType.SHIP,
                            "{\"payload\":\"test\"}",
                            now);

            assertThat(outbox.orderItemId()).isEqualTo(ShipmentFixtures.defaultOrderItemId());
            assertThat(outbox.orderItemIdValue())
                    .isEqualTo(ShipmentFixtures.defaultOrderItemId().value());
            assertThat(outbox.outboxType()).isEqualTo(ShipmentOutboxType.SHIP);
            assertThat(outbox.payload()).isEqualTo("{\"payload\":\"test\"}");
            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.PENDING);
            assertThat(outbox.createdAt()).isEqualTo(now);
            assertThat(outbox.updatedAt()).isEqualTo(now);
            assertThat(outbox.version()).isZero();
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("PENDING 상태로 재구성한다")
        void reconstitutePendingOutbox() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.PENDING);
            assertThat(outbox.isNew()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED 상태로 재구성하면 processedAt이 설정된다")
        void reconstituteCompletedOutbox() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.completedShipmentOutbox();

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.COMPLETED);
            assertThat(outbox.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태로 재구성하면 errorMessage와 retryCount가 설정된다")
        void reconstituteFailedOutbox() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.failedShipmentOutbox();

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.FAILED);
            assertThat(outbox.errorMessage()).isNotNull();
            assertThat(outbox.retryCount()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("startProcessing() - PENDING → PROCESSING")
    class StartProcessingTest {

        @Test
        @DisplayName("PENDING 상태의 아웃박스를 처리 시작한다")
        void startProcessingPendingOutbox() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.startProcessing(now);

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.PROCESSING);
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PENDING이 아닌 상태에서 startProcessing()을 호출하면 예외가 발생한다")
        void startProcessingNotPendingOutbox_ThrowsException() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();

            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PENDING");
        }

        @Test
        @DisplayName("COMPLETED 상태에서 startProcessing()을 호출하면 예외가 발생한다")
        void startProcessingCompletedOutbox_ThrowsException() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.completedShipmentOutbox();

            assertThatThrownBy(() -> outbox.startProcessing(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("complete() - PROCESSING → COMPLETED")
    class CompleteTest {

        @Test
        @DisplayName("PROCESSING 상태의 아웃박스를 완료 처리한다")
        void completeProcessingOutbox() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.complete(now);

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.COMPLETED);
            assertThat(outbox.processedAt()).isEqualTo(now);
            assertThat(outbox.updatedAt()).isEqualTo(now);
            assertThat(outbox.errorMessage()).isNull();
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태에서 complete()을 호출하면 예외가 발생한다")
        void completePendingOutbox_ThrowsException() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();

            assertThatThrownBy(() -> outbox.complete(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING");
        }
    }

    @Nested
    @DisplayName("failAndRetry() - 재시도 가능한 실패 처리")
    class FailAndRetryTest {

        @Test
        @DisplayName("재시도 횟수가 최대 미만이면 PENDING으로 복귀한다")
        void failAndRetryBelowMaxRetry_ReturnsToPending() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutboxWithRetry(1);
            Instant now = CommonVoFixtures.now();

            outbox.failAndRetry("외부 API 오류", now);

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(2);
            assertThat(outbox.errorMessage()).isEqualTo("외부 API 오류");
        }

        @Test
        @DisplayName("재시도 횟수가 최대에 도달하면 FAILED 상태가 된다")
        void failAndRetryAtMaxRetry_BecomeFailed() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutboxWithRetry(2);
            Instant now = CommonVoFixtures.now();

            outbox.failAndRetry("최종 실패", now);

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isEqualTo(3);
            assertThat(outbox.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태에서 failAndRetry()를 호출하면 예외가 발생한다")
        void failAndRetryPendingOutbox_ThrowsException() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();

            assertThatThrownBy(() -> outbox.failAndRetry("오류", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING");
        }
    }

    @Nested
    @DisplayName("fail() - 즉시 실패 처리")
    class FailTest {

        @Test
        @DisplayName("PROCESSING 상태의 아웃박스를 즉시 실패 처리한다")
        void failProcessingOutbox() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.fail("복구 불가 오류", now);

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.FAILED);
            assertThat(outbox.errorMessage()).isEqualTo("복구 불가 오류");
            assertThat(outbox.processedAt()).isEqualTo(now);
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태에서 fail()을 호출하면 예외가 발생한다")
        void failPendingOutbox_ThrowsException() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();

            assertThatThrownBy(() -> outbox.fail("오류", CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("recordFailure() - 외부 API 실패 결과 반영")
    class RecordFailureTest {

        @Test
        @DisplayName("canRetry=true이면 failAndRetry()를 호출한다")
        void recordFailureWithCanRetry_CallsFailAndRetry() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutboxWithRetry(0);

            outbox.recordFailure(true, "일시적 오류", CommonVoFixtures.now());

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("canRetry=false이면 fail()을 호출한다")
        void recordFailureWithoutCanRetry_CallsFail() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();

            outbox.recordFailure(false, "영구 오류", CommonVoFixtures.now());

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.FAILED);
        }
    }

    @Nested
    @DisplayName("retry() - 수동 재처리")
    class RetryTest {

        @Test
        @DisplayName("FAILED 상태의 아웃박스를 PENDING으로 수동 재처리한다")
        void retryFailedOutbox() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.failedShipmentOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.retry(now);

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isZero();
            assertThat(outbox.errorMessage()).isNull();
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("FAILED가 아닌 상태에서 retry()를 호출하면 예외가 발생한다")
        void retryPendingOutbox_ThrowsException() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();

            assertThatThrownBy(() -> outbox.retry(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("FAILED");
        }
    }

    @Nested
    @DisplayName("recoverFromTimeout() - 타임아웃 복구")
    class RecoverFromTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태의 아웃박스를 타임아웃 복구한다")
        void recoverProcessingOutboxFromTimeout() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.recoverFromTimeout(now);

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.PENDING);
            assertThat(outbox.errorMessage()).contains("타임아웃");
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("PROCESSING이 아닌 상태에서 recoverFromTimeout()을 호출하면 예외가 발생한다")
        void recoverPendingOutboxFromTimeout_ThrowsException() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();

            assertThatThrownBy(() -> outbox.recoverFromTimeout(CommonVoFixtures.now()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING");
        }
    }

    @Nested
    @DisplayName("canRetry() 테스트")
    class CanRetryTest {

        @Test
        @DisplayName("재시도 횟수가 최대 미만이고 PENDING이면 canRetry()가 true이다")
        void canRetryWhenBelowMaxRetryAndPending() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();

            assertThat(outbox.canRetry()).isTrue();
        }

        @Test
        @DisplayName("재시도 횟수가 최대에 도달하면 canRetry()가 false이다")
        void cannotRetryWhenAtMaxRetry() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.failedShipmentOutbox();

            assertThat(outbox.canRetry()).isFalse();
        }
    }

    @Nested
    @DisplayName("shouldProcess() 테스트")
    class ShouldProcessTest {

        @Test
        @DisplayName("PENDING 상태이면 shouldProcess()가 true이다")
        void pendingOutboxShouldProcess() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();

            assertThat(outbox.shouldProcess()).isTrue();
        }

        @Test
        @DisplayName("PROCESSING 상태이면 shouldProcess()가 false이다")
        void processingOutboxShouldNotProcess() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.processingShipmentOutbox();

            assertThat(outbox.shouldProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("상태 조회 편의 메서드 테스트")
    class StatusCheckTest {

        @Test
        @DisplayName("isPending()은 PENDING 상태에서만 true이다")
        void isPendingTest() {
            assertThat(ShipmentOutboxFixtures.pendingShipmentOutbox().isPending()).isTrue();
            assertThat(ShipmentOutboxFixtures.processingShipmentOutbox().isPending()).isFalse();
        }

        @Test
        @DisplayName("isProcessing()은 PROCESSING 상태에서만 true이다")
        void isProcessingTest() {
            assertThat(ShipmentOutboxFixtures.processingShipmentOutbox().isProcessing()).isTrue();
            assertThat(ShipmentOutboxFixtures.pendingShipmentOutbox().isProcessing()).isFalse();
        }

        @Test
        @DisplayName("isCompleted()는 COMPLETED 상태에서만 true이다")
        void isCompletedTest() {
            assertThat(ShipmentOutboxFixtures.completedShipmentOutbox().isCompleted()).isTrue();
            assertThat(ShipmentOutboxFixtures.pendingShipmentOutbox().isCompleted()).isFalse();
        }

        @Test
        @DisplayName("isFailed()는 FAILED 상태에서만 true이다")
        void isFailedTest() {
            assertThat(ShipmentOutboxFixtures.failedShipmentOutbox().isFailed()).isTrue();
            assertThat(ShipmentOutboxFixtures.pendingShipmentOutbox().isFailed()).isFalse();
        }
    }

    @Nested
    @DisplayName("refreshVersion() 테스트")
    class RefreshVersionTest {

        @Test
        @DisplayName("버전을 갱신한다")
        void refreshVersion() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();

            outbox.refreshVersion(5L);

            assertThat(outbox.version()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("전체 처리 흐름 테스트")
    class FullFlowTest {

        @Test
        @DisplayName("정상 처리 흐름: PENDING → PROCESSING → COMPLETED")
        void normalProcessingFlow() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.startProcessing(now);
            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.PROCESSING);

            outbox.complete(now);
            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.COMPLETED);
            assertThat(outbox.processedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("재시도 후 성공 흐름: PENDING → PROCESSING → PENDING → PROCESSING → COMPLETED")
        void retryThenSuccessFlow() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.startProcessing(now);
            outbox.failAndRetry("일시적 오류", now);
            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);

            outbox.startProcessing(now);
            outbox.complete(now);
            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.COMPLETED);
        }

        @Test
        @DisplayName("최대 재시도 초과 흐름: PENDING → PROCESSING → ... → FAILED")
        void maxRetryExceededFlow() {
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();
            Instant now = CommonVoFixtures.now();

            outbox.startProcessing(now);
            outbox.failAndRetry("오류1", now);

            outbox.startProcessing(now);
            outbox.failAndRetry("오류2", now);

            outbox.startProcessing(now);
            outbox.failAndRetry("오류3", now);

            assertThat(outbox.status()).isEqualTo(ShipmentOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isEqualTo(3);
        }
    }
}
