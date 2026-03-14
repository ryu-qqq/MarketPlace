package com.ryuqq.marketplace.adapter.out.client.naver;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceProductMapper;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
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
import com.ryuqq.marketplace.domain.productgroup.vo.CdnPath;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionInputType;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionValueName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
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

/** 옵션 상품 등록 → 옵션/재고/가격 수정 → 옵션 추가 → 옵션 삭제 → 삭제 통합 테스트. */
@Tag("external-integration")
@DisplayName("옵션 상품 수정 통합 테스트")
class NaverOptionUpdateTest {

    private static final long NAVER_CATEGORY_ID = 50000973L;
    private static final String IMAGE_URL =
            "https://shop-phinf.pstatic.net/20260313_294/17733696875206wuci_JPEG/288351393492979_1382318577.jpg";
    private static final long PG_ID = 99100L;

    private final NaverCommerceProductMapper mapper = new NaverCommerceProductMapper();
    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("옵션 상품: 등록 → 재고/가격 수정 → 옵션 추가 → 옵션 삭제 → 삭제")
    void optionProductFullLifecycle() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, new ObjectMapper());
        System.out.println("[OK] 토큰 발급 성공\n");

        // ===== 1. 옵션 상품 등록 (색상: 블랙/화이트, 사이즈: M/L → 4개 SKU) =====
        System.out.println("========== 1. 옵션 상품 등록 (4개 SKU) ==========\n");

        ProductGroupDetailBundle registerBundle =
                buildOptionBundle(
                        "테스트 옵션 상품",
                        List.of("블랙", "화이트"),
                        List.of("M", "L"),
                        List.of(
                                new SkuStock("SKU-BK-M", 15000, 10),
                                new SkuStock("SKU-BK-L", 15000, 8),
                                new SkuStock("SKU-WH-M", 16000, 12),
                                new SkuStock("SKU-WH-L", 16000, 6)));

        NaverProductRegistrationRequest registerReq =
                mapper.toRegistrationRequest(registerBundle, NAVER_CATEGORY_ID, null);

        String registerJson = objectMapper.writeValueAsString(registerReq);
        HttpResponse<String> registerResp =
                postJson(
                        httpClient, token, NaverAuthHelper.BASE_URL + "/v2/products", registerJson);

        System.out.println("  Status: " + registerResp.statusCode());
        if (registerResp.statusCode() != 200) {
            System.out.println("  [ERROR] " + registerResp.body());
        }
        assertThat(registerResp.statusCode()).as("등록 성공").isEqualTo(200);

        JsonNode registerBody = objectMapper.readTree(registerResp.body());
        long originProductNo = registerBody.path("originProductNo").asLong();
        System.out.println("  [PASS] originProductNo: " + originProductNo);

        // 옵션 조합 ID 확인
        Thread.sleep(1000);

        System.out.println("\n========== 1-1. 등록된 옵션 조회 ==========\n");
        NaverProductDetailResponse existingProduct =
                fetchProduct(httpClient, token, originProductNo);
        assertThat(existingProduct).isNotNull();
        assertThat(existingProduct.originProduct().detailAttribute().optionInfo()).isNotNull();

        var existingOptions =
                existingProduct.originProduct().detailAttribute().optionInfo().optionCombinations();
        System.out.println("  등록된 옵션 조합 수: " + existingOptions.size());
        for (var opt : existingOptions) {
            System.out.printf(
                    "  - id=%d, %s/%s, SKU=%s, 재고=%d, 가격=%d%n",
                    opt.id(),
                    opt.optionName1(),
                    opt.optionName2(),
                    opt.sellerManagerCode(),
                    opt.stockQuantity(),
                    opt.price());
        }
        assertThat(existingOptions).hasSize(4);

        // ===== 2. 재고/가격 수정 (기존 옵션 구조 유지) =====
        System.out.println("\n========== 2. 재고/가격 수정 ==========\n");
        Thread.sleep(500);

        ProductGroupDetailBundle updateBundle =
                buildOptionBundle(
                        "테스트 옵션 상품",
                        List.of("블랙", "화이트"),
                        List.of("M", "L"),
                        List.of(
                                new SkuStock("SKU-BK-M", 18000, 20), // 가격 15000→18000, 재고 10→20
                                new SkuStock("SKU-BK-L", 18000, 15), // 가격 15000→18000, 재고 8→15
                                new SkuStock("SKU-WH-M", 19000, 25), // 가격 16000→19000, 재고 12→25
                                new SkuStock("SKU-WH-L", 19000, 0))); // 가격 16000→19000, 재고 6→0 (품절)

        NaverProductRegistrationRequest updateReq =
                mapper.toUpdateRequest(
                        updateBundle,
                        NAVER_CATEGORY_ID,
                        null,
                        existingProduct,
                        Set.of(ChangedArea.PRICE, ChangedArea.STOCK));

        String updateJson = objectMapper.writeValueAsString(updateReq);
        HttpResponse<String> updateResp =
                putJson(
                        httpClient,
                        token,
                        NaverAuthHelper.BASE_URL
                                + "/v2/products/origin-products/"
                                + originProductNo,
                        updateJson);

        System.out.println("  Status: " + updateResp.statusCode());
        if (updateResp.statusCode() == 200) {
            System.out.println("  [PASS] 재고/가격 수정 성공");
        } else {
            System.out.println("  [FAIL] " + updateResp.body());
        }
        assertThat(updateResp.statusCode()).as("재고/가격 수정 성공").isEqualTo(200);

        // 수정 결과 확인
        Thread.sleep(500);
        NaverProductDetailResponse afterPriceUpdate =
                fetchProduct(httpClient, token, originProductNo);
        var updatedOptions =
                afterPriceUpdate
                        .originProduct()
                        .detailAttribute()
                        .optionInfo()
                        .optionCombinations();
        System.out.println("\n  수정 후 옵션:");
        for (var opt : updatedOptions) {
            System.out.printf(
                    "  - id=%d, %s/%s, SKU=%s, 재고=%d, 가격=%d%n",
                    opt.id(),
                    opt.optionName1(),
                    opt.optionName2(),
                    opt.sellerManagerCode(),
                    opt.stockQuantity(),
                    opt.price());
        }

        // 기존 combination ID가 유지되었는지 확인
        assertThat(updatedOptions).hasSize(4);
        for (int i = 0; i < existingOptions.size(); i++) {
            assertThat(updatedOptions.get(i).id())
                    .as("combination ID 유지: " + existingOptions.get(i).sellerManagerCode())
                    .isEqualTo(existingOptions.get(i).id());
        }
        System.out.println("  [PASS] 기존 combination ID 모두 유지됨");

        // ===== 3. 옵션 추가 (XL 사이즈 추가 → 6개 SKU) =====
        System.out.println("\n========== 3. 옵션 추가 (XL 사이즈) ==========\n");
        Thread.sleep(500);

        ProductGroupDetailBundle addOptionBundle =
                buildOptionBundle(
                        "테스트 옵션 상품",
                        List.of("블랙", "화이트"),
                        List.of("M", "L", "XL"),
                        List.of(
                                new SkuStock("SKU-BK-M", 18000, 20),
                                new SkuStock("SKU-BK-L", 18000, 15),
                                new SkuStock("SKU-BK-XL", 18000, 5), // 신규
                                new SkuStock("SKU-WH-M", 19000, 25),
                                new SkuStock("SKU-WH-L", 19000, 0),
                                new SkuStock("SKU-WH-XL", 19000, 7))); // 신규

        NaverProductRegistrationRequest addOptionReq =
                mapper.toUpdateRequest(
                        addOptionBundle,
                        NAVER_CATEGORY_ID,
                        null,
                        afterPriceUpdate,
                        Set.of(ChangedArea.OPTION));

        // 신규 옵션의 combination id가 null인지 확인
        var requestOptions =
                addOptionReq.originProduct().detailAttribute().optionInfo().optionCombinations();
        long newOptionCount = requestOptions.stream().filter(o -> o.id() == null).count();
        System.out.println("  기존 옵션: " + (requestOptions.size() - newOptionCount) + "개");
        System.out.println("  신규 옵션: " + newOptionCount + "개");

        String addOptionJson = objectMapper.writeValueAsString(addOptionReq);
        HttpResponse<String> addOptionResp =
                putJson(
                        httpClient,
                        token,
                        NaverAuthHelper.BASE_URL
                                + "/v2/products/origin-products/"
                                + originProductNo,
                        addOptionJson);

        System.out.println("  Status: " + addOptionResp.statusCode());
        if (addOptionResp.statusCode() == 200) {
            System.out.println("  [PASS] 옵션 추가 성공");
        } else {
            System.out.println("  [FAIL] " + addOptionResp.body());
        }
        assertThat(addOptionResp.statusCode()).as("옵션 추가 성공").isEqualTo(200);

        // 추가 결과 확인
        Thread.sleep(500);
        NaverProductDetailResponse afterAddOption =
                fetchProduct(httpClient, token, originProductNo);
        var addedOptions =
                afterAddOption.originProduct().detailAttribute().optionInfo().optionCombinations();
        System.out.println("\n  추가 후 옵션:");
        for (var opt : addedOptions) {
            System.out.printf(
                    "  - id=%d, %s/%s, SKU=%s, 재고=%d, 가격=%d%n",
                    opt.id(),
                    opt.optionName1(),
                    opt.optionName2(),
                    opt.sellerManagerCode(),
                    opt.stockQuantity(),
                    opt.price());
        }
        assertThat(addedOptions).hasSize(6);
        System.out.println("  [PASS] 옵션 6개 확인");

        // ===== 4. 옵션 삭제 (화이트 전체 삭제 → 3개 SKU) =====
        System.out.println("\n========== 4. 옵션 삭제 (화이트 제거) ==========\n");
        Thread.sleep(500);

        ProductGroupDetailBundle removeOptionBundle =
                buildOptionBundle(
                        "테스트 옵션 상품",
                        List.of("블랙"),
                        List.of("M", "L", "XL"),
                        List.of(
                                new SkuStock("SKU-BK-M", 18000, 20),
                                new SkuStock("SKU-BK-L", 18000, 15),
                                new SkuStock("SKU-BK-XL", 18000, 5)));

        NaverProductRegistrationRequest removeOptionReq =
                mapper.toUpdateRequest(
                        removeOptionBundle,
                        NAVER_CATEGORY_ID,
                        null,
                        afterAddOption,
                        Set.of(ChangedArea.OPTION));

        String removeOptionJson = objectMapper.writeValueAsString(removeOptionReq);
        HttpResponse<String> removeOptionResp =
                putJson(
                        httpClient,
                        token,
                        NaverAuthHelper.BASE_URL
                                + "/v2/products/origin-products/"
                                + originProductNo,
                        removeOptionJson);

        System.out.println("  Status: " + removeOptionResp.statusCode());
        if (removeOptionResp.statusCode() == 200) {
            System.out.println("  [PASS] 옵션 삭제 성공");
        } else {
            System.out.println("  [FAIL] " + removeOptionResp.body());
        }
        assertThat(removeOptionResp.statusCode()).as("옵션 삭제 성공").isEqualTo(200);

        // 삭제 결과 확인
        Thread.sleep(500);
        NaverProductDetailResponse afterRemoveOption =
                fetchProduct(httpClient, token, originProductNo);
        var remainingOptions =
                afterRemoveOption
                        .originProduct()
                        .detailAttribute()
                        .optionInfo()
                        .optionCombinations();
        System.out.println("\n  삭제 후 옵션:");
        for (var opt : remainingOptions) {
            System.out.printf(
                    "  - id=%d, %s/%s, SKU=%s, 재고=%d, 가격=%d%n",
                    opt.id(),
                    opt.optionName1(),
                    opt.optionName2(),
                    opt.sellerManagerCode(),
                    opt.stockQuantity(),
                    opt.price());
        }
        assertThat(remainingOptions).hasSize(3);
        System.out.println("  [PASS] 옵션 3개 확인 (화이트 삭제됨)");

        // ===== 5. 상품 삭제 =====
        System.out.println("\n========== 5. 상품 삭제 ==========\n");
        Thread.sleep(500);

        HttpRequest deleteReq =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        NaverAuthHelper.BASE_URL
                                                + "/v2/products/origin-products/"
                                                + originProductNo))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();
        HttpResponse<String> deleteResp =
                httpClient.send(deleteReq, HttpResponse.BodyHandlers.ofString());

        System.out.println("  Status: " + deleteResp.statusCode());
        assertThat(deleteResp.statusCode()).as("상품 삭제 성공").isEqualTo(200);
        System.out.println("  [PASS] 상품 삭제 완료");

        // ===== 최종 결과 =====
        System.out.println("\n========== 최종 결과 ==========");
        System.out.println("1. 등록 (4 SKU): PASS");
        System.out.println("2. 재고/가격 수정: PASS (combination ID 유지)");
        System.out.println("3. 옵션 추가 (4→6 SKU): PASS");
        System.out.println("4. 옵션 삭제 (6→3 SKU): PASS");
        System.out.println("5. 상품 삭제: PASS");
    }

    // ===== Helper =====

    private NaverProductDetailResponse fetchProduct(
            HttpClient client, String token, long originProductNo) throws Exception {
        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        NaverAuthHelper.BASE_URL
                                                + "/v2/products/origin-products/"
                                                + originProductNo))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            System.out.println("  [WARN] 상품 조회 실패: " + resp.statusCode() + " " + resp.body());
            return null;
        }
        return objectMapper.readValue(resp.body(), NaverProductDetailResponse.class);
    }

    private HttpResponse<String> postJson(HttpClient client, String token, String url, String json)
            throws Exception {
        return client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> putJson(HttpClient client, String token, String url, String json)
            throws Exception {
        return client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + token)
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private record SkuStock(String skuCode, int price, int stock) {}

    private ProductGroupDetailBundle buildOptionBundle(
            String productName,
            List<String> colorValues,
            List<String> sizeValues,
            List<SkuStock> skuStocks) {

        Instant now = Instant.now();

        // 옵션 그룹 + 값 구성
        long optValueIdSeq = 200L;
        List<SellerOptionValue> colorOptionValues = new ArrayList<>();
        for (int i = 0; i < colorValues.size(); i++) {
            colorOptionValues.add(
                    SellerOptionValue.reconstitute(
                            SellerOptionValueId.of(optValueIdSeq++),
                            SellerOptionGroupId.of(1L),
                            OptionValueName.of(colorValues.get(i)),
                            CanonicalOptionValueId.of((long) (i + 1)),
                            i,
                            DeletionStatus.active()));
        }

        List<SellerOptionValue> sizeOptionValues = new ArrayList<>();
        for (int i = 0; i < sizeValues.size(); i++) {
            sizeOptionValues.add(
                    SellerOptionValue.reconstitute(
                            SellerOptionValueId.of(optValueIdSeq++),
                            SellerOptionGroupId.of(2L),
                            OptionValueName.of(sizeValues.get(i)),
                            CanonicalOptionValueId.of((long) (100 + i + 1)),
                            i,
                            DeletionStatus.active()));
        }

        SellerOptionGroup colorGroup =
                SellerOptionGroup.reconstitute(
                        SellerOptionGroupId.of(1L),
                        ProductGroupId.of(PG_ID),
                        OptionGroupName.of("색상"),
                        CanonicalOptionGroupId.of(1L),
                        OptionInputType.PREDEFINED,
                        0,
                        colorOptionValues,
                        DeletionStatus.active());

        SellerOptionGroup sizeGroup =
                SellerOptionGroup.reconstitute(
                        SellerOptionGroupId.of(2L),
                        ProductGroupId.of(PG_ID),
                        OptionGroupName.of("사이즈"),
                        CanonicalOptionGroupId.of(2L),
                        OptionInputType.PREDEFINED,
                        1,
                        sizeOptionValues,
                        DeletionStatus.active());

        // 색상×사이즈 조합으로 Product 생성
        List<Product> products = new ArrayList<>();
        long productIdSeq = 300L;
        int skuIdx = 0;

        for (int ci = 0; ci < colorValues.size(); ci++) {
            for (int si = 0; si < sizeValues.size(); si++) {
                if (skuIdx >= skuStocks.size()) {
                    break;
                }
                SkuStock sku = skuStocks.get(skuIdx);

                SellerOptionValueId colorValId = colorOptionValues.get(ci).id();
                SellerOptionValueId sizeValId = sizeOptionValues.get(si).id();

                List<ProductOptionMapping> mappings =
                        List.of(
                                ProductOptionMapping.reconstitute(
                                        ProductOptionMappingId.of(productIdSeq * 10),
                                        ProductId.of(productIdSeq),
                                        colorValId,
                                        DeletionStatus.active()),
                                ProductOptionMapping.reconstitute(
                                        ProductOptionMappingId.of(productIdSeq * 10 + 1),
                                        ProductId.of(productIdSeq),
                                        sizeValId,
                                        DeletionStatus.active()));

                products.add(
                        Product.reconstitute(
                                ProductId.of(productIdSeq++),
                                ProductGroupId.of(PG_ID),
                                SkuCode.of(sku.skuCode()),
                                Money.of(sku.price() + 5000),
                                Money.of(sku.price()),
                                Money.of(sku.price()),
                                20,
                                sku.stock(),
                                ProductStatus.ACTIVE,
                                skuIdx,
                                mappings,
                                now,
                                now));
                skuIdx++;
            }
        }

        // ProductGroup
        ProductGroup group =
                ProductGroup.reconstitute(
                        ProductGroupId.of(PG_ID),
                        SellerId.of(25L),
                        BrandId.of(421L),
                        CategoryId.of(52L),
                        ShippingPolicyId.of(36L),
                        RefundPolicyId.of(35L),
                        ProductGroupName.of(productName),
                        OptionType.COMBINATION,
                        ProductGroupStatus.ACTIVE,
                        List.of(
                                ProductGroupImage.reconstitute(
                                        ProductGroupImageId.of(1L),
                                        ProductGroupId.of(PG_ID),
                                        ImageUrl.of(IMAGE_URL),
                                        ImageUrl.of(IMAGE_URL),
                                        ImageType.THUMBNAIL,
                                        0,
                                        DeletionStatus.active())),
                        List.of(colorGroup, sizeGroup),
                        now,
                        now);

        // Notice (CLOTHING 기본)
        List<NoticeField> noticeFields =
                List.of(
                        NoticeField.reconstitute(
                                NoticeFieldId.of(100L),
                                NoticeFieldCode.of("material"),
                                NoticeFieldName.of("material"),
                                true,
                                1),
                        NoticeField.reconstitute(
                                NoticeFieldId.of(101L),
                                NoticeFieldCode.of("color"),
                                NoticeFieldName.of("color"),
                                true,
                                2),
                        NoticeField.reconstitute(
                                NoticeFieldId.of(102L),
                                NoticeFieldCode.of("size"),
                                NoticeFieldName.of("size"),
                                true,
                                3),
                        NoticeField.reconstitute(
                                NoticeFieldId.of(103L),
                                NoticeFieldCode.of("manufacturer"),
                                NoticeFieldName.of("manufacturer"),
                                true,
                                4));

        NoticeCategory noticeCategory =
                NoticeCategory.reconstitute(
                        NoticeCategoryId.of(1L),
                        NoticeCategoryCode.of("CLOTHING"),
                        NoticeCategoryName.of("CLOTHING", "의류"),
                        CategoryGroup.CLOTHING,
                        true,
                        noticeFields,
                        now,
                        now);

        List<ProductNoticeEntry> entries =
                List.of(
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(1L),
                                ProductNoticeId.of(1L),
                                NoticeFieldId.of(100L),
                                NoticeFieldValue.of("면 100%")),
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(2L),
                                ProductNoticeId.of(1L),
                                NoticeFieldId.of(101L),
                                NoticeFieldValue.of("블랙/화이트")),
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(3L),
                                ProductNoticeId.of(1L),
                                NoticeFieldId.of(102L),
                                NoticeFieldValue.of("M/L/XL")),
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(4L),
                                ProductNoticeId.of(1L),
                                NoticeFieldId.of(103L),
                                NoticeFieldValue.of("테스트")));

        ProductNotice productNotice =
                ProductNotice.reconstitute(
                        ProductNoticeId.of(1L),
                        ProductGroupId.of(PG_ID),
                        NoticeCategoryId.of(1L),
                        entries,
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

        ProductGroupDescription description =
                ProductGroupDescription.reconstitute(
                        ProductGroupDescriptionId.of(1L),
                        ProductGroupId.of(PG_ID),
                        DescriptionHtml.of("<p>옵션 수정 테스트 상품</p>"),
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

        ProductGroupDetailCompositeQueryResult queryResult =
                new ProductGroupDetailCompositeQueryResult(
                        PG_ID,
                        25L,
                        "테스트 셀러",
                        421L,
                        "테스트 브랜드",
                        52L,
                        "테스트 카테고리",
                        "테스트",
                        "52",
                        productName,
                        "COMBINATION",
                        "ACTIVE",
                        now,
                        now,
                        shipping,
                        null);

        return new ProductGroupDetailBundle(
                queryResult,
                group,
                products,
                Optional.of(description),
                Optional.of(productNotice),
                Optional.of(noticeCategory),
                Optional.of(sellerCs),
                Map.of());
    }
}
