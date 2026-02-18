package com.ryuqq.marketplace.domain.refund.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundReason Value Object 단위 테스트")
class RefundReasonTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 환불 사유를 생성한다")
        void createWithValidValues() {
            // when
            RefundReason reason = RefundReason.of(RefundReasonType.CHANGE_OF_MIND, "단순 변심");

            // then
            assertThat(reason.reasonType()).isEqualTo(RefundReasonType.CHANGE_OF_MIND);
            assertThat(reason.reasonDetail()).isEqualTo("단순 변심");
        }

        @Test
        @DisplayName("reasonDetail이 null이어도 생성할 수 있다")
        void createWithNullDetail() {
            // when
            RefundReason reason = RefundReason.of(RefundReasonType.DEFECTIVE, null);

            // then
            assertThat(reason.reasonType()).isEqualTo(RefundReasonType.DEFECTIVE);
            assertThat(reason.reasonDetail()).isNull();
        }

        @Test
        @DisplayName("reasonType이 null이면 예외가 발생한다")
        void createWithNullReasonType_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> RefundReason.of(null, "상세 내용"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null일 수 없습니다");
        }

        @Test
        @DisplayName("모든 RefundReasonType으로 생성할 수 있다")
        void createWithAllReasonTypes() {
            for (RefundReasonType type : RefundReasonType.values()) {
                RefundReason reason = RefundReason.of(type, "상세 내용");
                assertThat(reason.reasonType()).isEqualTo(type);
            }
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            RefundReason reason1 = RefundReason.of(RefundReasonType.WRONG_PRODUCT, "잘못된 상품");
            RefundReason reason2 = RefundReason.of(RefundReasonType.WRONG_PRODUCT, "잘못된 상품");

            // then
            assertThat(reason1).isEqualTo(reason2);
            assertThat(reason1.hashCode()).isEqualTo(reason2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValuesAreNotEqual() {
            // given
            RefundReason reason1 = RefundReason.of(RefundReasonType.CHANGE_OF_MIND, "단순 변심");
            RefundReason reason2 = RefundReason.of(RefundReasonType.DEFECTIVE, "불량");

            // then
            assertThat(reason1).isNotEqualTo(reason2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("RefundReason은 record로 불변 객체이다")
        void refundReasonIsImmutable() {
            // given
            RefundReason reason = RefundReason.of(RefundReasonType.CHANGE_OF_MIND, "단순 변심");

            // then
            assertThat(reason.reasonType()).isEqualTo(RefundReasonType.CHANGE_OF_MIND);
            assertThat(reason.reasonDetail()).isEqualTo("단순 변심");
        }
    }
}
