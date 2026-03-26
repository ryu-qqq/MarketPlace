package com.ryuqq.marketplace.domain.exchange.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeStatus 상태 전이 규칙 테스트")
class ExchangeStatusTest {

    @Nested
    @DisplayName("COLLECTING 전이 가능 상태 테스트")
    class ToCollectingTest {

        @Test
        @DisplayName("REQUESTED에서 COLLECTING으로 전이할 수 있다")
        void requestedCanTransitionToCollecting() {
            assertThat(ExchangeStatus.REQUESTED.canTransitionTo(ExchangeStatus.COLLECTING))
                    .isTrue();
        }

        @Test
        @DisplayName("COLLECTING에서 COLLECTING으로 전이할 수 없다")
        void collectingCannotTransitionToCollecting() {
            assertThat(ExchangeStatus.COLLECTING.canTransitionTo(ExchangeStatus.COLLECTING))
                    .isFalse();
        }

        @Test
        @DisplayName("COLLECTED에서 COLLECTING으로 전이할 수 없다")
        void collectedCannotTransitionToCollecting() {
            assertThat(ExchangeStatus.COLLECTED.canTransitionTo(ExchangeStatus.COLLECTING))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("COLLECTED 전이 가능 상태 테스트")
    class ToCollectedTest {

        @Test
        @DisplayName("COLLECTING에서 COLLECTED로 전이할 수 있다")
        void collectingCanTransitionToCollected() {
            assertThat(ExchangeStatus.COLLECTING.canTransitionTo(ExchangeStatus.COLLECTED))
                    .isTrue();
        }

        @Test
        @DisplayName("REQUESTED에서 COLLECTED로 전이할 수 없다")
        void requestedCannotTransitionToCollected() {
            assertThat(ExchangeStatus.REQUESTED.canTransitionTo(ExchangeStatus.COLLECTED))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("PREPARING 전이 가능 상태 테스트")
    class ToPreparingTest {

        @Test
        @DisplayName("COLLECTED에서 PREPARING으로 전이할 수 있다")
        void collectedCanTransitionToPreparing() {
            assertThat(ExchangeStatus.COLLECTED.canTransitionTo(ExchangeStatus.PREPARING)).isTrue();
        }

        @Test
        @DisplayName("COLLECTING에서 PREPARING으로 전이할 수 없다")
        void collectingCannotTransitionToPreparing() {
            assertThat(ExchangeStatus.COLLECTING.canTransitionTo(ExchangeStatus.PREPARING))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("SHIPPING 전이 가능 상태 테스트")
    class ToShippingTest {

        @Test
        @DisplayName("PREPARING에서 SHIPPING으로 전이할 수 있다")
        void preparingCanTransitionToShipping() {
            assertThat(ExchangeStatus.PREPARING.canTransitionTo(ExchangeStatus.SHIPPING)).isTrue();
        }

        @Test
        @DisplayName("COLLECTED에서 SHIPPING으로 전이할 수 없다")
        void collectedCannotTransitionToShipping() {
            assertThat(ExchangeStatus.COLLECTED.canTransitionTo(ExchangeStatus.SHIPPING)).isFalse();
        }
    }

    @Nested
    @DisplayName("COMPLETED 전이 가능 상태 테스트")
    class ToCompletedTest {

        @Test
        @DisplayName("SHIPPING에서 COMPLETED로 전이할 수 있다")
        void shippingCanTransitionToCompleted() {
            assertThat(ExchangeStatus.SHIPPING.canTransitionTo(ExchangeStatus.COMPLETED)).isTrue();
        }

        @Test
        @DisplayName("PREPARING에서 COMPLETED로 전이할 수 없다")
        void preparingCannotTransitionToCompleted() {
            assertThat(ExchangeStatus.PREPARING.canTransitionTo(ExchangeStatus.COMPLETED))
                    .isFalse();
        }
    }

    @Nested
    @DisplayName("REJECTED 전이 가능 상태 테스트")
    class ToRejectedTest {

        @Test
        @DisplayName("REQUESTED에서 REJECTED로 전이할 수 있다")
        void requestedCanTransitionToRejected() {
            assertThat(ExchangeStatus.REQUESTED.canTransitionTo(ExchangeStatus.REJECTED)).isTrue();
        }

        @Test
        @DisplayName("COLLECTED에서 REJECTED로 전이할 수 있다")
        void collectedCanTransitionToRejected() {
            assertThat(ExchangeStatus.COLLECTED.canTransitionTo(ExchangeStatus.REJECTED)).isTrue();
        }

        @Test
        @DisplayName("PREPARING에서 REJECTED로 전이할 수 있다")
        void preparingCanTransitionToRejected() {
            assertThat(ExchangeStatus.PREPARING.canTransitionTo(ExchangeStatus.REJECTED)).isTrue();
        }

        @Test
        @DisplayName("COLLECTING에서 REJECTED로 전이할 수 있다")
        void collectingCanTransitionToRejected() {
            assertThat(ExchangeStatus.COLLECTING.canTransitionTo(ExchangeStatus.REJECTED)).isTrue();
        }

        @Test
        @DisplayName("SHIPPING에서 REJECTED로 전이할 수 없다")
        void shippingCannotTransitionToRejected() {
            assertThat(ExchangeStatus.SHIPPING.canTransitionTo(ExchangeStatus.REJECTED)).isFalse();
        }

        @Test
        @DisplayName("COMPLETED에서 REJECTED로 전이할 수 없다")
        void completedCannotTransitionToRejected() {
            assertThat(ExchangeStatus.COMPLETED.canTransitionTo(ExchangeStatus.REJECTED)).isFalse();
        }
    }

    @Nested
    @DisplayName("CANCELLED 전이 가능 상태 테스트")
    class ToCancelledTest {

        @Test
        @DisplayName("REQUESTED에서 CANCELLED로 전이할 수 있다")
        void requestedCanTransitionToCancelled() {
            assertThat(ExchangeStatus.REQUESTED.canTransitionTo(ExchangeStatus.CANCELLED)).isTrue();
        }

        @Test
        @DisplayName("COLLECTING에서 CANCELLED로 전이할 수 있다")
        void collectingCanTransitionToCancelled() {
            assertThat(ExchangeStatus.COLLECTING.canTransitionTo(ExchangeStatus.CANCELLED))
                    .isTrue();
        }

        @Test
        @DisplayName("COLLECTED에서 CANCELLED로 전이할 수 없다")
        void collectedCannotTransitionToCancelled() {
            assertThat(ExchangeStatus.COLLECTED.canTransitionTo(ExchangeStatus.CANCELLED))
                    .isFalse();
        }

        @Test
        @DisplayName("COMPLETED에서 CANCELLED로 전이할 수 없다")
        void completedCannotTransitionToCancelled() {
            assertThat(ExchangeStatus.COMPLETED.canTransitionTo(ExchangeStatus.CANCELLED))
                    .isFalse();
        }

        @Test
        @DisplayName("REJECTED에서 CANCELLED로 전이할 수 없다")
        void rejectedCannotTransitionToCancelled() {
            assertThat(ExchangeStatus.REJECTED.canTransitionTo(ExchangeStatus.CANCELLED)).isFalse();
        }
    }

    @Nested
    @DisplayName("REQUESTED 자기 자신으로의 전이 불가 테스트")
    class SelfTransitionTest {

        @Test
        @DisplayName("REQUESTED에서 REQUESTED로 전이할 수 없다")
        void requestedCannotTransitionToRequested() {
            assertThat(ExchangeStatus.REQUESTED.canTransitionTo(ExchangeStatus.REQUESTED))
                    .isFalse();
        }
    }
}
