package com.ryuqq.marketplace.domain.order.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Email;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BuyerInfo Value Object 테스트")
class BuyerInfoTest {

    @Nested
    @DisplayName("of() - 구매자 정보 생성")
    class OfTest {

        @Test
        @DisplayName("유효한 값으로 구매자 정보를 생성한다")
        void createWithValidValues() {
            // given
            BuyerName buyerName = BuyerName.of("홍길동");
            Email email = Email.of("buyer@example.com");
            var phoneNumber = CommonVoFixtures.defaultPhoneNumber();

            // when
            BuyerInfo buyerInfo = BuyerInfo.of(buyerName, email, phoneNumber);

            // then
            assertThat(buyerInfo.buyerName()).isEqualTo(buyerName);
            assertThat(buyerInfo.email()).isEqualTo(email);
            assertThat(buyerInfo.phoneNumber()).isEqualTo(phoneNumber);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 BuyerInfo는 동일하다")
        void sameValuesAreEqual() {
            // given
            BuyerInfo buyerInfo1 = OrderFixtures.defaultBuyerInfo();
            BuyerInfo buyerInfo2 = OrderFixtures.defaultBuyerInfo();

            // then
            assertThat(buyerInfo1).isEqualTo(buyerInfo2);
            assertThat(buyerInfo1.hashCode()).isEqualTo(buyerInfo2.hashCode());
        }

        @Test
        @DisplayName("다른 구매자명을 가진 BuyerInfo는 동일하지 않다")
        void differentBuyerNameNotEqual() {
            // given
            BuyerInfo buyerInfo1 = OrderFixtures.defaultBuyerInfo();
            BuyerInfo buyerInfo2 =
                    BuyerInfo.of(
                            BuyerName.of("김철수"),
                            Email.of("buyer@example.com"),
                            CommonVoFixtures.defaultPhoneNumber());

            // then
            assertThat(buyerInfo1).isNotEqualTo(buyerInfo2);
        }
    }
}
