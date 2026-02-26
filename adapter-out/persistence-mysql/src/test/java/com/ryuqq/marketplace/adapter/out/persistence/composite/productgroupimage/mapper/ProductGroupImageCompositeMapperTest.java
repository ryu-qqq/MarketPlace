package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto.ProductGroupImageCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto.ProductGroupImageCompositeDtoFixtures;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ProductGroupImageCompositeMapper 단위 테스트.
 *
 * <p>Composite DTO를 Application Result로 올바르게 변환하는지 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ProductGroupImageCompositeMapper 단위 테스트")
class ProductGroupImageCompositeMapperTest {

    private final ProductGroupImageCompositeMapper sut = new ProductGroupImageCompositeMapper();

    @Nested
    @DisplayName("toResult 메서드 테스트")
    class ToResultTest {

        @Test
        @DisplayName("이미지 2개가 모두 COMPLETED 상태일 때 집계 카운트가 올바르게 반환됩니다")
        void toResult_WithAllCompleted_ReturnsCorrectCounts() {
            // given
            ProductGroupImageCompositeDto dto =
                    ProductGroupImageCompositeDtoFixtures.defaultCompositeDto();

            // when
            ProductGroupImageUploadStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.productGroupId())
                    .isEqualTo(ProductGroupImageCompositeDtoFixtures.DEFAULT_PRODUCT_GROUP_ID);
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.completedCount()).isEqualTo(2);
            assertThat(result.pendingCount()).isEqualTo(0);
            assertThat(result.processingCount()).isEqualTo(0);
            assertThat(result.failedCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("혼합 상태(COMPLETED/PENDING/FAILED)일 때 각 카운트가 올바르게 집계됩니다")
        void toResult_WithMixedStatus_ReturnsCorrectCounts() {
            // given
            ProductGroupImageCompositeDto dto =
                    ProductGroupImageCompositeDtoFixtures.mixedStatusCompositeDto(1L);

            // when
            ProductGroupImageUploadStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.totalCount()).isEqualTo(3);
            assertThat(result.completedCount()).isEqualTo(1);
            assertThat(result.pendingCount()).isEqualTo(1);
            assertThat(result.failedCount()).isEqualTo(1);
            assertThat(result.processingCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("아웃박스가 없는 이미지는 상태가 null로 매핑됩니다")
        void toResult_WithoutOutboxes_ImageStatusIsNull() {
            // given
            ProductGroupImageCompositeDto dto =
                    ProductGroupImageCompositeDtoFixtures.compositeDtoWithoutOutboxes(1L);

            // when
            ProductGroupImageUploadStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.totalCount()).isEqualTo(2);
            assertThat(result.completedCount()).isEqualTo(0);
            assertThat(result.pendingCount()).isEqualTo(0);
            assertThat(result.processingCount()).isEqualTo(0);
            assertThat(result.failedCount()).isEqualTo(0);
            result.images().forEach(detail -> assertThat(detail.outboxStatus()).isNull());
        }

        @Test
        @DisplayName("이미지가 없을 때 빈 목록이 반환됩니다")
        void toResult_WithEmptyImages_ReturnsEmptyDetails() {
            // given
            ProductGroupImageCompositeDto dto =
                    ProductGroupImageCompositeDtoFixtures.emptyCompositeDto(1L);

            // when
            ProductGroupImageUploadStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.totalCount()).isEqualTo(0);
            assertThat(result.completedCount()).isEqualTo(0);
            assertThat(result.images()).isEmpty();
        }

        @Test
        @DisplayName("단일 PROCESSING 상태 이미지가 올바르게 매핑됩니다")
        void toResult_WithSingleProcessingImage_ReturnsMappedDetail() {
            // given
            Long productGroupId = 5L;
            ProductGroupImageCompositeDto dto =
                    ProductGroupImageCompositeDtoFixtures.singleProcessingCompositeDto(
                            productGroupId);

            // when
            ProductGroupImageUploadStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.productGroupId()).isEqualTo(productGroupId);
            assertThat(result.totalCount()).isEqualTo(1);
            assertThat(result.processingCount()).isEqualTo(1);
            assertThat(result.completedCount()).isEqualTo(0);

            ProductGroupImageUploadStatusResult.ImageUploadDetail detail = result.images().get(0);
            assertThat(detail.outboxStatus()).isEqualTo("PROCESSING");
            assertThat(detail.retryCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("이미지 ImageUploadDetail에 원본 URL과 업로드 URL이 올바르게 매핑됩니다")
        void toResult_WithCompletedImage_MapsUrlsCorrectly() {
            // given
            ProductGroupImageCompositeDto dto =
                    ProductGroupImageCompositeDtoFixtures.defaultCompositeDto();

            // when
            ProductGroupImageUploadStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.images()).hasSize(2);
            ProductGroupImageUploadStatusResult.ImageUploadDetail detail = result.images().get(0);
            assertThat(detail.imageId())
                    .isEqualTo(ProductGroupImageCompositeDtoFixtures.DEFAULT_IMAGE_ID_1);
            assertThat(detail.imageType())
                    .isEqualTo(ProductGroupImageCompositeDtoFixtures.DEFAULT_IMAGE_TYPE);
            assertThat(detail.originUrl())
                    .isEqualTo(ProductGroupImageCompositeDtoFixtures.DEFAULT_ORIGIN_URL);
            assertThat(detail.uploadedUrl())
                    .isEqualTo(ProductGroupImageCompositeDtoFixtures.DEFAULT_UPLOADED_URL);
            assertThat(detail.outboxStatus()).isEqualTo("COMPLETED");
            assertThat(detail.retryCount()).isEqualTo(0);
            assertThat(detail.errorMessage()).isNull();
        }

        @Test
        @DisplayName("중복 sourceId가 있을 때 첫 번째 아웃박스가 우선 적용됩니다")
        void toResult_WithDuplicateSourceId_FirstOutboxWins() {
            // given
            Long imageId = 50L;
            List<
                            com.ryuqq.marketplace.adapter.out.persistence.composite
                                    .productgroupimage.dto.ImageProjectionDto>
                    images =
                            List.of(
                                    ProductGroupImageCompositeDtoFixtures
                                            .completedImageProjectionDto(imageId));
            List<
                            com.ryuqq.marketplace.adapter.out.persistence.composite
                                    .productgroupimage.dto.ImageOutboxProjectionDto>
                    outboxes =
                            List.of(
                                    ProductGroupImageCompositeDtoFixtures
                                            .completedOutboxProjectionDto(imageId),
                                    ProductGroupImageCompositeDtoFixtures.failedOutboxProjectionDto(
                                            imageId));
            ProductGroupImageCompositeDto dto =
                    new ProductGroupImageCompositeDto(1L, images, outboxes);

            // when
            ProductGroupImageUploadStatusResult result = sut.toResult(dto);

            // then
            assertThat(result.completedCount()).isEqualTo(1);
            assertThat(result.failedCount()).isEqualTo(0);
            assertThat(result.images().get(0).outboxStatus()).isEqualTo("COMPLETED");
        }
    }
}
