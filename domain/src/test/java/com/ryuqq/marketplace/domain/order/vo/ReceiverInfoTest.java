package com.ryuqq.marketplace.domain.order.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.order.OrderFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ReceiverInfo Value Object 테스트")
class ReceiverInfoTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 ReceiverInfo를 생성한다")
        void createWithValidValues() {
            // when
            ReceiverInfo info = OrderFixtures.defaultReceiverInfo();

            // then
            assertThat(info.receiverName()).isEqualTo("김수령");
            assertThat(info.receiverPhone())
                    .isEqualTo(CommonVoFixtures.phoneNumber("010-9876-5432"));
            assertThat(info.address()).isNotNull();
            assertThat(info.deliveryRequest()).isEqualTo("부재시 문앞에 놓아주세요");
        }

        @Test
        @DisplayName("수령인 이름이 null이면 예외가 발생한다")
        void createWithNullReceiverName_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ReceiverInfo.of(
                                            null,
                                            CommonVoFixtures.defaultPhoneNumber(),
                                            Address.of("12345", "서울시 강남구"),
                                            null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수령인 이름은 필수");
        }

        @Test
        @DisplayName("수령인 이름이 빈 문자열이면 예외가 발생한다")
        void createWithBlankReceiverName_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    ReceiverInfo.of(
                                            "   ",
                                            CommonVoFixtures.defaultPhoneNumber(),
                                            Address.of("12345", "서울시 강남구"),
                                            null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("수령인 이름은 필수");
        }

        @Test
        @DisplayName("배송 요청사항은 null을 허용한다")
        void createWithNullDeliveryRequest() {
            // when & then
            assertThatCode(
                            () ->
                                    ReceiverInfo.of(
                                            "김수령",
                                            CommonVoFixtures.defaultPhoneNumber(),
                                            Address.of("12345", "서울시 강남구"),
                                            null))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값의 ReceiverInfo는 동일하다")
        void sameValuesAreEqual() {
            // when
            ReceiverInfo info1 = OrderFixtures.defaultReceiverInfo();
            ReceiverInfo info2 = OrderFixtures.defaultReceiverInfo();

            // then
            assertThat(info1).isEqualTo(info2);
            assertThat(info1.hashCode()).isEqualTo(info2.hashCode());
        }
    }
}
