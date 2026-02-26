package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.ProductGroupImageApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.command.UpdateProductGroupImagesApiRequest;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupImageCommandApiMapper 단위 테스트")
class ProductGroupImageCommandApiMapperTest {

    private ProductGroupImageCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductGroupImageCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateProductGroupImagesApiRequest) - 이미지 수정 Command 변환")
    class ToCommandTest {

        @Test
        @DisplayName("productGroupId와 이미지 목록이 정확히 Command로 변환된다")
        void toCommand_ValidRequest_ReturnsCommand() {
            // given
            Long productGroupId = ProductGroupImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductGroupImagesApiRequest request =
                    ProductGroupImageApiFixtures.updateRequest();

            // when
            UpdateProductGroupImagesCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.images()).hasSize(2);
        }

        @Test
        @DisplayName("이미지의 imageType이 Command에 정확히 전달된다")
        void toCommand_ImageType_IsCorrectlyMapped() {
            // given
            Long productGroupId = ProductGroupImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductGroupImagesApiRequest request =
                    ProductGroupImageApiFixtures.updateRequest();

            // when
            UpdateProductGroupImagesCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.images().get(0).imageType()).isEqualTo("THUMBNAIL");
            assertThat(command.images().get(1).imageType()).isEqualTo("DETAIL");
        }

        @Test
        @DisplayName("이미지의 originUrl이 Command에 정확히 전달된다")
        void toCommand_OriginUrl_IsCorrectlyMapped() {
            // given
            Long productGroupId = ProductGroupImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductGroupImagesApiRequest request =
                    ProductGroupImageApiFixtures.updateRequest();

            // when
            UpdateProductGroupImagesCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.images().get(0).originUrl())
                    .isEqualTo("https://origin.example.com/img1.jpg");
            assertThat(command.images().get(1).originUrl())
                    .isEqualTo("https://origin.example.com/img2.jpg");
        }

        @Test
        @DisplayName("이미지의 sortOrder가 Command에 정확히 전달된다")
        void toCommand_SortOrder_IsCorrectlyMapped() {
            // given
            Long productGroupId = ProductGroupImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductGroupImagesApiRequest request =
                    ProductGroupImageApiFixtures.updateRequest();

            // when
            UpdateProductGroupImagesCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.images().get(0).sortOrder()).isEqualTo(1);
            assertThat(command.images().get(1).sortOrder()).isEqualTo(2);
        }

        @Test
        @DisplayName("단일 이미지 요청도 정확히 변환된다")
        void toCommand_SingleImage_ReturnsCommandWithOneImage() {
            // given
            Long productGroupId = ProductGroupImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductGroupImagesApiRequest request =
                    ProductGroupImageApiFixtures.updateRequestSingle();

            // when
            UpdateProductGroupImagesCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.images()).hasSize(1);
            assertThat(command.images().get(0).imageType()).isEqualTo("THUMBNAIL");
        }

        @Test
        @DisplayName("다른 productGroupId도 정확히 Command에 전달된다")
        void toCommand_DifferentProductGroupId_IsCorrectlyMapped() {
            // given
            Long productGroupId = 999L;
            UpdateProductGroupImagesApiRequest request =
                    ProductGroupImageApiFixtures.updateRequestSingle();

            // when
            UpdateProductGroupImagesCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(999L);
        }
    }
}
