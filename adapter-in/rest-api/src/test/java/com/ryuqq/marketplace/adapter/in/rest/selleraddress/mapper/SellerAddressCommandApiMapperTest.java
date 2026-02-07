package com.ryuqq.marketplace.adapter.in.rest.selleraddress.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.selleraddress.SellerAddressApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.command.RegisterSellerAddressApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.command.UpdateSellerAddressApiRequest;
import com.ryuqq.marketplace.application.selleraddress.dto.command.DeleteSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.RegisterSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.UpdateSellerAddressCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAddressCommandApiMapper 단위 테스트")
class SellerAddressCommandApiMapperTest {

    private SellerAddressCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerAddressCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand() - 등록 요청 변환")
    class ToRegisterCommandTest {

        @Test
        @DisplayName(
                "sellerId와 RegisterSellerAddressApiRequest를 RegisterSellerAddressCommand로 변환한다")
        void toCommand_ConvertsRequest_ReturnsRegisterCommand() {
            // given
            Long sellerId = 1L;
            RegisterSellerAddressApiRequest request = SellerAddressApiFixtures.registerRequest();

            // when
            RegisterSellerAddressCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(1L);
            assertThat(command.addressType())
                    .isEqualTo(SellerAddressApiFixtures.DEFAULT_ADDRESS_TYPE);
            assertThat(command.addressName())
                    .isEqualTo(SellerAddressApiFixtures.DEFAULT_ADDRESS_NAME);
            assertThat(command.address().zipCode())
                    .isEqualTo(SellerAddressApiFixtures.DEFAULT_ZIP_CODE);
            assertThat(command.address().line1()).isEqualTo(SellerAddressApiFixtures.DEFAULT_LINE1);
            assertThat(command.address().line2()).isEqualTo(SellerAddressApiFixtures.DEFAULT_LINE2);
            assertThat(command.defaultAddress()).isFalse();
        }

        @Test
        @DisplayName("기본 주소 true인 등록 요청을 변환한다")
        void toCommand_DefaultAddressTrue_ConvertsCorrectly() {
            // given
            Long sellerId = 2L;
            RegisterSellerAddressApiRequest request =
                    new RegisterSellerAddressApiRequest(
                            "RETURN",
                            "반품센터",
                            SellerAddressApiFixtures.defaultAddressRequest(),
                            true);

            // when
            RegisterSellerAddressCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(2L);
            assertThat(command.addressType()).isEqualTo("RETURN");
            assertThat(command.defaultAddress()).isTrue();
        }
    }

    @Nested
    @DisplayName("toCommand() - 수정 요청 변환")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("addressId와 UpdateSellerAddressApiRequest를 UpdateSellerAddressCommand로 변환한다")
        void toCommand_ConvertsRequest_ReturnsUpdateCommand() {
            // given
            Long addressId = 10L;
            UpdateSellerAddressApiRequest request = SellerAddressApiFixtures.updateRequest();

            // when
            UpdateSellerAddressCommand command = mapper.toCommand(addressId, request);

            // then
            assertThat(command.addressId()).isEqualTo(10L);
            assertThat(command.addressName()).isEqualTo("물류센터");
            assertThat(command.address().zipCode())
                    .isEqualTo(SellerAddressApiFixtures.DEFAULT_ZIP_CODE);
            assertThat(command.address().line2()).isEqualTo("6층");
            assertThat(command.defaultAddress()).isTrue();
        }

        @Test
        @DisplayName("defaultAddress가 null이면 null로 전달한다")
        void toCommand_NullDefaultAddress_PassesNull() {
            // given
            Long addressId = 5L;
            UpdateSellerAddressApiRequest request =
                    new UpdateSellerAddressApiRequest(
                            "창고 A", SellerAddressApiFixtures.updateAddressRequest(), null);

            // when
            UpdateSellerAddressCommand command = mapper.toCommand(addressId, request);

            // then
            assertThat(command.defaultAddress()).isNull();
        }
    }

    @Nested
    @DisplayName("toDeleteCommand() - 삭제 Command")
    class ToDeleteCommandTest {

        @Test
        @DisplayName("addressId를 DeleteSellerAddressCommand로 변환한다")
        void toDeleteCommand_ConvertsId_ReturnsDeleteCommand() {
            // given
            Long addressId = 7L;

            // when
            DeleteSellerAddressCommand command = mapper.toDeleteCommand(addressId);

            // then
            assertThat(command.addressId()).isEqualTo(7L);
        }
    }
}
