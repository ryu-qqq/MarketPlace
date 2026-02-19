package com.ryuqq.marketplace.application.externalbrandmapping.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.externalbrandmapping.ExternalBrandMappingCommandFixtures;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.UpdateExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.factory.ExternalBrandMappingCommandFactory;
import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingCommandManager;
import com.ryuqq.marketplace.application.externalbrandmapping.validator.ExternalBrandMappingValidator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.externalbrandmapping.ExternalBrandMappingFixtures;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import com.ryuqq.marketplace.domain.externalbrandmapping.vo.ExternalBrandMappingUpdateData;
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
@DisplayName("UpdateExternalBrandMappingService 단위 테스트")
class UpdateExternalBrandMappingServiceTest {

    @InjectMocks private UpdateExternalBrandMappingService sut;

    @Mock private ExternalBrandMappingValidator validator;
    @Mock private ExternalBrandMappingCommandFactory commandFactory;
    @Mock private ExternalBrandMappingCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 브랜드 매핑 수정")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 외부 브랜드 매핑을 수정한다")
        void execute_ValidCommand_UpdatesMapping() {
            // given
            Long mappingId = 1L;
            UpdateExternalBrandMappingCommand command =
                    ExternalBrandMappingCommandFixtures.updateCommand(mappingId);
            ExternalBrandMappingUpdateData updateData =
                    ExternalBrandMappingUpdateData.of(
                            command.externalBrandName(),
                            command.internalBrandId(),
                            com.ryuqq.marketplace.domain.externalbrandmapping.vo
                                    .ExternalBrandMappingStatus.ACTIVE);
            UpdateContext<Long, ExternalBrandMappingUpdateData> context =
                    new UpdateContext<>(mappingId, updateData, CommonVoFixtures.now());
            ExternalBrandMapping mapping = ExternalBrandMappingFixtures.activeMapping(mappingId);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(mapping);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(context.id());
            then(commandManager).should().persist(mapping);
        }
    }
}
