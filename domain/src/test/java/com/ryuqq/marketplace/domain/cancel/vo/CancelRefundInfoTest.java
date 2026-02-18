package com.ryuqq.marketplace.domain.cancel.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CancelRefundInfo Value Object 테스트")
class CancelRefundInfoTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 환불 정보를 생성한다")
        void createWithValidValues() {
            // given
            Money refundAmount = Money.of(10000);

            // when
            CancelRefundInfo refundInfo =
                    CancelRefundInfo.of(
                            refundAmount,
                            "CARD",
                            "REFUNDED",
                            CommonVoFixtures.now(),
                            "PG-REFUND-001");

            // then
            assertThat(refundInfo.refundAmount()).isEqualTo(refundAmount);
            assertThat(refundInfo.refundMethod()).isEqualTo("CARD");
            assertThat(refundInfo.refundStatus()).isEqualTo("REFUNDED");
            assertThat(refundInfo.pgRefundId()).isEqualTo("PG-REFUND-001");
        }

        @Test
        @DisplayName("환불 금액이 0원으로 생성된다")
        void createWithZeroRefundAmount() {
            // when
            CancelRefundInfo refundInfo =
                    CancelRefundInfo.of(Money.zero(), "VIRTUAL_ACCOUNT", "PENDING", null, null);

            // then
            assertThat(refundInfo.refundAmount().isZero()).isTrue();
        }

        @Test
        @DisplayName("선택적 필드(refundedAt, pgRefundId)가 null이어도 생성된다")
        void createWithNullOptionalFields() {
            // when
            CancelRefundInfo refundInfo =
                    CancelRefundInfo.of(Money.of(5000), "BANK_TRANSFER", "PENDING", null, null);

            // then
            assertThat(refundInfo).isNotNull();
            assertThat(refundInfo.refundedAt()).isNull();
            assertThat(refundInfo.pgRefundId()).isNull();
        }

        @Test
        @DisplayName("환불 금액이 null이면 예외가 발생한다")
        void createWithNullRefundAmount_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    CancelRefundInfo.of(
                                            null,
                                            "CARD",
                                            "REFUNDED",
                                            CommonVoFixtures.now(),
                                            "PG-REFUND-001"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("환불 금액은 필수");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            CancelRefundInfo refundInfo1 = CancelFixtures.defaultCancelRefundInfo();
            CancelRefundInfo refundInfo2 =
                    CancelRefundInfo.of(
                            Money.of(10000),
                            "CARD",
                            "REFUNDED",
                            refundInfo1.refundedAt(),
                            "PG-REFUND-001");

            // then
            assertThat(refundInfo1.refundAmount()).isEqualTo(refundInfo2.refundAmount());
            assertThat(refundInfo1.refundMethod()).isEqualTo(refundInfo2.refundMethod());
        }

        @Test
        @DisplayName("다른 환불 금액이면 동일하지 않다")
        void differentAmountsAreNotEqual() {
            // given
            CancelRefundInfo refundInfo1 = CancelFixtures.cancelRefundInfo(Money.of(10000));
            CancelRefundInfo refundInfo2 = CancelFixtures.cancelRefundInfo(Money.of(20000));

            // then
            assertThat(refundInfo1).isNotEqualTo(refundInfo2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("record 생성자 호출 시 refundAmount는 변경되지 않는다")
        void refundAmountIsImmutable() {
            // given
            Money originalAmount = Money.of(10000);
            CancelRefundInfo refundInfo =
                    CancelRefundInfo.of(originalAmount, "CARD", "REFUNDED", null, null);

            // then
            assertThat(refundInfo.refundAmount()).isEqualTo(originalAmount);
            assertThat(refundInfo.refundAmount().value()).isEqualTo(10000);
        }
    }
}
