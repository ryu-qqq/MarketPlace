package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.DescriptionCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.ProductGroupDescriptionCompositeDtoFixtures;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ProductGroupDescriptionCompositeMapper 단위 테스트.
 *
 * <p>Composite DTO를 Application Result로 올바르게 변환하는지 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ProductGroupDescriptionCompositeMapper 단위 테스트")
class ProductGroupDescriptionCompositeMapperTest {

    private final ProductGroupDescriptionCompositeMapper sut =
            new ProductGroupDescriptionCompositeMapper();

    @Nested
    @DisplayName("toResult 메서드 테스트")
    class ToResultTest {

        @Test
        @DisplayName("PENDING 상태 DTO를 변환할 때 publishStatus와 productGroupId가 올바르게 매핑됩니다")
        void toResult_WithPendingDto_ReturnsMappedResult() {
            // given
            DescriptionCompositeDto dto =
                    ProductGroupDescriptionCompositeDtoFixtures.defaultCompositeDto();

            // when
            DescriptionPublishStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.productGroupId())
                    .isEqualTo(
                            ProductGroupDescriptionCompositeDtoFixtures.DEFAULT_PRODUCT_GROUP_ID);
            assertThat(result.descriptionId())
                    .isEqualTo(ProductGroupDescriptionCompositeDtoFixtures.DEFAULT_DESCRIPTION_ID);
            assertThat(result.publishStatus())
                    .isEqualTo(
                            ProductGroupDescriptionCompositeDtoFixtures
                                    .DEFAULT_PUBLISH_STATUS_PENDING);
            assertThat(result.cdnPath()).isNull();
        }

        @Test
        @DisplayName("PUBLISHED 상태 DTO를 변환할 때 cdnPath가 올바르게 매핑됩니다")
        void toResult_WithPublishedDto_ReturnsCdnPath() {
            // given
            DescriptionCompositeDto dto =
                    ProductGroupDescriptionCompositeDtoFixtures.publishedCompositeDto(1L);

            // when
            DescriptionPublishStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.publishStatus())
                    .isEqualTo(
                            ProductGroupDescriptionCompositeDtoFixtures
                                    .DEFAULT_PUBLISH_STATUS_PUBLISHED);
            assertThat(result.cdnPath())
                    .isEqualTo(ProductGroupDescriptionCompositeDtoFixtures.DEFAULT_CDN_PATH);
        }

        @Test
        @DisplayName("이미지 2개가 모두 COMPLETED 상태일 때 집계 카운트가 올바르게 반환됩니다")
        void toResult_WithAllCompletedImages_ReturnsCorrectCounts() {
            // given
            DescriptionCompositeDto dto =
                    ProductGroupDescriptionCompositeDtoFixtures.defaultCompositeDto();

            // when
            DescriptionPublishStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.totalImageCount()).isEqualTo(2);
            assertThat(result.completedImageCount()).isEqualTo(2);
            assertThat(result.pendingImageCount()).isEqualTo(0);
            assertThat(result.failedImageCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("혼합 상태(COMPLETED/PENDING/FAILED)일 때 각 카운트가 올바르게 집계됩니다")
        void toResult_WithMixedStatusImages_ReturnsCorrectCounts() {
            // given
            DescriptionCompositeDto dto =
                    ProductGroupDescriptionCompositeDtoFixtures.mixedStatusCompositeDto(1L);

            // when
            DescriptionPublishStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.totalImageCount()).isEqualTo(3);
            assertThat(result.completedImageCount()).isEqualTo(1);
            assertThat(result.pendingImageCount()).isEqualTo(1);
            assertThat(result.failedImageCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("아웃박스가 없는 이미지는 상태가 null로 매핑됩니다")
        void toResult_WithImagesWithoutOutboxes_ImageStatusIsNull() {
            // given
            DescriptionCompositeDto dto =
                    ProductGroupDescriptionCompositeDtoFixtures.compositeDtoWithoutOutboxes(1L);

            // when
            DescriptionPublishStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.totalImageCount()).isEqualTo(2);
            assertThat(result.completedImageCount()).isEqualTo(0);
            assertThat(result.pendingImageCount()).isEqualTo(0);
            assertThat(result.failedImageCount()).isEqualTo(0);
            result.images().forEach(detail -> assertThat(detail.outboxStatus()).isNull());
        }

        @Test
        @DisplayName("이미지가 없을 때 빈 목록과 카운트 0이 반환됩니다")
        void toResult_WithNoImages_ReturnsZeroCountsAndEmptyList() {
            // given
            DescriptionCompositeDto dto =
                    ProductGroupDescriptionCompositeDtoFixtures.compositeDtoWithoutImages(1L);

            // when
            DescriptionPublishStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.totalImageCount()).isEqualTo(0);
            assertThat(result.completedImageCount()).isEqualTo(0);
            assertThat(result.pendingImageCount()).isEqualTo(0);
            assertThat(result.failedImageCount()).isEqualTo(0);
            assertThat(result.images()).isEmpty();
        }

        @Test
        @DisplayName(
                "이미지 ImageUploadDetail에 originUrl, uploadedUrl, retryCount, errorMessage가 올바르게"
                        + " 매핑됩니다")
        void toResult_WithCompletedImage_MapsAllDetailFieldsCorrectly() {
            // given
            DescriptionCompositeDto dto =
                    ProductGroupDescriptionCompositeDtoFixtures.defaultCompositeDto();

            // when
            DescriptionPublishStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.images()).hasSize(2);
            DescriptionPublishStatusResult.DescriptionImageUploadDetail detail =
                    result.images().get(0);
            assertThat(detail.imageId())
                    .isEqualTo(ProductGroupDescriptionCompositeDtoFixtures.DEFAULT_IMAGE_ID_1);
            assertThat(detail.originUrl())
                    .isEqualTo(ProductGroupDescriptionCompositeDtoFixtures.DEFAULT_ORIGIN_URL);
            assertThat(detail.uploadedUrl())
                    .isEqualTo(ProductGroupDescriptionCompositeDtoFixtures.DEFAULT_UPLOADED_URL);
            assertThat(detail.outboxStatus()).isEqualTo("COMPLETED");
            assertThat(detail.retryCount()).isEqualTo(0);
            assertThat(detail.errorMessage()).isNull();
        }

        @Test
        @DisplayName("FAILED 상태 이미지에 errorMessage가 올바르게 매핑됩니다")
        void toResult_WithFailedImage_MapsErrorMessageCorrectly() {
            // given
            DescriptionCompositeDto dto =
                    ProductGroupDescriptionCompositeDtoFixtures.mixedStatusCompositeDto(1L);

            // when
            DescriptionPublishStatusResult result = sut.toResult(dto);

            // then
            DescriptionPublishStatusResult.DescriptionImageUploadDetail failedDetail =
                    result.images().stream()
                            .filter(d -> "FAILED".equals(d.outboxStatus()))
                            .findFirst()
                            .orElseThrow();
            assertThat(failedDetail.retryCount()).isEqualTo(3);
            assertThat(failedDetail.errorMessage()).isNotBlank();
        }

        @Test
        @DisplayName("중복 sourceId가 있을 때 첫 번째 아웃박스가 우선 적용됩니다")
        void toResult_WithDuplicateSourceId_FirstOutboxWins() {
            // given
            Long imageId = 500L;
            var images =
                    java.util.List.of(
                            ProductGroupDescriptionCompositeDtoFixtures.completedImageProjectionDto(
                                    imageId));
            var outboxes =
                    java.util.List.of(
                            ProductGroupDescriptionCompositeDtoFixtures
                                    .completedOutboxProjectionDto(imageId),
                            ProductGroupDescriptionCompositeDtoFixtures.failedOutboxProjectionDto(
                                    imageId));
            DescriptionCompositeDto dto =
                    new DescriptionCompositeDto(
                            1L,
                            ProductGroupDescriptionCompositeDtoFixtures
                                    .pendingDescriptionProjectionDto(),
                            images,
                            outboxes);

            // when
            DescriptionPublishStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.completedImageCount()).isEqualTo(1);
            assertThat(result.failedImageCount()).isEqualTo(0);
            assertThat(result.images().get(0).outboxStatus()).isEqualTo("COMPLETED");
        }
    }
}
