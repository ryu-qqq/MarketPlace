package com.ryuqq.marketplace.application.saleschannelbrand.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.saleschannelbrand.SalesChannelBrandCommandFixtures;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.command.RegisterSalesChannelBrandCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SalesChannelBrandCommandFactory 단위 테스트")
class SalesChannelBrandCommandFactoryTest {

    @InjectMocks private SalesChannelBrandCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - SalesChannelBrand 도메인 생성")
    class CreateTest {

        @Test
        @DisplayName("RegisterSalesChannelBrandCommand로 SalesChannelBrand를 생성한다")
        void create_ReturnsSalesChannelBrand() {
            // given
            RegisterSalesChannelBrandCommand command =
                    SalesChannelBrandCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            SalesChannelBrand result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.salesChannelId()).isEqualTo(command.salesChannelId());
            assertThat(result.externalBrandCode()).isEqualTo(command.externalBrandCode());
            assertThat(result.externalBrandName()).isEqualTo(command.externalBrandName());
            assertThat(result.isActive()).isTrue();
            assertThat(result.createdAt()).isEqualTo(now);
            assertThat(result.updatedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("다른 판매채널 ID로 SalesChannelBrand를 생성한다")
        void create_DifferentSalesChannelId_ReturnsSalesChannelBrand() {
            // given
            RegisterSalesChannelBrandCommand command =
                    SalesChannelBrandCommandFixtures.registerCommand(2L, "BRAND-002", "다른 브랜드");
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            SalesChannelBrand result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.salesChannelId()).isEqualTo(2L);
            assertThat(result.externalBrandCode()).isEqualTo("BRAND-002");
            assertThat(result.externalBrandName()).isEqualTo("다른 브랜드");
        }
    }
}
