package com.ryuqq.marketplace.application.commoncodetype.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.application.commoncodetype.CommonCodeTypeCommandFixtures;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.RegisterCommonCodeTypeCommand;
import com.ryuqq.marketplace.application.commoncodetype.factory.CommonCodeTypeCommandFactory;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeCommandManager;
import com.ryuqq.marketplace.application.commoncodetype.validator.CommonCodeTypeValidator;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.exception.CommonCodeTypeException;
import com.ryuqq.marketplace.domain.commoncodetype.exception.DuplicateCommonCodeTypeCodeException;
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
@DisplayName("RegisterCommonCodeTypeService 단위 테스트")
class RegisterCommonCodeTypeServiceTest {

    @InjectMocks private RegisterCommonCodeTypeService sut;

    @Mock private CommonCodeTypeCommandFactory commandFactory;

    @Mock private CommonCodeTypeCommandManager commandManager;

    @Mock private CommonCodeTypeValidator validator;

    @Nested
    @DisplayName("execute() - 공통 코드 타입 등록")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 Command로 공통 코드 타입을 등록한다")
        void registerCommonCodeType_Success() {
            // given
            RegisterCommonCodeTypeCommand command = CommonCodeTypeCommandFixtures.registerCommand();
            CommonCodeType commonCodeType = CommonCodeTypeFixtures.newCommonCodeType();
            Long expectedId = 1L;

            given(commandFactory.create(command)).willReturn(commonCodeType);
            given(commandManager.persist(commonCodeType)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator).should().validateCodeNotDuplicate(command.code());
            then(commandFactory).should().create(command);
            then(commandManager).should().persist(commonCodeType);
        }

        @Test
        @DisplayName("중복된 코드로 등록 시 예외가 발생한다")
        void registerCommonCodeType_DuplicateCode_ThrowsException() {
            // given
            RegisterCommonCodeTypeCommand command = CommonCodeTypeCommandFixtures.registerCommand();

            willThrow(new DuplicateCommonCodeTypeCodeException(command.code()))
                    .given(validator)
                    .validateCodeNotDuplicate(command.code());

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(CommonCodeTypeException.class);

            then(commandFactory).shouldHaveNoInteractions();
            then(commandManager).shouldHaveNoInteractions();
        }
    }
}
