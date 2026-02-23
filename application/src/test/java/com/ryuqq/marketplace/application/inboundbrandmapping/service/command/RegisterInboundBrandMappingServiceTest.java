package com.ryuqq.marketplace.application.inboundbrandmapping.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.inboundbrandmapping.InboundBrandMappingCommandFixtures;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.RegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.factory.InboundBrandMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundbrandmapping.manager.InboundBrandMappingCommandManager;
import com.ryuqq.marketplace.application.inboundbrandmapping.validator.InboundBrandMappingValidator;
import com.ryuqq.marketplace.domain.inboundbrandmapping.InboundBrandMappingFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
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
@DisplayName("RegisterInboundBrandMappingService 단위 테스트")
class RegisterInboundBrandMappingServiceTest {

    @InjectMocks private RegisterInboundBrandMappingService sut;

    @Mock private InboundBrandMappingValidator validator;
    @Mock private InboundBrandMappingCommandFactory commandFactory;
    @Mock private InboundBrandMappingCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 브랜드 매핑 등록")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 외부 브랜드 매핑을 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsMappingId() {
            // given
            RegisterInboundBrandMappingCommand command =
                    InboundBrandMappingCommandFixtures.registerCommand();
            InboundBrandMapping mapping = InboundBrandMappingFixtures.newMapping();
            Long expectedId = 1L;

            willDoNothing()
                    .given(validator)
                    .validateNotDuplicate(command.inboundSourceId(), command.externalBrandCode());
            given(commandFactory.create(command)).willReturn(mapping);
            given(commandManager.persist(mapping)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator)
                    .should()
                    .validateNotDuplicate(command.inboundSourceId(), command.externalBrandCode());
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(mapping);
        }
    }
}
