package com.ryuqq.marketplace.application.productnotice.service.command;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
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
@DisplayName("UpdateProductNoticeService 단위 테스트")
class UpdateProductNoticeServiceTest {

    @InjectMocks private UpdateProductNoticeService sut;

    @Mock private ProductNoticeCommandCoordinator productNoticeCommandCoordinator;

    @Nested
    @DisplayName("execute() - 고시정보 수정")
    class ExecuteTest {

        @Test
        @DisplayName("수정 커맨드를 Coordinator에 위임한다")
        void execute_ValidCommand_DelegatesToCoordinator() {
            // given
            List<UpdateProductNoticeCommand.NoticeEntryCommand> entries =
                    List.of(new UpdateProductNoticeCommand.NoticeEntryCommand(100L, "수정된 제조국"));
            UpdateProductNoticeCommand command = new UpdateProductNoticeCommand(1L, 10L, entries);

            // when
            sut.execute(command);

            // then
            then(productNoticeCommandCoordinator).should().update(command);
        }
    }
}
