package com.ryuqq.marketplace.application.productgroupimage.service.command;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
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
@DisplayName("UpdateProductGroupImagesService 단위 테스트")
class UpdateProductGroupImagesServiceTest {

    @InjectMocks private UpdateProductGroupImagesService sut;

    @Mock private ImageCommandCoordinator imageCommandCoordinator;

    @Nested
    @DisplayName("execute() - 이미지 수정")
    class ExecuteTest {

        @Test
        @DisplayName("이미지 수정 커맨드를 Coordinator에 위임한다")
        void execute_ValidCommand_DelegatesToCoordinator() {
            // given
            List<UpdateProductGroupImagesCommand.ImageCommand> imageCommands =
                    List.of(
                            new UpdateProductGroupImagesCommand.ImageCommand(
                                    "THUMBNAIL", "https://example.com/thumb.jpg", 0));
            UpdateProductGroupImagesCommand command =
                    new UpdateProductGroupImagesCommand(1L, imageCommands);

            // when
            sut.execute(command);

            // then
            then(imageCommandCoordinator).should().update(command);
        }
    }
}
