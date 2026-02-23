package com.ryuqq.marketplace.adapter.in.rest.productgroup;

import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.BatchChangeProductGroupStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.BatchRegisterProductGroupApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.RegisterProductGroupApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupBasicInfoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupFullApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.query.SearchProductGroupsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.*;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.NonReturnableConditionApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.RefundPolicyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.response.ShippingPolicyApiResponse;
import com.ryuqq.marketplace.application.product.dto.response.ProductDetailResult;
import com.ryuqq.marketplace.application.product.dto.response.ResolvedProductOptionResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.OptionGroupSummaryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.*;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionImageResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.ProductGroupDescriptionResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeEntryResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.NonReturnableConditionResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

/**
 * ProductGroup API 테스트 Fixtures.
 *
 * <p>ProductGroup REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ProductGroupApiFixtures {

    private ProductGroupApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final String DEFAULT_SELLER_NAME = "테스트 셀러";
    public static final Long DEFAULT_BRAND_ID = 100L;
    public static final String DEFAULT_BRAND_NAME = "테스트 브랜드";
    public static final Long DEFAULT_CATEGORY_ID = 1000L;
    public static final String DEFAULT_CATEGORY_NAME = "상의";
    public static final String DEFAULT_CATEGORY_PATH = "패션 > 남성패션 > 상의";
    public static final String DEFAULT_CATEGORY_ID_PATH = "1/5/1000";
    public static final String DEFAULT_PRODUCT_GROUP_NAME = "테스트 상품 그룹";
    public static final String DEFAULT_OPTION_TYPE = "COMBINATION";
    public static final String DEFAULT_STATUS = "ACTIVE";
    public static final String DEFAULT_THUMBNAIL_URL = "https://cdn.example.com/thumbnail.jpg";

    // ===== Command Request Fixtures =====

    public static RegisterProductGroupApiRequest registerRequest() {
        List<RegisterProductGroupApiRequest.ImageApiRequest> images =
                List.of(
                        new RegisterProductGroupApiRequest.ImageApiRequest(
                                "THUMBNAIL", "https://origin.example.com/img1.jpg", 1),
                        new RegisterProductGroupApiRequest.ImageApiRequest(
                                "DETAIL", "https://origin.example.com/img2.jpg", 2));

        List<RegisterProductGroupApiRequest.OptionGroupApiRequest> optionGroups =
                List.of(
                        new RegisterProductGroupApiRequest.OptionGroupApiRequest(
                                "색상",
                                10L,
                                "PREDEFINED",
                                List.of(
                                        new RegisterProductGroupApiRequest.OptionValueApiRequest(
                                                "블랙", 100L, 1),
                                        new RegisterProductGroupApiRequest.OptionValueApiRequest(
                                                "화이트", 101L, 2))));

        List<RegisterProductGroupApiRequest.ProductApiRequest> products =
                List.of(
                        new RegisterProductGroupApiRequest.ProductApiRequest(
                                "SKU-001",
                                30000,
                                25000,
                                100,
                                1,
                                List.of(
                                        new RegisterProductGroupApiRequest.SelectedOptionApiRequest(
                                                "색상", "블랙"))),
                        new RegisterProductGroupApiRequest.ProductApiRequest(
                                "SKU-002",
                                30000,
                                25000,
                                50,
                                2,
                                List.of(
                                        new RegisterProductGroupApiRequest.SelectedOptionApiRequest(
                                                "색상", "화이트"))));

        RegisterProductGroupApiRequest.DescriptionApiRequest description =
                new RegisterProductGroupApiRequest.DescriptionApiRequest("<p>상품 상세 설명</p>");

        RegisterProductGroupApiRequest.NoticeApiRequest notice =
                new RegisterProductGroupApiRequest.NoticeApiRequest(
                        1L,
                        List.of(
                                new RegisterProductGroupApiRequest.NoticeEntryApiRequest(1L, "제조사"),
                                new RegisterProductGroupApiRequest.NoticeEntryApiRequest(
                                        2L, "한국")));

        return new RegisterProductGroupApiRequest(
                DEFAULT_SELLER_ID,
                DEFAULT_BRAND_ID,
                DEFAULT_CATEGORY_ID,
                1L,
                1L,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_OPTION_TYPE,
                images,
                optionGroups,
                products,
                description,
                notice);
    }

    public static UpdateProductGroupFullApiRequest updateFullRequest() {
        List<UpdateProductGroupFullApiRequest.ImageApiRequest> images =
                List.of(
                        new UpdateProductGroupFullApiRequest.ImageApiRequest(
                                "THUMBNAIL", "https://origin.example.com/updated-img1.jpg", 1));

        List<UpdateProductGroupFullApiRequest.OptionGroupApiRequest> optionGroups =
                List.of(
                        new UpdateProductGroupFullApiRequest.OptionGroupApiRequest(
                                1L,
                                "색상",
                                10L,
                                "PREDEFINED",
                                List.of(
                                        new UpdateProductGroupFullApiRequest.OptionValueApiRequest(
                                                1L, "블랙", 100L, 1),
                                        new UpdateProductGroupFullApiRequest.OptionValueApiRequest(
                                                2L, "화이트", 101L, 2))));

        List<UpdateProductGroupFullApiRequest.ProductApiRequest> products =
                List.of(
                        new UpdateProductGroupFullApiRequest.ProductApiRequest(
                                1L,
                                "SKU-001",
                                35000,
                                30000,
                                80,
                                1,
                                List.of(
                                        new UpdateProductGroupFullApiRequest
                                                .SelectedOptionApiRequest("색상", "블랙"))));

        UpdateProductGroupFullApiRequest.DescriptionApiRequest description =
                new UpdateProductGroupFullApiRequest.DescriptionApiRequest("<p>수정된 상품 상세 설명</p>");

        UpdateProductGroupFullApiRequest.NoticeApiRequest notice =
                new UpdateProductGroupFullApiRequest.NoticeApiRequest(
                        1L,
                        List.of(
                                new UpdateProductGroupFullApiRequest.NoticeEntryApiRequest(
                                        1L, "수정된 제조사")));

        return new UpdateProductGroupFullApiRequest(
                "수정된 " + DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_BRAND_ID,
                DEFAULT_CATEGORY_ID,
                1L,
                1L,
                images,
                optionGroups,
                products,
                description,
                notice);
    }

    public static UpdateProductGroupBasicInfoApiRequest updateBasicInfoRequest() {
        return new UpdateProductGroupBasicInfoApiRequest(
                "수정된 " + DEFAULT_PRODUCT_GROUP_NAME, DEFAULT_BRAND_ID, DEFAULT_CATEGORY_ID, 1L, 1L);
    }

    public static BatchChangeProductGroupStatusApiRequest batchChangeStatusRequest() {
        return new BatchChangeProductGroupStatusApiRequest(List.of(1L, 2L, 3L), "ACTIVE");
    }

    public static BatchRegisterProductGroupApiRequest batchRegisterRequest() {
        return new BatchRegisterProductGroupApiRequest(
                List.of(registerRequest(), registerRequest()));
    }

    // ===== SearchProductGroupsApiRequest =====

    public static SearchProductGroupsApiRequest searchRequest() {
        return new SearchProductGroupsApiRequest(
                null, null, null, null, null, null, null, null, null, null, null, 0, 20);
    }

    public static SearchProductGroupsApiRequest searchRequest(int page, int size) {
        return new SearchProductGroupsApiRequest(
                null, null, null, null, null, null, null, null, null, null, null, page, size);
    }

    public static SearchProductGroupsApiRequest searchRequest(
            List<Long> sellerIds, String searchField, String searchWord) {
        return new SearchProductGroupsApiRequest(
                null,
                sellerIds,
                null,
                null,
                null,
                searchField,
                searchWord,
                null,
                null,
                null,
                null,
                0,
                20);
    }

    // ===== ProductGroupListCompositeResult (Application) =====

    public static ProductGroupListCompositeResult productGroupListResult(Long id) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");
        List<OptionGroupSummaryResult> optionGroups =
                List.of(
                        new OptionGroupSummaryResult("색상", List.of("블랙", "화이트")),
                        new OptionGroupSummaryResult("사이즈", List.of("S", "M", "L")));

        return new ProductGroupListCompositeResult(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_SELLER_NAME,
                DEFAULT_BRAND_ID,
                DEFAULT_BRAND_NAME,
                DEFAULT_CATEGORY_ID,
                DEFAULT_CATEGORY_NAME,
                DEFAULT_CATEGORY_PATH,
                DEFAULT_CATEGORY_ID_PATH,
                3,
                "MEN",
                "TOP",
                DEFAULT_PRODUCT_GROUP_NAME + "_" + id,
                DEFAULT_OPTION_TYPE,
                DEFAULT_STATUS,
                DEFAULT_THUMBNAIL_URL,
                6,
                10000,
                50000,
                30,
                optionGroups,
                now,
                now);
    }

    public static ProductGroupListCompositeResult productGroupListResult(
            Long id, String productGroupName) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");
        List<OptionGroupSummaryResult> optionGroups =
                List.of(new OptionGroupSummaryResult("색상", List.of("블랙", "화이트")));

        return new ProductGroupListCompositeResult(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_SELLER_NAME,
                DEFAULT_BRAND_ID,
                DEFAULT_BRAND_NAME,
                DEFAULT_CATEGORY_ID,
                DEFAULT_CATEGORY_NAME,
                DEFAULT_CATEGORY_PATH,
                DEFAULT_CATEGORY_ID_PATH,
                3,
                "MEN",
                "TOP",
                productGroupName,
                DEFAULT_OPTION_TYPE,
                DEFAULT_STATUS,
                DEFAULT_THUMBNAIL_URL,
                3,
                15000,
                30000,
                20,
                optionGroups,
                now,
                now);
    }

    public static List<ProductGroupListCompositeResult> productGroupListResults(int count) {
        return java.util.stream.IntStream.rangeClosed(1, count)
                .mapToObj(
                        i -> productGroupListResult((long) i, DEFAULT_PRODUCT_GROUP_NAME + "_" + i))
                .toList();
    }

    public static ProductGroupPageResult pageResult(int count, int page, int size) {
        List<ProductGroupListCompositeResult> results = productGroupListResults(count);
        return ProductGroupPageResult.of(results, page, size, (long) count);
    }

    public static ProductGroupPageResult emptyPageResult() {
        return ProductGroupPageResult.of(List.of(), 0, 20, 0L);
    }

    // ===== ProductGroupListApiResponse =====

    public static ProductGroupListApiResponse productGroupListApiResponse(Long id) {
        List<OptionGroupSummaryApiResponse> optionGroups =
                List.of(
                        new OptionGroupSummaryApiResponse("색상", List.of("블랙", "화이트")),
                        new OptionGroupSummaryApiResponse("사이즈", List.of("S", "M", "L")));

        return new ProductGroupListApiResponse(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_SELLER_NAME,
                DEFAULT_BRAND_ID,
                DEFAULT_BRAND_NAME,
                DEFAULT_CATEGORY_ID,
                DEFAULT_CATEGORY_NAME,
                DEFAULT_CATEGORY_PATH,
                DEFAULT_CATEGORY_ID_PATH,
                3,
                "MEN",
                "TOP",
                DEFAULT_PRODUCT_GROUP_NAME + "_" + id,
                DEFAULT_OPTION_TYPE,
                DEFAULT_STATUS,
                DEFAULT_THUMBNAIL_URL,
                6,
                10000,
                50000,
                30,
                optionGroups,
                "2025-02-10T10:30:00+09:00",
                "2025-02-10T10:30:00+09:00");
    }

    // ===== ProductGroupDetailCompositeResult (Application) =====

    public static ProductGroupDetailCompositeResult productGroupDetailResult(Long id) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");

        List<ProductGroupImageResult> images =
                List.of(
                        new ProductGroupImageResult(
                                1L,
                                "https://origin.example.com/img1.jpg",
                                "https://cdn.example.com/img1.jpg",
                                "THUMBNAIL",
                                1),
                        new ProductGroupImageResult(
                                2L,
                                "https://origin.example.com/img2.jpg",
                                "https://cdn.example.com/img2.jpg",
                                "DETAIL",
                                2));

        ProductOptionMatrixResult matrix = createProductOptionMatrixResult();
        ShippingPolicyResult shippingPolicy = createShippingPolicyResult();
        RefundPolicyResult refundPolicy = createRefundPolicyResult();
        ProductGroupDescriptionResult description = createProductGroupDescriptionResult();
        ProductNoticeResult productNotice = createProductNoticeResult();

        return new ProductGroupDetailCompositeResult(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_SELLER_NAME,
                DEFAULT_BRAND_ID,
                DEFAULT_BRAND_NAME,
                DEFAULT_CATEGORY_ID,
                DEFAULT_CATEGORY_NAME,
                DEFAULT_CATEGORY_PATH,
                DEFAULT_CATEGORY_ID_PATH,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_OPTION_TYPE,
                DEFAULT_STATUS,
                now,
                now,
                images,
                matrix,
                shippingPolicy,
                refundPolicy,
                description,
                productNotice);
    }

    public static ProductGroupDetailCompositeResult productGroupDetailResultMinimal(Long id) {
        Instant now = Instant.parse("2025-02-10T01:30:00Z");

        List<ProductGroupImageResult> images =
                List.of(
                        new ProductGroupImageResult(
                                1L,
                                "https://origin.example.com/img1.jpg",
                                "https://cdn.example.com/img1.jpg",
                                "THUMBNAIL",
                                1));

        ProductOptionMatrixResult matrix = createProductOptionMatrixResult();

        return new ProductGroupDetailCompositeResult(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_SELLER_NAME,
                DEFAULT_BRAND_ID,
                DEFAULT_BRAND_NAME,
                DEFAULT_CATEGORY_ID,
                DEFAULT_CATEGORY_NAME,
                DEFAULT_CATEGORY_PATH,
                DEFAULT_CATEGORY_ID_PATH,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_OPTION_TYPE,
                DEFAULT_STATUS,
                now,
                now,
                images,
                matrix,
                null,
                null,
                null,
                null);
    }

    // ===== ProductGroupDetailApiResponse =====

    public static ProductGroupDetailApiResponse productGroupDetailApiResponse(Long id) {
        List<ProductGroupImageApiResponse> images =
                List.of(
                        new ProductGroupImageApiResponse(
                                1L,
                                "https://origin.example.com/img1.jpg",
                                "https://cdn.example.com/img1.jpg",
                                "THUMBNAIL",
                                1),
                        new ProductGroupImageApiResponse(
                                2L,
                                "https://origin.example.com/img2.jpg",
                                "https://cdn.example.com/img2.jpg",
                                "DETAIL",
                                2));

        ProductOptionMatrixApiResponse matrix = createProductOptionMatrixApiResponse();
        ShippingPolicyApiResponse shippingPolicy = createShippingPolicyApiResponse();
        RefundPolicyApiResponse refundPolicy = createRefundPolicyApiResponse();
        ProductGroupDescriptionApiResponse description = createProductGroupDescriptionApiResponse();
        ProductNoticeApiResponse productNotice = createProductNoticeApiResponse();

        return new ProductGroupDetailApiResponse(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_SELLER_NAME,
                DEFAULT_BRAND_ID,
                DEFAULT_BRAND_NAME,
                DEFAULT_CATEGORY_ID,
                DEFAULT_CATEGORY_NAME,
                DEFAULT_CATEGORY_PATH,
                DEFAULT_CATEGORY_ID_PATH,
                DEFAULT_PRODUCT_GROUP_NAME,
                DEFAULT_OPTION_TYPE,
                DEFAULT_STATUS,
                "2025-02-10T10:30:00+09:00",
                "2025-02-10T10:30:00+09:00",
                images,
                matrix,
                shippingPolicy,
                refundPolicy,
                description,
                productNotice);
    }

    // ===== Helper Methods =====

    private static ProductOptionMatrixResult createProductOptionMatrixResult() {
        SellerOptionValueResult optionValue1 = new SellerOptionValueResult(1L, 1L, "블랙", 100L, 1);
        SellerOptionValueResult optionValue2 = new SellerOptionValueResult(2L, 1L, "화이트", 101L, 2);

        SellerOptionGroupResult optionGroup =
                new SellerOptionGroupResult(
                        1L, "색상", 10L, "PREDEFINED", 1, List.of(optionValue1, optionValue2));

        List<ResolvedProductOptionResult> productOptions =
                List.of(new ResolvedProductOptionResult(1L, "색상", 1L, "블랙"));

        ProductDetailResult product =
                new ProductDetailResult(
                        1L,
                        "SKU-001",
                        30000,
                        25000,
                        5000,
                        16,
                        100,
                        "ACTIVE",
                        1,
                        productOptions,
                        Instant.parse("2025-02-10T01:30:00Z"),
                        Instant.parse("2025-02-10T01:30:00Z"));

        return new ProductOptionMatrixResult(List.of(optionGroup), List.of(product));
    }

    private static ShippingPolicyResult createShippingPolicyResult() {
        return new ShippingPolicyResult(
                1L,
                DEFAULT_SELLER_ID,
                "기본 배송 정책",
                true,
                true,
                "PAID",
                "고정 배송비",
                3000L,
                30000L,
                5000L,
                3000L,
                5000L,
                5000L,
                2,
                3,
                LocalTime.of(15, 0),
                Instant.parse("2025-02-10T01:30:00Z"),
                Instant.parse("2025-02-10T01:30:00Z"));
    }

    private static RefundPolicyResult createRefundPolicyResult() {
        List<NonReturnableConditionResult> conditions =
                List.of(
                        new NonReturnableConditionResult("OPENED_PACKAGING", "포장 개봉"),
                        new NonReturnableConditionResult("USED_PRODUCT", "사용 흔적"));

        return new RefundPolicyResult(
                1L,
                DEFAULT_SELLER_ID,
                "기본 환불 정책",
                true,
                true,
                7,
                7,
                conditions,
                true,
                true,
                3,
                "추가 정보 없음",
                Instant.parse("2025-02-10T01:30:00Z"),
                Instant.parse("2025-02-10T01:30:00Z"));
    }

    private static ProductGroupDescriptionResult createProductGroupDescriptionResult() {
        List<DescriptionImageResult> images =
                List.of(
                        new DescriptionImageResult(
                                1L,
                                "https://origin.example.com/desc1.jpg",
                                "https://cdn.example.com/desc1.jpg",
                                1));

        return new ProductGroupDescriptionResult(
                1L, "<p>상품 상세 설명</p>", "https://cdn.example.com/description/", images);
    }

    private static ProductNoticeResult createProductNoticeResult() {
        List<ProductNoticeEntryResult> entries =
                List.of(
                        new ProductNoticeEntryResult(1L, 1L, "제조사"),
                        new ProductNoticeEntryResult(2L, 2L, "한국"));

        return new ProductNoticeResult(
                1L,
                1L,
                entries,
                Instant.parse("2025-02-10T01:30:00Z"),
                Instant.parse("2025-02-10T01:30:00Z"));
    }

    private static ProductOptionMatrixApiResponse createProductOptionMatrixApiResponse() {
        SellerOptionValueApiResponse optionValue1 =
                new SellerOptionValueApiResponse(1L, 1L, "블랙", 100L, 1);
        SellerOptionValueApiResponse optionValue2 =
                new SellerOptionValueApiResponse(2L, 1L, "화이트", 101L, 2);

        SellerOptionGroupApiResponse optionGroup =
                new SellerOptionGroupApiResponse(
                        1L, "색상", 10L, "PREDEFINED", 1, List.of(optionValue1, optionValue2));

        List<ResolvedProductOptionApiResponse> productOptions =
                List.of(new ResolvedProductOptionApiResponse(1L, "색상", 1L, "블랙"));

        ProductDetailApiResponse product =
                new ProductDetailApiResponse(
                        1L,
                        "SKU-001",
                        30000,
                        25000,
                        16,
                        100,
                        "ACTIVE",
                        1,
                        productOptions,
                        "2025-02-10T10:30:00+09:00",
                        "2025-02-10T10:30:00+09:00");

        return new ProductOptionMatrixApiResponse(List.of(optionGroup), List.of(product));
    }

    private static ShippingPolicyApiResponse createShippingPolicyApiResponse() {
        return new ShippingPolicyApiResponse(
                1L,
                DEFAULT_SELLER_ID,
                "기본 배송 정책",
                true,
                true,
                "PAID",
                "고정 배송비",
                3000L,
                30000L,
                5000L,
                3000L,
                5000L,
                5000L,
                2,
                3,
                "15:00",
                "2025-02-10T10:30:00+09:00",
                "2025-02-10T10:30:00+09:00");
    }

    private static RefundPolicyApiResponse createRefundPolicyApiResponse() {
        List<NonReturnableConditionApiResponse> conditions =
                List.of(
                        new NonReturnableConditionApiResponse("OPENED_PACKAGING", "포장 개봉"),
                        new NonReturnableConditionApiResponse("USED_PRODUCT", "사용 흔적"));

        return new RefundPolicyApiResponse(
                1L,
                DEFAULT_SELLER_ID,
                "기본 환불 정책",
                true,
                true,
                7,
                7,
                conditions,
                true,
                true,
                3,
                "추가 정보 없음",
                "2025-02-10T10:30:00+09:00",
                "2025-02-10T10:30:00+09:00");
    }

    private static ProductGroupDescriptionApiResponse createProductGroupDescriptionApiResponse() {
        List<DescriptionImageApiResponse> images =
                List.of(
                        new DescriptionImageApiResponse(
                                1L,
                                "https://origin.example.com/desc1.jpg",
                                "https://cdn.example.com/desc1.jpg",
                                1));

        return new ProductGroupDescriptionApiResponse(
                1L, "<p>상품 상세 설명</p>", "https://cdn.example.com/description/", images);
    }

    private static ProductNoticeApiResponse createProductNoticeApiResponse() {
        List<ProductNoticeEntryApiResponse> entries =
                List.of(
                        new ProductNoticeEntryApiResponse(1L, 1L, "제조사"),
                        new ProductNoticeEntryApiResponse(2L, 2L, "한국"));

        return new ProductNoticeApiResponse(
                1L, 1L, entries, "2025-02-10T10:30:00+09:00", "2025-02-10T10:30:00+09:00");
    }
}
