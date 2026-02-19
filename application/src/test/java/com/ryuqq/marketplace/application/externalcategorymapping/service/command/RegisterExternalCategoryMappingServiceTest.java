package com.ryuqq.marketplace.application.externalcategorymapping.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.externalcategorymapping.ExternalCategoryMappingCommandFixtures;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.command.RegisterExternalCategoryMappingCommand;
import com.ryuqq.marketplace.application.externalcategorymapping.factory.ExternalCategoryMappingCommandFactory;
import com.ryuqq.marketplace.application.externalcategorymapping.manager.ExternalCategoryMappingCommandManager;
import com.ryuqq.marketplace.application.externalcategorymapping.validator.ExternalCategoryMappingValidator;
import com.ryuqq.marketplace.domain.externalcategorymapping.ExternalCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
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
@DisplayName("RegisterExternalCategoryMappingService 단위 테스트")
class RegisterExternalCategoryMappingServiceTest {

    @InjectMocks private RegisterExternalCategoryMappingService sut;

    @Mock private ExternalCategoryMappingValidator validator;
    @Mock private ExternalCategoryMappingCommandFactory commandFactory;
    @Mock private ExternalCategoryMappingCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 카테고리 매핑 등록")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 외부 카테고리 매핑을 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsMappingId() {
            // given
            RegisterExternalCategoryMappingCommand command =
                    ExternalCategoryMappingCommandFixtures.registerCommand();
            ExternalCategoryMapping mapping = ExternalCategoryMappingFixtures.newMapping();
            Long expectedId = 1L;

            willDoNothing()
                    .given(validator)
                    .validateNotDuplicate(
                            command.externalSourceId(), command.externalCategoryCode());
            given(commandFactory.create(command)).willReturn(mapping);
            given(commandManager.persist(mapping)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator)
                    .should()
                    .validateNotDuplicate(
                            command.externalSourceId(), command.externalCategoryCode());
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(mapping);
        }
    }
}
