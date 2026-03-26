package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreatePriceRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyOptionDto;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductFetchResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyProductStatusResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateClothesDetailRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateDeliveryNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateProductStatusRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateRefundNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyProductGroupDetailsRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacySearchProductGroupByOffsetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateDisplayYnRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateStatusRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyCreateProductGroupResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyBrandResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyClothesDetailResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyCrawlProductInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyDeliveryNoticeResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyPriceResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyProductGroupInfoResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyProductImageResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyProductNoticeResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse.LegacyRefundNoticeResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductGroupListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductGroupListApiResponse.LegacyBrandInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductGroupListApiResponse.LegacyPriceInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductGroupListApiResponse.LegacyProductGroupDetailItem;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductGroupListApiResponse.LegacyProductGroupInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductGroupListApiResponse.LegacyProductStatusInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.dto.request.LegacyUpdateProductDescriptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.legacy.shared.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyDeliveryResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyImageResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyNoticeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyOptionMappingResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyProductResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Legacy ProductGroup API 테스트 Fixtures.
 *
 * <p>Legacy 상품그룹 REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyProductGroupApiFixtures {

    private LegacyProductGroupApiFixtures() {}

    // ===== 상수 =====
    public static final long DEFAULT_PRODUCT_GROUP_ID = 100L;
    public static final long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_SELLER_NAME = "테스트 셀러";
    public static final long DEFAULT_BRAND_ID = 10L;
    public static final String DEFAULT_BRAND_NAME = "테스트 브랜드";
    public static final long DEFAULT_CATEGORY_ID = 1000L;
    public static final String DEFAULT_PRODUCT_GROUP_NAME = "레거시 테스트 상품";
    public static final String DEFAULT_OPTION_TYPE = "OPTION_ONE";
    public static final String DEFAULT_MANAGEMENT_TYPE = "SETOF";
    public static final long DEFAULT_REGULAR_PRICE = 50000L;
    public static final long DEFAULT_CURRENT_PRICE = 45000L;
    public static final String DEFAULT_PRODUCT_CONDITION = "NEW";
    public static final String DEFAULT_ORIGIN = "대한민국";
    public static final String DEFAULT_STYLE_CODE = "CASUAL";

    // ===== Request Fixtures: LegacyCreateProductGroupRequest =====

    public static LegacyCreateProductGroupRequest createRequest() {
        return new LegacyCreateProductGroupRequest(
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_SELLER_ID,
                DEFAULT_OPTION_TYPE,
                DEFAULT_MANAGEMENT_TYPE,
                DEFAULT_CATEGORY_ID,
                DEFAULT_BRAND_ID,
                productStatus("N", "Y"),
                price(DEFAULT_REGULAR_PRICE, DEFAULT_CURRENT_PRICE),
                productNotice(),
                clothesDetail(),
                deliveryNotice(),
                refundNotice(),
                imageList(),
                "<p>상품 상세 설명</p>",
                optionList());
    }

    public static LegacyCreateProductGroupRequest createRequestSingleOption() {
        List<LegacyCreateOptionRequest> singleOptions =
                List.of(new LegacyCreateOptionRequest(null, 100, BigDecimal.ZERO, List.of()));

        return new LegacyCreateProductGroupRequest(
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_SELLER_ID,
                "SINGLE",
                DEFAULT_MANAGEMENT_TYPE,
                DEFAULT_CATEGORY_ID,
                DEFAULT_BRAND_ID,
                productStatus("N", "Y"),
                price(DEFAULT_REGULAR_PRICE, DEFAULT_CURRENT_PRICE),
                productNotice(),
                clothesDetail(),
                deliveryNotice(),
                refundNotice(),
                imageList(),
                "<p>상품 상세 설명</p>",
                singleOptions);
    }

    // ===== Request Fixtures: LegacyUpdateProductGroupRequest =====

    public static LegacyUpdateProductGroupRequest updateRequest() {
        return new LegacyUpdateProductGroupRequest(
                productGroupDetails(),
                deliveryNotice(),
                refundNotice(),
                productNotice(),
                imageList(),
                new LegacyUpdateProductDescriptionRequest("<p>수정된 상세 설명</p>"),
                optionList(),
                updateStatus(true, true, true, true, true, true, true));
    }

    public static LegacyUpdateProductGroupRequest updateRequestMinimal() {
        return new LegacyUpdateProductGroupRequest(
                null,
                null,
                null,
                null,
                List.of(),
                null,
                List.of(),
                updateStatus(false, false, false, false, false, false, false));
    }

    // ===== Request Fixtures: LegacyUpdateDisplayYnRequest =====

    public static LegacyUpdateDisplayYnRequest displayYnRequest(String displayYn) {
        return new LegacyUpdateDisplayYnRequest(displayYn);
    }

    public static LegacyUpdateDisplayYnRequest displayOnRequest() {
        return new LegacyUpdateDisplayYnRequest("Y");
    }

    public static LegacyUpdateDisplayYnRequest displayOffRequest() {
        return new LegacyUpdateDisplayYnRequest("N");
    }

    // ===== Sub-object Fixtures =====

    public static LegacyCreateProductStatusRequest productStatus(
            String soldOutYn, String displayYn) {
        return new LegacyCreateProductStatusRequest(soldOutYn, displayYn);
    }

    public static LegacyCreatePriceRequest price(long regular, long current) {
        return new LegacyCreatePriceRequest(regular, current);
    }

    public static LegacyCreateProductNoticeRequest productNotice() {
        return new LegacyCreateProductNoticeRequest(
                "면 100%", "블랙", "FREE", "자체제작", "대한민국", "손세탁", "2024-01", "KC 인증", "02-1234-5678");
    }

    public static LegacyCreateClothesDetailRequest clothesDetail() {
        return new LegacyCreateClothesDetailRequest(
                DEFAULT_PRODUCT_CONDITION, DEFAULT_ORIGIN, DEFAULT_STYLE_CODE);
    }

    public static LegacyCreateDeliveryNoticeRequest deliveryNotice() {
        return new LegacyCreateDeliveryNoticeRequest("전국", 3000L, 3);
    }

    public static LegacyCreateRefundNoticeRequest refundNotice() {
        return new LegacyCreateRefundNoticeRequest("택배", "CJ대한통운", 3000, "서울시 강남구 테헤란로 1");
    }

    public static List<LegacyCreateProductImageRequest> imageList() {
        return List.of(
                new LegacyCreateProductImageRequest(
                        "MAIN",
                        "https://cdn.example.com/main.jpg",
                        "https://origin.example.com/main.jpg"),
                new LegacyCreateProductImageRequest(
                        "DETAIL",
                        "https://cdn.example.com/detail.jpg",
                        "https://origin.example.com/detail.jpg"));
    }

    public static List<LegacyCreateOptionRequest> optionList() {
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

    public static List<LegacyCreateOptionRequest> optionListWithIds() {
        return List.of(
                new LegacyCreateOptionRequest(
                        1001L,
                        80,
                        BigDecimal.ZERO,
                        List.of(new LegacyCreateOptionRequest.OptionDetail(10L, 100L, "색상", "블랙"))),
                new LegacyCreateOptionRequest(
                        1002L,
                        40,
                        BigDecimal.ZERO,
                        List.of(
                                new LegacyCreateOptionRequest.OptionDetail(
                                        10L, 101L, "색상", "화이트"))));
    }

    public static LegacyProductGroupDetailsRequest productGroupDetails() {
        return new LegacyProductGroupDetailsRequest(
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_OPTION_TYPE,
                DEFAULT_MANAGEMENT_TYPE,
                price(DEFAULT_REGULAR_PRICE, DEFAULT_CURRENT_PRICE),
                productStatus("N", "Y"),
                clothesDetail(),
                DEFAULT_SELLER_ID,
                DEFAULT_CATEGORY_ID,
                DEFAULT_BRAND_ID);
    }

    public static LegacyUpdateStatusRequest updateStatus(
            boolean productStatus,
            boolean noticeStatus,
            boolean imageStatus,
            boolean descriptionStatus,
            boolean stockOptionStatus,
            boolean deliveryStatus,
            boolean refundStatus) {
        return new LegacyUpdateStatusRequest(
                productStatus,
                noticeStatus,
                imageStatus,
                descriptionStatus,
                stockOptionStatus,
                deliveryStatus,
                refundStatus);
    }

    // ===== Application Result Fixtures =====

    public static LegacyProductRegistrationResult registrationResult() {
        return new LegacyProductRegistrationResult(
                DEFAULT_PRODUCT_GROUP_ID, DEFAULT_SELLER_ID, List.of(2001L, 2002L));
    }

    public static LegacyProductGroupDetailResult productGroupDetailResult() {
        return productGroupDetailResult(DEFAULT_PRODUCT_GROUP_ID);
    }

    public static LegacyProductGroupDetailResult productGroupDetailResult(long productGroupId) {
        LegacyNoticeResult notice =
                new LegacyNoticeResult(
                        "면 100%",
                        "블랙", "FREE", "자체제작", "대한민국", "손세탁", "2024-01", "KC 인증", "02-1234-5678");

        List<LegacyImageResult> images =
                List.of(
                        new LegacyImageResult("MAIN", "https://cdn.example.com/main.jpg"),
                        new LegacyImageResult("DETAIL", "https://cdn.example.com/detail.jpg"));

        LegacyDeliveryResult delivery =
                new LegacyDeliveryResult("전국", 3000, 3, "택배", "CJ대한통운", 3000, "서울시 강남구");

        List<LegacyOptionMappingResult> optionMappings =
                List.of(new LegacyOptionMappingResult(10L, 100L, "색상", "블랙"));

        List<LegacyProductResult> products =
                List.of(
                        new LegacyProductResult(2001L, 100, false, optionMappings),
                        new LegacyProductResult(
                                2002L,
                                50,
                                false,
                                List.of(new LegacyOptionMappingResult(10L, 101L, "색상", "화이트"))));

        return new LegacyProductGroupDetailResult(
                productGroupId,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_SELLER_ID,
                DEFAULT_SELLER_NAME,
                DEFAULT_BRAND_ID,
                DEFAULT_BRAND_NAME,
                DEFAULT_CATEGORY_ID,
                "패션 > 의류",
                DEFAULT_OPTION_TYPE,
                DEFAULT_MANAGEMENT_TYPE,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_CURRENT_PRICE,
                5000L,
                10,
                10,
                false,
                true,
                DEFAULT_PRODUCT_CONDITION,
                DEFAULT_ORIGIN,
                DEFAULT_STYLE_CODE,
                "admin",
                "admin",
                LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                LocalDateTime.of(2025, 1, 2, 10, 0, 0),
                notice,
                images,
                "<p>상품 상세 설명</p>",
                delivery,
                products);
    }

    public static LegacyProductGroupDetailResult productGroupDetailResultWithoutDelivery(
            long productGroupId) {
        LegacyNoticeResult notice =
                new LegacyNoticeResult(
                        "면 100%",
                        "블랙", "FREE", "자체제작", "대한민국", "손세탁", "2024-01", "KC 인증", "02-1234-5678");

        List<LegacyImageResult> images =
                List.of(new LegacyImageResult("MAIN", "https://cdn.example.com/main.jpg"));

        List<LegacyProductResult> products =
                List.of(new LegacyProductResult(2001L, 100, false, List.of()));

        return new LegacyProductGroupDetailResult(
                productGroupId,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_SELLER_ID,
                DEFAULT_SELLER_NAME,
                DEFAULT_BRAND_ID,
                DEFAULT_BRAND_NAME,
                DEFAULT_CATEGORY_ID,
                "패션 > 의류",
                DEFAULT_OPTION_TYPE,
                DEFAULT_MANAGEMENT_TYPE,
                DEFAULT_REGULAR_PRICE,
                DEFAULT_CURRENT_PRICE,
                DEFAULT_CURRENT_PRICE,
                0L,
                0,
                0,
                false,
                true,
                DEFAULT_PRODUCT_CONDITION,
                DEFAULT_ORIGIN,
                DEFAULT_STYLE_CODE,
                "admin",
                "admin",
                LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                LocalDateTime.of(2025, 1, 2, 10, 0, 0),
                notice,
                images,
                "<p>상품 상세 설명</p>",
                null,
                products);
    }

    // ===== 목록 조회 Fixtures =====

    public static LegacySearchProductGroupByOffsetApiRequest searchRequest() {
        return new LegacySearchProductGroupByOffsetApiRequest(
                null,
                null,
                null,
                null,
                DEFAULT_BRAND_ID,
                DEFAULT_SELLER_ID,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                20);
    }

    public static LegacyProductGroupSearchParams legacySearchParams() {
        return LegacyProductGroupSearchParams.of(
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                20);
    }

    public static LegacySearchProductGroupByOffsetApiRequest searchRequestWithFilters() {
        return new LegacySearchProductGroupByOffsetApiRequest(
                LocalDateTime.of(2025, 1, 1, 0, 0, 0),
                LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                null,
                DEFAULT_CATEGORY_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_SELLER_ID,
                "N",
                "Y",
                null,
                null,
                null,
                null,
                null,
                null,
                0,
                20);
    }

    public static LegacyProductGroupListApiResponse productGroupListApiResponse() {
        var productGroupInfo =
                new LegacyProductGroupInfo(
                        DEFAULT_PRODUCT_GROUP_ID,
                        DEFAULT_PRODUCT_GROUP_NAME,
                        DEFAULT_SELLER_ID,
                        DEFAULT_SELLER_NAME,
                        DEFAULT_CATEGORY_ID,
                        DEFAULT_OPTION_TYPE,
                        DEFAULT_MANAGEMENT_TYPE,
                        new LegacyBrandInfo(DEFAULT_BRAND_ID, DEFAULT_BRAND_NAME),
                        new LegacyPriceInfo(
                                java.math.BigDecimal.valueOf(DEFAULT_REGULAR_PRICE),
                                java.math.BigDecimal.valueOf(DEFAULT_CURRENT_PRICE),
                                10),
                        "https://cdn.example.com/main.jpg",
                        "패션 > 의류",
                        LegacyProductStatusInfo.of(false, true),
                        LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                        LocalDateTime.of(2025, 1, 2, 10, 0, 0));
        var products =
                List.of(
                        new LegacyProductGroupListApiResponse.LegacyProductItem(2001L, 100, "색상블랙"),
                        new LegacyProductGroupListApiResponse.LegacyProductItem(
                                2002L, 50, "색상화이트"));
        List<LegacyProductGroupDetailItem> items =
                List.of(new LegacyProductGroupDetailItem(productGroupInfo, products));
        return LegacyProductGroupListApiResponse.of(items, 1, 0, 20);
    }

    // ===== API Response Fixtures =====

    public static LegacyCreateProductGroupResponse createResponse() {
        return new LegacyCreateProductGroupResponse(
                DEFAULT_PRODUCT_GROUP_ID, DEFAULT_SELLER_ID, List.of(2001L, 2002L));
    }

    public static LegacyProductDetailApiResponse productDetailApiResponse() {
        return productDetailApiResponse(DEFAULT_PRODUCT_GROUP_ID);
    }

    public static LegacyProductDetailApiResponse productDetailApiResponse(long productGroupId) {
        LegacyProductGroupInfoResponse info =
                new LegacyProductGroupInfoResponse(
                        productGroupId,
                        DEFAULT_PRODUCT_GROUP_NAME,
                        DEFAULT_SELLER_ID,
                        DEFAULT_SELLER_NAME,
                        DEFAULT_CATEGORY_ID,
                        DEFAULT_OPTION_TYPE,
                        DEFAULT_MANAGEMENT_TYPE,
                        new LegacyBrandResponse(DEFAULT_BRAND_ID, DEFAULT_BRAND_NAME),
                        new LegacyPriceResponse(
                                BigDecimal.valueOf(DEFAULT_REGULAR_PRICE),
                                BigDecimal.valueOf(DEFAULT_CURRENT_PRICE),
                                BigDecimal.valueOf(DEFAULT_CURRENT_PRICE),
                                BigDecimal.valueOf(5000L),
                                10,
                                10),
                        new LegacyClothesDetailResponse(
                                DEFAULT_PRODUCT_CONDITION, DEFAULT_ORIGIN, DEFAULT_STYLE_CODE),
                        new LegacyDeliveryNoticeResponse("전국", 3000L, 3),
                        new LegacyRefundNoticeResponse("택배", "CJ대한통운", 3000, "서울시 강남구"),
                        "https://cdn.example.com/main.jpg",
                        "패션 > 의류",
                        LegacyProductStatusResponse.of(false, true),
                        LocalDateTime.of(2025, 1, 1, 10, 0, 0),
                        LocalDateTime.of(2025, 1, 2, 10, 0, 0),
                        "admin",
                        "admin",
                        LegacyCrawlProductInfoResponse.defaultValue(),
                        0L,
                        List.of(),
                        "");

        LegacyOptionDto optionDto = new LegacyOptionDto(10L, 100L, "색상", "블랙");
        Set<LegacyOptionDto> options = new LinkedHashSet<>();
        options.add(optionDto);

        Set<LegacyProductFetchResponse> products = new LinkedHashSet<>();
        products.add(
                new LegacyProductFetchResponse(
                        2001L,
                        100,
                        LegacyProductStatusResponse.of(false, true),
                        "색상블랙",
                        options,
                        BigDecimal.ZERO));

        LegacyProductNoticeResponse noticeResponse =
                new LegacyProductNoticeResponse(
                        "면 100%",
                        "블랙", "FREE", "자체제작", "대한민국", "손세탁", "2024-01", "KC 인증", "02-1234-5678");

        List<LegacyProductImageResponse> imageResponses =
                List.of(
                        new LegacyProductImageResponse("MAIN", "https://cdn.example.com/main.jpg"),
                        new LegacyProductImageResponse(
                                "DETAIL", "https://cdn.example.com/detail.jpg"));

        return new LegacyProductDetailApiResponse(
                info, products, noticeResponse, imageResponses, "<p>상품 상세 설명</p>", List.of());
    }
}
