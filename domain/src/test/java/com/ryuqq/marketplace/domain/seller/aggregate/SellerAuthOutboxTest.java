package com.ryuqq.marketplace.domain.seller.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.SellerAuthOutboxStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAuthOutbox Aggregate 테스트")
class SellerAuthOutboxTest {

    @Nested
    @DisplayName("forNew() - 신규 Outbox 생성")
    class ForNewTest {

        @Test
        @DisplayName("SellerId 없이 새 Outbox를 생성한다")
        void createNewOutboxWithoutSellerId() {
            // given
            String payload = SellerFixtures.defaultAuthOutboxPayload();
            Instant now = CommonVoFixtures.now();

            // when
            SellerAuthOutbox outbox = SellerAuthOutbox.forNew(payload, now);

            // then
            assertThat(outbox.isNew()).isTrue();
            assertThat(outbox.sellerId()).isNull();
            assertThat(outbox.payload()).isEqualTo(payload);
            assertThat(outbox.status()).isEqualTo(SellerAuthOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isZero();
            assertThat(outbox.maxRetry()).isEqualTo(3);
            assertThat(outbox.createdAt()).isEqualTo(now);
            assertThat(outbox.updatedAt()).isEqualTo(now);
            assertThat(outbox.processedAt()).isNull();
            assertThat(outbox.errorMessage()).isNull();
            assertThat(outbox.version()).isZero();
            assertThat(outbox.idempotencyKeyValue()).startsWith("SAO:");
        }

        @Test
        @DisplayName("SellerId와 함께 새 Outbox를 생성한다")
        void createNewOutboxWithSellerId() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            String payload = SellerFixtures.defaultAuthOutboxPayload();
            Instant now = CommonVoFixtures.now();

            // when
            SellerAuthOutbox outbox = SellerAuthOutbox.forNew(sellerId, payload, now);

            // then
            assertThat(outbox.sellerId()).isEqualTo(sellerId);
            assertThat(outbox.status()).isEqualTo(SellerAuthOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("최대 재시도 횟수를 지정하여 Outbox를 생성한다")
        void createNewOutboxWithCustomMaxRetry() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            String payload = SellerFixtures.defaultAuthOutboxPayload();
            int maxRetry = 5;
            Instant now = CommonVoFixtures.now();

            // when
            SellerAuthOutbox outbox = SellerAuthOutbox.forNew(sellerId, payload, maxRetry, now);

            // then
            assertThat(outbox.maxRetry()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("assignSellerId() - SellerId 할당")
    class AssignSellerIdTest {

        @Test
        @DisplayName("SellerId를 할당한다")
        void assignSellerId() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.newSellerAuthOutbox();
            SellerId sellerId = SellerId.of(100L);

            // when
            outbox.assignSellerId(sellerId);

            // then
            assertThat(outbox.sellerId()).isEqualTo(sellerId);
            assertThat(outbox.sellerIdValue()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("startProcessing() - 처리 시작")
    class StartProcessingTest {

        @Test
        @DisplayName("PENDING 상태에서 처리를 시작한다")
        void startProcessingFromPending() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.pendingSellerAuthOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.startProcessing(now);

            // then
            assertThat(outbox.status()).isEqualTo(SellerAuthOutboxStatus.PROCESSING);
            assertThat(outbox.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("COMPLETED 상태에서 처리 시작 시 예외가 발생한다")
        void startProcessingFromCompleted_ThrowsException() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.completedSellerAuthOutbox();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(now))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("처리할 수 없는 상태");
        }

        @Test
        @DisplayName("FAILED 상태에서 처리 시작 시 예외가 발생한다")
        void startProcessingFromFailed_ThrowsException() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.failedSellerAuthOutbox();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> outbox.startProcessing(now))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("처리할 수 없는 상태");
        }
    }

    @Nested
    @DisplayName("complete() - 처리 완료")
    class CompleteTest {

        @Test
        @DisplayName("처리를 완료한다")
        void complete() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.processingSellerAuthOutbox();
            Instant now = CommonVoFixtures.now();

            // when
            outbox.complete(now);

            // then
            assertThat(outbox.status()).isEqualTo(SellerAuthOutboxStatus.COMPLETED);
            assertThat(outbox.processedAt()).isEqualTo(now);
            assertThat(outbox.errorMessage()).isNull();
            assertThat(outbox.isCompleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("failAndRetry() - 실패 및 재시도")
    class FailAndRetryTest {

        @Test
        @DisplayName("실패 후 재시도 가능하면 PENDING 상태로 변경한다")
        void failAndRetry_WhenRetryAvailable() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.pendingSellerAuthOutbox();
            String errorMessage = "연결 실패";
            Instant now = CommonVoFixtures.now();

            // when
            outbox.failAndRetry(errorMessage, now);

            // then
            assertThat(outbox.status()).isEqualTo(SellerAuthOutboxStatus.PENDING);
            assertThat(outbox.retryCount()).isEqualTo(1);
            assertThat(outbox.errorMessage()).isEqualTo(errorMessage);
            assertThat(outbox.processedAt()).isNull();
        }

        @Test
        @DisplayName("최대 재시도 횟수 초과 시 FAILED 상태로 변경한다")
        void failAndRetry_WhenMaxRetryExceeded() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.retriableSellerAuthOutbox();
            String errorMessage = "연결 실패";
            Instant now = CommonVoFixtures.now();

            // when
            outbox.failAndRetry(errorMessage, now);
            outbox.failAndRetry(errorMessage, now);

            // then
            assertThat(outbox.status()).isEqualTo(SellerAuthOutboxStatus.FAILED);
            assertThat(outbox.retryCount()).isEqualTo(3);
            assertThat(outbox.processedAt()).isEqualTo(now);
            assertThat(outbox.isFailed()).isTrue();
        }
    }

    @Nested
    @DisplayName("fail() - 즉시 실패")
    class FailTest {

        @Test
        @DisplayName("재시도 없이 즉시 실패 처리한다")
        void fail() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.pendingSellerAuthOutbox();
            String errorMessage = "복구 불가능한 에러";
            Instant now = CommonVoFixtures.now();

            // when
            outbox.fail(errorMessage, now);

            // then
            assertThat(outbox.status()).isEqualTo(SellerAuthOutboxStatus.FAILED);
            assertThat(outbox.errorMessage()).isEqualTo(errorMessage);
            assertThat(outbox.processedAt()).isEqualTo(now);
            assertThat(outbox.retryCount()).isZero();
        }
    }

    @Nested
    @DisplayName("canRetry() - 재시도 가능 여부")
    class CanRetryTest {

        @Test
        @DisplayName("PENDING 상태이고 재시도 횟수가 남았으면 true")
        void canRetry_WhenPendingAndRetryAvailable() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.pendingSellerAuthOutbox();

            // when & then
            assertThat(outbox.canRetry()).isTrue();
        }

        @Test
        @DisplayName("FAILED 상태이면 false")
        void canRetry_WhenFailed() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.failedSellerAuthOutbox();

            // when & then
            assertThat(outbox.canRetry()).isFalse();
        }

        @Test
        @DisplayName("COMPLETED 상태이면 false")
        void canRetry_WhenCompleted() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.completedSellerAuthOutbox();

            // when & then
            assertThat(outbox.canRetry()).isFalse();
        }
    }

    @Nested
    @DisplayName("shouldProcess() - 처리 대상 여부")
    class ShouldProcessTest {

        @Test
        @DisplayName("PENDING 상태이면 true")
        void shouldProcess_WhenPending() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.pendingSellerAuthOutbox();

            // when & then
            assertThat(outbox.shouldProcess()).isTrue();
            assertThat(outbox.isPending()).isTrue();
        }

        @Test
        @DisplayName("PROCESSING 상태이면 false")
        void shouldProcess_WhenProcessing() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.processingSellerAuthOutbox();

            // when & then
            assertThat(outbox.shouldProcess()).isFalse();
        }
    }

    @Nested
    @DisplayName("isProcessingTimeout() - PROCESSING 타임아웃 확인")
    class IsProcessingTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태이고 타임아웃 시간이 지났으면 true")
        void isProcessingTimeout_WhenExpired() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.processingTimeoutSellerAuthOutbox(120);
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThat(outbox.isProcessingTimeout(now, 60)).isTrue();
        }

        @Test
        @DisplayName("PROCESSING 상태이고 타임아웃 시간이 지나지 않았으면 false")
        void isProcessingTimeout_WhenNotExpired() {
            // given - 방금 PROCESSING으로 전환된 Outbox 생성
            SellerAuthOutbox outbox = SellerFixtures.pendingSellerAuthOutbox();
            Instant now = Instant.now();
            outbox.startProcessing(now);

            // when & then - 같은 시간으로 체크하면 타임아웃 아님
            assertThat(outbox.isProcessingTimeout(now, 60)).isFalse();
        }

        @Test
        @DisplayName("PENDING 상태이면 false")
        void isProcessingTimeout_WhenPending() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.pendingSellerAuthOutbox();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThat(outbox.isProcessingTimeout(now, 60)).isFalse();
        }
    }

    @Nested
    @DisplayName("recoverFromTimeout() - 타임아웃 복구")
    class RecoverFromTimeoutTest {

        @Test
        @DisplayName("PROCESSING 상태에서 타임아웃 복구한다")
        void recoverFromTimeout() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.processingTimeoutSellerAuthOutbox(120);
            Instant now = CommonVoFixtures.now();

            // when
            outbox.recoverFromTimeout(now);

            // then
            assertThat(outbox.status()).isEqualTo(SellerAuthOutboxStatus.PENDING);
            assertThat(outbox.updatedAt()).isEqualTo(now);
            assertThat(outbox.errorMessage()).contains("타임아웃");
        }

        @Test
        @DisplayName("PENDING 상태에서 타임아웃 복구 시 예외가 발생한다")
        void recoverFromTimeout_WhenPending_ThrowsException() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.pendingSellerAuthOutbox();
            Instant now = CommonVoFixtures.now();

            // when & then
            assertThatThrownBy(() -> outbox.recoverFromTimeout(now))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("PROCESSING 상태에서만");
        }
    }

    @Nested
    @DisplayName("idempotencyKey 생성 테스트")
    class IdempotencyKeyTest {

        @Test
        @DisplayName("멱등키는 SAO:{sellerId}:{epochMilli} 형식이다")
        void idempotencyKey_Format() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            Instant now = CommonVoFixtures.now();

            // when
            SellerAuthOutbox outbox =
                    SellerAuthOutbox.forNew(
                            sellerId, SellerFixtures.defaultAuthOutboxPayload(), now);

            // then
            String expectedPrefix = "SAO:" + sellerId.value() + ":";
            assertThat(outbox.idempotencyKeyValue()).startsWith(expectedPrefix);
        }

        @Test
        @DisplayName("SellerId가 null이면 0으로 생성된다")
        void idempotencyKey_WithNullSellerId() {
            // given
            Instant now = CommonVoFixtures.now();

            // when
            SellerAuthOutbox outbox =
                    SellerAuthOutbox.forNew(SellerFixtures.defaultAuthOutboxPayload(), now);

            // then
            assertThat(outbox.idempotencyKeyValue()).startsWith("SAO:0:");
        }

        @Test
        @DisplayName("VO는 equals와 hashCode가 구현되어 있다")
        void idempotencyKey_Equality() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            Instant now = CommonVoFixtures.now();
            SellerAuthOutbox outbox1 = SellerAuthOutbox.forNew(sellerId, "payload1", now);
            SellerAuthOutbox outbox2 = SellerAuthOutbox.forNew(sellerId, "payload2", now);

            // when & then - 같은 sellerId와 시간으로 생성된 키는 동일
            assertThat(outbox1.idempotencyKey()).isEqualTo(outbox2.idempotencyKey());
        }
    }
}
