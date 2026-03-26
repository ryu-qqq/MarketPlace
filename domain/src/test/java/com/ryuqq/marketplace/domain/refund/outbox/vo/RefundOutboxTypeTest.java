package com.ryuqq.marketplace.domain.refund.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundOutboxType 단위 테스트")
class RefundOutboxTypeTest {

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("REQUEST는 환불 요청 설명을 가진다")
        void requestHasCorrectDescription() {
            assertThat(RefundOutboxType.REQUEST.description()).isEqualTo("환불 요청");
        }

        @Test
        @DisplayName("APPROVE는 환불 승인 설명을 가진다")
        void approveHasCorrectDescription() {
            assertThat(RefundOutboxType.APPROVE.description()).isEqualTo("환불 승인");
        }

        @Test
        @DisplayName("COLLECT는 수거 완료 설명을 가진다")
        void collectHasCorrectDescription() {
            assertThat(RefundOutboxType.COLLECT.description()).isEqualTo("수거 완료");
        }

        @Test
        @DisplayName("REJECT는 환불 거절 설명을 가진다")
        void rejectHasCorrectDescription() {
            assertThat(RefundOutboxType.REJECT.description()).isEqualTo("환불 거절");
        }

        @Test
        @DisplayName("COMPLETE는 환불 완료 설명을 가진다")
        void completeHasCorrectDescription() {
            assertThat(RefundOutboxType.COMPLETE.description()).isEqualTo("환불 완료");
        }

        @Test
        @DisplayName("HOLD는 환불 보류 설명을 가진다")
        void holdHasCorrectDescription() {
            assertThat(RefundOutboxType.HOLD.description()).isEqualTo("환불 보류");
        }

        @Test
        @DisplayName("RELEASE_HOLD는 환불 보류 해제 설명을 가진다")
        void releaseHoldHasCorrectDescription() {
            assertThat(RefundOutboxType.RELEASE_HOLD.description()).isEqualTo("환불 보류 해제");
        }
    }

    @Nested
    @DisplayName("enum 값 테스트")
    class EnumValuesTest {

        @Test
        @DisplayName("7개의 타입이 정의되어 있다")
        void hasSevenTypes() {
            assertThat(RefundOutboxType.values()).hasSize(7);
        }

        @Test
        @DisplayName("모든 타입이 존재한다")
        void allTypesExist() {
            assertThat(RefundOutboxType.values())
                    .containsExactly(
                            RefundOutboxType.REQUEST,
                            RefundOutboxType.APPROVE,
                            RefundOutboxType.COLLECT,
                            RefundOutboxType.REJECT,
                            RefundOutboxType.COMPLETE,
                            RefundOutboxType.HOLD,
                            RefundOutboxType.RELEASE_HOLD);
        }

        @Test
        @DisplayName("모든 타입은 비어있지 않은 설명을 가진다")
        void allTypesHaveNonBlankDescription() {
            for (RefundOutboxType type : RefundOutboxType.values()) {
                assertThat(type.description()).isNotBlank();
            }
        }
    }
}
