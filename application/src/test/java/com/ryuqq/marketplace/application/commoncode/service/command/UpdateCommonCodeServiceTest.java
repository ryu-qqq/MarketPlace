package com.ryuqq.marketplace.application.commoncode.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.commoncode.CommonCodeCommandFixtures;
import com.ryuqq.marketplace.application.commoncode.dto.command.UpdateCommonCodeCommand;
import com.ryuqq.marketplace.application.commoncode.factory.CommonCodeCommandFactory;
import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeCommandManager;
import com.ryuqq.marketplace.application.commoncode.validator.CommonCodeValidator;
import com.ryuqq.marketplace.domain.commoncode.CommonCodeFixtures;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCodeUpdateData;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
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
@DisplayName("UpdateCommonCodeService 단위 테스트")
class UpdateCommonCodeServiceTest {

    @InjectMocks private UpdateCommonCodeService sut;

    @Mock private CommonCodeCommandFactory commandFactory;
    @Mock private CommonCodeCommandManager commandManager;
    @Mock private CommonCodeValidator validator;

    @Nested
    @DisplayName("execute() - 공통 코드 수정")
    class ExecuteTest {

        @Test
        @DisplayName("공통 코드를 수정한다")
        void execute_UpdatesCommonCode() {
            // given
            Long id = 100L;
            UpdateCommonCodeCommand command = CommonCodeCommandFixtures.updateCommand(id);
            CommonCode commonCode = CommonCodeFixtures.activeCommonCode(id);

            Instant changedAt = Instant.now();
            CommonCodeUpdateData updateData = CommonCodeFixtures.commonCodeUpdateData();
            UpdateContext<CommonCodeId, CommonCodeUpdateData> context =
                    new UpdateContext<>(CommonCodeId.of(id), updateData, changedAt);

            given(commandFactory.createUpdateContext(command)).willReturn(context);
            given(validator.findExistingOrThrow(context.id())).willReturn(commonCode);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateContext(command);
            then(validator).should().findExistingOrThrow(context.id());
            then(commandManager).should().persist(commonCode);
        }
    }
}
