package com.ryuqq.marketplace.application.inboundcategorymapping.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.inboundcategorymapping.InboundCategoryMappingCommandFixtures;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.factory.InboundCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.inboundcategorymapping.manager.InboundCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.inboundcategorymapping.validator.InboundCategoryMappingValidator;
import com.ryuqq.marketplace.domain.inboundcategorymapping.InboundCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
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
@DisplayName("RegisterInboundCategoryMappingService 단위 테스트")
class RegisterInboundCategoryMappingServiceTest {

    @InjectMocks private RegisterInboundCategoryMappingService sut;

    @Mock private InboundCategoryMappingValidator validator;
    @Mock private InboundCategoryMappingCommandFactory commandFactory;
    @Mock private InboundCategoryMappingCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 카테고리 매핑 등록")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 외부 카테고리 매핑을 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsMappingId() {
            // given
            RegisterInboundCategoryMappingCommand command =
                    InboundCategoryMappingCommandFixtures.registerCommand();
            InboundCategoryMapping mapping = InboundCategoryMappingFixtures.newMapping();
            Long expectedId = 1L;

            willDoNothing()
                    .given(validator)
                    .validateNotDuplicate(
                            command.inboundSourceId(), command.externalCategoryCode());
            given(commandFactory.create(command)).willReturn(mapping);
            given(commandManager.persist(mapping)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator)
                    .should()
                    .validateNotDuplicate(
                            command.inboundSourceId(), command.externalCategoryCode());
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(mapping);
        }
    }
}
