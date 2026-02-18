package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.ProductGroupDescriptionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.command.UpdateProductGroupDescriptionApiRequest;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupDescriptionCommandApiMapper 단위 테스트")
class ProductGroupDescriptionCommandApiMapperTest {

    private ProductGroupDescriptionCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductGroupDescriptionCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateProductGroupDescriptionApiRequest) - 설명 수정 Command 변환")
    class ToCommandTest {

        @Test
        @DisplayName("productGroupId와 content가 정확히 Command로 변환된다")
        void toCommand_ValidRequest_ReturnsCommand() {
            // given
            Long productGroupId = ProductGroupDescriptionApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductGroupDescriptionApiRequest request =
                    ProductGroupDescriptionApiFixtures.updateRequest();

            // when
            UpdateProductGroupDescriptionCommand command =
                    mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.content())
                    .isEqualTo(ProductGroupDescriptionApiFixtures.DEFAULT_CONTENT);
        }

        @Test
        @DisplayName("HTML 형식의 content가 Command에 그대로 전달된다")
        void toCommand_HtmlContent_IsCorrectlyMapped() {
            // given
            Long productGroupId = ProductGroupDescriptionApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            String htmlContent = "<p>상품 상세 설명 <strong>HTML</strong> 내용</p>";
            UpdateProductGroupDescriptionApiRequest request =
                    ProductGroupDescriptionApiFixtures.updateRequest(htmlContent);

            // when
            UpdateProductGroupDescriptionCommand command =
                    mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.content()).isEqualTo(htmlContent);
        }

        @Test
        @DisplayName("다른 productGroupId도 정확히 Command에 전달된다")
        void toCommand_DifferentProductGroupId_IsCorrectlyMapped() {
            // given
            Long productGroupId = 999L;
            UpdateProductGroupDescriptionApiRequest request =
                    ProductGroupDescriptionApiFixtures.updateRequest();

            // when
            UpdateProductGroupDescriptionCommand command =
                    mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(999L);
        }
    }
}
