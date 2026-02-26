package com.ryuqq.marketplace.adapter.in.rest.inboundproduct;

import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.ReceiveInboundProductApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductDescriptionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductImagesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductPriceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductStockApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductConversionApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductDetailApiResponse;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductDetailResult;
import java.util.List;

/**
 * InboundProduct API 테스트 Fixtures.
 *
 * <p>InboundProduct REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class InboundProductApiFixtures {

    private InboundProductApiFixtures() {}

    // ===== 상수 =====
    public static final long DEFAULT_INBOUND_SOURCE_ID = 1L;
    public static final String DEFAULT_EXTERNAL_PRODUCT_CODE = "EXT-001";
    public static final String DEFAULT_PRODUCT_NAME = "나이키 에어맥스 90";
    public static final String DEFAULT_EXTERNAL_BRAND_CODE = "BRAND-001";
    public static final String DEFAULT_EXTERNAL_CATEGORY_CODE = "CAT-001";
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final int DEFAULT_REGULAR_PRICE = 30000;
    public static final int DEFAULT_CURRENT_PRICE = 25000;
    public static final String DEFAULT_OPTION_TYPE = "SINGLE";
    public static final String DEFAULT_STATUS = "CONVERTED";
    public static final String DEFAULT_ACTION = "CREATED";
    public static final Long DEFAULT_INBOUND_PRODUCT_ID = 100L;
    public static final Long DEFAULT_INTERNAL_PRODUCT_GROUP_ID = 200L;

    // ===== Command Request Fixtures =====

    public static ReceiveInboundProductApiRequest receiveRequest() {
        List<ReceiveInboundProductApiRequest.ImageRequest> images =
                List.of(
                        new ReceiveInboundProductApiRequest.ImageRequest(
                                "THUMBNAIL", "https://example.com/image1.jpg", 0),
                        new ReceiveInboundProductApiRequest.ImageRequest(
                                "DETAIL", "https://example.com/image2.jpg", 1));

        List<ReceiveInboundProductApiRequest.OptionGroupRequest> optionGroups =
                List.of(
                        new ReceiveInboundProductApiRequest.OptionGroupRequest(
                                "색상",
                                "PREDEFINED",
                                List.of(
                                        new ReceiveInboundProductApiRequest.OptionValueRequest(
                                                "블랙", 0),
                                        new ReceiveInboundProductApiRequest.OptionValueRequest(
                                                "화이트", 1))));

        List<ReceiveInboundProductApiRequest.ProductRequest> products =
                List.of(
                        new ReceiveInboundProductApiRequest.ProductRequest(
                                "SKU-001",
                                DEFAULT_REGULAR_PRICE,
                                DEFAULT_CURRENT_PRICE,
                                100,
                                0,
                                List.of(
                                        new ReceiveInboundProductApiRequest.SelectedOptionRequest(
                                                "색상", "블랙"))),
                        new ReceiveInboundProductApiRequest.ProductRequest(
                                "SKU-002",
                                DEFAULT_REGULAR_PRICE,
                                DEFAULT_CURRENT_PRICE,
                                50,
                                1,
                                List.of(
                                        new ReceiveInboundProductApiRequest.SelectedOptionRequest(
                                                "색상", "화이트"))));

        ReceiveInboundProductApiRequest.DescriptionRequest description =
                new ReceiveInboundProductApiRequest.DescriptionRequest("<p>상품 상세 설명입니다.</p>");

        ReceiveInboundProductApiRequest.NoticeRequest notice =
                new ReceiveInboundProductApiRequest.NoticeRequest(
                        List.of(
                                new ReceiveInboundProductApiRequest.NoticeEntryRequest(
                                        "MATERIAL", "면 100%"),
                                new ReceiveInboundProductApiRequest.NoticeEntryRequest(
                                        "ORIGIN", "한국")));

        return new ReceiveInboundProductApiRequest(
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_PRODUCT_CODE,
                DEFAULT_PRODUCT_NAME,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_SELLER_ID,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_OPTION_TYPE,
                images,
                optionGroups,
                products,
                description,
                notice);
    }

    public static ReceiveInboundProductApiRequest receiveRequestWithoutNotice() {
        List<ReceiveInboundProductApiRequest.ImageRequest> images =
                List.of(
                        new ReceiveInboundProductApiRequest.ImageRequest(
                                "THUMBNAIL", "https://example.com/image1.jpg", 0));

        List<ReceiveInboundProductApiRequest.ProductRequest> products =
                List.of(
                        new ReceiveInboundProductApiRequest.ProductRequest(
                                "SKU-001",
                                DEFAULT_REGULAR_PRICE,
                                DEFAULT_CURRENT_PRICE,
                                100,
                                0,
                                List.of()));

        ReceiveInboundProductApiRequest.DescriptionRequest description =
                new ReceiveInboundProductApiRequest.DescriptionRequest("<p>상세 설명</p>");

        return new ReceiveInboundProductApiRequest(
                DEFAULT_INBOUND_SOURCE_ID,
                DEFAULT_EXTERNAL_PRODUCT_CODE,
                DEFAULT_PRODUCT_NAME,
                DEFAULT_EXTERNAL_BRAND_CODE,
                DEFAULT_EXTERNAL_CATEGORY_CODE,
                DEFAULT_SELLER_ID,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_OPTION_TYPE,
                images,
                List.of(),
                products,
                description,
                null);
    }

    public static UpdateInboundProductPriceApiRequest updatePriceRequest() {
        return new UpdateInboundProductPriceApiRequest(50000, 45000);
    }

    public static UpdateInboundProductStockApiRequest updateStockRequest() {
        return new UpdateInboundProductStockApiRequest(
                List.of(
                        new UpdateInboundProductStockApiRequest.StockEntry(1L, 100),
                        new UpdateInboundProductStockApiRequest.StockEntry(2L, 50)));
    }

    public static UpdateInboundProductImagesApiRequest updateImagesRequest() {
        return new UpdateInboundProductImagesApiRequest(
                List.of(
                        new UpdateInboundProductImagesApiRequest.ImageEntry(
                                "THUMBNAIL", "https://example.com/new-thumbnail.jpg", 0),
                        new UpdateInboundProductImagesApiRequest.ImageEntry(
                                "DETAIL", "https://example.com/new-detail.jpg", 1)));
    }

    public static UpdateInboundProductDescriptionApiRequest updateDescriptionRequest() {
        return new UpdateInboundProductDescriptionApiRequest("<p>수정된 상품 상세 설명입니다.</p>");
    }

    // ===== Application Result Fixtures =====

    public static InboundProductConversionResult conversionResult() {
        return InboundProductConversionResult.created(
                DEFAULT_INBOUND_PRODUCT_ID, DEFAULT_INTERNAL_PRODUCT_GROUP_ID);
    }

    public static InboundProductConversionResult conversionUpdatedResult() {
        return InboundProductConversionResult.updated(
                DEFAULT_INBOUND_PRODUCT_ID, DEFAULT_INTERNAL_PRODUCT_GROUP_ID);
    }

    public static InboundProductConversionResult conversionPendingMappingResult() {
        return InboundProductConversionResult.pendingMapping(DEFAULT_INBOUND_PRODUCT_ID);
    }

    public static InboundProductDetailResult detailResult() {
        List<InboundProductDetailResult.ProductItem> products =
                List.of(
                        new InboundProductDetailResult.ProductItem(
                                1L,
                                "SKU-001",
                                DEFAULT_REGULAR_PRICE,
                                DEFAULT_CURRENT_PRICE,
                                100,
                                0,
                                List.of(new InboundProductDetailResult.OptionItem("색상", "블랙"))),
                        new InboundProductDetailResult.ProductItem(
                                2L,
                                "SKU-002",
                                DEFAULT_REGULAR_PRICE,
                                DEFAULT_CURRENT_PRICE,
                                50,
                                1,
                                List.of(new InboundProductDetailResult.OptionItem("색상", "화이트"))));

        return new InboundProductDetailResult(
                DEFAULT_STATUS,
                DEFAULT_EXTERNAL_PRODUCT_CODE,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                products);
    }

    public static InboundProductDetailResult detailResultNotConverted() {
        return new InboundProductDetailResult(
                "PENDING_MAPPING", DEFAULT_EXTERNAL_PRODUCT_CODE, null, List.of());
    }

    // ===== API Response Fixtures =====

    public static InboundProductConversionApiResponse conversionApiResponse() {
        return new InboundProductConversionApiResponse(
                DEFAULT_INBOUND_PRODUCT_ID,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                DEFAULT_STATUS,
                DEFAULT_ACTION);
    }

    public static InboundProductDetailApiResponse detailApiResponse() {
        List<InboundProductDetailApiResponse.ProductItemApiResponse> products =
                List.of(
                        new InboundProductDetailApiResponse.ProductItemApiResponse(
                                1L,
                                "SKU-001",
                                DEFAULT_REGULAR_PRICE,
                                DEFAULT_CURRENT_PRICE,
                                100,
                                0,
                                List.of(
                                        new InboundProductDetailApiResponse.OptionItemApiResponse(
                                                "색상", "블랙"))),
                        new InboundProductDetailApiResponse.ProductItemApiResponse(
                                2L,
                                "SKU-002",
                                DEFAULT_REGULAR_PRICE,
                                DEFAULT_CURRENT_PRICE,
                                50,
                                1,
                                List.of(
                                        new InboundProductDetailApiResponse.OptionItemApiResponse(
                                                "색상", "화이트"))));

        return new InboundProductDetailApiResponse(
                DEFAULT_STATUS,
                DEFAULT_EXTERNAL_PRODUCT_CODE,
                DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                products);
    }

    public static InboundProductDetailApiResponse detailApiResponseNotConverted() {
        return new InboundProductDetailApiResponse(
                "PENDING_MAPPING", DEFAULT_EXTERNAL_PRODUCT_CODE, null, List.of());
    }
}
