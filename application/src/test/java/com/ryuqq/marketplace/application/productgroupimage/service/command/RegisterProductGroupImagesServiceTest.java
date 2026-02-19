package com.ryuqq.marketplace.application.productgroupimage.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.factory.ProductGroupImageFactory;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
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
@DisplayName("RegisterProductGroupImagesService 단위 테스트")
class RegisterProductGroupImagesServiceTest {

    @InjectMocks private RegisterProductGroupImagesService sut;

    @Mock private ProductGroupImageFactory imageFactory;
    @Mock private ImageCommandCoordinator imageCommandCoordinator;

    @Nested
    @DisplayName("execute() - 이미지 등록")
    class ExecuteTest {

        @Test
        @DisplayName("이미지 등록 커맨드로 이미지를 생성하고 저장 후 ID 목록을 반환한다")
        void execute_ValidCommand_ReturnsImageIds() {
            // given
            List<RegisterProductGroupImagesCommand.ImageCommand> imageCommands =
                    List.of(
                            new RegisterProductGroupImagesCommand.ImageCommand(
                                    "THUMBNAIL", "https://example.com/thumb.jpg", 0),
                            new RegisterProductGroupImagesCommand.ImageCommand(
                                    "DETAIL", "https://example.com/detail.jpg", 1));
            RegisterProductGroupImagesCommand command =
                    new RegisterProductGroupImagesCommand(1L, imageCommands);
            ProductGroupImages images = ProductGroupFixtures.productGroupImagesWithDetails();
            List<Long> expectedIds = List.of(10L, 11L);

            given(imageFactory.createFromImageRegistration(any(ProductGroupId.class), anyList()))
                    .willReturn(images);
            given(imageCommandCoordinator.register(images)).willReturn(expectedIds);

            // when
            List<Long> result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedIds);
            then(imageFactory)
                    .should()
                    .createFromImageRegistration(any(ProductGroupId.class), anyList());
            then(imageCommandCoordinator).should().register(images);
        }
    }
}
