package com.ryuqq.marketplace.application.productgroupimage.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.factory.ProductGroupImageFactory;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageReadManager;
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
@DisplayName("ImageCommandCoordinator 단위 테스트")
class ImageCommandCoordinatorTest {

    @InjectMocks private ImageCommandCoordinator sut;

    @Mock private ProductGroupImageCommandManager imageCommandManager;
    @Mock private ProductGroupImageReadManager imageReadManager;
    @Mock private ProductGroupImageFactory imageFactory;
    @Mock private ImageUploadOutboxCommandManager outboxCommandManager;

    @Nested
    @DisplayName("register(RegisterProductGroupImagesCommand) - 이미지 등록 Command 처리")
    class RegisterWithCommandTest {

        @Test
        @DisplayName("이미지 등록 Command로 이미지를 생성하고 저장 후 아웃박스를 생성한다")
        void register_ValidCommand_SavesImagesAndCreatesOutboxes() {
            // given
            List<RegisterProductGroupImagesCommand.ImageCommand> imageCommands =
                    List.of(
                            new RegisterProductGroupImagesCommand.ImageCommand(
                                    "THUMBNAIL", "https://example.com/thumb.jpg", 0));
            RegisterProductGroupImagesCommand command =
                    new RegisterProductGroupImagesCommand(1L, imageCommands);
            ProductGroupImages images = ProductGroupFixtures.defaultProductGroupImages();
            List<Long> expectedIds = List.of(10L);

            given(imageFactory.createFromImageRegistration(any(ProductGroupId.class), anyList()))
                    .willReturn(images);
            given(imageCommandManager.persistAll(anyList())).willReturn(expectedIds);
            given(imageFactory.createProductGroupImageOutboxes(anyList(), anyList()))
                    .willReturn(List.of());

            // when
            List<Long> result = sut.register(command);

            // then
            assertThat(result).isEqualTo(expectedIds);
            then(imageFactory)
                    .should()
                    .createFromImageRegistration(any(ProductGroupId.class), anyList());
            then(imageCommandManager).should().persistAll(anyList());
            then(outboxCommandManager).should().persistAll(anyList());
        }
    }

    @Nested
    @DisplayName("register(ProductGroupImages) - ProductGroupImages 직접 등록")
    class RegisterWithImagesTest {

        @Test
        @DisplayName("ProductGroupImages를 저장하고 아웃박스를 생성한다")
        void register_ValidImages_SavesAndCreatesOutboxes() {
            // given
            ProductGroupImages images = ProductGroupFixtures.defaultProductGroupImages();
            List<Long> expectedIds = List.of(10L);

            given(imageCommandManager.persistAll(anyList())).willReturn(expectedIds);
            given(imageFactory.createProductGroupImageOutboxes(anyList(), anyList()))
                    .willReturn(List.of());

            // when
            List<Long> result = sut.register(images);

            // then
            assertThat(result).isEqualTo(expectedIds);
            then(imageCommandManager).should().persistAll(anyList());
            then(outboxCommandManager).should().persistAll(anyList());
        }
    }
}
