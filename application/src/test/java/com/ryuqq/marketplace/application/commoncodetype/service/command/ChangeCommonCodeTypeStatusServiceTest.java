package com.ryuqq.marketplace.application.commoncodetype.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.commoncodetype.CommonCodeTypeCommandFixtures;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.ChangeActiveStatusCommand;
import com.ryuqq.marketplace.application.commoncodetype.factory.CommonCodeTypeCommandFactory;
import com.ryuqq.marketplace.application.commoncodetype.manager.CommonCodeTypeCommandManager;
import com.ryuqq.marketplace.application.commoncodetype.validator.CommonCodeTypeValidator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.exception.ActiveCommonCodesExistException;
import com.ryuqq.marketplace.domain.commoncodetype.exception.CommonCodeTypeException;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.time.Instant;
import java.util.List;
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
@DisplayName("ChangeCommonCodeTypeStatusService 단위 테스트")
class ChangeCommonCodeTypeStatusServiceTest {

    @InjectMocks private ChangeCommonCodeTypeStatusService sut;

    @Mock private CommonCodeTypeCommandFactory commandFactory;

    @Mock private CommonCodeTypeCommandManager commandManager;

    @Mock private CommonCodeTypeValidator validator;

    @Nested
    @DisplayName("execute() - 활성화 상태 변경")
    class ExecuteTest {

        @Test
        @DisplayName("공통 코드 타입을 활성화한다")
        void activateCommonCodeType_Success() {
            // given
            Long targetId = 1L;
            ChangeActiveStatusCommand command =
                    CommonCodeTypeCommandFixtures.activateCommand(targetId);
            CommonCodeType commonCodeType = CommonCodeTypeFixtures.inactiveCommonCodeType();
            Instant changedAt = CommonVoFixtures.now();

            List<StatusChangeContext<CommonCodeTypeId>> contexts =
                    List.of(new StatusChangeContext<>(CommonCodeTypeId.of(targetId), changedAt));
            List<CommonCodeTypeId> ids = List.of(CommonCodeTypeId.of(targetId));

            given(commandFactory.createStatusChangeContexts(command)).willReturn(contexts);
            given(validator.findAllExistingOrThrow(ids)).willReturn(List.of(commonCodeType));

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createStatusChangeContexts(command);
            then(validator).should().findAllExistingOrThrow(ids);
            then(commandManager).should().persistAll(anyList());
        }

        @Test
        @DisplayName("공통 코드 타입을 비활성화한다")
        void deactivateCommonCodeType_Success() {
            // given
            Long targetId = 1L;
            ChangeActiveStatusCommand command =
                    CommonCodeTypeCommandFixtures.deactivateCommand(targetId);
            CommonCodeType commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType(targetId);
            Instant changedAt = CommonVoFixtures.now();

            List<StatusChangeContext<CommonCodeTypeId>> contexts =
                    List.of(new StatusChangeContext<>(CommonCodeTypeId.of(targetId), changedAt));
            List<CommonCodeTypeId> ids = List.of(CommonCodeTypeId.of(targetId));

            given(commandFactory.createStatusChangeContexts(command)).willReturn(contexts);
            given(validator.findAllExistingOrThrow(ids)).willReturn(List.of(commonCodeType));

            // when
            sut.execute(command);

            // then
            then(validator).should().validateNoActiveCommonCodes(targetId);
            then(commandManager).should().persistAll(anyList());
        }

        @Test
        @DisplayName("빈 ID 목록으로 요청 시 아무 작업도 수행하지 않는다")
        void changeStatus_EmptyIds_DoNothing() {
            // given
            ChangeActiveStatusCommand command =
                    CommonCodeTypeCommandFixtures.changeStatusCommand(List.of(), true);

            given(commandFactory.createStatusChangeContexts(command)).willReturn(List.of());

            // when
            sut.execute(command);

            // then
            then(validator).shouldHaveNoMoreInteractions();
            then(commandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("활성화된 하위 공통 코드가 존재하면 비활성화 실패")
        void deactivate_WithActiveCommonCodes_ThrowsException() {
            // given
            Long targetId = 1L;
            ChangeActiveStatusCommand command =
                    CommonCodeTypeCommandFixtures.deactivateCommand(targetId);
            CommonCodeType commonCodeType = CommonCodeTypeFixtures.activeCommonCodeType(targetId);
            Instant changedAt = CommonVoFixtures.now();

            List<StatusChangeContext<CommonCodeTypeId>> contexts =
                    List.of(new StatusChangeContext<>(CommonCodeTypeId.of(targetId), changedAt));
            List<CommonCodeTypeId> ids = List.of(CommonCodeTypeId.of(targetId));

            given(commandFactory.createStatusChangeContexts(command)).willReturn(contexts);
            given(validator.findAllExistingOrThrow(ids)).willReturn(List.of(commonCodeType));
            willThrow(new ActiveCommonCodesExistException(String.valueOf(targetId)))
                    .given(validator)
                    .validateNoActiveCommonCodes(targetId);

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(CommonCodeTypeException.class);

            then(commandManager).shouldHaveNoInteractions();
        }
    }
}
