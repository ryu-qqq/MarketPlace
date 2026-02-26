package com.ryuqq.marketplace.application.saleschannel.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.saleschannel.SalesChannelCommandFixtures;
import com.ryuqq.marketplace.application.saleschannel.dto.command.UpdateSalesChannelCommand;
import com.ryuqq.marketplace.application.saleschannel.factory.SalesChannelCommandFactory;
import com.ryuqq.marketplace.application.saleschannel.manager.SalesChannelCommandManager;
import com.ryuqq.marketplace.application.saleschannel.validator.SalesChannelValidator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
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
@DisplayName("UpdateSalesChannelService 단위 테스트")
class UpdateSalesChannelServiceTest {

    @InjectMocks private UpdateSalesChannelService sut;

    @Mock private SalesChannelCommandFactory commandFactory;
    @Mock private SalesChannelCommandManager commandManager;
    @Mock private SalesChannelValidator validator;

    @Nested
    @DisplayName("execute() - 판매채널 수정")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 판매채널을 수정한다")
        void execute_ValidCommand_UpdatesSalesChannel() {
            // given
            Long salesChannelId = 1L;
            UpdateSalesChannelCommand command =
                    SalesChannelCommandFixtures.updateCommand(salesChannelId);
            SalesChannelId id = SalesChannelId.of(salesChannelId);
            SalesChannelUpdateData updateData =
                    SalesChannelUpdateData.of("수정된 판매채널", SalesChannelStatus.ACTIVE);
            Instant changedAt = CommonVoFixtures.now();
            UpdateContext<SalesChannelId, SalesChannelUpdateData> context =
                    new UpdateContext<>(id, updateData, changedAt);
            SalesChannel salesChannel = SalesChannelFixtures.activeSalesChannel(salesChannelId);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(id)).willReturn(salesChannel);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(id);
            then(commandManager).should().persist(salesChannel);
        }

        @Test
        @DisplayName("판매채널 상태를 INACTIVE로 수정할 수 있다")
        void execute_WithInactiveStatus_UpdatesSalesChannel() {
            // given
            Long salesChannelId = 1L;
            UpdateSalesChannelCommand command =
                    SalesChannelCommandFixtures.updateCommand(
                            salesChannelId, "수정된 판매채널", "INACTIVE");
            SalesChannelId id = SalesChannelId.of(salesChannelId);
            SalesChannelUpdateData updateData =
                    SalesChannelUpdateData.of("수정된 판매채널", SalesChannelStatus.INACTIVE);
            Instant changedAt = CommonVoFixtures.now();
            UpdateContext<SalesChannelId, SalesChannelUpdateData> context =
                    new UpdateContext<>(id, updateData, changedAt);
            SalesChannel salesChannel = SalesChannelFixtures.activeSalesChannel(salesChannelId);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(id)).willReturn(salesChannel);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(id);
            then(commandManager).should().persist(salesChannel);
        }
    }
}
