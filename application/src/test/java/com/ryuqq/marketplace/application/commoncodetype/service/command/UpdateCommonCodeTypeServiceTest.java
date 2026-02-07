package com.ryuqq.marketplace.application.commoncodetype.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.commoncodetype.CommonCodeTypeCommandFixtures;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.UpdateCommonCodeTypeCommand;
import com.ryuqq.marketplace.application.commoncodetype.factory.CommonCodeTypeCommandFactory;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeCommandManager;
import com.ryuqq.marketplace.application.commoncodetype.validator.CommonCodeTypeValidator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeTypeUpdateData;
import com.ryuqq.marketplace.domain.commoncodetype.exception.CommonCodeTypeException;
import com.ryuqq.marketplace.domain.commoncodetype.exception.CommonCodeTypeNotFoundException;
import com.ryuqq.marketplace.domain.commoncodetype.exception.DuplicateCommonCodeTypeDisplayOrderException;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.time.Instant;
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
@DisplayName("UpdateCommonCodeTypeService 단위 테스트")
class UpdateCommonCodeTypeServiceTest {

    @InjectMocks private UpdateCommonCodeTypeService sut;

    @Mock private CommonCodeTypeCommandFactory commandFactory;

    @Mock private CommonCodeTypeCommandManager commandManager;

    @Mock private CommonCodeTypeValidator validator;

    @Nested
    @DisplayName("execute() - 공통 코드 타입 수정")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 Command로 공통 코드 타입을 수정한다")
        void updateCommonCodeType_Success() {
            // given
            Long targetId = 1L;
            UpdateCommonCodeTypeCommand command =
                    CommonCodeTypeCommandFixtures.updateCommand(targetId);
            CommonCodeType commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType(targetId);

            CommonCodeTypeId id = CommonCodeTypeId.of(targetId);
            CommonCodeTypeUpdateData updateData =
                    CommonCodeTypeUpdateData.of(
                            command.name(), command.description(), command.displayOrder());
            Instant changedAt = CommonVoFixtures.now();
            UpdateContext<CommonCodeTypeId, CommonCodeTypeUpdateData> context =
                    new UpdateContext<>(id, updateData, changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(id)).willReturn(commonCodeType);
            given(commandManager.persist(commonCodeType)).willReturn(targetId);

            // when
            sut.execute(command);

            // then
            then(validator)
                    .should()
                    .validateDisplayOrderNotDuplicate(command.displayOrder(), command.id());
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(id);
            then(commandManager).should().persist(commonCodeType);
        }

        @Test
        @DisplayName("존재하지 않는 공통 코드 타입 수정 시 예외가 발생한다")
        void updateCommonCodeType_NotFound_ThrowsException() {
            // given
            Long targetId = 999L;
            UpdateCommonCodeTypeCommand command =
                    CommonCodeTypeCommandFixtures.updateCommand(targetId);

            CommonCodeTypeId id = CommonCodeTypeId.of(targetId);
            CommonCodeTypeUpdateData updateData =
                    CommonCodeTypeUpdateData.of(
                            command.name(), command.description(), command.displayOrder());
            Instant changedAt = CommonVoFixtures.now();
            UpdateContext<CommonCodeTypeId, CommonCodeTypeUpdateData> context =
                    new UpdateContext<>(id, updateData, changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            willThrow(new CommonCodeTypeNotFoundException(targetId))
                    .given(validator)
                    .findExistingOrThrow(id);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(CommonCodeTypeException.class);

            then(commandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("중복된 표시 순서로 수정 시 예외가 발생한다")
        void updateCommonCodeType_DuplicateDisplayOrder_ThrowsException() {
            // given
            Long targetId = 1L;
            UpdateCommonCodeTypeCommand command =
                    CommonCodeTypeCommandFixtures.updateCommand(targetId);

            willThrow(new DuplicateCommonCodeTypeDisplayOrderException(command.displayOrder()))
                    .given(validator)
                    .validateDisplayOrderNotDuplicate(command.displayOrder(), command.id());

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(CommonCodeTypeException.class);

            then(commandFactory).shouldHaveNoInteractions();
            then(commandManager).shouldHaveNoInteractions();
        }
    }
}
