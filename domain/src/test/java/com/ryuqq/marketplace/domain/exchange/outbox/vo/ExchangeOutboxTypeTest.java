package com.ryuqq.marketplace.domain.exchange.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExchangeOutboxType 단위 테스트")
class ExchangeOutboxTypeTest {

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("COLLECT는 수거 완료 설명을 가진다")
        void collectHasCorrectDescription() {
            assertThat(ExchangeOutboxType.COLLECT.description()).isEqualTo("수거 완료");
        }

        @Test
        @DisplayName("SHIP은 재배송 설명을 가진다")
        void shipHasCorrectDescription() {
            assertThat(ExchangeOutboxType.SHIP.description()).isEqualTo("재배송");
        }

        @Test
        @DisplayName("REJECT는 교환 거절 설명을 가진다")
        void rejectHasCorrectDescription() {
            assertThat(ExchangeOutboxType.REJECT.description()).isEqualTo("교환 거절");
        }

        @Test
        @DisplayName("HOLD는 교환 보류 설명을 가진다")
        void holdHasCorrectDescription() {
            assertThat(ExchangeOutboxType.HOLD.description()).isEqualTo("교환 보류");
        }

        @Test
        @DisplayName("RELEASE_HOLD는 교환 보류 해제 설명을 가진다")
        void releaseHoldHasCorrectDescription() {
            assertThat(ExchangeOutboxType.RELEASE_HOLD.description()).isEqualTo("교환 보류 해제");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("5개의 타입이 정의되어 있다")
        void hasFiveTypes() {
            assertThat(ExchangeOutboxType.values()).hasSize(5);
        }

        @Test
        @DisplayName("모든 타입이 존재한다")
        void allTypesExist() {
            assertThat(ExchangeOutboxType.values())
                    .containsExactly(
                            ExchangeOutboxType.COLLECT,
                            ExchangeOutboxType.SHIP,
                            ExchangeOutboxType.REJECT,
                            ExchangeOutboxType.HOLD,
                            ExchangeOutboxType.RELEASE_HOLD);
        }

        @Test
        @DisplayName("모든 타입은 비어있지 않은 설명을 가진다")
        void allTypesHaveNonBlankDescription() {
            for (ExchangeOutboxType type : ExchangeOutboxType.values()) {
                assertThat(type.description()).isNotBlank();
            }
        }
    }
}
