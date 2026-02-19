package com.ryuqq.marketplace.adapter.in.rest.product;

import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.BatchChangeProductStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductPriceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductStockApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductsApiRequest;
import java.util.List;

/**
 * Product API 테스트 Fixtures.
 *
 * <p>Product REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ProductApiFixtures {

    private ProductApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_PRODUCT_ID = 1L;
    public static final Long DEFAULT_PRODUCT_GROUP_ID = 10L;
    public static final Long DEFAULT_SELLER_ID = 100L;
    public static final int DEFAULT_REGULAR_PRICE = 100000;
    public static final int DEFAULT_CURRENT_PRICE = 90000;
    public static final int DEFAULT_STOCK_QUANTITY = 50;

    // ===== UpdateProductPriceApiRequest =====

    public static UpdateProductPriceApiRequest updatePriceRequest() {
        return new UpdateProductPriceApiRequest(DEFAULT_REGULAR_PRICE, DEFAULT_CURRENT_PRICE);
    }

    public static UpdateProductPriceApiRequest updatePriceRequest(
            int regularPrice, int currentPrice) {
        return new UpdateProductPriceApiRequest(regularPrice, currentPrice);
    }

    // ===== UpdateProductStockApiRequest =====

    public static UpdateProductStockApiRequest updateStockRequest() {
        return new UpdateProductStockApiRequest(DEFAULT_STOCK_QUANTITY);
    }

    public static UpdateProductStockApiRequest updateStockRequest(int stockQuantity) {
        return new UpdateProductStockApiRequest(stockQuantity);
    }

    // ===== BatchChangeProductStatusApiRequest =====

    public static BatchChangeProductStatusApiRequest batchChangeStatusRequest() {
        return new BatchChangeProductStatusApiRequest(List.of(1L, 2L, 3L), "ACTIVE");
    }

    public static BatchChangeProductStatusApiRequest batchChangeStatusRequest(
            List<Long> productIds, String targetStatus) {
        return new BatchChangeProductStatusApiRequest(productIds, targetStatus);
    }

    // ===== UpdateProductsApiRequest =====

    public static UpdateProductsApiRequest updateProductsRequest() {
        List<UpdateProductsApiRequest.OptionValueApiRequest> values1 =
                List.of(
                        new UpdateProductsApiRequest.OptionValueApiRequest(1L, "블랙", 101L, 0),
                        new UpdateProductsApiRequest.OptionValueApiRequest(2L, "화이트", 102L, 1));

        List<UpdateProductsApiRequest.OptionValueApiRequest> values2 =
                List.of(
                        new UpdateProductsApiRequest.OptionValueApiRequest(3L, "S", 201L, 0),
                        new UpdateProductsApiRequest.OptionValueApiRequest(4L, "M", 202L, 1));

        List<UpdateProductsApiRequest.OptionGroupApiRequest> optionGroups =
                List.of(
                        new UpdateProductsApiRequest.OptionGroupApiRequest(
                                10L, "색상", 1001L, "PREDEFINED", values1),
                        new UpdateProductsApiRequest.OptionGroupApiRequest(
                                20L, "사이즈", 1002L, "PREDEFINED", values2));

        List<UpdateProductsApiRequest.ProductDataApiRequest> products =
                List.of(
                        new UpdateProductsApiRequest.ProductDataApiRequest(
                                1L,
                                "SKU-001",
                                100000,
                                90000,
                                50,
                                1,
                                List.of(
                                        new UpdateProductsApiRequest.SelectedOptionApiRequest(
                                                "색상", "블랙"),
                                        new UpdateProductsApiRequest.SelectedOptionApiRequest(
                                                "사이즈", "S"))),
                        new UpdateProductsApiRequest.ProductDataApiRequest(
                                2L,
                                "SKU-002",
                                100000,
                                90000,
                                30,
                                2,
                                List.of(
                                        new UpdateProductsApiRequest.SelectedOptionApiRequest(
                                                "색상", "블랙"),
                                        new UpdateProductsApiRequest.SelectedOptionApiRequest(
                                                "사이즈", "M"))),
                        new UpdateProductsApiRequest.ProductDataApiRequest(
                                3L,
                                "SKU-003",
                                100000,
                                90000,
                                20,
                                3,
                                List.of(
                                        new UpdateProductsApiRequest.SelectedOptionApiRequest(
                                                "색상", "화이트"),
                                        new UpdateProductsApiRequest.SelectedOptionApiRequest(
                                                "사이즈", "S"))),
                        new UpdateProductsApiRequest.ProductDataApiRequest(
                                4L,
                                "SKU-004",
                                100000,
                                90000,
                                10,
                                4,
                                List.of(
                                        new UpdateProductsApiRequest.SelectedOptionApiRequest(
                                                "색상", "화이트"),
                                        new UpdateProductsApiRequest.SelectedOptionApiRequest(
                                                "사이즈", "M"))));

        return new UpdateProductsApiRequest(optionGroups, products);
    }

    public static UpdateProductsApiRequest updateProductsRequestSingleOption() {
        List<UpdateProductsApiRequest.OptionValueApiRequest> values =
                List.of(new UpdateProductsApiRequest.OptionValueApiRequest(1L, "블랙", 101L, 0));

        List<UpdateProductsApiRequest.OptionGroupApiRequest> optionGroups =
                List.of(
                        new UpdateProductsApiRequest.OptionGroupApiRequest(
                                10L, "색상", 1001L, "PREDEFINED", values));

        List<UpdateProductsApiRequest.ProductDataApiRequest> products =
                List.of(
                        new UpdateProductsApiRequest.ProductDataApiRequest(
                                1L,
                                "SKU-001",
                                100000,
                                90000,
                                50,
                                1,
                                List.of(
                                        new UpdateProductsApiRequest.SelectedOptionApiRequest(
                                                "색상", "블랙"))));

        return new UpdateProductsApiRequest(optionGroups, products);
    }

    public static UpdateProductsApiRequest updateProductsRequestWithNewProduct() {
        List<UpdateProductsApiRequest.OptionValueApiRequest> values =
                List.of(new UpdateProductsApiRequest.OptionValueApiRequest(null, "레드", null, 0));

        List<UpdateProductsApiRequest.OptionGroupApiRequest> optionGroups =
                List.of(
                        new UpdateProductsApiRequest.OptionGroupApiRequest(
                                null, "색상", null, "PREDEFINED", values));

        List<UpdateProductsApiRequest.ProductDataApiRequest> products =
                List.of(
                        new UpdateProductsApiRequest.ProductDataApiRequest(
                                null,
                                "SKU-NEW",
                                80000,
                                70000,
                                100,
                                1,
                                List.of(
                                        new UpdateProductsApiRequest.SelectedOptionApiRequest(
                                                "색상", "레드"))));

        return new UpdateProductsApiRequest(optionGroups, products);
    }
}
