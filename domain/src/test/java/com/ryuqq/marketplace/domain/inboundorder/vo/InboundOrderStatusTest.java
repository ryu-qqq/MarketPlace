package com.ryuqq.marketplace.domain.inboundorder.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundOrderStatus 단위 테스트")
class InboundOrderStatusTest {

    @Nested
    @DisplayName("canApplyMapping() 테스트")
    class CanApplyMappingTest {

        @Test
        @DisplayName("RECEIVED 상태는 매핑 가능하다")
        void receivedCanApplyMapping() {
            assertThat(InboundOrderStatus.RECEIVED.canApplyMapping()).isTrue();
        }

        @Test
        @DisplayName("PENDING_MAPPING 상태는 매핑 가능하다")
        void pendingMappingCanApplyMapping() {
            assertThat(InboundOrderStatus.PENDING_MAPPING.canApplyMapping()).isTrue();
        }

        @Test
        @DisplayName("MAPPED 상태는 매핑 불가하다")
        void mappedCannotApplyMapping() {
            assertThat(InboundOrderStatus.MAPPED.canApplyMapping()).isFalse();
        }

        @Test
        @DisplayName("CONVERTED 상태는 매핑 불가하다")
        void convertedCannotApplyMapping() {
            assertThat(InboundOrderStatus.CONVERTED.canApplyMapping()).isFalse();
        }

        @Test
        @DisplayName("FAILED 상태는 매핑 불가하다")
        void failedCannotApplyMapping() {
            assertThat(InboundOrderStatus.FAILED.canApplyMapping()).isFalse();
        }
    }

    @Nested
    @DisplayName("canConvert() 테스트")
    class CanConvertTest {

        @Test
        @DisplayName("MAPPED 상태만 변환 가능하다")
        void onlyMappedCanConvert() {
            assertThat(InboundOrderStatus.MAPPED.canConvert()).isTrue();
            assertThat(InboundOrderStatus.RECEIVED.canConvert()).isFalse();
            assertThat(InboundOrderStatus.PENDING_MAPPING.canConvert()).isFalse();
            assertThat(InboundOrderStatus.CONVERTED.canConvert()).isFalse();
            assertThat(InboundOrderStatus.FAILED.canConvert()).isFalse();
        }
    }

    @Nested
    @DisplayName("isTerminal() 테스트")
    class IsTerminalTest {

        @Test
        @DisplayName("CONVERTED와 FAILED는 종료 상태이다")
        void convertedAndFailedAreTerminal() {
            assertThat(InboundOrderStatus.CONVERTED.isTerminal()).isTrue();
            assertThat(InboundOrderStatus.FAILED.isTerminal()).isTrue();
        }

        @Test
        @DisplayName("RECEIVED, PENDING_MAPPING, MAPPED는 종료 상태가 아니다")
        void otherStatusesAreNotTerminal() {
            assertThat(InboundOrderStatus.RECEIVED.isTerminal()).isFalse();
            assertThat(InboundOrderStatus.PENDING_MAPPING.isTerminal()).isFalse();
            assertThat(InboundOrderStatus.MAPPED.isTerminal()).isFalse();
        }
    }
}
