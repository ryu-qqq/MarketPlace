package com.ryuqq.marketplace.domain.refund.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundInfo Value Object 단위 테스트")
class RefundInfoTest {

    @Nested
    @DisplayName("of() - 직접 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 값으로 환불 정보를 생성한다")
        void createWithValidValues() {
            // when
            RefundInfo info =
                    RefundInfo.of(
                            Money.of(10000),
                            Money.of(7000),
                            Money.of(3000),
                            "배송비 차감",
                            "CARD",
                            CommonVoFixtures.now());

            // then
            assertThat(info.originalAmount()).isEqualTo(Money.of(10000));
            assertThat(info.finalAmount()).isEqualTo(Money.of(7000));
            assertThat(info.deductionAmount()).isEqualTo(Money.of(3000));
            assertThat(info.deductionReason()).isEqualTo("배송비 차감");
            assertThat(info.refundMethod()).isEqualTo("CARD");
        }

        @Test
        @DisplayName("originalAmount가 null이면 예외가 발생한다")
        void createWithNullOriginalAmount_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    RefundInfo.of(
                                            null,
                                            Money.of(10000),
                                            Money.zero(),
                                            null,
                                            "CARD",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("원래 금액");
        }

        @Test
        @DisplayName("finalAmount가 null이면 예외가 발생한다")
        void createWithNullFinalAmount_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    RefundInfo.of(
                                            Money.of(10000),
                                            null,
                                            Money.zero(),
                                            null,
                                            "CARD",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("최종 환불 금액");
        }

        @Test
        @DisplayName("deductionAmount가 null이면 예외가 발생한다")
        void createWithNullDeductionAmount_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    RefundInfo.of(
                                            Money.of(10000),
                                            Money.of(10000),
                                            null,
                                            null,
                                            "CARD",
                                            CommonVoFixtures.now()))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("차감 금액");
        }
    }

    @Nested
    @DisplayName("fullRefund() - 전액 환불")
    class FullRefundTest {

        @Test
        @DisplayName("전액 환불 시 finalAmount와 originalAmount가 동일하다")
        void fullRefundHasSameOriginalAndFinal() {
            // given
            Money amount = Money.of(10000);

            // when
            RefundInfo info = RefundInfo.fullRefund(amount, "CARD", CommonVoFixtures.now());

            // then
            assertThat(info.originalAmount()).isEqualTo(amount);
            assertThat(info.finalAmount()).isEqualTo(amount);
        }

        @Test
        @DisplayName("전액 환불 시 deductionAmount는 0이다")
        void fullRefundHasZeroDeduction() {
            // when
            RefundInfo info =
                    RefundInfo.fullRefund(Money.of(10000), "CARD", CommonVoFixtures.now());

            // then
            assertThat(info.deductionAmount()).isEqualTo(Money.zero());
        }

        @Test
        @DisplayName("전액 환불 시 deductionReason은 null이다")
        void fullRefundHasNullDeductionReason() {
            // when
            RefundInfo info =
                    RefundInfo.fullRefund(Money.of(10000), "CARD", CommonVoFixtures.now());

            // then
            assertThat(info.deductionReason()).isNull();
        }
    }

    @Nested
    @DisplayName("partialRefund() - 부분 환불")
    class PartialRefundTest {

        @Test
        @DisplayName("부분 환불 시 finalAmount = originalAmount - deductionAmount이다")
        void partialRefundFinalAmountIsCorrect() {
            // given
            Money originalAmount = Money.of(10000);
            Money deductionAmount = Money.of(3000);

            // when
            RefundInfo info =
                    RefundInfo.partialRefund(
                            originalAmount,
                            deductionAmount,
                            "왕복 배송비",
                            "CARD",
                            CommonVoFixtures.now());

            // then
            assertThat(info.originalAmount()).isEqualTo(Money.of(10000));
            assertThat(info.deductionAmount()).isEqualTo(Money.of(3000));
            assertThat(info.finalAmount()).isEqualTo(Money.of(7000));
        }

        @Test
        @DisplayName("부분 환불 시 차감 사유가 설정된다")
        void partialRefundHasDeductionReason() {
            // when
            RefundInfo info =
                    RefundInfo.partialRefund(
                            Money.of(10000),
                            Money.of(3000),
                            "왕복 배송비",
                            "CARD",
                            CommonVoFixtures.now());

            // then
            assertThat(info.deductionReason()).isEqualTo("왕복 배송비");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            Money amount = Money.of(10000);

            RefundInfo info1 = RefundInfo.fullRefund(amount, "CARD", CommonVoFixtures.now());
            RefundInfo info2 = RefundInfo.fullRefund(amount, "CARD", info1.refundedAt());

            // then
            assertThat(info1).isEqualTo(info2);
        }
    }
}
