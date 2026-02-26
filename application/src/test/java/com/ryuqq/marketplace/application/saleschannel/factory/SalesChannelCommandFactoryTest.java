package com.ryuqq.marketplace.application.saleschannel.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.saleschannel.SalesChannelCommandFixtures;
import com.ryuqq.marketplace.application.saleschannel.dto.command.RegisterSalesChannelCommand;
import com.ryuqq.marketplace.application.saleschannel.dto.command.UpdateSalesChannelCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannelUpdateData;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.saleschannel.vo.SalesChannelStatus;
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
@DisplayName("SalesChannelCommandFactory 단위 테스트")
class SalesChannelCommandFactoryTest {

    @InjectMocks private SalesChannelCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - 판매채널 생성")
    class CreateTest {

        @Test
        @DisplayName("등록 커맨드로부터 판매채널을 생성한다")
        void create_ValidCommand_ReturnsSalesChannel() {
            // given
            RegisterSalesChannelCommand command = SalesChannelCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            SalesChannel result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.channelName()).isEqualTo(command.channelName());
            assertThat(result.status()).isEqualTo(SalesChannelStatus.ACTIVE);
            assertThat(result.createdAt()).isEqualTo(now);
            assertThat(result.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("createUpdateContext() - 수정 컨텍스트 생성")
    class CreateUpdateContextTest {

        @Test
        @DisplayName("수정 커맨드로부터 UpdateContext를 생성한다")
        void createUpdateContext_ValidCommand_ReturnsUpdateContext() {
            // given
            Long salesChannelId = 1L;
            UpdateSalesChannelCommand command =
                    SalesChannelCommandFixtures.updateCommand(salesChannelId, "수정된 채널", "ACTIVE");
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<SalesChannelId, SalesChannelUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(salesChannelId);
            assertThat(result.updateData().channelName()).isEqualTo("수정된 채널");
            assertThat(result.updateData().status()).isEqualTo(SalesChannelStatus.ACTIVE);
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("INACTIVE 상태로 UpdateContext를 생성한다")
        void createUpdateContext_WithInactiveStatus_ReturnsUpdateContext() {
            // given
            Long salesChannelId = 1L;
            UpdateSalesChannelCommand command =
                    SalesChannelCommandFixtures.updateCommand(salesChannelId, "비활성 채널", "INACTIVE");
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<SalesChannelId, SalesChannelUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result.updateData().status()).isEqualTo(SalesChannelStatus.INACTIVE);
        }
    }
}
