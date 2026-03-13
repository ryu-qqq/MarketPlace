package com.ryuqq.marketplace.adapter.in.rest.outboundproduct;

import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsPartnersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsProductsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsShopsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchSyncHistoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsPartnerApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.BrandResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.OptionResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.PriceResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.ProductGroupResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.ProductResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.ProductStatusResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse.SyncSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsShopApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.SyncHistoryApiResponse;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductDetailResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductListResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductPageResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryPageResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncSummaryResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductDetailResult;
import com.ryuqq.marketplace.application.product.dto.response.ResolvedProductOptionResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductOptionMatrixResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerResult;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.dto.response.ShopResult;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * OMS API 테스트 Fixtures.
 *
 * <p>OmsProduct / OmsPartner / OmsShop REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class OmsApiFixtures {

    private OmsApiFixtures() {}

    // ===== 공통 상수 =====
    public static final Instant DEFAULT_INSTANT = Instant.parse("2026-01-15T01:30:00Z");
    public static final String DEFAULT_FORMATTED_TIME = "2026-01-15T10:30:00+09:00";

    // ===== SearchOmsProductsApiRequest =====

    public static SearchOmsProductsApiRequest searchProductsRequest() {
        return new SearchOmsProductsApiRequest(
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
                "CREATED_AT",
                "DESC",
                0,
                10);
    }

    public static SearchOmsProductsApiRequest searchProductsRequest(
            String dateType,
            String startDate,
            String endDate,
            List<String> statuses,
            List<String> syncStatuses,
            String searchField,
            String searchWord,
            List<Long> shopIds,
            List<Long> partnerIds,
            List<String> productCodes,
            int page,
            int size) {
        return new SearchOmsProductsApiRequest(
                dateType,
                startDate,
                endDate,
                statuses,
                syncStatuses,
                searchField,
                searchWord,
                shopIds,
                partnerIds,
                productCodes,
                "CREATED_AT",
                "DESC",
                page,
                size);
    }

    // ===== SearchSyncHistoryApiRequest =====

    public static SearchSyncHistoryApiRequest searchSyncHistoryRequest() {
        return new SearchSyncHistoryApiRequest(null, null, 0, 10);
    }

    public static SearchSyncHistoryApiRequest searchSyncHistoryRequest(
            String status, int page, int size) {
        return new SearchSyncHistoryApiRequest(null, status, page, size);
    }

    public static SearchSyncHistoryApiRequest searchSyncHistoryRequest(
            Long shopId, String status, int page, int size) {
        return new SearchSyncHistoryApiRequest(shopId, status, page, size);
    }

    // ===== SearchOmsPartnersApiRequest =====

    public static SearchOmsPartnersApiRequest searchPartnersRequest() {
        return new SearchOmsPartnersApiRequest(null, "CREATED_AT", "ASC", 0, 100);
    }

    public static SearchOmsPartnersApiRequest searchPartnersRequest(
            String keyword, int page, int size) {
        return new SearchOmsPartnersApiRequest(keyword, "CREATED_AT", "ASC", page, size);
    }

    // ===== SearchOmsShopsApiRequest =====

    public static SearchOmsShopsApiRequest searchShopsRequest() {
        return new SearchOmsShopsApiRequest(null, "CREATED_AT", "ASC", 0, 100);
    }

    public static SearchOmsShopsApiRequest searchShopsRequest(String keyword, int page, int size) {
        return new SearchOmsShopsApiRequest(keyword, "CREATED_AT", "ASC", page, size);
    }

    // ===== OmsProductListResult (Application) =====

    public static OmsProductListResult productListResult(long id) {
        return new OmsProductListResult(
                id,
                "PG-" + id,
                "테스트상품_" + id,
                "https://example.com/image/" + id + ".jpg",
                89000,
                150,
                "ACTIVE",
                "판매중",
                "나이키코리아",
                DEFAULT_INSTANT,
                "SUCCESS",
                "연동완료",
                DEFAULT_INSTANT,
                1L,
                "스마트스토어");
    }

    public static List<OmsProductListResult> productListResults(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> productListResult((long) i)).toList();
    }

    public static OmsProductPageResult productPageResult(int count, int page, int size) {
        List<OmsProductListResult> results = productListResults(count);
        return OmsProductPageResult.of(results, page, size, count);
    }

    public static OmsProductPageResult emptyProductPageResult() {
        return OmsProductPageResult.empty(10);
    }

    // ===== OmsProductDetailResult (Application) =====

    public static OmsProductDetailResult productDetailResult(long productGroupId) {
        ProductGroupDetailCompositeResult productGroup =
                productGroupDetailCompositeResult(productGroupId);
        SyncSummaryResult syncSummary = syncSummaryResult();
        return new OmsProductDetailResult(productGroup, syncSummary);
    }

    public static ProductGroupDetailCompositeResult productGroupDetailCompositeResult(long id) {
        ProductDetailResult product1 = productDetailResultItem(1001L, 129000, "ACTIVE");
        ProductDetailResult product2 = productDetailResultItem(1002L, 139000, "ACTIVE");
        ProductOptionMatrixResult matrix =
                new ProductOptionMatrixResult(List.of(), List.of(product1, product2));

        return new ProductGroupDetailCompositeResult(
                id,
                1001L,
                "나이키코리아",
                501L,
                "Nike",
                200101001L,
                "패딩",
                "여성패션 > 아우터 > 패딩",
                "1/2/3",
                "나이키 에어포스 1 '07 화이트",
                "OPTION_ONE",
                "ACTIVE",
                DEFAULT_INSTANT,
                DEFAULT_INSTANT,
                List.of(productGroupImageResult()),
                matrix,
                null,
                null,
                null,
                null);
    }

    public static ProductDetailResult productDetailResultItem(
            long id, int currentPrice, String status) {
        ResolvedProductOptionResult option =
                new ResolvedProductOptionResult(1L, "SIZE", 101L, "250");
        return new ProductDetailResult(
                id,
                "SKU-" + id,
                159000,
                currentPrice,
                null,
                19,
                10,
                status,
                1,
                List.of(option),
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static com.ryuqq.marketplace.application.productgroupimage.dto.response
                    .ProductGroupImageResult
            productGroupImageResult() {
        return new com.ryuqq.marketplace.application.productgroupimage.dto.response
                .ProductGroupImageResult(
                1L,
                "https://example.com/origin.jpg",
                "https://example.com/uploaded.jpg",
                "MAIN",
                1,
                java.util.List.of());
    }

    public static SyncSummaryResult syncSummaryResult() {
        return new SyncSummaryResult(5L, 3L, 1L, 1L, DEFAULT_INSTANT);
    }

    // ===== SyncHistoryListResult (Application) =====

    public static SyncHistoryListResult syncHistoryListResult(long id) {
        return new SyncHistoryListResult(
                id,
                "스마트스토어",
                "trexi001",
                null,
                "COMPLETED",
                "완료",
                0,
                null,
                "NAVER-12345678",
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<SyncHistoryListResult> syncHistoryListResults(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> syncHistoryListResult((long) i))
                .toList();
    }

    public static SyncHistoryPageResult syncHistoryPageResult(int count, int page, int size) {
        List<SyncHistoryListResult> results = syncHistoryListResults(count);
        return SyncHistoryPageResult.of(results, page, size, count);
    }

    public static SyncHistoryPageResult emptySyncHistoryPageResult() {
        return SyncHistoryPageResult.empty(10);
    }

    // ===== SellerResult / SellerPageResult (Application) =====

    public static SellerResult sellerResult(long id) {
        return new SellerResult(
                id,
                "나이키코리아_" + id,
                "Nike Korea " + id,
                "https://example.com/logo.png",
                "나이키 공식 파트너",
                true,
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<SellerResult> sellerResults(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> sellerResult((long) i)).toList();
    }

    public static SellerPageResult sellerPageResult(int count, int page, int size) {
        List<SellerResult> results = sellerResults(count);
        return SellerPageResult.of(results, count, page, size);
    }

    public static SellerPageResult emptySellerPageResult() {
        return SellerPageResult.of(List.of(), 0, 0, 100);
    }

    // ===== ShopResult / ShopPageResult (Application) =====

    public static ShopResult shopResult(long id) {
        return new ShopResult(
                id,
                1L,
                "스마트스토어_" + id,
                "account_" + id,
                "ACTIVE",
                DEFAULT_INSTANT,
                DEFAULT_INSTANT);
    }

    public static List<ShopResult> shopResults(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> shopResult((long) i)).toList();
    }

    public static ShopPageResult shopPageResult(int count, int page, int size) {
        List<ShopResult> results = shopResults(count);
        return ShopPageResult.of(results, page, size, count);
    }

    public static ShopPageResult emptyShopPageResult() {
        return ShopPageResult.empty(100);
    }

    // ===== OmsProductApiResponse =====

    public static OmsProductApiResponse productApiResponse(long id) {
        return new OmsProductApiResponse(
                id,
                "PG-" + id,
                "테스트상품_" + id,
                "https://example.com/image/" + id + ".jpg",
                89000,
                150,
                "ACTIVE",
                "판매중",
                "나이키코리아",
                DEFAULT_FORMATTED_TIME,
                "SUCCESS",
                "연동완료",
                DEFAULT_FORMATTED_TIME,
                1L,
                "스마트스토어");
    }

    public static List<OmsProductApiResponse> productApiResponses(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> productApiResponse((long) i)).toList();
    }

    // ===== OmsProductDetailApiResponse =====

    public static OmsProductDetailApiResponse productDetailApiResponse(long productGroupId) {
        ProductGroupResponse productGroup =
                new ProductGroupResponse(
                        productGroupId,
                        "나이키 에어포스 1 '07 화이트",
                        1001L,
                        "나이키코리아",
                        200101001L,
                        "OPTION_ONE",
                        null,
                        new BrandResponse(501L, "Nike", null),
                        new PriceResponse(159000, 129000, 129000, 0, 0, 19),
                        "https://example.com/uploaded.jpg",
                        "여성패션 > 아우터 > 패딩",
                        new ProductStatusResponse("N", "Y"),
                        DEFAULT_FORMATTED_TIME,
                        DEFAULT_FORMATTED_TIME,
                        null,
                        null);

        List<ProductResponse> products =
                List.of(
                        new ProductResponse(
                                1001L,
                                10,
                                new ProductStatusResponse("N", "Y"),
                                "250",
                                List.of(new OptionResponse(1L, 101L, "SIZE", "250")),
                                0));

        SyncSummaryApiResponse syncSummary =
                new SyncSummaryApiResponse(5L, 3L, 1L, 1L, DEFAULT_FORMATTED_TIME);

        return new OmsProductDetailApiResponse(productGroup, products, syncSummary);
    }

    // ===== SyncHistoryApiResponse =====

    public static SyncHistoryApiResponse syncHistoryApiResponse(long id) {
        return new SyncHistoryApiResponse(
                id,
                "SYNC-20260115-" + String.format("%03d", id),
                "스마트스토어",
                "trexi001",
                null,
                "COMPLETED",
                "완료",
                DEFAULT_FORMATTED_TIME,
                DEFAULT_FORMATTED_TIME,
                "NAVER-12345678",
                null,
                0);
    }

    public static List<SyncHistoryApiResponse> syncHistoryApiResponses(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> syncHistoryApiResponse((long) i))
                .toList();
    }

    // ===== OmsPartnerApiResponse =====

    public static OmsPartnerApiResponse partnerApiResponse(long id) {
        return new OmsPartnerApiResponse(id, "Nike Korea " + id, "나이키코리아_" + id, "ACTIVE");
    }

    public static List<OmsPartnerApiResponse> partnerApiResponses(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> partnerApiResponse((long) i)).toList();
    }

    // ===== OmsShopApiResponse =====

    public static OmsShopApiResponse shopApiResponse(long id) {
        return new OmsShopApiResponse(id, "스마트스토어_" + id, 1L, "account_" + id, "ACTIVE");
    }

    public static List<OmsShopApiResponse> shopApiResponses(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> shopApiResponse((long) i)).toList();
    }
}
