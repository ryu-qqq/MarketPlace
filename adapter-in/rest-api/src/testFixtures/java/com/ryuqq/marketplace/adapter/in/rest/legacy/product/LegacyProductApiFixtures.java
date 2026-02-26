package com.ryuqq.marketplace.adapter.in.rest.legacy.product;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreatePriceRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductStockRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyOptionDto;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductFetchResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductStatusResponse;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Legacy Product API 테스트 Fixtures.
 *
 * <p>Legacy 상품(SKU) REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyProductApiFixtures {

    private LegacyProductApiFixtures() {}

    // ===== 상수 =====
    public static final long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final long DEFAULT_PRODUCT_ID_1 = 2001L;
    public static final long DEFAULT_PRODUCT_ID_2 = 2002L;
    public static final long DEFAULT_REGULAR_PRICE = 50000L;
    public static final long DEFAULT_CURRENT_PRICE = 45000L;
    public static final int DEFAULT_STOCK_QUANTITY = 100;

    // ===== Request Fixtures: LegacyCreatePriceRequest =====

    public static LegacyCreatePriceRequest priceRequest() {
        return new LegacyCreatePriceRequest(DEFAULT_REGULAR_PRICE, DEFAULT_CURRENT_PRICE);
    }

    public static LegacyCreatePriceRequest priceRequest(long regular, long current) {
        return new LegacyCreatePriceRequest(regular, current);
    }

    // ===== Request Fixtures: LegacyUpdateProductStockRequest =====

    public static List<LegacyUpdateProductStockRequest> stockRequests() {
        return List.of(
                new LegacyUpdateProductStockRequest(DEFAULT_PRODUCT_ID_1, 80),
                new LegacyUpdateProductStockRequest(DEFAULT_PRODUCT_ID_2, 40));
    }

    public static LegacyUpdateProductStockRequest stockRequest(long productId, int quantity) {
        return new LegacyUpdateProductStockRequest(productId, quantity);
    }

    // ===== Request Fixtures: LegacyCreateOptionRequest =====

    public static List<LegacyCreateOptionRequest> optionRequestsWithIds() {
        return List.of(
                new LegacyCreateOptionRequest(
                        DEFAULT_PRODUCT_ID_1,
                        80,
                        BigDecimal.ZERO,
                        List.of(new LegacyCreateOptionRequest.OptionDetail(10L, 100L, "색상", "블랙"))),
                new LegacyCreateOptionRequest(
                        DEFAULT_PRODUCT_ID_2,
                        40,
                        BigDecimal.ZERO,
                        List.of(
                                new LegacyCreateOptionRequest.OptionDetail(
                                        10L, 101L, "색상", "화이트"))));
    }

    public static List<LegacyCreateOptionRequest> optionRequestsWithoutIds() {
        return List.of(
                new LegacyCreateOptionRequest(
                        null,
                        100,
                        BigDecimal.ZERO,
                        List.of(
                                new LegacyCreateOptionRequest.OptionDetail(
                                        null, null, "색상", "블랙"))),
                new LegacyCreateOptionRequest(
                        null,
                        50,
                        BigDecimal.ZERO,
                        List.of(
                                new LegacyCreateOptionRequest.OptionDetail(
                                        null, null, "색상", "화이트"))));
    }

    public static List<LegacyCreateOptionRequest> singleOptionRequests() {
        return List.of(
                new LegacyCreateOptionRequest(
                        null, DEFAULT_STOCK_QUANTITY, BigDecimal.ZERO, List.of()));
    }

    // ===== Response Fixtures =====

    public static LegacyProductFetchResponse productFetchResponse(long productId) {
        LegacyOptionDto optionDto = new LegacyOptionDto(10L, 100L, "색상", "블랙");
        Set<LegacyOptionDto> options = new LinkedHashSet<>();
        options.add(optionDto);

        return new LegacyProductFetchResponse(
                productId,
                DEFAULT_STOCK_QUANTITY,
                LegacyProductStatusResponse.of(false, true),
                "색상블랙",
                options,
                BigDecimal.ZERO);
    }

    public static LegacyProductFetchResponse soldOutProductFetchResponse(long productId) {
        return new LegacyProductFetchResponse(
                productId,
                0,
                LegacyProductStatusResponse.of(true, false),
                "색상블랙",
                Set.of(new LegacyOptionDto(10L, 100L, "색상", "블랙")),
                BigDecimal.ZERO);
    }

    public static Set<LegacyProductFetchResponse> productFetchResponses() {
        Set<LegacyProductFetchResponse> responses = new LinkedHashSet<>();
        responses.add(productFetchResponse(DEFAULT_PRODUCT_ID_1));
        responses.add(productFetchResponse(DEFAULT_PRODUCT_ID_2));
        return responses;
    }

    // ===== LegacyProductStatusResponse Fixtures =====

    public static LegacyProductStatusResponse activeStatus() {
        return LegacyProductStatusResponse.of(false, true);
    }

    public static LegacyProductStatusResponse soldOutStatus() {
        return LegacyProductStatusResponse.of(true, false);
    }
}
