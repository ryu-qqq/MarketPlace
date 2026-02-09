package com.ryuqq.marketplace.adapter.in.rest.shop.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.shop.ShopApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.command.RegisterShopApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.command.UpdateShopApiRequest;
import com.ryuqq.marketplace.application.shop.dto.command.RegisterShopCommand;
import com.ryuqq.marketplace.application.shop.dto.command.UpdateShopCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShopCommandApiMapper 단위 테스트")
class ShopCommandApiMapperTest {

    private ShopCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShopCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(RegisterShopApiRequest) - 등록 요청 변환")
    class ToRegisterCommandTest {

        @Test
        @DisplayName("RegisterShopApiRequest를 RegisterShopCommand로 변환한다")
        void toCommand_ConvertsRegisterRequest_ReturnsCommand() {
            // given
            RegisterShopApiRequest request = ShopApiFixtures.registerRequest();

            // when
            RegisterShopCommand command = mapper.toCommand(request);

            // then
            assertThat(command.shopName()).isEqualTo("테스트몰");
            assertThat(command.accountId()).isEqualTo("test_account_01");
        }

        @Test
        @DisplayName("커스텀 값으로 요청을 변환한다")
        void toCommand_ConvertsCustomRequest_ReturnsCommand() {
            // given
            RegisterShopApiRequest request =
                    ShopApiFixtures.registerRequest("커스텀몰", "custom_account");

            // when
            RegisterShopCommand command = mapper.toCommand(request);

            // then
            assertThat(command.shopName()).isEqualTo("커스텀몰");
            assertThat(command.accountId()).isEqualTo("custom_account");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateShopApiRequest) - 수정 요청 변환")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdateShopApiRequest를 UpdateShopCommand로 변환한다")
        void toCommand_ConvertsUpdateRequest_ReturnsCommand() {
            // given
            Long shopId = 10L;
            UpdateShopApiRequest request = ShopApiFixtures.updateRequest();

            // when
            UpdateShopCommand command = mapper.toCommand(shopId, request);

            // then
            assertThat(command.shopId()).isEqualTo(10L);
            assertThat(command.shopName()).isEqualTo("수정된몰명");
            assertThat(command.accountId()).isEqualTo("updated_account_01");
            assertThat(command.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("shopId와 요청 데이터가 정확히 매핑된다")
        void toCommand_MapsShopIdAndRequest_ReturnsCorrectCommand() {
            // given
            Long shopId = 99L;
            UpdateShopApiRequest request =
                    ShopApiFixtures.updateRequest("새몰명", "new_account", "INACTIVE");

            // when
            UpdateShopCommand command = mapper.toCommand(shopId, request);

            // then
            assertThat(command.shopId()).isEqualTo(99L);
            assertThat(command.shopName()).isEqualTo("새몰명");
            assertThat(command.accountId()).isEqualTo("new_account");
            assertThat(command.status()).isEqualTo("INACTIVE");
        }
    }
}
