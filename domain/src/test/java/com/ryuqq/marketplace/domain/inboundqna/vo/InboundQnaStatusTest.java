package com.ryuqq.marketplace.domain.inboundqna.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundQnaStatus 단위 테스트")
class InboundQnaStatusTest {

    @Nested
    @DisplayName("canConvert() 테스트")
    class CanConvertTest {

        @Test
        @DisplayName("RECEIVED 상태에서만 canConvert()가 true이다")
        void onlyReceivedCanConvert() {
            assertThat(InboundQnaStatus.RECEIVED.canConvert()).isTrue();
            assertThat(InboundQnaStatus.CONVERTED.canConvert()).isFalse();
            assertThat(InboundQnaStatus.FAILED.canConvert()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() 테스트")
    class IsTerminalTest {

        @Test
        @DisplayName("CONVERTED와 FAILED는 isTerminal()이 true이다")
        void convertedAndFailedAreTerminal() {
            assertThat(InboundQnaStatus.CONVERTED.isTerminal()).isTrue();
            assertThat(InboundQnaStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("RECEIVED는 isTerminal()이 false이다")
        void receivedIsNotTerminal() {
            assertThat(InboundQnaStatus.RECEIVED.isTerminal()).isFalse();
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("3개의 상태가 정의되어 있다")
        void hasThreeStatuses() {
            assertThat(InboundQnaStatus.values()).hasSize(3);
        }

        @Test
        @DisplayName("모든 상태 값이 존재한다")
        void allStatusesExist() {
            assertThat(InboundQnaStatus.values())
                    .containsExactly(
                            InboundQnaStatus.RECEIVED,
                            InboundQnaStatus.CONVERTED,
                            InboundQnaStatus.FAILED);
        }
    }
}
