package com.ryuqq.marketplace.application.externalbrandmapping.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.externalbrandmapping.ExternalBrandMappingCommandFixtures;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.command.RegisterExternalBrandMappingCommand;
import com.ryuqq.marketplace.application.externalbrandmapping.factory.ExternalBrandMappingCommandFactory;
import com.ryuqq.marketplace.application.externalbrandmapping.manager.ExternalBrandMappingCommandManager;
import com.ryuqq.marketplace.application.externalbrandmapping.validator.ExternalBrandMappingValidator;
import com.ryuqq.marketplace.domain.externalbrandmapping.ExternalBrandMappingFixtures;
import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
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
@DisplayName("RegisterExternalBrandMappingService 단위 테스트")
class RegisterExternalBrandMappingServiceTest {

    @InjectMocks private RegisterExternalBrandMappingService sut;

    @Mock private ExternalBrandMappingValidator validator;
    @Mock private ExternalBrandMappingCommandFactory commandFactory;
    @Mock private ExternalBrandMappingCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 브랜드 매핑 등록")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 외부 브랜드 매핑을 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsMappingId() {
            // given
            RegisterExternalBrandMappingCommand command =
                    ExternalBrandMappingCommandFixtures.registerCommand();
            ExternalBrandMapping mapping = ExternalBrandMappingFixtures.newMapping();
            Long expectedId = 1L;

            willDoNothing()
                    .given(validator)
                    .validateNotDuplicate(command.externalSourceId(), command.externalBrandCode());
            given(commandFactory.create(command)).willReturn(mapping);
            given(commandManager.persist(mapping)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator)
                    .should()
                    .validateNotDuplicate(command.externalSourceId(), command.externalBrandCode());
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(mapping);
        }
    }
}
