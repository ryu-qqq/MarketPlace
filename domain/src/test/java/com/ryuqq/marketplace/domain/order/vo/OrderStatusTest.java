package com.ryuqq.marketplace.domain.order.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderStatus canTransitionTo 테스트")
class OrderStatusTest {

    @Nested
    @DisplayName("ORDERED 상태에서 허용되는 전이")
    class FromOrderedTest {

        @Test
        @DisplayName("ORDERED → PREPARING 전이가 가능하다")
        void orderedCanTransitionToPreparing() {
            assertThat(OrderStatus.ORDERED.canTransitionTo(OrderStatus.PREPARING)).isTrue();
        }

        @Test
        @DisplayName("ORDERED → CANCELLED 전이가 가능하다")
        void orderedCanTransitionToCancelled() {
            assertThat(OrderStatus.ORDERED.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("ORDERED → SHIPPED 전이는 불가하다")
        void orderedCannotTransitionToShipped() {
            assertThat(OrderStatus.ORDERED.canTransitionTo(OrderStatus.SHIPPED)).isFalse();
        }

        @Test
        @DisplayName("ORDERED → DELIVERED 전이는 불가하다")
        void orderedCannotTransitionToDelivered() {
            assertThat(OrderStatus.ORDERED.canTransitionTo(OrderStatus.DELIVERED)).isFalse();
        }

        @Test
        @DisplayName("ORDERED → CONFIRMED 전이는 불가하다")
        void orderedCannotTransitionToConfirmed() {
            assertThat(OrderStatus.ORDERED.canTransitionTo(OrderStatus.CONFIRMED)).isFalse();
        }

        @Test
        @DisplayName("ORDERED → CLAIM_IN_PROGRESS 전이는 불가하다")
        void orderedCannotTransitionToClaimInProgress() {
            assertThat(OrderStatus.ORDERED.canTransitionTo(OrderStatus.CLAIM_IN_PROGRESS))
                    .isFalse();
        }

        @Test
        @DisplayName("ORDERED → REFUNDED 전이는 불가하다")
        void orderedCannotTransitionToRefunded() {
            assertThat(OrderStatus.ORDERED.canTransitionTo(OrderStatus.REFUNDED)).isFalse();
        }

        @Test
        @DisplayName("ORDERED → EXCHANGED 전이는 불가하다")
        void orderedCannotTransitionToExchanged() {
            assertThat(OrderStatus.ORDERED.canTransitionTo(OrderStatus.EXCHANGED)).isFalse();
        }
    }

    @Nested
    @DisplayName("PREPARING 상태에서 허용되는 전이")
    class FromPreparingTest {

        @Test
        @DisplayName("PREPARING → SHIPPED 전이가 가능하다")
        void preparingCanTransitionToShipped() {
            assertThat(OrderStatus.PREPARING.canTransitionTo(OrderStatus.SHIPPED)).isTrue();
        }

        @Test
        @DisplayName("PREPARING → CANCELLED 전이가 가능하다")
        void preparingCanTransitionToCancelled() {
            assertThat(OrderStatus.PREPARING.canTransitionTo(OrderStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("PREPARING → ORDERED 전이는 불가하다")
        void preparingCannotTransitionToOrdered() {
            assertThat(OrderStatus.PREPARING.canTransitionTo(OrderStatus.ORDERED)).isFalse();
        }

        @Test
        @DisplayName("PREPARING → DELIVERED 전이는 불가하다")
        void preparingCannotTransitionToDelivered() {
            assertThat(OrderStatus.PREPARING.canTransitionTo(OrderStatus.DELIVERED)).isFalse();
        }
    }

    @Nested
    @DisplayName("SHIPPED 상태에서 허용되는 전이")
    class FromShippedTest {

        @Test
        @DisplayName("SHIPPED → DELIVERED 전이가 가능하다")
        void shippedCanTransitionToDelivered() {
            assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.DELIVERED)).isTrue();
        }

        @Test
        @DisplayName("SHIPPED → CLAIM_IN_PROGRESS 전이가 가능하다")
        void shippedCanTransitionToClaimInProgress() {
            assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.CLAIM_IN_PROGRESS)).isTrue();
        }

        @Test
        @DisplayName("SHIPPED → CANCELLED 전이는 불가하다")
        void shippedCannotTransitionToCancelled() {
            assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.CANCELLED)).isFalse();
        }

        @Test
        @DisplayName("SHIPPED → CONFIRMED 전이는 불가하다")
        void shippedCannotTransitionToConfirmed() {
            assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.CONFIRMED)).isFalse();
        }
    }

    @Nested
    @DisplayName("DELIVERED 상태에서 허용되는 전이")
    class FromDeliveredTest {

        @Test
        @DisplayName("DELIVERED → CONFIRMED 전이가 가능하다")
        void deliveredCanTransitionToConfirmed() {
            assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CONFIRMED)).isTrue();
        }

        @Test
        @DisplayName("DELIVERED → CLAIM_IN_PROGRESS 전이가 가능하다")
        void deliveredCanTransitionToClaimInProgress() {
            assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CLAIM_IN_PROGRESS))
                    .isTrue();
        }

        @Test
        @DisplayName("DELIVERED → CANCELLED 전이는 불가하다")
        void deliveredCannotTransitionToCancelled() {
            assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CANCELLED)).isFalse();
        }

        @Test
        @DisplayName("DELIVERED → REFUNDED 전이는 불가하다")
        void deliveredCannotTransitionToRefunded() {
            assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.REFUNDED)).isFalse();
        }
    }

    @Nested
    @DisplayName("CONFIRMED 상태에서 허용되는 전이")
    class FromConfirmedTest {

        @Test
        @DisplayName("CONFIRMED는 어떤 상태로도 전이할 수 없다")
        void confirmedCannotTransitionToAnyStatus() {
            for (OrderStatus target : OrderStatus.values()) {
                assertThat(OrderStatus.CONFIRMED.canTransitionTo(target))
                        .as("CONFIRMED → %s 는 불가해야 한다", target)
                        .isFalse();
            }
        }
    }

    @Nested
    @DisplayName("CANCELLED 상태에서 허용되는 전이")
    class FromCancelledTest {

        @Test
        @DisplayName("CANCELLED는 어떤 상태로도 전이할 수 없다")
        void cancelledCannotTransitionToAnyStatus() {
            for (OrderStatus target : OrderStatus.values()) {
                assertThat(OrderStatus.CANCELLED.canTransitionTo(target))
                        .as("CANCELLED → %s 는 불가해야 한다", target)
                        .isFalse();
            }
        }
    }

    @Nested
    @DisplayName("CLAIM_IN_PROGRESS 상태에서 허용되는 전이")
    class FromClaimInProgressTest {

        @Test
        @DisplayName("CLAIM_IN_PROGRESS → REFUNDED 전이가 가능하다")
        void claimInProgressCanTransitionToRefunded() {
            assertThat(OrderStatus.CLAIM_IN_PROGRESS.canTransitionTo(OrderStatus.REFUNDED))
                    .isTrue();
        }

        @Test
        @DisplayName("CLAIM_IN_PROGRESS → EXCHANGED 전이가 가능하다")
        void claimInProgressCanTransitionToExchanged() {
            assertThat(OrderStatus.CLAIM_IN_PROGRESS.canTransitionTo(OrderStatus.EXCHANGED))
                    .isTrue();
        }

        @Test
        @DisplayName("CLAIM_IN_PROGRESS → CONFIRMED 전이는 불가하다")
        void claimInProgressCannotTransitionToConfirmed() {
            assertThat(OrderStatus.CLAIM_IN_PROGRESS.canTransitionTo(OrderStatus.CONFIRMED))
                    .isFalse();
        }

        @Test
        @DisplayName("CLAIM_IN_PROGRESS → CANCELLED 전이는 불가하다")
        void claimInProgressCannotTransitionToCancelled() {
            assertThat(OrderStatus.CLAIM_IN_PROGRESS.canTransitionTo(OrderStatus.CANCELLED))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("REFUNDED, EXCHANGED 상태에서 허용되는 전이")
    class FromTerminalStatusTest {

        @Test
        @DisplayName("REFUNDED는 어떤 상태로도 전이할 수 없다")
        void refundedCannotTransitionToAnyStatus() {
            for (OrderStatus target : OrderStatus.values()) {
                assertThat(OrderStatus.REFUNDED.canTransitionTo(target))
                        .as("REFUNDED → %s 는 불가해야 한다", target)
                        .isFalse();
            }
        }

        @Test
        @DisplayName("EXCHANGED는 어떤 상태로도 전이할 수 없다")
        void exchangedCannotTransitionToAnyStatus() {
            for (OrderStatus target : OrderStatus.values()) {
                assertThat(OrderStatus.EXCHANGED.canTransitionTo(target))
                        .as("EXCHANGED → %s 는 불가해야 한다", target)
                        .isFalse();
            }
        }
    }

    @Nested
    @DisplayName("전체 happy path 전이 체인 테스트")
    class TransitionChainTest {

        @Test
        @DisplayName("정상 주문 처리 체인: ORDERED → PREPARING → SHIPPED → DELIVERED → CONFIRMED")
        void normalOrderChain() {
            assertThat(OrderStatus.ORDERED.canTransitionTo(OrderStatus.PREPARING)).isTrue();
            assertThat(OrderStatus.PREPARING.canTransitionTo(OrderStatus.SHIPPED)).isTrue();
            assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.DELIVERED)).isTrue();
            assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CONFIRMED)).isTrue();
        }

        @Test
        @DisplayName("클레임 처리 체인: DELIVERED → CLAIM_IN_PROGRESS → REFUNDED")
        void claimRefundChain() {
            assertThat(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.CLAIM_IN_PROGRESS))
                    .isTrue();
            assertThat(OrderStatus.CLAIM_IN_PROGRESS.canTransitionTo(OrderStatus.REFUNDED))
                    .isTrue();
        }

        @Test
        @DisplayName("교환 처리 체인: SHIPPED → CLAIM_IN_PROGRESS → EXCHANGED")
        void claimExchangeChain() {
            assertThat(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.CLAIM_IN_PROGRESS)).isTrue();
            assertThat(OrderStatus.CLAIM_IN_PROGRESS.canTransitionTo(OrderStatus.EXCHANGED))
                    .isTrue();
        }
    }
}
