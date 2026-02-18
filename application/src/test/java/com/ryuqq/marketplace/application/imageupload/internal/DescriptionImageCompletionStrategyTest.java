package com.ryuqq.marketplace.application.imageupload.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.productgroupdescription.manager.DescriptionImageCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.DescriptionImageReadManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
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
@DisplayName("DescriptionImageCompletionStrategy 단위 테스트")
class DescriptionImageCompletionStrategyTest {

    @InjectMocks private DescriptionImageCompletionStrategy sut;

    @Mock private DescriptionImageReadManager descriptionImageReadManager;
    @Mock private DescriptionImageCommandManager descriptionImageCommandManager;
    @Mock private ProductGroupDescriptionReadManager descriptionReadManager;
    @Mock private ProductGroupDescriptionCommandManager descriptionCommandManager;

    @Nested
    @DisplayName("supports() - sourceType 지원 여부")
    class SupportsTest {

        @Test
        @DisplayName("DESCRIPTION_IMAGE 타입을 지원한다")
        void supports_DescriptionImageType_ReturnsTrue() {
            // when
            boolean result = sut.supports(ImageSourceType.DESCRIPTION_IMAGE);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("PRODUCT_GROUP_IMAGE 타입을 지원하지 않는다")
        void supports_ProductGroupImageType_ReturnsFalse() {
            // when
            boolean result = sut.supports(ImageSourceType.PRODUCT_GROUP_IMAGE);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("complete() - 상세설명 이미지 업로드 완료 처리")
    class CompleteTest {

        @Test
        @DisplayName("이미지 uploaded_url을 업데이트하고 Description을 조회한다")
        void complete_ValidSourceId_UpdatesImageUrlAndChecksDescription() {
            // given
            Long sourceId = 1L;
            ImageUrl uploadedUrl = ProductGroupFixtures.defaultImageUrl();
            String fileAssetId = "asset-id-123";

            DescriptionImage image = ProductGroupFixtures.uploadedDescriptionImage();
            ProductGroupDescription description =
                    ProductGroupFixtures.defaultProductGroupDescription();

            given(descriptionImageReadManager.getById(sourceId)).willReturn(image);
            given(descriptionImageCommandManager.persist(image)).willReturn(sourceId);
            given(descriptionReadManager.getById(image.productGroupDescriptionIdValue()))
                    .willReturn(description);

            // when
            sut.complete(sourceId, uploadedUrl, fileAssetId);

            // then
            then(descriptionImageReadManager).should().getById(sourceId);
            then(descriptionImageCommandManager).should().persist(image);
            then(descriptionReadManager).should().getById(image.productGroupDescriptionIdValue());
        }

        @Test
        @DisplayName("모든 이미지가 업로드되고 PENDING 상태이면 Description을 PUBLISH_READY로 전환한다")
        void complete_AllImagesUploaded_PendingDescription_MarksPublishReady() {
            // given
            Long sourceId = 1L;
            ImageUrl uploadedUrl = ProductGroupFixtures.defaultImageUrl();
            String fileAssetId = null;

            DescriptionImage image = ProductGroupFixtures.uploadedDescriptionImage();

            ProductGroupDescription description =
                    ProductGroupDescription.reconstitute(
                            ProductGroupFixtures.defaultProductGroupDescriptionId(),
                            ProductGroupFixtures.newProductGroupId(),
                            ProductGroupFixtures.defaultDescriptionHtml(),
                            null,
                            DescriptionPublishStatus.PENDING,
                            List.of(image),
                            Instant.now(),
                            Instant.now());

            given(descriptionImageReadManager.getById(sourceId)).willReturn(image);
            given(descriptionImageCommandManager.persist(image)).willReturn(sourceId);
            given(descriptionReadManager.getById(image.productGroupDescriptionIdValue()))
                    .willReturn(description);
            given(descriptionCommandManager.persist(description)).willReturn(1L);

            // when
            sut.complete(sourceId, uploadedUrl, fileAssetId);

            // then
            assertThat(description.publishStatus())
                    .isEqualTo(DescriptionPublishStatus.PUBLISH_READY);
            then(descriptionCommandManager).should().persist(description);
        }

        @Test
        @DisplayName("PENDING 상태가 아닌 Description은 상태 변경 없이 종료한다")
        void complete_DescriptionNotPending_DoesNotChangeStatus() {
            // given
            Long sourceId = 1L;
            ImageUrl uploadedUrl = ProductGroupFixtures.defaultImageUrl();
            String fileAssetId = null;

            DescriptionImage image = ProductGroupFixtures.uploadedDescriptionImage();

            ProductGroupDescription description =
                    ProductGroupDescription.reconstitute(
                            ProductGroupFixtures.defaultProductGroupDescriptionId(),
                            ProductGroupFixtures.newProductGroupId(),
                            ProductGroupFixtures.defaultDescriptionHtml(),
                            ProductGroupFixtures.defaultCdnPath(),
                            DescriptionPublishStatus.PUBLISHED,
                            List.of(image),
                            Instant.now(),
                            Instant.now());

            given(descriptionImageReadManager.getById(sourceId)).willReturn(image);
            given(descriptionImageCommandManager.persist(image)).willReturn(sourceId);
            given(descriptionReadManager.getById(image.productGroupDescriptionIdValue()))
                    .willReturn(description);

            // when
            sut.complete(sourceId, uploadedUrl, fileAssetId);

            // then
            assertThat(description.publishStatus()).isEqualTo(DescriptionPublishStatus.PUBLISHED);
            then(descriptionCommandManager).should(never()).persist(any());
        }

        @Test
        @DisplayName("이미지가 아직 업로드되지 않은 항목이 있으면 Description 상태를 변경하지 않는다")
        void complete_NotAllImagesUploaded_DoesNotMarkPublishReady() {
            // given
            Long sourceId = 1L;
            ImageUrl uploadedUrl = ProductGroupFixtures.defaultImageUrl();
            String fileAssetId = null;

            DescriptionImage uploadedImage = ProductGroupFixtures.uploadedDescriptionImage();
            DescriptionImage pendingImage = ProductGroupFixtures.defaultDescriptionImage();

            ProductGroupDescription description =
                    ProductGroupDescription.reconstitute(
                            ProductGroupFixtures.defaultProductGroupDescriptionId(),
                            ProductGroupFixtures.newProductGroupId(),
                            ProductGroupFixtures.defaultDescriptionHtml(),
                            null,
                            DescriptionPublishStatus.PENDING,
                            List.of(uploadedImage, pendingImage),
                            Instant.now(),
                            Instant.now());

            given(descriptionImageReadManager.getById(sourceId)).willReturn(uploadedImage);
            given(descriptionImageCommandManager.persist(uploadedImage)).willReturn(sourceId);
            given(descriptionReadManager.getById(uploadedImage.productGroupDescriptionIdValue()))
                    .willReturn(description);

            // when
            sut.complete(sourceId, uploadedUrl, fileAssetId);

            // then
            assertThat(description.publishStatus()).isEqualTo(DescriptionPublishStatus.PENDING);
            then(descriptionCommandManager).should(never()).persist(any());
        }
    }
}
