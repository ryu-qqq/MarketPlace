package com.ryuqq.marketplace.domain.cancel.outbox.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelOutboxType 단위 테스트")
class CancelOutboxTypeTest {

    @Nested
    @DisplayName("description() 테스트")
    class DescriptionTest {

        @Test
        @DisplayName("SELLER_CANCEL의 description은 판매자 취소이다")
        void sellerCancelDescription() {
            assertThat(CancelOutboxType.SELLER_CANCEL.description()).isEqualTo("판매자 취소");
        }

        @Test
        @DisplayName("APPROVE의 description은 취소 승인이다")
        void approveDescription() {
            assertThat(CancelOutboxType.APPROVE.description()).isEqualTo("취소 승인");
        }

        @Test
        @DisplayName("REJECT의 description은 취소 거절이다")
        void rejectDescription() {
            assertThat(CancelOutboxType.REJECT.description()).isEqualTo("취소 거절");
        }
    }

    @Nested
    @DisplayName("enum 값 검증")
    class EnumValueTest {

        @Test
        @DisplayName("CancelOutboxType은 3가지 값이다")
        void outboxTypeValues() {
            assertThat(CancelOutboxType.values())
                    .containsExactlyInAnyOrder(
                            CancelOutboxType.SELLER_CANCEL,
                            CancelOutboxType.APPROVE,
                            CancelOutboxType.REJECT);
        }
    }
}
