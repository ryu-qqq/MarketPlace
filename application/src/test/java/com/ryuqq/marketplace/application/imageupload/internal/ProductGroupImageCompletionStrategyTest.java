package com.ryuqq.marketplace.application.imageupload.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformOutboxFactory;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageReadManager;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
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
@DisplayName("ProductGroupImageCompletionStrategy 단위 테스트")
class ProductGroupImageCompletionStrategyTest {

    @InjectMocks private ProductGroupImageCompletionStrategy sut;

    @Mock private ProductGroupImageReadManager productGroupImageReadManager;
    @Mock private ProductGroupImageCommandManager productGroupImageCommandManager;
    @Mock private ImageTransformOutboxFactory transformOutboxFactory;
    @Mock private ImageTransformOutboxCommandManager transformOutboxCommandManager;

    @Nested
    @DisplayName("supports() - sourceType 지원 여부")
    class SupportsTest {

        @Test
        @DisplayName("PRODUCT_GROUP_IMAGE 타입을 지원한다")
        void supports_ProductGroupImageType_ReturnsTrue() {
            // when
            boolean result = sut.supports(ImageSourceType.PRODUCT_GROUP_IMAGE);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("DESCRIPTION_IMAGE 타입을 지원하지 않는다")
        void supports_DescriptionImageType_ReturnsFalse() {
            // when
            boolean result = sut.supports(ImageSourceType.DESCRIPTION_IMAGE);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("complete() - 상품 그룹 이미지 업로드 완료 처리")
    class CompleteTest {

        @Test
        @DisplayName("이미지 uploaded_url을 업데이트하고 변환 Outbox를 생성한다")
        void complete_ValidSourceId_UpdatesUrlAndCreatesTransformOutboxes() {
            // given
            Long sourceId = 1L;
            ImageUrl uploadedUrl = ProductGroupFixtures.defaultImageUrl();
            String fileAssetId = "asset-id-123";

            ProductGroupImage image = ProductGroupFixtures.thumbnailImage();
            List<ImageTransformOutbox> transformOutboxes =
                    List.of(ImageTransformFixtures.pendingOutbox(1L));

            given(productGroupImageReadManager.getById(sourceId)).willReturn(image);
            given(productGroupImageCommandManager.persist(image)).willReturn(sourceId);
            given(
                            transformOutboxFactory.createOutboxes(
                                    eq(sourceId),
                                    eq(ImageSourceType.PRODUCT_GROUP_IMAGE),
                                    eq(uploadedUrl),
                                    eq(fileAssetId)))
                    .willReturn(transformOutboxes);

            // when
            sut.complete(sourceId, uploadedUrl, fileAssetId);

            // then
            then(productGroupImageReadManager).should().getById(sourceId);
            then(productGroupImageCommandManager).should().persist(image);
            then(transformOutboxFactory)
                    .should()
                    .createOutboxes(
                            sourceId,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            uploadedUrl,
                            fileAssetId);
            then(transformOutboxCommandManager).should().persistAll(transformOutboxes);
        }

        @Test
        @DisplayName("완료 처리 후 이미지의 uploadedUrl이 업데이트된다")
        void complete_ValidSourceId_ImageUploadedUrlIsUpdated() {
            // given
            Long sourceId = 1L;
            ImageUrl uploadedUrl = ImageUrl.of("https://cdn.example.com/uploaded.jpg");
            String fileAssetId = "asset-id-abc";

            ProductGroupImage image = ProductGroupFixtures.thumbnailImage();
            List<ImageTransformOutbox> transformOutboxes = List.of();

            given(productGroupImageReadManager.getById(sourceId)).willReturn(image);
            given(productGroupImageCommandManager.persist(image)).willReturn(sourceId);
            given(
                            transformOutboxFactory.createOutboxes(
                                    any(Long.class),
                                    any(ImageSourceType.class),
                                    any(ImageUrl.class),
                                    any(String.class)))
                    .willReturn(transformOutboxes);

            // when
            sut.complete(sourceId, uploadedUrl, fileAssetId);

            // then
            assertThat(image.uploadedUrlValue()).isEqualTo(uploadedUrl.value());
        }
    }
}
