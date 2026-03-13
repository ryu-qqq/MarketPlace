package com.ryuqq.marketplace.application.productgroupimage.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.common.port.out.InternalImageUrlChecker;
import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformOutboxFactory;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.factory.ProductGroupImageFactory;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageReadManager;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
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
    @Mock private ImageUploadOutboxCommandManager uploadOutboxCommandManager;
    @Mock private ImageTransformOutboxFactory transformOutboxFactory;
    @Mock private ImageTransformOutboxCommandManager transformOutboxCommandManager;
    @Mock private InternalImageUrlChecker internalImageUrlChecker;

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
            then(uploadOutboxCommandManager).should().persistAll(anyList());
        }
    }

    @Nested
    @DisplayName("register(ProductGroupImages) - ProductGroupImages 직접 등록")
    class RegisterWithImagesTest {

        @Test
        @DisplayName("외부 URL 이미지를 저장하고 ImageUploadOutbox를 생성한다")
        void register_ExternalUrl_CreatesUploadOutbox() {
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
            then(uploadOutboxCommandManager).should().persistAll(anyList());
            then(transformOutboxCommandManager).should(never()).persistAll(anyList());
        }

        @Test
        @DisplayName("내부 CDN URL 이미지는 ImageUploadOutbox를 건너뛰고 ImageTransformOutbox를 바로 생성한다")
        void register_InternalUrl_SkipsUploadAndCreatesTransformOutbox() {
            // given
            String internalUrl = "https://cdn.set-of.com/images/thumb.jpg";
            ProductGroupImage internalImage = ProductGroupImage.forNew(
                    ProductGroupId.of(1L),
                    ImageUrl.of(internalUrl),
                    ImageType.THUMBNAIL,
                    0);
            ProductGroupImages images = ProductGroupImages.of(List.of(internalImage));
            List<Long> expectedIds = List.of(10L);

            given(internalImageUrlChecker.isInternal(internalUrl)).willReturn(true);
            given(imageCommandManager.persistAll(anyList())).willReturn(expectedIds);
            given(transformOutboxFactory.createOutboxes(
                    anyLong(), any(), any(), isNull(String.class)))
                    .willReturn(List.of());

            // when
            List<Long> result = sut.register(images);

            // then
            assertThat(result).isEqualTo(expectedIds);
            then(uploadOutboxCommandManager).should(never()).persistAll(anyList());
            then(transformOutboxFactory).should().createOutboxes(
                    anyLong(), any(), any(), isNull(String.class));
        }
    }
}
