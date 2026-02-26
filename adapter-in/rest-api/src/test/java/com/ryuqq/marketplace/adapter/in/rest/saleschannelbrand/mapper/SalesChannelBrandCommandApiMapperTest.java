package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.SalesChannelBrandApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.command.RegisterSalesChannelBrandApiRequest;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.command.RegisterSalesChannelBrandCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelBrandCommandApiMapper 단위 테스트")
class SalesChannelBrandCommandApiMapperTest {

    private SalesChannelBrandCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SalesChannelBrandCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand 메서드 테스트")
    class ToCommandTest {

        @Test
        @DisplayName("RegisterSalesChannelBrandApiRequest를 RegisterSalesChannelBrandCommand로 변환한다")
        void toCommand_ConvertsRequest_ReturnsCommand() {
            // given
            Long salesChannelId = 1L;
            RegisterSalesChannelBrandApiRequest request =
                    SalesChannelBrandApiFixtures.registerRequest();

            // when
            RegisterSalesChannelBrandCommand command = mapper.toCommand(salesChannelId, request);

            // then
            assertThat(command).isNotNull();
            assertThat(command.salesChannelId()).isEqualTo(1L);
            assertThat(command.externalBrandCode()).isEqualTo("BRD001");
            assertThat(command.externalBrandName()).isEqualTo("나이키");
        }

        @Test
        @DisplayName("salesChannelId와 요청 정보가 모두 Command에 포함된다")
        void toCommand_IncludesSalesChannelIdAndRequest_ReturnsCommand() {
            // given
            Long salesChannelId = 99L;
            RegisterSalesChannelBrandApiRequest request =
                    SalesChannelBrandApiFixtures.registerRequest("CUSTOM_CODE", "커스텀브랜드");

            // when
            RegisterSalesChannelBrandCommand command = mapper.toCommand(salesChannelId, request);

            // then
            assertThat(command.salesChannelId()).isEqualTo(99L);
            assertThat(command.externalBrandCode()).isEqualTo("CUSTOM_CODE");
            assertThat(command.externalBrandName()).isEqualTo("커스텀브랜드");
        }
    }
}
