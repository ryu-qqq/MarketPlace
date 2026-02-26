package com.ryuqq.marketplace.application.inboundbrandmapping.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.inboundbrandmapping.InboundBrandMappingCommandFixtures;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.UpdateInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.factory.InboundBrandMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundbrandmapping.manager.InboundBrandMappingCommandManager;
import com.ryuqq.marketplace.application.inboundbrandmapping.validator.InboundBrandMappingValidator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.InboundBrandMappingFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingUpdateData;
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
@DisplayName("UpdateInboundBrandMappingService 단위 테스트")
class UpdateInboundBrandMappingServiceTest {

    @InjectMocks private UpdateInboundBrandMappingService sut;

    @Mock private InboundBrandMappingValidator validator;
    @Mock private InboundBrandMappingCommandFactory commandFactory;
    @Mock private InboundBrandMappingCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 브랜드 매핑 수정")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 외부 브랜드 매핑을 수정한다")
        void execute_ValidCommand_UpdatesMapping() {
            // given
            Long mappingId = 1L;
            UpdateInboundBrandMappingCommand command =
                    InboundBrandMappingCommandFixtures.updateCommand(mappingId);
            InboundBrandMappingUpdateData updateData =
                    InboundBrandMappingUpdateData.of(
                            command.externalBrandName(),
                            command.internalBrandId(),
                            com.ryuqq.marketplace.domain.inboundbrandmapping.vo
                                    .InboundBrandMappingStatus.ACTIVE);
            UpdateContext<Long, InboundBrandMappingUpdateData> context =
                    new UpdateContext<>(mappingId, updateData, CommonVoFixtures.now());
            InboundBrandMapping mapping = InboundBrandMappingFixtures.activeMapping(mappingId);

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
