package com.ryuqq.marketplace.domain.order.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OrderItemStatus 상태 전이 규칙 단위 테스트")
class OrderItemStatusTest {

    @Nested
    @DisplayName("READY 상태에서 전이 가능한 상태")
    class FromReadyTest {

        @Test
        @DisplayName("READY -> CONFIRMED 전이 가능하다")
        void readyCanTransitionToConfirmed() {
            assertThat(OrderItemStatus.READY.canTransitionTo(OrderItemStatus.CONFIRMED)).isTrue();
        }

        @Test
        @DisplayName("READY -> CANCELLED 전이 가능하다")
        void readyCanTransitionToCancelled() {
            assertThat(OrderItemStatus.READY.canTransitionTo(OrderItemStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("READY -> RETURN_REQUESTED 전이 불가능하다")
        void readyCannotTransitionToReturnRequested() {
            assertThat(OrderItemStatus.READY.canTransitionTo(OrderItemStatus.RETURN_REQUESTED))
                    .isFalse();
        }

        @Test
        @DisplayName("READY -> RETURNED 전이 불가능하다")
        void readyCannotTransitionToReturned() {
            assertThat(OrderItemStatus.READY.canTransitionTo(OrderItemStatus.RETURNED)).isFalse();
        }

        @Test
        @DisplayName("READY -> READY 전이 불가능하다")
        void readyCannotTransitionToReady() {
            assertThat(OrderItemStatus.READY.canTransitionTo(OrderItemStatus.READY)).isFalse();
        }
    }

    @Nested
    @DisplayName("CONFIRMED 상태에서 전이 가능한 상태")
    class FromConfirmedTest {

        @Test
        @DisplayName("CONFIRMED -> CANCELLED 전이 가능하다")
        void confirmedCanTransitionToCancelled() {
            assertThat(OrderItemStatus.CONFIRMED.canTransitionTo(OrderItemStatus.CANCELLED))
                    .isTrue();
        }

        @Test
        @DisplayName("CONFIRMED -> RETURN_REQUESTED 전이 가능하다")
        void confirmedCanTransitionToReturnRequested() {
            assertThat(OrderItemStatus.CONFIRMED.canTransitionTo(OrderItemStatus.RETURN_REQUESTED))
                    .isTrue();
        }

        @Test
        @DisplayName("CONFIRMED -> CONFIRMED 전이 불가능하다")
        void confirmedCannotTransitionToConfirmed() {
            assertThat(OrderItemStatus.CONFIRMED.canTransitionTo(OrderItemStatus.CONFIRMED))
                    .isFalse();
        }

        @Test
        @DisplayName("CONFIRMED -> READY 전이 불가능하다")
        void confirmedCannotTransitionToReady() {
            assertThat(OrderItemStatus.CONFIRMED.canTransitionTo(OrderItemStatus.READY)).isFalse();
        }

        @Test
        @DisplayName("CONFIRMED -> RETURNED 전이 불가능하다")
        void confirmedCannotTransitionToReturned() {
            assertThat(OrderItemStatus.CONFIRMED.canTransitionTo(OrderItemStatus.RETURNED))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("CANCELLED 상태에서 전이")
    class FromCancelledTest {

        @Test
        @DisplayName("CANCELLED 상태에서 어떤 상태로도 전이 불가능하다")
        void cancelledCannotTransitionToAnyStatus() {
            for (OrderItemStatus target : OrderItemStatus.values()) {
                assertThat(OrderItemStatus.CANCELLED.canTransitionTo(target))
                        .as("CANCELLED -> %s 전이 불가능해야 한다", target)
                        .isFalse();
            }
        }
    }

    @Nested
    @DisplayName("RETURN_REQUESTED 상태에서 전이")
    class FromReturnRequestedTest {

        @Test
        @DisplayName("RETURN_REQUESTED -> RETURNED 전이 가능하다")
        void returnRequestedCanTransitionToReturned() {
            assertThat(OrderItemStatus.RETURN_REQUESTED.canTransitionTo(OrderItemStatus.RETURNED))
                    .isTrue();
        }

        @Test
        @DisplayName("RETURN_REQUESTED -> CONFIRMED 전이 불가능하다")
        void returnRequestedCannotTransitionToConfirmed() {
            assertThat(OrderItemStatus.RETURN_REQUESTED.canTransitionTo(OrderItemStatus.CONFIRMED))
                    .isFalse();
        }

        @Test
        @DisplayName("RETURN_REQUESTED -> CANCELLED 전이 불가능하다")
        void returnRequestedCannotTransitionToCancelled() {
            assertThat(OrderItemStatus.RETURN_REQUESTED.canTransitionTo(OrderItemStatus.CANCELLED))
                    .isFalse();
        }

        @Test
        @DisplayName("RETURN_REQUESTED -> READY 전이 불가능하다")
        void returnRequestedCannotTransitionToReady() {
            assertThat(OrderItemStatus.RETURN_REQUESTED.canTransitionTo(OrderItemStatus.READY))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("RETURNED 상태에서 전이")
    class FromReturnedTest {

        @Test
        @DisplayName("RETURNED 상태에서 어떤 상태로도 전이 불가능하다")
        void returnedCannotTransitionToAnyStatus() {
            for (OrderItemStatus target : OrderItemStatus.values()) {
                assertThat(OrderItemStatus.RETURNED.canTransitionTo(target))
                        .as("RETURNED -> %s 전이 불가능해야 한다", target)
                        .isFalse();
            }
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("모든 OrderItemStatus 값이 정의되어 있다")
        void allValuesExist() {
            assertThat(OrderItemStatus.values())
                    .containsExactly(
                            OrderItemStatus.READY,
                            OrderItemStatus.CONFIRMED,
                            OrderItemStatus.CANCELLED,
                            OrderItemStatus.RETURN_REQUESTED,
                            OrderItemStatus.RETURNED);
        }
    }
}
