package com.ryuqq.marketplace.application.saleschannel.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.saleschannel.SalesChannelCommandFixtures;
import com.ryuqq.marketplace.application.saleschannel.dto.command.RegisterSalesChannelCommand;
import com.ryuqq.marketplace.application.saleschannel.factory.SalesChannelCommandFactory;
import com.ryuqq.marketplace.application.saleschannel.manager.SalesChannelCommandManager;
import com.ryuqq.marketplace.application.saleschannel.validator.SalesChannelValidator;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
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
@DisplayName("RegisterSalesChannelService 단위 테스트")
class RegisterSalesChannelServiceTest {

    @InjectMocks private RegisterSalesChannelService sut;

    @Mock private SalesChannelValidator validator;
    @Mock private SalesChannelCommandFactory commandFactory;
    @Mock private SalesChannelCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 판매채널 등록")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 판매채널을 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsSalesChannelId() {
            // given
            RegisterSalesChannelCommand command = SalesChannelCommandFixtures.registerCommand();
            SalesChannel salesChannel = SalesChannelFixtures.newSalesChannel();
            Long expectedId = 1L;

            given(commandFactory.create(command)).willReturn(salesChannel);
            given(commandManager.persist(salesChannel)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator).should().validateChannelNameNotDuplicate(command.channelName());
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(salesChannel);
        }

        @Test
        @DisplayName("다른 이름으로 판매채널을 등록할 수 있다")
        void execute_WithDifferentName_RegistersSalesChannel() {
            // given
            String channelName = "새로운 판매채널";
            RegisterSalesChannelCommand command =
                    SalesChannelCommandFixtures.registerCommand(channelName);
            SalesChannel salesChannel = SalesChannelFixtures.newSalesChannel(channelName);
            Long expectedId = 2L;

            given(commandFactory.create(command)).willReturn(salesChannel);
            given(commandManager.persist(salesChannel)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator).should().validateChannelNameNotDuplicate(channelName);
        }
    }
}
