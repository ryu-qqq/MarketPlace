package com.ryuqq.marketplace.adapter.out.client.naver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceProductMapper;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.common.vo.Money;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeCategoryName;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldCode;
import com.ryuqq.marketplace.domain.notice.vo.NoticeFieldName;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.id.ProductOptionMappingId;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupDescriptionId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.*;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.id.ProductGroupImageId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeEntryId;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeId;
import com.ryuqq.marketplace.domain.productnotice.vo.NoticeFieldValue;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.id.SellerCsId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.seller.vo.CsContact;
import com.ryuqq.marketplace.domain.seller.vo.OperatingHours;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** 77288 상품(13198454659) 기준 품절 처리 로컬 테스트. */
@Tag("external-integration")
@DisplayName("네이버 품절 처리 로컬 테스트")
class NaverSoldOutTest {

    private static final long ORIGIN_PRODUCT_NO = 13198454659L;
    private static final long NAVER_CATEGORY_ID = 50004190L;
    private static final String IMAGE_URL =
            "https://cdn.set-of.com/product/2024-02-18/a6d97c0c-b15e-4b8b-9dac-66140b8a3342.jpg";

    private final NaverCommerceProductMapper mapper = new NaverCommerceProductMapper();
    private final ObjectMapper om = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("품절 처리: 매퍼로 SOLD_OUT 요청 생성 → 네이버 수정 → OUTOFSTOCK 확인")
    void soldOutTest() throws Exception {
        HttpClient http = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(http, om);

        // 1. 현재 상태 확인
        System.out.println("===== 1. 현재 상태 =====");
        NaverProductDetailResponse existing = fetchDetail(http, token, ORIGIN_PRODUCT_NO);
        System.out.println("statusType: " + existing.originProduct().statusType());
        System.out.println("stockQuantity: " + existing.originProduct().stockQuantity());

        // 2. SOLD_OUT 상태의 번들 구성 + 매퍼로 요청 생성
        System.out.println("\n===== 2. SOLD_OUT 요청 생성 =====");
        ProductGroupDetailBundle soldOutBundle = buildBundle(ProductGroupStatus.SOLD_OUT);
        NaverProductRegistrationRequest soldOutReq =
                mapper.toUpdateRequest(
                        ProductGroupSyncData.from(soldOutBundle),
                        NAVER_CATEGORY_ID,
                        null,
                        existing,
                        Set.of(ChangedArea.STATUS));

        String json = om.writeValueAsString(soldOutReq);
        System.out.println("요청 JSON (옵션 부분):");
        var optNode =
                om.readTree(json).path("originProduct").path("detailAttribute").path("optionInfo");
        System.out.println(om.writeValueAsString(optNode));
        System.out.println(
                "stockQuantity: "
                        + om.readTree(json).path("originProduct").path("stockQuantity").asInt());
        System.out.println(
                "statusType: "
                        + om.readTree(json).path("originProduct").path("statusType").asText());

        // 3. 네이버 수정 호출
        System.out.println("\n===== 3. 네이버 수정 =====");
        HttpResponse<String> resp =
                putJson(
                        http,
                        token,
                        NaverAuthHelper.BASE_URL
                                + "/v2/products/origin-products/"
                                + ORIGIN_PRODUCT_NO,
                        json);
        System.out.println("수정 status: " + resp.statusCode());
        if (resp.statusCode() != 200) {
            System.out.println("ERROR: " + resp.body());
            return;
        }
        Thread.sleep(1000);

        // 4. 확인
        System.out.println("\n===== 4. OUTOFSTOCK 확인 =====");
        NaverProductDetailResponse after = fetchDetail(http, token, ORIGIN_PRODUCT_NO);
        System.out.println("statusType: " + after.originProduct().statusType());
        System.out.println("stockQuantity: " + after.originProduct().stockQuantity());

        // 5. 복구 (ACTIVE)
        System.out.println("\n===== 5. ACTIVE 복구 =====");
        ProductGroupDetailBundle activeBundle = buildBundle(ProductGroupStatus.ACTIVE);
        NaverProductRegistrationRequest activeReq =
                mapper.toUpdateRequest(
                        ProductGroupSyncData.from(activeBundle), NAVER_CATEGORY_ID, null, after, Set.of(ChangedArea.STATUS));
        HttpResponse<String> restoreResp =
                putJson(
                        http,
                        token,
                        NaverAuthHelper.BASE_URL
                                + "/v2/products/origin-products/"
                                + ORIGIN_PRODUCT_NO,
                        om.writeValueAsString(activeReq));
        System.out.println("복구 status: " + restoreResp.statusCode());
        if (restoreResp.statusCode() != 200) {
            System.out.println("ERROR: " + restoreResp.body());
        }
        Thread.sleep(1000);

        NaverProductDetailResponse restored = fetchDetail(http, token, ORIGIN_PRODUCT_NO);
        System.out.println("복구 후 statusType: " + restored.originProduct().statusType());
        System.out.println("복구 후 stockQuantity: " + restored.originProduct().stockQuantity());
    }

    private ProductGroupDetailBundle buildBundle(ProductGroupStatus status) {
        Instant now = Instant.now();

        SellerOptionGroup sizeGroup =
                SellerOptionGroup.reconstitute(
                        SellerOptionGroupId.of(1L),
                        ProductGroupId.of(77288L),
                        OptionGroupName.of("사이즈"),
                        CanonicalOptionGroupId.of(1L),
                        OptionInputType.FREE_INPUT,
                        0,
                        List.of(
                                SellerOptionValue.reconstitute(
                                        SellerOptionValueId.of(1L),
                                        SellerOptionGroupId.of(1L),
                                        OptionValueName.of("10"),
                                        CanonicalOptionValueId.of(1L),
                                        0,
                                        DeletionStatus.active()),
                                SellerOptionValue.reconstitute(
                                        SellerOptionValueId.of(2L),
                                        SellerOptionGroupId.of(1L),
                                        OptionValueName.of("12"),
                                        CanonicalOptionValueId.of(2L),
                                        1,
                                        DeletionStatus.active()),
                                SellerOptionValue.reconstitute(
                                        SellerOptionValueId.of(3L),
                                        SellerOptionGroupId.of(1L),
                                        OptionValueName.of("13"),
                                        CanonicalOptionValueId.of(3L),
                                        2,
                                        DeletionStatus.active()),
                                SellerOptionValue.reconstitute(
                                        SellerOptionValueId.of(4L),
                                        SellerOptionGroupId.of(1L),
                                        OptionValueName.of("14"),
                                        CanonicalOptionValueId.of(4L),
                                        3,
                                        DeletionStatus.active())),
                        DeletionStatus.active());

        List<Product> products =
                List.of(
                        buildProduct(100L, "DSN-RING-10", 79000, 10, 0, List.of(1L)),
                        buildProduct(101L, "DSN-RING-12", 79000, 8, 1, List.of(2L)),
                        buildProduct(102L, "DSN-RING-13", 79000, 5, 2, List.of(3L)),
                        buildProduct(103L, "DSN-RING-14", 79000, 3, 3, List.of(4L)));

        ProductGroup group =
                ProductGroup.reconstitute(
                        ProductGroupId.of(77288L),
                        SellerId.of(25L),
                        BrandId.of(421L),
                        CategoryId.of(2382L),
                        ShippingPolicyId.of(36L),
                        RefundPolicyId.of(35L),
                        ProductGroupName.of("[네이버 테스트 상품 준비] 디즈니 엘사 겨울왕국 부츠 2"),
                        OptionType.SINGLE,
                        status,
                        List.of(
                                ProductGroupImage.reconstitute(
                                        ProductGroupImageId.of(1L),
                                        ProductGroupId.of(77288L),
                                        ImageUrl.of(IMAGE_URL),
                                        ImageUrl.of(IMAGE_URL),
                                        ImageType.THUMBNAIL,
                                        0,
                                        DeletionStatus.active())),
                        List.of(sizeGroup),
                        now,
                        now);

        NoticeCategory nc =
                NoticeCategory.reconstitute(
                        NoticeCategoryId.of(2L),
                        NoticeCategoryCode.of("SHOES"),
                        NoticeCategoryName.of("SHOES", "신발"),
                        CategoryGroup.CLOTHING,
                        true,
                        List.of(
                                NoticeField.reconstitute(
                                        NoticeFieldId.of(9L),
                                        NoticeFieldCode.of("material"),
                                        NoticeFieldName.of("material"),
                                        true,
                                        1),
                                NoticeField.reconstitute(
                                        NoticeFieldId.of(10L),
                                        NoticeFieldCode.of("sole"),
                                        NoticeFieldName.of("sole"),
                                        true,
                                        2),
                                NoticeField.reconstitute(
                                        NoticeFieldId.of(11L),
                                        NoticeFieldCode.of("color"),
                                        NoticeFieldName.of("color"),
                                        true,
                                        3),
                                NoticeField.reconstitute(
                                        NoticeFieldId.of(12L),
                                        NoticeFieldCode.of("size"),
                                        NoticeFieldName.of("size"),
                                        true,
                                        4),
                                NoticeField.reconstitute(
                                        NoticeFieldId.of(13L),
                                        NoticeFieldCode.of("manufacturer"),
                                        NoticeFieldName.of("manufacturer"),
                                        true,
                                        5),
                                NoticeField.reconstitute(
                                        NoticeFieldId.of(14L),
                                        NoticeFieldCode.of("origin"),
                                        NoticeFieldName.of("origin"),
                                        true,
                                        6),
                                NoticeField.reconstitute(
                                        NoticeFieldId.of(15L),
                                        NoticeFieldCode.of("caution"),
                                        NoticeFieldName.of("caution"),
                                        true,
                                        7)),
                        now,
                        now);

        ProductNotice notice =
                ProductNotice.reconstitute(
                        ProductNoticeId.of(1L),
                        ProductGroupId.of(77288L),
                        NoticeCategoryId.of(2L),
                        List.of(
                                ProductNoticeEntry.reconstitute(
                                        ProductNoticeEntryId.of(1L),
                                        ProductNoticeId.of(1L),
                                        NoticeFieldId.of(9L),
                                        NoticeFieldValue.of("합성가죽")),
                                ProductNoticeEntry.reconstitute(
                                        ProductNoticeEntryId.of(2L),
                                        ProductNoticeId.of(1L),
                                        NoticeFieldId.of(10L),
                                        NoticeFieldValue.of("TPR")),
                                ProductNoticeEntry.reconstitute(
                                        ProductNoticeEntryId.of(3L),
                                        ProductNoticeId.of(1L),
                                        NoticeFieldId.of(11L),
                                        NoticeFieldValue.of("블루")),
                                ProductNoticeEntry.reconstitute(
                                        ProductNoticeEntryId.of(4L),
                                        ProductNoticeId.of(1L),
                                        NoticeFieldId.of(12L),
                                        NoticeFieldValue.of("10/12/13/14호")),
                                ProductNoticeEntry.reconstitute(
                                        ProductNoticeEntryId.of(5L),
                                        ProductNoticeId.of(1L),
                                        NoticeFieldId.of(13L),
                                        NoticeFieldValue.of("(주)디즈니코리아")),
                                ProductNoticeEntry.reconstitute(
                                        ProductNoticeEntryId.of(6L),
                                        ProductNoticeId.of(1L),
                                        NoticeFieldId.of(14L),
                                        NoticeFieldValue.of("베트남")),
                                ProductNoticeEntry.reconstitute(
                                        ProductNoticeEntryId.of(7L),
                                        ProductNoticeId.of(1L),
                                        NoticeFieldId.of(15L),
                                        NoticeFieldValue.of("관련 법에 따름"))),
                        now,
                        now);

        SellerCs sellerCs =
                SellerCs.reconstitute(
                        SellerCsId.of(1L),
                        SellerId.of(25L),
                        CsContact.of("01051304844", null, "test@test.com"),
                        OperatingHours.of(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                        "MON,TUE,WED,THU,FRI",
                        null,
                        now,
                        now);

        ProductGroupDescription desc =
                ProductGroupDescription.reconstitute(
                        ProductGroupDescriptionId.of(1L),
                        ProductGroupId.of(77288L),
                        DescriptionHtml.of("<p>품절 테스트</p>"),
                        CdnPath.of("https://cdn.test.com/test.html"),
                        DescriptionPublishStatus.PUBLISHED,
                        List.of(),
                        now,
                        now);

        ShippingPolicyResult shipping =
                new ShippingPolicyResult(
                        36L,
                        25L,
                        "기본",
                        true,
                        true,
                        "FREE",
                        "무료배송",
                        0L,
                        null,
                        3000L,
                        5000L,
                        3000L,
                        6000L,
                        1,
                        3,
                        LocalTime.of(14, 0),
                        now,
                        now);

        ProductGroupDetailCompositeQueryResult qr =
                new ProductGroupDetailCompositeQueryResult(
                        77288L,
                        25L,
                        "테스트 셀러",
                        421L,
                        "디즈니",
                        2382L,
                        "테스트",
                        "테스트",
                        "2382",
                        "[네이버 테스트 상품 준비] 디즈니 엘사 겨울왕국 부츠 2",
                        "SINGLE",
                        status.name(),
                        now,
                        now,
                        shipping,
                        null);

        return new ProductGroupDetailBundle(
                qr,
                group,
                products,
                Optional.of(desc),
                Optional.of(notice),
                Optional.of(nc),
                Optional.of(sellerCs),
                Map.of());
    }

    private Product buildProduct(
            long id, String sku, int price, int stock, int sort, List<Long> optionValueIds) {
        List<ProductOptionMapping> mappings = new ArrayList<>();
        for (int i = 0; i < optionValueIds.size(); i++) {
            mappings.add(
                    ProductOptionMapping.reconstitute(
                            ProductOptionMappingId.of(id * 10 + i), ProductId.of(id),
                            SellerOptionValueId.of(optionValueIds.get(i)),
                                    DeletionStatus.active()));
        }
        Instant now = Instant.now();
        return Product.reconstitute(
                ProductId.of(id),
                ProductGroupId.of(77288L),
                SkuCode.of(sku),
                Money.of(price + 10000),
                Money.of(price),
                Money.of(price),
                20,
                stock,
                ProductStatus.ACTIVE,
                sort,
                mappings,
                now,
                now);
    }

    private NaverProductDetailResponse fetchDetail(HttpClient http, String token, long no)
            throws Exception {
        HttpResponse<String> resp =
                http.send(
                        HttpRequest.newBuilder()
                                .uri(
                                        URI.create(
                                                NaverAuthHelper.BASE_URL
                                                        + "/v2/products/origin-products/"
                                                        + no))
                                .header("Authorization", "Bearer " + token)
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.ofString());
        return om.readValue(resp.body(), NaverProductDetailResponse.class);
    }

    private HttpResponse<String> putJson(HttpClient http, String token, String url, String json)
            throws Exception {
        return http.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
    }
}
