package com.ryuqq.marketplace.application.inboundcategorymapping.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.inboundcategorymapping.InboundCategoryMappingCommandFixtures;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.factory.InboundCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundcategorymapping.manager.InboundCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.inboundcategorymapping.validator.InboundCategoryMappingValidator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.InboundCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingStatus;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingUpdateData;
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
@DisplayName("UpdateInboundCategoryMappingService 단위 테스트")
class UpdateInboundCategoryMappingServiceTest {

    @InjectMocks private UpdateInboundCategoryMappingService sut;

    @Mock private InboundCategoryMappingValidator validator;
    @Mock private InboundCategoryMappingCommandFactory commandFactory;
    @Mock private InboundCategoryMappingCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 카테고리 매핑 수정")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 외부 카테고리 매핑을 수정한다")
        void execute_ValidCommand_UpdatesMapping() {
            // given
            Long mappingId = 1L;
            UpdateInboundCategoryMappingCommand command =
                    InboundCategoryMappingCommandFixtures.updateCommand(mappingId);
            InboundCategoryMappingUpdateData updateData =
                    InboundCategoryMappingUpdateData.of(
                            command.externalCategoryName(),
                            command.internalCategoryId(),
                            InboundCategoryMappingStatus.ACTIVE);
            UpdateContext<Long, InboundCategoryMappingUpdateData> context =
                    new UpdateContext<>(mappingId, updateData, CommonVoFixtures.now());
            InboundCategoryMapping mapping =
                    InboundCategoryMappingFixtures.activeMapping(mappingId);

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
