package com.ryuqq.marketplace.application.commoncode.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.commoncode.CommonCodeCommandFixtures;
import com.ryuqq.marketplace.application.commoncode.dto.command.ChangeCommonCodeStatusCommand;
import com.ryuqq.marketplace.application.commoncode.factory.CommonCodeCommandFactory;
import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeCommandManager;
import com.ryuqq.marketplace.application.commoncode.validator.CommonCodeValidator;
import com.ryuqq.marketplace.domain.commoncode.CommonCodeFixtures;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import java.time.Instant;
import java.util.Collections;
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
@DisplayName("ChangeCommonCodeStatusService 단위 테스트")
class ChangeCommonCodeStatusServiceTest {

    @InjectMocks private ChangeCommonCodeStatusService sut;

    @Mock private CommonCodeCommandFactory commandFactory;
    @Mock private CommonCodeCommandManager commandManager;
    @Mock private CommonCodeValidator validator;

    @Nested
    @DisplayName("execute() - 공통 코드 상태 변경")
    class ExecuteTest {

        @Test
        @DisplayName("공통 코드를 활성화한다")
        void execute_Activate_ActivatesCommonCodes() {
            // given
            Long id = 100L;
            ChangeCommonCodeStatusCommand command = CommonCodeCommandFixtures.activateCommand(id);

            Instant changedAt = Instant.now();
            List<StatusChangeContext<CommonCodeId>> contexts =
                    List.of(new StatusChangeContext<>(CommonCodeId.of(id), changedAt));

            CommonCode commonCode = CommonCodeFixtures.inactiveCommonCode();
            List<CommonCode> commonCodes = List.of(commonCode);

            given(commandFactory.createStatusChangeContexts(command)).willReturn(contexts);
            given(validator.findAllExistingOrThrow(List.of(CommonCodeId.of(id))))
                    .willReturn(commonCodes);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createStatusChangeContexts(command);
            then(validator).should().findAllExistingOrThrow(List.of(CommonCodeId.of(id)));
            then(commandManager).should().persistAll(commonCodes);
        }

        @Test
        @DisplayName("공통 코드를 비활성화한다")
        void execute_Deactivate_DeactivatesCommonCodes() {
            // given
            Long id = 100L;
            ChangeCommonCodeStatusCommand command = CommonCodeCommandFixtures.deactivateCommand(id);

            Instant changedAt = Instant.now();
            List<StatusChangeContext<CommonCodeId>> contexts =
                    List.of(new StatusChangeContext<>(CommonCodeId.of(id), changedAt));

            CommonCode commonCode = CommonCodeFixtures.activeCommonCode(id);
            List<CommonCode> commonCodes = List.of(commonCode);

            given(commandFactory.createStatusChangeContexts(command)).willReturn(contexts);
            given(validator.findAllExistingOrThrow(List.of(CommonCodeId.of(id))))
                    .willReturn(commonCodes);

            // when
            sut.execute(command);

            // then
            then(commandManager).should().persistAll(commonCodes);
        }

        @Test
        @DisplayName("빈 ID 목록이면 아무 작업도 하지 않는다")
        void execute_EmptyIds_DoesNothing() {
            // given
            ChangeCommonCodeStatusCommand command =
                    new ChangeCommonCodeStatusCommand(Collections.emptyList(), true);

            given(commandFactory.createStatusChangeContexts(command))
                    .willReturn(Collections.emptyList());

            // when
            sut.execute(command);

            // then
            then(validator).should(never()).findAllExistingOrThrow(List.of());
            then(commandManager).should(never()).persistAll(List.of());
        }
    }
}
