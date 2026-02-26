package com.ryuqq.marketplace.application.commoncode.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.commoncode.CommonCodeCommandFixtures;
import com.ryuqq.marketplace.application.commoncode.dto.command.RegisterCommonCodeCommand;
import com.ryuqq.marketplace.application.commoncode.factory.CommonCodeCommandFactory;
import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeCommandManager;
import com.ryuqq.marketplace.application.commoncode.validator.CommonCodeValidator;
import com.ryuqq.marketplace.domain.commoncode.CommonCodeFixtures;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
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
@DisplayName("RegisterCommonCodeService 단위 테스트")
class RegisterCommonCodeServiceTest {

    @InjectMocks private RegisterCommonCodeService sut;

    @Mock private CommonCodeCommandFactory commandFactory;
    @Mock private CommonCodeCommandManager commandManager;
    @Mock private CommonCodeValidator validator;

    @Nested
    @DisplayName("execute() - 공통 코드 등록")
    class ExecuteTest {

        @Test
        @DisplayName("공통 코드를 등록하고 ID를 반환한다")
        void execute_RegistersCommonCode_ReturnsId() {
            // given
            Long commonCodeTypeId = 1L;
            Long expectedId = 100L;
            RegisterCommonCodeCommand command =
                    CommonCodeCommandFixtures.registerCommand(commonCodeTypeId);
            CommonCode commonCode = CommonCodeFixtures.newCommonCode();

            given(commandFactory.create(command)).willReturn(commonCode);
            given(commandManager.persist(commonCode)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator).should().validateCommonCodeTypeExists(commonCodeTypeId);
            then(validator).should().validateNotDuplicate(commonCodeTypeId, command.code());
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(commonCode);
        }
    }
}
