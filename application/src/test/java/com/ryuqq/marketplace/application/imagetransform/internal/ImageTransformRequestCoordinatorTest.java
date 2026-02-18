package com.ryuqq.marketplace.application.imagetransform.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imagetransform.ImageTransformCommandFixtures;
import com.ryuqq.marketplace.application.imagetransform.dto.command.RequestImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformOutboxFactory;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxReadManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageReadManager;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.id.ProductGroupImageId;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
@DisplayName("ImageTransformRequestCoordinator 단위 테스트")
class ImageTransformRequestCoordinatorTest {

    @InjectMocks private ImageTransformRequestCoordinator sut;

    @Mock private ProductGroupImageReadManager imageReadManager;
    @Mock private ImageTransformOutboxReadManager outboxReadManager;
    @Mock private ImageTransformOutboxFactory outboxFactory;
    @Mock private ImageTransformOutboxCommandManager outboxCommandManager;

    @Nested
    @DisplayName("request() - 수동 이미지 변환 요청 처리")
    class RequestTest {

        @Test
        @DisplayName("업로드된 이미지가 있고 활성 Outbox가 없으면 Outbox를 생성하고 저장한다")
        void request_UploadedImagesWithNoActiveOutboxes_CreatesAndPersistsOutboxes() {
            // given
            RequestImageTransformCommand command =
                    ImageTransformCommandFixtures.requestAllVariantsCommand(1L);

            ProductGroupImage uploadedImage = reconstitutedUploadedImage(10L);
            ProductGroupImages images = ProductGroupImages.reconstitute(List.of(uploadedImage));

            List<ImageTransformOutbox> createdOutboxes =
                    List.of(
                            ImageTransformFixtures.newPendingOutbox(ImageVariantType.SMALL_WEBP),
                            ImageTransformFixtures.newPendingOutbox(ImageVariantType.MEDIUM_WEBP));

            given(imageReadManager.getByProductGroupId(ProductGroupId.of(1L))).willReturn(images);
            given(
                            outboxReadManager.findActiveVariantTypesBySourceImageIds(
                                    List.of(10L), command.resolvedVariantTypes()))
                    .willReturn(Map.of());
            given(
                            outboxFactory.createOutboxes(
                                    any(),
                                    any(ImageSourceType.class),
                                    any(ImageUrl.class),
                                    anyList()))
                    .willReturn(createdOutboxes);

            // when
            sut.request(command);

            // then
            then(outboxCommandManager).should().persistAll(createdOutboxes);
        }

        @Test
        @DisplayName("업로드된 이미지가 없으면 Outbox를 생성하지 않는다")
        void request_NoUploadedImages_DoesNotCreateOutboxes() {
            // given
            RequestImageTransformCommand command =
                    ImageTransformCommandFixtures.requestAllVariantsCommand(1L);

            // 업로드 URL이 없는 이미지 (originUrl만 있음)
            ProductGroupImage nonUploadedImage =
                    ProductGroupImage.reconstitute(
                            ProductGroupImageId.of(10L),
                            ProductGroupId.of(1L),
                            ImageUrl.of("https://cdn.example.com/origin/image.jpg"),
                            null, // uploadedUrl이 null
                            ImageType.THUMBNAIL,
                            0,
                            DeletionStatus.active());
            ProductGroupImages images = ProductGroupImages.reconstitute(List.of(nonUploadedImage));

            given(imageReadManager.getByProductGroupId(ProductGroupId.of(1L))).willReturn(images);

            // when
            sut.request(command);

            // then
            then(outboxReadManager).shouldHaveNoInteractions();
            then(outboxFactory).shouldHaveNoInteractions();
            then(outboxCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("모든 Variant 타입에 활성 Outbox가 이미 있으면 새 Outbox를 생성하지 않는다")
        void request_AllVariantTypesAlreadyActive_DoesNotCreateOutboxes() {
            // given
            RequestImageTransformCommand command =
                    ImageTransformCommandFixtures.requestSpecificVariantsCommand(
                            1L, List.of(ImageVariantType.SMALL_WEBP, ImageVariantType.MEDIUM_WEBP));

            ProductGroupImage uploadedImage = reconstitutedUploadedImage(10L);
            ProductGroupImages images = ProductGroupImages.reconstitute(List.of(uploadedImage));

            Map<Long, Set<ImageVariantType>> activeMap =
                    Map.of(10L, Set.of(ImageVariantType.SMALL_WEBP, ImageVariantType.MEDIUM_WEBP));

            given(imageReadManager.getByProductGroupId(ProductGroupId.of(1L))).willReturn(images);
            given(
                            outboxReadManager.findActiveVariantTypesBySourceImageIds(
                                    List.of(10L), command.resolvedVariantTypes()))
                    .willReturn(activeMap);

            // when
            sut.request(command);

            // then
            then(outboxFactory).shouldHaveNoInteractions();
            then(outboxCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("일부 Variant 타입만 활성 Outbox가 있으면 나머지 타입에 대해서만 Outbox를 생성한다")
        void request_SomeVariantTypesActive_CreatesOutboxesForRemainingTypes() {
            // given
            RequestImageTransformCommand command =
                    ImageTransformCommandFixtures.requestSpecificVariantsCommand(
                            1L, List.of(ImageVariantType.SMALL_WEBP, ImageVariantType.MEDIUM_WEBP));

            ProductGroupImage uploadedImage = reconstitutedUploadedImage(10L);
            ProductGroupImages images = ProductGroupImages.reconstitute(List.of(uploadedImage));

            // SMALL_WEBP는 이미 활성 상태
            Map<Long, Set<ImageVariantType>> activeMap =
                    Map.of(10L, Set.of(ImageVariantType.SMALL_WEBP));

            List<ImageTransformOutbox> outboxesForMedium =
                    List.of(ImageTransformFixtures.newPendingOutbox(ImageVariantType.MEDIUM_WEBP));

            given(imageReadManager.getByProductGroupId(ProductGroupId.of(1L))).willReturn(images);
            given(
                            outboxReadManager.findActiveVariantTypesBySourceImageIds(
                                    List.of(10L), command.resolvedVariantTypes()))
                    .willReturn(activeMap);
            given(
                            outboxFactory.createOutboxes(
                                    any(),
                                    any(ImageSourceType.class),
                                    any(ImageUrl.class),
                                    anyList()))
                    .willReturn(outboxesForMedium);

            // when
            sut.request(command);

            // then
            then(outboxCommandManager).should().persistAll(outboxesForMedium);
        }
    }

    // ===== Helper =====

    private ProductGroupImage reconstitutedUploadedImage(Long imageId) {
        return ProductGroupImage.reconstitute(
                ProductGroupImageId.of(imageId),
                ProductGroupId.of(1L),
                ImageUrl.of("https://cdn.example.com/origin/image.jpg"),
                ImageUrl.of("https://cdn.example.com/uploaded/image.jpg"),
                ImageType.THUMBNAIL,
                0,
                DeletionStatus.active());
    }
}
