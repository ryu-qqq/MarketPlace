package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.ProductGroupImageApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.response.ProductGroupImageUploadStatusApiResponse;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupImageQueryApiMapper 단위 테스트")
class ProductGroupImageQueryApiMapperTest {

    private ProductGroupImageQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductGroupImageQueryApiMapper();
    }

    @Nested
    @DisplayName("toResponse(ProductGroupImageUploadStatusResult) - 업로드 상태 응답 변환")
    class ToResponseTest {

        @Test
        @DisplayName("업로드 상태 Result를 ApiResponse로 변환한다")
        void toResponse_ValidResult_ReturnsApiResponse() {
            // given
            Long productGroupId = ProductGroupImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            ProductGroupImageUploadStatusResult result =
                    ProductGroupImageApiFixtures.uploadStatusResult(productGroupId);

            // when
            ProductGroupImageUploadStatusApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroupId()).isEqualTo(productGroupId);
            assertThat(response.totalCount()).isEqualTo(2);
            assertThat(response.completedCount()).isEqualTo(1);
            assertThat(response.pendingCount()).isEqualTo(1);
            assertThat(response.processingCount()).isEqualTo(0);
            assertThat(response.failedCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("이미지 목록이 정확히 변환된다")
        void toResponse_ImagesAreMappedCorrectly() {
            // given
            Long productGroupId = ProductGroupImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            ProductGroupImageUploadStatusResult result =
                    ProductGroupImageApiFixtures.uploadStatusResult(productGroupId);

            // when
            ProductGroupImageUploadStatusApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.images()).hasSize(2);
            assertThat(response.images().get(0).imageId()).isEqualTo(100L);
            assertThat(response.images().get(0).imageType()).isEqualTo("THUMBNAIL");
            assertThat(response.images().get(0).outboxStatus()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("모두 완료된 상태에서 completedCount가 정확히 반환된다")
        void toResponse_AllCompleted_ReturnsCorrectCount() {
            // given
            Long productGroupId = ProductGroupImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            ProductGroupImageUploadStatusResult result =
                    ProductGroupImageApiFixtures.uploadStatusResultAllCompleted(productGroupId);

            // when
            ProductGroupImageUploadStatusApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.completedCount()).isEqualTo(1);
            assertThat(response.pendingCount()).isEqualTo(0);
            assertThat(response.failedCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("실패 이미지가 있을 때 failedCount와 에러 메시지가 정확히 반환된다")
        void toResponse_WithFailed_ReturnsFailedCountAndErrorMessage() {
            // given
            Long productGroupId = ProductGroupImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            ProductGroupImageUploadStatusResult result =
                    ProductGroupImageApiFixtures.uploadStatusResultWithFailed(productGroupId);

            // when
            ProductGroupImageUploadStatusApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.failedCount()).isEqualTo(1);
            assertThat(response.images().get(0).outboxStatus()).isEqualTo("FAILED");
            assertThat(response.images().get(0).retryCount()).isEqualTo(3);
            assertThat(response.images().get(0).errorMessage()).isEqualTo("업로드 실패: 네트워크 오류");
        }

        @Test
        @DisplayName("원본 URL과 업로드 URL이 정확히 변환된다")
        void toResponse_UrlFields_AreMappedCorrectly() {
            // given
            Long productGroupId = ProductGroupImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            ProductGroupImageUploadStatusResult result =
                    ProductGroupImageApiFixtures.uploadStatusResult(productGroupId);

            // when
            ProductGroupImageUploadStatusApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.images().get(0).originUrl())
                    .isEqualTo("https://origin.example.com/img1.jpg");
            assertThat(response.images().get(0).uploadedUrl())
                    .isEqualTo("https://cdn.example.com/img1.jpg");
        }
    }
}
