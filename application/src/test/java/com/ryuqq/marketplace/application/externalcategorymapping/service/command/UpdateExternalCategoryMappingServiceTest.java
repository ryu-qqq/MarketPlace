package com.ryuqq.marketplace.application.externalcategorymapping.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.externalcategorymapping.ExternalCategoryMappingCommandFixtures;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.UpdateExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.factory.ExternalCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.externalcategorymapping.validator.ExternalCategoryMappingValidator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.externalcategorymapping.ExternalCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import com.ryuqq.marketplace.domain.externalcategorymapping.vo.ExternalCategoryMappingStatus;
import com.ryuqq.marketplace.domain.externalcategorymapping.vo.ExternalCategoryMappingUpdateData;
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
@DisplayName("UpdateExternalCategoryMappingService 단위 테스트")
class UpdateExternalCategoryMappingServiceTest {

    @InjectMocks private UpdateExternalCategoryMappingService sut;

    @Mock private ExternalCategoryMappingValidator validator;
    @Mock private ExternalCategoryMappingCommandFactory commandFactory;
    @Mock private ExternalCategoryMappingCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 카테고리 매핑 수정")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 외부 카테고리 매핑을 수정한다")
        void execute_ValidCommand_UpdatesMapping() {
            // given
            Long mappingId = 1L;
            UpdateExternalCategoryMappingCommand command =
                    ExternalCategoryMappingCommandFixtures.updateCommand(mappingId);
            ExternalCategoryMappingUpdateData updateData =
                    ExternalCategoryMappingUpdateData.of(
                            command.externalCategoryName(),
                            command.internalCategoryId(),
                            ExternalCategoryMappingStatus.ACTIVE);
            UpdateContext<Long, ExternalCategoryMappingUpdateData> context =
                    new UpdateContext<>(mappingId, updateData, CommonVoFixtures.now());
            ExternalCategoryMapping mapping =
                    ExternalCategoryMappingFixtures.activeMapping(mappingId);

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
