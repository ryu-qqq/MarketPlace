package com.ryuqq.marketplace.application.externalsource.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

import com.ryuqq.marketplace.application.externalsource.ExternalSourceCommandFixtures;
import com.ryuqq.marketplace.application.externalsource.dto.command.RegisterExternalSourceCommand;
import com.ryuqq.marketplace.application.externalsource.factory.ExternalSourceCommandFactory;
import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceCommandManager;
import com.ryuqq.marketplace.application.externalsource.validator.ExternalSourceValidator;
import com.ryuqq.marketplace.domain.externalsource.ExternalSourceFixtures;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
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
@DisplayName("RegisterExternalSourceService 단위 테스트")
class RegisterExternalSourceServiceTest {

    @InjectMocks private RegisterExternalSourceService sut;

    @Mock private ExternalSourceValidator validator;
    @Mock private ExternalSourceCommandFactory commandFactory;
    @Mock private ExternalSourceCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 외부 소스 등록")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 외부 소스를 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsExternalSourceId() {
            // given
            RegisterExternalSourceCommand command = ExternalSourceCommandFixtures.registerCommand();
            ExternalSource externalSource = ExternalSourceFixtures.newExternalSource();
            Long expectedId = 1L;

            willDoNothing().given(validator).validateCodeNotDuplicate(command.code());
            given(commandFactory.create(command)).willReturn(externalSource);
            given(commandManager.persist(externalSource)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator).should().validateCodeNotDuplicate(command.code());
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(externalSource);
        }
    }
}
