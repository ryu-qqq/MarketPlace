package com.ryuqq.marketplace.domain.order.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import com.ryuqq.marketplace.domain.order.exception.OrderException;
import com.ryuqq.marketplace.domain.order.vo.OrderItemStatus;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderItem 상태 전이 단위 테스트")
class OrderItemStatusTransitionTest {

    @Nested
    @DisplayName("confirm() - 구매 확정")
    class ConfirmTest {

        @Test
        @DisplayName("READY 상태의 주문 상품을 CONFIRMED로 변경한다")
        void confirmReadyOrderItem() {
            // given
            OrderItem item = OrderFixtures.defaultOrderItem();
            Instant now = CommonVoFixtures.now();

            // when
            item.confirm("system", now);

            // then
            assertThat(item.status()).isEqualTo(OrderItemStatus.CONFIRMED);
        }

        @Test
        @DisplayName("confirm 후 이력이 기록된다")
        void confirmRecordsHistory() {
            // given
            OrderItem item = OrderFixtures.defaultOrderItem();
            Instant now = CommonVoFixtures.now();

            // when
            item.confirm("admin", now);

            // then
            assertThat(item.histories()).hasSize(1);
            OrderItemHistory history = item.histories().get(0);
            assertThat(history.fromStatus()).isEqualTo(OrderItemStatus.READY);
            assertThat(history.toStatus()).isEqualTo(OrderItemStatus.CONFIRMED);
            assertThat(history.changedBy()).isEqualTo("admin");
            assertThat(history.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("CONFIRMED 상태에서 confirm을 호출하면 예외가 발생한다")
        void confirmConfirmedOrderItem_ThrowsException() {
            // given
            OrderItem item = OrderFixtures.confirmedOrderItem();

            // when & then
            assertThatThrownBy(() -> item.confirm("system", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }

        @Test
        @DisplayName("CANCELLED 상태에서 confirm을 호출하면 예외가 발생한다")
        void confirmCancelledOrderItem_ThrowsException() {
            // given
            OrderItem item = OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.CANCELLED);

            // when & then
            assertThatThrownBy(() -> item.confirm("system", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }
    }

    @Nested
    @DisplayName("cancel() - 주문 취소")
    class CancelTest {

        @Test
        @DisplayName("READY 상태의 주문 상품을 CANCELLED로 변경한다")
        void cancelReadyOrderItem() {
            // given
            OrderItem item = OrderFixtures.defaultOrderItem();
            Instant now = CommonVoFixtures.now();

            // when
            item.cancel("seller", "단순 변심", now);

            // then
            assertThat(item.status()).isEqualTo(OrderItemStatus.CANCELLED);
        }

        @Test
        @DisplayName("CONFIRMED 상태의 주문 상품을 CANCELLED로 변경한다")
        void cancelConfirmedOrderItem() {
            // given
            OrderItem item = OrderFixtures.confirmedOrderItem();
            Instant now = CommonVoFixtures.now();

            // when
            item.cancel("seller", "품절", now);

            // then
            assertThat(item.status()).isEqualTo(OrderItemStatus.CANCELLED);
        }

        @Test
        @DisplayName("cancel 후 취소 이유가 이력에 기록된다")
        void cancelRecordsReasonInHistory() {
            // given
            OrderItem item = OrderFixtures.defaultOrderItem();
            String reason = "단순 변심";
            Instant now = CommonVoFixtures.now();

            // when
            item.cancel("customer", reason, now);

            // then
            assertThat(item.histories()).hasSize(1);
            OrderItemHistory history = item.histories().get(0);
            assertThat(history.reason()).isEqualTo(reason);
            assertThat(history.fromStatus()).isEqualTo(OrderItemStatus.READY);
            assertThat(history.toStatus()).isEqualTo(OrderItemStatus.CANCELLED);
        }

        @Test
        @DisplayName("CANCELLED 상태에서 cancel을 호출하면 예외가 발생한다")
        void cancelCancelledOrderItem_ThrowsException() {
            // given
            OrderItem item = OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.CANCELLED);

            // when & then
            assertThatThrownBy(() -> item.cancel("system", "이유", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }
    }

    @Nested
    @DisplayName("partialCancel() - 부분 취소")
    class PartialCancelTest {

        @Test
        @DisplayName("qty=2 중 1건 부분취소 시 상태는 READY를 유지한다")
        void partialCancel_KeepsReadyStatus() {
            OrderItem item = OrderFixtures.defaultOrderItem();
            Instant now = CommonVoFixtures.now();

            item.partialCancel(1, "seller", "부분취소", now);

            assertThat(item.status()).isEqualTo(OrderItemStatus.READY);
            assertThat(item.cancelledQty()).isEqualTo(1);
            assertThat(item.remainingCancelableQty()).isEqualTo(1);
            assertThat(item.isFullyCancelled()).isFalse();
        }

        @Test
        @DisplayName("qty=2 전량 취소 시 CANCELLED로 전환된다")
        void partialCancel_AllQty_TransitionsToCancelled() {
            OrderItem item = OrderFixtures.defaultOrderItem();
            Instant now = CommonVoFixtures.now();

            item.partialCancel(2, "seller", "전량취소", now);

            assertThat(item.status()).isEqualTo(OrderItemStatus.CANCELLED);
            assertThat(item.cancelledQty()).isEqualTo(2);
            assertThat(item.isFullyCancelled()).isTrue();
        }

        @Test
        @DisplayName("2회에 걸친 부분취소로 전량 소진 시 CANCELLED 전환")
        void partialCancel_TwoSteps_TransitionsToCancelled() {
            OrderItem item = OrderFixtures.defaultOrderItem();
            Instant now = CommonVoFixtures.now();

            item.partialCancel(1, "seller", "1차 부분취소", now);
            assertThat(item.status()).isEqualTo(OrderItemStatus.READY);

            item.partialCancel(1, "seller", "2차 부분취소", now);
            assertThat(item.status()).isEqualTo(OrderItemStatus.CANCELLED);
            assertThat(item.cancelledQty()).isEqualTo(2);
            assertThat(item.histories()).hasSize(2);
        }

        @Test
        @DisplayName("취소 가능 수량을 초과하면 예외가 발생한다")
        void partialCancel_ExceedsRemaining_ThrowsException() {
            OrderItem item = OrderFixtures.defaultOrderItem();
            Instant now = CommonVoFixtures.now();

            assertThatThrownBy(() -> item.partialCancel(3, "seller", "초과", now))
                    .isInstanceOf(OrderException.class);
        }

        @Test
        @DisplayName("cancelQty=0 이면 예외가 발생한다")
        void partialCancel_ZeroQty_ThrowsException() {
            OrderItem item = OrderFixtures.defaultOrderItem();
            Instant now = CommonVoFixtures.now();

            assertThatThrownBy(() -> item.partialCancel(0, "seller", "0개", now))
                    .isInstanceOf(OrderException.class);
        }

        @Test
        @DisplayName("부분취소 이력에 수량이 기록된다")
        void partialCancel_RecordsQuantityInHistory() {
            OrderItem item = OrderFixtures.defaultOrderItem();
            Instant now = CommonVoFixtures.now();

            item.partialCancel(1, "seller", "부분취소", now);

            OrderItemHistory history = item.histories().get(0);
            assertThat(history.quantity()).isEqualTo(1);
            assertThat(history.reason()).isEqualTo("부분취소");
        }
    }

    @Nested
    @DisplayName("requestReturn() - 반품 요청")
    class RequestReturnTest {

        @Test
        @DisplayName("CONFIRMED 상태의 주문 상품에 반품 요청한다")
        void requestReturnForConfirmedOrderItem() {
            // given
            OrderItem item = OrderFixtures.confirmedOrderItem();
            Instant now = CommonVoFixtures.now();

            // when
            item.requestReturn("customer", "불량품", now);

            // then
            assertThat(item.status()).isEqualTo(OrderItemStatus.RETURN_REQUESTED);
        }

        @Test
        @DisplayName("READY 상태에서 반품 요청하면 예외가 발생한다")
        void requestReturnForReadyOrderItem_ThrowsException() {
            // given
            OrderItem item = OrderFixtures.defaultOrderItem();

            // when & then
            assertThatThrownBy(() -> item.requestReturn("customer", "이유", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }

        @Test
        @DisplayName("requestReturn 후 이력이 기록된다")
        void requestReturnRecordsHistory() {
            // given
            OrderItem item = OrderFixtures.confirmedOrderItem();
            String reason = "불량품";
            Instant now = CommonVoFixtures.now();

            // when
            item.requestReturn("customer", reason, now);

            // then
            assertThat(item.histories()).hasSize(1);
            OrderItemHistory history = item.histories().get(0);
            assertThat(history.fromStatus()).isEqualTo(OrderItemStatus.CONFIRMED);
            assertThat(history.toStatus()).isEqualTo(OrderItemStatus.RETURN_REQUESTED);
            assertThat(history.reason()).isEqualTo(reason);
        }
    }

    @Nested
    @DisplayName("completeReturn() - 반품 완료")
    class CompleteReturnTest {

        @Test
        @DisplayName("RETURN_REQUESTED 상태의 주문 상품을 RETURNED로 변경한다")
        void completeReturnForReturnRequestedOrderItem() {
            // given
            OrderItem item =
                    OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.RETURN_REQUESTED);
            Instant now = CommonVoFixtures.now();

            // when
            item.completeReturn("admin", now);

            // then
            assertThat(item.status()).isEqualTo(OrderItemStatus.RETURNED);
        }

        @Test
        @DisplayName("CONFIRMED 상태에서 반품 완료하면 예외가 발생한다")
        void completeReturnForConfirmedOrderItem_ThrowsException() {
            // given
            OrderItem item = OrderFixtures.confirmedOrderItem();

            // when & then
            assertThatThrownBy(() -> item.completeReturn("admin", CommonVoFixtures.now()))
                    .isInstanceOf(OrderException.class);
        }
    }

    @Nested
    @DisplayName("isConfirmable() - 구매 확정 가능 여부")
    class IsConfirmableTest {

        @Test
        @DisplayName("READY 상태면 구매 확정 가능하다")
        void readyOrderItemIsConfirmable() {
            // given
            OrderItem item = OrderFixtures.defaultOrderItem();

            // then
            assertThat(item.isConfirmable()).isTrue();
        }

        @Test
        @DisplayName("CONFIRMED 상태면 구매 확정 불가능하다")
        void confirmedOrderItemIsNotConfirmable() {
            // given
            OrderItem item = OrderFixtures.confirmedOrderItem();

            // then
            assertThat(item.isConfirmable()).isFalse();
        }

        @Test
        @DisplayName("CANCELLED 상태면 구매 확정 불가능하다")
        void cancelledOrderItemIsNotConfirmable() {
            // given
            OrderItem item = OrderFixtures.reconstitutedOrderItem(1L, OrderItemStatus.CANCELLED);

            // then
            assertThat(item.isConfirmable()).isFalse();
        }
    }
}
