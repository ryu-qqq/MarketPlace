package com.ryuqq.marketplace.domain.claim.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.common.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShippingFeeInfo Value Object 단위 테스트")
class ShippingFeeInfoTest {

    @Nested
    @DisplayName("of() - 일반 생성 테스트")
    class OfTest {

        @Test
        @DisplayName("유효한 값으로 ShippingFeeInfo를 생성한다")
        void createWithValidValues() {
            // given
            Money amount = Money.of(3000);
            FeePayer payer = FeePayer.BUYER;

            // when
            ShippingFeeInfo feeInfo = ShippingFeeInfo.of(amount, payer, false);

            // then
            assertThat(feeInfo.amount()).isEqualTo(amount);
            assertThat(feeInfo.payer()).isEqualTo(payer);
            assertThat(feeInfo.includeInPackage()).isFalse();
        }

        @Test
        @DisplayName("includeInPackage가 true인 경우도 생성한다")
        void createWithIncludeInPackageTrue() {
            // given & when
            ShippingFeeInfo feeInfo = ShippingFeeInfo.of(Money.of(3000), FeePayer.SELLER, true);

            // then
            assertThat(feeInfo.includeInPackage()).isTrue();
        }

        @Test
        @DisplayName("amount가 null이면 예외가 발생한다")
        void createWithNullAmount_ThrowsException() {
            assertThatThrownBy(() -> ShippingFeeInfo.of(null, FeePayer.BUYER, false))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("배송비 금액은 null일 수 없습니다");
        }

        @Test
        @DisplayName("payer가 null이면 예외가 발생한다")
        void createWithNullPayer_ThrowsException() {
            assertThatThrownBy(() -> ShippingFeeInfo.of(Money.of(3000), null, false))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("배송비 부담 주체는 필수");
        }

        @Test
        @DisplayName("SELLER 부담으로 배송비 정보를 생성한다")
        void createWithSellerPayer() {
            // given & when
            ShippingFeeInfo feeInfo = ShippingFeeInfo.of(Money.of(5000), FeePayer.SELLER, false);

            // then
            assertThat(feeInfo.payer()).isEqualTo(FeePayer.SELLER);
            assertThat(feeInfo.amount()).isEqualTo(Money.of(5000));
        }
    }

    @Nested
    @DisplayName("free() - 무료 배송 팩토리 메서드")
    class FreeTest {

        @Test
        @DisplayName("free() 팩토리로 무료 배송비 정보를 생성한다")
        void freeFactory_CreatesFreeShipping() {
            // given & when
            ShippingFeeInfo feeInfo = ShippingFeeInfo.free();

            // then
            assertThat(feeInfo.amount()).isEqualTo(Money.zero());
            assertThat(feeInfo.amount().isZero()).isTrue();
        }

        @Test
        @DisplayName("free() 팩토리로 생성하면 FeePayer는 SELLER이다")
        void freeFactory_PayerIsSeller() {
            // given & when
            ShippingFeeInfo feeInfo = ShippingFeeInfo.free();

            // then
            assertThat(feeInfo.payer()).isEqualTo(FeePayer.SELLER);
        }

        @Test
        @DisplayName("free() 팩토리로 생성하면 includeInPackage는 false이다")
        void freeFactory_IncludeInPackageIsFalse() {
            // given & when
            ShippingFeeInfo feeInfo = ShippingFeeInfo.free();

            // then
            assertThat(feeInfo.includeInPackage()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValuesAreEqual() {
            // given
            ShippingFeeInfo feeInfo1 = ShippingFeeInfo.of(Money.of(3000), FeePayer.BUYER, false);
            ShippingFeeInfo feeInfo2 = ShippingFeeInfo.of(Money.of(3000), FeePayer.BUYER, false);

            // then
            assertThat(feeInfo1).isEqualTo(feeInfo2);
            assertThat(feeInfo1.hashCode()).isEqualTo(feeInfo2.hashCode());
        }

        @Test
        @DisplayName("금액이 다르면 동일하지 않다")
        void differentAmountAreNotEqual() {
            // given
            ShippingFeeInfo feeInfo1 = ShippingFeeInfo.of(Money.of(3000), FeePayer.BUYER, false);
            ShippingFeeInfo feeInfo2 = ShippingFeeInfo.of(Money.of(5000), FeePayer.BUYER, false);

            // then
            assertThat(feeInfo1).isNotEqualTo(feeInfo2);
        }

        @Test
        @DisplayName("부담 주체가 다르면 동일하지 않다")
        void differentPayerAreNotEqual() {
            // given
            ShippingFeeInfo feeInfo1 = ShippingFeeInfo.of(Money.of(3000), FeePayer.BUYER, false);
            ShippingFeeInfo feeInfo2 = ShippingFeeInfo.of(Money.of(3000), FeePayer.SELLER, false);

            // then
            assertThat(feeInfo1).isNotEqualTo(feeInfo2);
        }

        @Test
        @DisplayName("두 free() 팩토리 결과는 동일하다")
        void twoFreeShippingInfoAreEqual() {
            // given
            ShippingFeeInfo feeInfo1 = ShippingFeeInfo.free();
            ShippingFeeInfo feeInfo2 = ShippingFeeInfo.free();

            // then
            assertThat(feeInfo1).isEqualTo(feeInfo2);
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("ShippingFeeInfo는 record이므로 불변이다")
        void shippingFeeInfoIsImmutable() {
            // given
            Money originalAmount = Money.of(3000);
            ShippingFeeInfo feeInfo = ShippingFeeInfo.of(originalAmount, FeePayer.BUYER, false);

            // then
            assertThat(feeInfo.amount()).isEqualTo(originalAmount);
            assertThat(feeInfo.payer()).isEqualTo(FeePayer.BUYER);
        }
    }
}
