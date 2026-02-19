package com.ryuqq.marketplace.application.externalsource.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.externalsource.ExternalSourceCommandFixtures;
import com.ryuqq.marketplace.application.externalsource.dto.command.UpdateExternalSourceCommand;
import com.ryuqq.marketplace.application.externalsource.factory.ExternalSourceCommandFactory;
import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceCommandManager;
import com.ryuqq.marketplace.application.externalsource.validator.ExternalSourceValidator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.externalsource.ExternalSourceFixtures;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceStatus;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceUpdateData;
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
@DisplayName("UpdateExternalSourceService 단위 테스트")
class UpdateExternalSourceServiceTest {

    @InjectMocks private UpdateExternalSourceService sut;

    @Mock private ExternalSourceValidator validator;
    @Mock private ExternalSourceCommandFactory commandFactory;
    @Mock private ExternalSourceCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 소스 수정")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 외부 소스를 수정한다")
        void execute_ValidCommand_UpdatesExternalSource() {
            // given
            Long externalSourceId = 1L;
            UpdateExternalSourceCommand command =
                    ExternalSourceCommandFixtures.updateCommand(externalSourceId);
            ExternalSourceUpdateData updateData =
                    ExternalSourceUpdateData.of(
                            command.name(), command.description(), ExternalSourceStatus.ACTIVE);
            UpdateContext<Long, ExternalSourceUpdateData> context =
                    new UpdateContext<>(externalSourceId, updateData, CommonVoFixtures.now());
            ExternalSource externalSource =
                    ExternalSourceFixtures.activeExternalSource(externalSourceId);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(externalSource);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(context.id());
            then(commandManager).should().persist(externalSource);
        }
    }
}
