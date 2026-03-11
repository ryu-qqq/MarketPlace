package com.ryuqq.marketplace.domain.order.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.order.id.PaymentNumber;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("PaymentInfo Value Object 테스트")
class PaymentInfoTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("모든 필드를 지정하여 PaymentInfo를 생성한다")
        void createWithAllFields() {
            // given
            PaymentNumber paymentNumber = PaymentNumber.of("PAY-20260218-0001");
            Money amount = Money.of(20000);
            Instant paidAt = CommonVoFixtures.now();

            // when
            PaymentInfo info = PaymentInfo.of(paymentNumber, "CARD", amount, paidAt);

            // then
            assertThat(info.paymentNumber()).isEqualTo(paymentNumber);
            assertThat(info.paymentMethod()).isEqualTo("CARD");
            assertThat(info.totalPaymentAmount()).isEqualTo(amount);
            assertThat(info.paidAt()).isEqualTo(paidAt);
        }

        @Test
        @DisplayName("paidAt이 null이어도 정상 생성된다")
        void createWithNullPaidAt() {
            // given
            PaymentNumber paymentNumber = PaymentNumber.of("PAY-20260218-0001");

            // when
            PaymentInfo info = PaymentInfo.of(paymentNumber, "CARD", Money.of(10000), null);

            // then
            assertThat(info.paidAt()).isNull();
        }

        @Test
        @DisplayName("totalPaymentAmount가 null이면 Money.zero()로 기본값이 설정된다")
        void createWithNullTotalPaymentAmount_DefaultsToZero() {
            // given
            PaymentNumber paymentNumber = PaymentNumber.of("PAY-20260218-0001");

            // when
            PaymentInfo info = PaymentInfo.of(paymentNumber, "CARD", null, null);

            // then
            assertThat(info.totalPaymentAmount()).isEqualTo(Money.zero());
            assertThat(info.totalPaymentAmount().value()).isZero();
        }
    }

    @Nested
    @DisplayName("paymentNumber 필드 테스트")
    class PaymentNumberFieldTest {

        @Test
        @DisplayName("paymentNumber 값이 올바르게 저장된다")
        void paymentNumberIsStoredCorrectly() {
            // given
            PaymentNumber paymentNumber = PaymentNumber.of("PAY-20260310-1234");

            // when
            PaymentInfo info = PaymentInfo.of(paymentNumber, "KAKAO_PAY", Money.of(5000), null);

            // then
            assertThat(info.paymentNumber().value()).isEqualTo("PAY-20260310-1234");
        }

        @Test
        @DisplayName("generate()로 생성된 PaymentNumber도 올바르게 저장된다")
        void generatedPaymentNumberIsStoredCorrectly() {
            // given
            PaymentNumber generated = PaymentNumber.generate();

            // when
            PaymentInfo info =
                    PaymentInfo.of(generated, "CARD", Money.of(30000), CommonVoFixtures.now());

            // then
            assertThat(info.paymentNumber()).isEqualTo(generated);
            assertThat(info.paymentNumber().value()).matches("PAY-\\d{8}-\\d{4}");
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값의 PaymentInfo는 동일하다")
        void sameValuesAreEqual() {
            // given
            PaymentNumber paymentNumber = PaymentNumber.of("PAY-20260218-0001");
            Money amount = Money.of(20000);
            Instant paidAt = Instant.ofEpochSecond(1000000);

            // when
            PaymentInfo info1 = PaymentInfo.of(paymentNumber, "CARD", amount, paidAt);
            PaymentInfo info2 = PaymentInfo.of(paymentNumber, "CARD", amount, paidAt);

            // then
            assertThat(info1).isEqualTo(info2);
            assertThat(info1.hashCode()).isEqualTo(info2.hashCode());
        }

        @Test
        @DisplayName("paymentNumber가 다르면 동일하지 않다")
        void differentPaymentNumberAreNotEqual() {
            // given
            PaymentInfo info1 =
                    PaymentInfo.of(
                            PaymentNumber.of("PAY-20260218-0001"), "CARD", Money.of(20000), null);
            PaymentInfo info2 =
                    PaymentInfo.of(
                            PaymentNumber.of("PAY-20260218-0002"), "CARD", Money.of(20000), null);

            // then
            assertThat(info1).isNotEqualTo(info2);
        }
    }
}
