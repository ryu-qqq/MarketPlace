package com.ryuqq.marketplace.adapter.in.rest.saleschannel.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.saleschannel.SalesChannelApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command.RegisterSalesChannelApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.command.UpdateSalesChannelApiRequest;
import com.ryuqq.marketplace.application.saleschannel.dto.command.RegisterSalesChannelCommand;
import com.ryuqq.marketplace.application.saleschannel.dto.command.UpdateSalesChannelCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelCommandApiMapper 단위 테스트")
class SalesChannelCommandApiMapperTest {

    private SalesChannelCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SalesChannelCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(RegisterSalesChannelApiRequest) 메서드 테스트")
    class ToRegisterCommandTest {

        @Test
        @DisplayName("RegisterSalesChannelApiRequest를 RegisterSalesChannelCommand로 변환한다")
        void toCommand_ConvertsRegisterRequest_ReturnsCommand() {
            // given
            RegisterSalesChannelApiRequest request = SalesChannelApiFixtures.registerRequest();

            // when
            RegisterSalesChannelCommand command = mapper.toCommand(request);

            // then
            assertThat(command).isNotNull();
            assertThat(command.channelName()).isEqualTo("쿠팡");
        }

        @Test
        @DisplayName("커스텀 채널명으로 Command를 생성한다")
        void toCommand_CustomChannelName_ReturnsCommand() {
            // given
            RegisterSalesChannelApiRequest request =
                    SalesChannelApiFixtures.registerRequest("네이버 스마트스토어");

            // when
            RegisterSalesChannelCommand command = mapper.toCommand(request);

            // then
            assertThat(command.channelName()).isEqualTo("네이버 스마트스토어");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateSalesChannelApiRequest) 메서드 테스트")
    class ToUpdateCommandTest {

        @Test
        @DisplayName(
                "salesChannelId와 UpdateSalesChannelApiRequest를 UpdateSalesChannelCommand로 변환한다")
        void toCommand_ConvertsUpdateRequest_ReturnsCommand() {
            // given
            Long salesChannelId = 1L;
            UpdateSalesChannelApiRequest request = SalesChannelApiFixtures.updateRequest();

            // when
            UpdateSalesChannelCommand command = mapper.toCommand(salesChannelId, request);

            // then
            assertThat(command).isNotNull();
            assertThat(command.salesChannelId()).isEqualTo(1L);
            assertThat(command.channelName()).isEqualTo("쿠팡");
            assertThat(command.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("salesChannelId가 Command에 포함된다")
        void toCommand_IncludesSalesChannelId_ReturnsCommand() {
            // given
            Long salesChannelId = 99L;
            UpdateSalesChannelApiRequest request =
                    SalesChannelApiFixtures.updateRequest("G마켓", "INACTIVE");

            // when
            UpdateSalesChannelCommand command = mapper.toCommand(salesChannelId, request);

            // then
            assertThat(command.salesChannelId()).isEqualTo(99L);
            assertThat(command.channelName()).isEqualTo("G마켓");
            assertThat(command.status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("INACTIVE 상태로 Command를 생성한다")
        void toCommand_InactiveStatus_ReturnsCommand() {
            // given
            Long salesChannelId = 1L;
            UpdateSalesChannelApiRequest request =
                    SalesChannelApiFixtures.updateRequest("쿠팡", "INACTIVE");

            // when
            UpdateSalesChannelCommand command = mapper.toCommand(salesChannelId, request);

            // then
            assertThat(command.status()).isEqualTo("INACTIVE");
        }
    }
}
