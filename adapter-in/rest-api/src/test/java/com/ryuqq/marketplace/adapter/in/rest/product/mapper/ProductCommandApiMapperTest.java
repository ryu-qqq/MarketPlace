package com.ryuqq.marketplace.adapter.in.rest.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.product.ProductApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.BatchChangeProductStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductPriceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductStockApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductsApiRequest;
import com.ryuqq.marketplace.application.product.dto.command.BatchChangeProductStatusCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductPriceCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductCommandApiMapper 단위 테스트")
class ProductCommandApiMapperTest {

    private ProductCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateProductPriceApiRequest) - 가격 수정 Command 변환")
    class ToUpdatePriceCommandTest {

        @Test
        @DisplayName("productId와 가격 정보가 정확히 Command로 변환된다")
        void toCommand_ValidRequest_ReturnsCommand() {
            // given
            Long productId = ProductApiFixtures.DEFAULT_PRODUCT_ID;
            UpdateProductPriceApiRequest request = ProductApiFixtures.updatePriceRequest();

            // when
            UpdateProductPriceCommand command = mapper.toCommand(productId, request);

            // then
            assertThat(command.productId()).isEqualTo(productId);
            assertThat(command.regularPrice()).isEqualTo(ProductApiFixtures.DEFAULT_REGULAR_PRICE);
            assertThat(command.currentPrice()).isEqualTo(ProductApiFixtures.DEFAULT_CURRENT_PRICE);
        }

        @Test
        @DisplayName("다른 productId도 정확히 Command에 전달된다")
        void toCommand_DifferentProductId_IsCorrectlyMapped() {
            // given
            Long productId = 999L;
            UpdateProductPriceApiRequest request = ProductApiFixtures.updatePriceRequest();

            // when
            UpdateProductPriceCommand command = mapper.toCommand(productId, request);

            // then
            assertThat(command.productId()).isEqualTo(999L);
        }

        @Test
        @DisplayName("정가와 판매가가 동일해도 Command로 변환된다")
        void toCommand_SamePrices_ReturnsCommand() {
            // given
            Long productId = ProductApiFixtures.DEFAULT_PRODUCT_ID;
            UpdateProductPriceApiRequest request =
                    ProductApiFixtures.updatePriceRequest(50000, 50000);

            // when
            UpdateProductPriceCommand command = mapper.toCommand(productId, request);

            // then
            assertThat(command.regularPrice()).isEqualTo(50000);
            assertThat(command.currentPrice()).isEqualTo(50000);
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateProductStockApiRequest) - 재고 수정 Command 변환")
    class ToUpdateStockCommandTest {

        @Test
        @DisplayName("productId와 재고 수량이 정확히 Command로 변환된다")
        void toCommand_ValidRequest_ReturnsCommand() {
            // given
            Long productId = ProductApiFixtures.DEFAULT_PRODUCT_ID;
            UpdateProductStockApiRequest request = ProductApiFixtures.updateStockRequest();

            // when
            UpdateProductStockCommand command = mapper.toCommand(productId, request);

            // then
            assertThat(command.productId()).isEqualTo(productId);
            assertThat(command.stockQuantity())
                    .isEqualTo(ProductApiFixtures.DEFAULT_STOCK_QUANTITY);
        }

        @Test
        @DisplayName("재고 수량 0도 정확히 Command로 변환된다")
        void toCommand_ZeroStock_ReturnsCommand() {
            // given
            Long productId = ProductApiFixtures.DEFAULT_PRODUCT_ID;
            UpdateProductStockApiRequest request = ProductApiFixtures.updateStockRequest(0);

            // when
            UpdateProductStockCommand command = mapper.toCommand(productId, request);

            // then
            assertThat(command.stockQuantity()).isEqualTo(0);
        }

        @Test
        @DisplayName("다른 productId도 정확히 Command에 전달된다")
        void toCommand_DifferentProductId_IsCorrectlyMapped() {
            // given
            Long productId = 777L;
            UpdateProductStockApiRequest request = ProductApiFixtures.updateStockRequest();

            // when
            UpdateProductStockCommand command = mapper.toCommand(productId, request);

            // then
            assertThat(command.productId()).isEqualTo(777L);
        }
    }

    @Nested
    @DisplayName("toCommand(long, Long, BatchChangeProductStatusApiRequest) - 배치 상태 변경 Command 변환")
    class ToBatchChangeStatusCommandTest {

        @Test
        @DisplayName("sellerId, productGroupId, 상품 ID 목록, 상태가 정확히 Command로 변환된다")
        void toCommand_ValidRequest_ReturnsCommand() {
            // given
            long sellerId = ProductApiFixtures.DEFAULT_SELLER_ID;
            Long productGroupId = ProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            BatchChangeProductStatusApiRequest request =
                    ProductApiFixtures.batchChangeStatusRequest();

            // when
            BatchChangeProductStatusCommand command =
                    mapper.toCommand(sellerId, productGroupId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(sellerId);
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.productIds()).containsExactly(1L, 2L, 3L);
            assertThat(command.targetStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("targetStatus INACTIVE도 정확히 Command로 변환된다")
        void toCommand_InactiveStatus_ReturnsCommand() {
            // given
            long sellerId = ProductApiFixtures.DEFAULT_SELLER_ID;
            Long productGroupId = ProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            BatchChangeProductStatusApiRequest request =
                    ProductApiFixtures.batchChangeStatusRequest(List.of(10L, 20L), "INACTIVE");

            // when
            BatchChangeProductStatusCommand command =
                    mapper.toCommand(sellerId, productGroupId, request);

            // then
            assertThat(command.targetStatus()).isEqualTo("INACTIVE");
            assertThat(command.productIds()).containsExactly(10L, 20L);
        }

        @Test
        @DisplayName("단일 상품 ID 목록도 정확히 Command로 변환된다")
        void toCommand_SingleProductId_ReturnsCommand() {
            // given
            long sellerId = ProductApiFixtures.DEFAULT_SELLER_ID;
            Long productGroupId = ProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            BatchChangeProductStatusApiRequest request =
                    ProductApiFixtures.batchChangeStatusRequest(List.of(5L), "SOLDOUT");

            // when
            BatchChangeProductStatusCommand command =
                    mapper.toCommand(sellerId, productGroupId, request);

            // then
            assertThat(command.productIds()).hasSize(1);
            assertThat(command.productIds().get(0)).isEqualTo(5L);
            assertThat(command.targetStatus()).isEqualTo("SOLDOUT");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateProductsApiRequest) - 상품 일괄 수정 Command 변환")
    class ToUpdateProductsCommandTest {

        @Test
        @DisplayName("productGroupId와 옵션 그룹 목록이 정확히 Command로 변환된다")
        void toCommand_ValidRequest_ReturnsCommand() {
            // given
            Long productGroupId = ProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductsApiRequest request = ProductApiFixtures.updateProductsRequest();

            // when
            UpdateProductsCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.optionGroups()).hasSize(2);
            assertThat(command.products()).hasSize(4);
        }

        @Test
        @DisplayName("옵션 그룹명이 정확히 Command로 변환된다")
        void toCommand_OptionGroupNames_AreCorrectlyMapped() {
            // given
            Long productGroupId = ProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductsApiRequest request = ProductApiFixtures.updateProductsRequest();

            // when
            UpdateProductsCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.optionGroups().get(0).optionGroupName()).isEqualTo("색상");
            assertThat(command.optionGroups().get(0).sellerOptionGroupId()).isEqualTo(10L);
            assertThat(command.optionGroups().get(0).canonicalOptionGroupId()).isEqualTo(1001L);
            assertThat(command.optionGroups().get(1).optionGroupName()).isEqualTo("사이즈");
        }

        @Test
        @DisplayName("옵션 값 목록이 정확히 Command로 변환된다")
        void toCommand_OptionValues_AreCorrectlyMapped() {
            // given
            Long productGroupId = ProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductsApiRequest request = ProductApiFixtures.updateProductsRequest();

            // when
            UpdateProductsCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.optionGroups().get(0).optionValues()).hasSize(2);
            assertThat(command.optionGroups().get(0).optionValues().get(0).optionValueName())
                    .isEqualTo("블랙");
            assertThat(command.optionGroups().get(0).optionValues().get(0).sellerOptionValueId())
                    .isEqualTo(1L);
            assertThat(command.optionGroups().get(0).optionValues().get(0).canonicalOptionValueId())
                    .isEqualTo(101L);
            assertThat(command.optionGroups().get(0).optionValues().get(0).sortOrder())
                    .isEqualTo(0);
        }

        @Test
        @DisplayName("상품 데이터가 정확히 Command로 변환된다")
        void toCommand_ProductData_IsCorrectlyMapped() {
            // given
            Long productGroupId = ProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductsApiRequest request = ProductApiFixtures.updateProductsRequest();

            // when
            UpdateProductsCommand command = mapper.toCommand(productGroupId, request);

            // then
            UpdateProductsCommand.ProductData firstProduct = command.products().get(0);
            assertThat(firstProduct.productId()).isEqualTo(1L);
            assertThat(firstProduct.skuCode()).isEqualTo("SKU-001");
            assertThat(firstProduct.regularPrice()).isEqualTo(100000);
            assertThat(firstProduct.currentPrice()).isEqualTo(90000);
            assertThat(firstProduct.stockQuantity()).isEqualTo(50);
            assertThat(firstProduct.sortOrder()).isEqualTo(1);
            assertThat(firstProduct.selectedOptions()).hasSize(2);
            assertThat(firstProduct.selectedOptions().get(0).optionGroupName()).isEqualTo("색상");
            assertThat(firstProduct.selectedOptions().get(0).optionValueName()).isEqualTo("블랙");
            assertThat(firstProduct.selectedOptions().get(1).optionGroupName()).isEqualTo("사이즈");
            assertThat(firstProduct.selectedOptions().get(1).optionValueName()).isEqualTo("S");
        }

        @Test
        @DisplayName("신규 상품 (productId=null) 도 정확히 Command로 변환된다")
        void toCommand_NewProduct_NullProductId_IsCorrectlyMapped() {
            // given
            Long productGroupId = ProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductsApiRequest request =
                    ProductApiFixtures.updateProductsRequestWithNewProduct();

            // when
            UpdateProductsCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.products().get(0).productId()).isNull();
            assertThat(command.products().get(0).skuCode()).isEqualTo("SKU-NEW");
            assertThat(command.optionGroups().get(0).sellerOptionGroupId()).isNull();
        }

        @Test
        @DisplayName("단일 옵션 그룹 요청도 정확히 Command로 변환된다")
        void toCommand_SingleOptionGroup_ReturnsCommand() {
            // given
            Long productGroupId = ProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductsApiRequest request =
                    ProductApiFixtures.updateProductsRequestSingleOption();

            // when
            UpdateProductsCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.optionGroups()).hasSize(1);
            assertThat(command.products()).hasSize(1);
        }
    }
}
