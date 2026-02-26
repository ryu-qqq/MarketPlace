package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.ProductGroupDescriptionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.response.DescriptionPublishStatusApiResponse;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupDescriptionQueryApiMapper 단위 테스트")
class ProductGroupDescriptionQueryApiMapperTest {

    private ProductGroupDescriptionQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductGroupDescriptionQueryApiMapper();
    }

    @Nested
    @DisplayName("toResponse(DescriptionPublishStatusResult) - 발행 상태 응답 변환")
    class ToResponseTest {

        @Test
        @DisplayName("발행 상태 Result를 ApiResponse로 변환한다")
        void toResponse_ValidResult_ReturnsApiResponse() {
            // given
            Long productGroupId = ProductGroupDescriptionApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            DescriptionPublishStatusResult result =
                    ProductGroupDescriptionApiFixtures.publishStatusResult(productGroupId);

            // when
            DescriptionPublishStatusApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroupId()).isEqualTo(productGroupId);
            assertThat(response.descriptionId())
                    .isEqualTo(ProductGroupDescriptionApiFixtures.DEFAULT_DESCRIPTION_ID);
            assertThat(response.publishStatus()).isEqualTo("DRAFT");
            assertThat(response.cdnPath())
                    .isEqualTo(ProductGroupDescriptionApiFixtures.DEFAULT_CDN_PATH);
        }

        @Test
        @DisplayName("이미지 업로드 카운트가 정확히 변환된다")
        void toResponse_ImageCounts_AreMappedCorrectly() {
            // given
            Long productGroupId = ProductGroupDescriptionApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            DescriptionPublishStatusResult result =
                    ProductGroupDescriptionApiFixtures.publishStatusResult(productGroupId);

            // when
            DescriptionPublishStatusApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.totalImageCount()).isEqualTo(2);
            assertThat(response.completedImageCount()).isEqualTo(1);
            assertThat(response.pendingImageCount()).isEqualTo(1);
            assertThat(response.failedImageCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("이미지 목록이 정확히 변환된다")
        void toResponse_Images_AreMappedCorrectly() {
            // given
            Long productGroupId = ProductGroupDescriptionApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            DescriptionPublishStatusResult result =
                    ProductGroupDescriptionApiFixtures.publishStatusResult(productGroupId);

            // when
            DescriptionPublishStatusApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.images()).hasSize(2);
            assertThat(response.images().get(0).imageId()).isEqualTo(200L);
            assertThat(response.images().get(0).outboxStatus()).isEqualTo("COMPLETED");
            assertThat(response.images().get(1).outboxStatus()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("PUBLISHED 상태의 Result도 정확히 변환된다")
        void toResponse_PublishedStatus_IsCorrectlyMapped() {
            // given
            Long productGroupId = ProductGroupDescriptionApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            DescriptionPublishStatusResult result =
                    ProductGroupDescriptionApiFixtures.publishStatusResultPublished(productGroupId);

            // when
            DescriptionPublishStatusApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.publishStatus()).isEqualTo("PUBLISHED");
            assertThat(response.completedImageCount()).isEqualTo(1);
            assertThat(response.pendingImageCount()).isEqualTo(0);
        }

        @Test
        @DisplayName("비어있는 Result도 정확히 변환된다")
        void toResponse_EmptyResult_ReturnsEmptyResponse() {
            // given
            Long productGroupId = ProductGroupDescriptionApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            DescriptionPublishStatusResult emptyResult =
                    ProductGroupDescriptionApiFixtures.publishStatusResultEmpty(productGroupId);

            // when
            DescriptionPublishStatusApiResponse response = mapper.toResponse(emptyResult);

            // then
            assertThat(response.productGroupId()).isEqualTo(productGroupId);
            assertThat(response.descriptionId()).isNull();
            assertThat(response.totalImageCount()).isEqualTo(0);
            assertThat(response.images()).isEmpty();
        }
    }
}
