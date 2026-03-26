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
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
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

/**
 * 옵션 inputType 시나리오별 네이버 API 통합 테스트.
 *
 * <p>모든 옵션 조합(PREDEFINED/FREE_INPUT)을 네이버 API에 등록하고 실제 반영을 확인합니다.
 */
@Tag("external-integration")
@DisplayName("옵션 inputType 시나리오별 통합 테스트")
class NaverOptionCustomIntegrationTest {

    private static final long NAVER_CATEGORY_ID = 50000973L;
    private static final String IMAGE_URL =
            "https://shop-phinf.pstatic.net/20260313_294/17733696875206wuci_JPEG/288351393492979_1382318577.jpg";
    private static final long PG_ID = 99200L;

    private final NaverCommerceProductMapper mapper = new NaverCommerceProductMapper();
    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("시나리오 A: SINGLE + PREDEFINED → optionCombinations")
    void scenarioA_singlePredefined() throws Exception {
        HttpClient http = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(http, objectMapper);

        System.out.println("===== 시나리오 A: SINGLE + PREDEFINED =====\n");

        SellerOptionGroup sizeGroup =
                buildOptionGroup(
                        1L, "사이즈", OptionInputType.PREDEFINED, 0, List.of("230", "240", "250"));

        ProductGroupDetailBundle bundle =
                buildBundle(
                        "옵션테스트A SINGLE+PREDEFINED",
                        OptionType.SINGLE,
                        List.of(sizeGroup),
                        List.of(
                                new SkuProduct("SKU-A-230", 29000, 10, Map.of(1L, 0)),
                                new SkuProduct("SKU-A-240", 29000, 8, Map.of(1L, 1)),
                                new SkuProduct("SKU-A-250", 29000, 5, Map.of(1L, 2))));

        long productNo = registerAndVerify(http, token, bundle, "A");

        NaverProductDetailResponse detail = fetchProduct(http, token, productNo);
        var optionInfo = detail.originProduct().detailAttribute().optionInfo();

        assertThat(optionInfo.optionCombinations()).hasSize(3);
        assertThat(optionInfo.optionCustom())
                .satisfiesAnyOf(c -> assertThat(c).isNull(), c -> assertThat(c).isEmpty());
        System.out.println("  [PASS] optionCombinations=3, optionCustom=없음\n");

        printOptionDetail(detail);
        deleteProduct(http, token, productNo, "A");
    }

    @Test
    @DisplayName("시나리오 B: SINGLE + FREE_INPUT → optionCustom만")
    void scenarioB_singleFreeInput() throws Exception {
        HttpClient http = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(http, objectMapper);

        System.out.println("===== 시나리오 B: SINGLE + FREE_INPUT =====\n");

        SellerOptionGroup engravingGroup =
                buildOptionGroup(1L, "각인문구", OptionInputType.FREE_INPUT, 0, List.of("기본값"));

        ProductGroupDetailBundle bundle =
                buildBundle(
                        "옵션테스트B SINGLE+FREE_INPUT",
                        OptionType.SINGLE,
                        List.of(engravingGroup),
                        List.of(new SkuProduct("SKU-B-1", 35000, 20, Map.of(1L, 0))));

        // FREE_INPUT만 있는 경우: optionCombinations 없이 optionCustom만
        NaverProductRegistrationRequest req =
                mapper.toRegistrationRequest(
                        ProductGroupSyncData.from(bundle), NAVER_CATEGORY_ID, null);
        String json = objectMapper.writeValueAsString(req);
        System.out.println("  요청 JSON (optionInfo 부분):");
        JsonNode reqNode = objectMapper.readTree(json);
        System.out.println(
                objectMapper.writeValueAsString(
                        reqNode.path("originProduct").path("detailAttribute").path("optionInfo")));

        long productNo = registerAndVerify(http, token, bundle, "B");

        NaverProductDetailResponse detail = fetchProduct(http, token, productNo);
        var optionInfo = detail.originProduct().detailAttribute().optionInfo();

        if (optionInfo != null && optionInfo.optionCustom() != null) {
            System.out.println("  optionCustom 수: " + optionInfo.optionCustom().size());
            for (var oc : optionInfo.optionCustom()) {
                System.out.printf(
                        "    id=%d, groupName=%s, usable=%s%n",
                        oc.id(), oc.groupName(), oc.usable());
            }
            System.out.println("  [PASS] optionCustom 등록 확인\n");
        } else {
            System.out.println("  [INFO] optionInfo 또는 optionCustom이 null\n");
        }

        printOptionDetail(detail);
        deleteProduct(http, token, productNo, "B");
    }

    @Test
    @DisplayName("시나리오 C: COMBINATION + 전부 PREDEFINED → optionCombinations (2그룹)")
    void scenarioC_combinationAllPredefined() throws Exception {
        HttpClient http = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(http, objectMapper);

        System.out.println("===== 시나리오 C: COMBINATION + 전부 PREDEFINED =====\n");

        SellerOptionGroup colorGroup =
                buildOptionGroup(1L, "색상", OptionInputType.PREDEFINED, 0, List.of("블랙", "화이트"));
        SellerOptionGroup sizeGroup =
                buildOptionGroup(2L, "사이즈", OptionInputType.PREDEFINED, 1, List.of("M", "L"));

        ProductGroupDetailBundle bundle =
                buildBundle(
                        "옵션테스트C COMB+ALL_PREDEFINED",
                        OptionType.COMBINATION,
                        List.of(colorGroup, sizeGroup),
                        List.of(
                                new SkuProduct("SKU-C-BK-M", 39000, 10, Map.of(1L, 0, 2L, 0)),
                                new SkuProduct("SKU-C-BK-L", 39000, 8, Map.of(1L, 0, 2L, 1)),
                                new SkuProduct("SKU-C-WH-M", 41000, 12, Map.of(1L, 1, 2L, 0)),
                                new SkuProduct("SKU-C-WH-L", 41000, 6, Map.of(1L, 1, 2L, 1))));

        long productNo = registerAndVerify(http, token, bundle, "C");

        NaverProductDetailResponse detail = fetchProduct(http, token, productNo);
        var optionInfo = detail.originProduct().detailAttribute().optionInfo();

        assertThat(optionInfo.optionCombinations()).hasSize(4);
        assertThat(optionInfo.optionCustom())
                .satisfiesAnyOf(c -> assertThat(c).isNull(), c -> assertThat(c).isEmpty());
        System.out.println("  [PASS] optionCombinations=4, optionCustom=없음\n");

        printOptionDetail(detail);
        deleteProduct(http, token, productNo, "C");
    }

    @Test
    @DisplayName("시나리오 D: COMBINATION + 1 PREDEFINED + 1 FREE_INPUT → 혼합")
    void scenarioD_combinationMixed() throws Exception {
        HttpClient http = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(http, objectMapper);

        System.out.println("===== 시나리오 D: COMBINATION + PREDEFINED + FREE_INPUT =====\n");

        SellerOptionGroup sizeGroup =
                buildOptionGroup(1L, "사이즈", OptionInputType.PREDEFINED, 0, List.of("S", "M", "L"));
        SellerOptionGroup engravingGroup =
                buildOptionGroup(2L, "각인문구", OptionInputType.FREE_INPUT, 1, List.of("기본값"));

        ProductGroupDetailBundle bundle =
                buildBundle(
                        "옵션테스트D COMB+MIXED",
                        OptionType.COMBINATION,
                        List.of(sizeGroup, engravingGroup),
                        List.of(
                                new SkuProduct("SKU-D-S", 45000, 10, Map.of(1L, 0, 2L, 0)),
                                new SkuProduct("SKU-D-M", 45000, 15, Map.of(1L, 1, 2L, 0)),
                                new SkuProduct("SKU-D-L", 45000, 8, Map.of(1L, 2, 2L, 0))));

        NaverProductRegistrationRequest req =
                mapper.toRegistrationRequest(
                        ProductGroupSyncData.from(bundle), NAVER_CATEGORY_ID, null);
        String json = objectMapper.writeValueAsString(req);
        System.out.println("  요청 JSON (optionInfo 부분):");
        JsonNode reqNode = objectMapper.readTree(json);
        System.out.println(
                objectMapper.writeValueAsString(
                        reqNode.path("originProduct").path("detailAttribute").path("optionInfo")));

        long productNo = registerAndVerify(http, token, bundle, "D");

        NaverProductDetailResponse detail = fetchProduct(http, token, productNo);
        var optionInfo = detail.originProduct().detailAttribute().optionInfo();

        if (optionInfo.optionCombinations() != null) {
            System.out.println(
                    "  optionCombinations: " + optionInfo.optionCombinations().size() + "건");
            for (var c : optionInfo.optionCombinations()) {
                System.out.printf(
                        "    id=%d, %s/%s, SKU=%s%n",
                        c.id(), c.optionName1(), c.optionName2(), c.sellerManagerCode());
            }
        }
        if (optionInfo.optionCustom() != null) {
            System.out.println("  optionCustom: " + optionInfo.optionCustom().size() + "건");
            for (var oc : optionInfo.optionCustom()) {
                System.out.printf("    id=%d, groupName=%s%n", oc.id(), oc.groupName());
            }
        }
        System.out.println("  [PASS] 혼합 옵션 등록 확인\n");

        // 수정 테스트: 가격 변경 후 옵션 구조 유지 확인
        System.out.println("  --- 수정 테스트: 가격 변경 ---");
        Thread.sleep(1000);

        ProductGroupDetailBundle updateBundle =
                buildBundle(
                        "옵션테스트D COMB+MIXED",
                        OptionType.COMBINATION,
                        List.of(sizeGroup, engravingGroup),
                        List.of(
                                new SkuProduct("SKU-D-S", 48000, 10, Map.of(1L, 0, 2L, 0)),
                                new SkuProduct("SKU-D-M", 48000, 15, Map.of(1L, 1, 2L, 0)),
                                new SkuProduct("SKU-D-L", 48000, 8, Map.of(1L, 2, 2L, 0))));

        NaverProductRegistrationRequest updateReq =
                mapper.toUpdateRequest(
                        ProductGroupSyncData.from(updateBundle),
                        NAVER_CATEGORY_ID,
                        null,
                        detail,
                        Set.of(ChangedArea.PRICE, ChangedArea.OPTION));

        String updateJson = objectMapper.writeValueAsString(updateReq);
        HttpResponse<String> updateResp =
                putJson(
                        http,
                        token,
                        NaverAuthHelper.BASE_URL + "/v2/products/origin-products/" + productNo,
                        updateJson);

        System.out.println("  수정 Status: " + updateResp.statusCode());
        if (updateResp.statusCode() != 200) {
            System.out.println("  [FAIL] " + updateResp.body());
        } else {
            System.out.println("  [PASS] 혼합 옵션 수정 성공");
        }

        deleteProduct(http, token, productNo, "D");
    }

    @Test
    @DisplayName("시나리오 E: COMBINATION + 전부 FREE_INPUT → optionCustom 2개")
    void scenarioE_combinationAllFreeInput() throws Exception {
        HttpClient http = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(http, objectMapper);

        System.out.println("===== 시나리오 E: COMBINATION + 전부 FREE_INPUT =====\n");

        SellerOptionGroup engraving1 =
                buildOptionGroup(1L, "각인문구", OptionInputType.FREE_INPUT, 0, List.of("기본값1"));
        SellerOptionGroup engraving2 =
                buildOptionGroup(2L, "메시지카드", OptionInputType.FREE_INPUT, 1, List.of("기본값2"));

        ProductGroupDetailBundle bundle =
                buildBundle(
                        "옵션테스트E COMB+ALL_FREE_INPUT",
                        OptionType.COMBINATION,
                        List.of(engraving1, engraving2),
                        List.of(new SkuProduct("SKU-E-1", 55000, 20, Map.of(1L, 0, 2L, 0))));

        NaverProductRegistrationRequest req =
                mapper.toRegistrationRequest(
                        ProductGroupSyncData.from(bundle), NAVER_CATEGORY_ID, null);
        String json = objectMapper.writeValueAsString(req);
        System.out.println("  요청 JSON (optionInfo 부분):");
        JsonNode reqNode = objectMapper.readTree(json);
        System.out.println(
                objectMapper.writeValueAsString(
                        reqNode.path("originProduct").path("detailAttribute").path("optionInfo")));

        long productNo = registerAndVerify(http, token, bundle, "E");

        NaverProductDetailResponse detail = fetchProduct(http, token, productNo);
        var optionInfo = detail.originProduct().detailAttribute().optionInfo();

        if (optionInfo != null && optionInfo.optionCustom() != null) {
            System.out.println("  optionCustom: " + optionInfo.optionCustom().size() + "건");
            for (var oc : optionInfo.optionCustom()) {
                System.out.printf("    id=%d, groupName=%s%n", oc.id(), oc.groupName());
            }
            assertThat(optionInfo.optionCustom()).hasSize(2);
            System.out.println("  [PASS] optionCustom=2 확인");
        }

        if (optionInfo != null && optionInfo.optionCombinations() != null) {
            System.out.println(
                    "  [WARN] optionCombinations도 존재: " + optionInfo.optionCombinations().size());
        } else {
            System.out.println("  [PASS] optionCombinations=없음");
        }

        deleteProduct(http, token, productNo, "E");
    }

    // ===== Helper =====

    private long registerAndVerify(
            HttpClient http, String token, ProductGroupDetailBundle bundle, String scenario)
            throws Exception {
        NaverProductRegistrationRequest req =
                mapper.toRegistrationRequest(
                        ProductGroupSyncData.from(bundle), NAVER_CATEGORY_ID, null);
        String json = objectMapper.writeValueAsString(req);

        HttpResponse<String> resp =
                postJson(http, token, NaverAuthHelper.BASE_URL + "/v2/products", json);

        System.out.println("  등록 Status: " + resp.statusCode());
        if (resp.statusCode() != 200) {
            System.out.println("  [FAIL] " + resp.body());
            throw new RuntimeException("시나리오 " + scenario + " 등록 실패: " + resp.body());
        }

        JsonNode body = objectMapper.readTree(resp.body());
        long productNo = body.path("originProductNo").asLong();
        System.out.println("  [PASS] 등록 성공, originProductNo=" + productNo);
        Thread.sleep(1000);
        return productNo;
    }

    private void deleteProduct(HttpClient http, String token, long productNo, String scenario)
            throws Exception {
        Thread.sleep(500);
        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        NaverAuthHelper.BASE_URL
                                                + "/v2/products/origin-products/"
                                                + productNo))
                        .header("Authorization", "Bearer " + token)
                        .DELETE()
                        .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("  삭제 Status: " + resp.statusCode());
        if (resp.statusCode() == 200) {
            System.out.println("  [PASS] 시나리오 " + scenario + " 삭제 완료\n");
        } else {
            System.out.println("  [WARN] 삭제 실패: " + resp.body());
        }
    }

    private void printOptionDetail(NaverProductDetailResponse detail) {
        var optionInfo = detail.originProduct().detailAttribute().optionInfo();
        if (optionInfo == null) {
            System.out.println("  optionInfo: null");
            return;
        }
        if (optionInfo.optionCombinations() != null) {
            System.out.println("  -- optionCombinations --");
            for (var c : optionInfo.optionCombinations()) {
                System.out.printf(
                        "    id=%d | %s/%s/%s | SKU=%s | stock=%d | price=%d%n",
                        c.id(),
                        c.optionName1(),
                        c.optionName2(),
                        c.optionName3(),
                        c.sellerManagerCode(),
                        c.stockQuantity(),
                        c.price());
            }
        }
        if (optionInfo.optionCustom() != null) {
            System.out.println("  -- optionCustom --");
            for (var oc : optionInfo.optionCustom()) {
                System.out.printf(
                        "    id=%d | groupName=%s | usable=%s%n",
                        oc.id(), oc.groupName(), oc.usable());
            }
        }
    }

    private NaverProductDetailResponse fetchProduct(HttpClient client, String token, long productNo)
            throws Exception {
        HttpRequest req =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        NaverAuthHelper.BASE_URL
                                                + "/v2/products/origin-products/"
                                                + productNo))
                        .header("Authorization", "Bearer " + token)
                        .GET()
                        .build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() != 200) {
            System.out.println("  [WARN] 조회 실패: " + resp.statusCode() + " " + resp.body());
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

    // ===== 도메인 객체 빌더 =====

    private record SkuProduct(
            String skuCode, int price, int stock, Map<Long, Integer> optionMapping) {}

    private SellerOptionGroup buildOptionGroup(
            long groupId,
            String groupName,
            OptionInputType inputType,
            int sortOrder,
            List<String> valueNames) {
        long valueIdBase = groupId * 1000;
        List<SellerOptionValue> values = new ArrayList<>();
        for (int i = 0; i < valueNames.size(); i++) {
            values.add(
                    SellerOptionValue.reconstitute(
                            SellerOptionValueId.of(valueIdBase + i),
                            SellerOptionGroupId.of(groupId),
                            OptionValueName.of(valueNames.get(i)),
                            CanonicalOptionValueId.of(valueIdBase + i),
                            i,
                            DeletionStatus.active()));
        }
        return SellerOptionGroup.reconstitute(
                SellerOptionGroupId.of(groupId),
                ProductGroupId.of(PG_ID),
                OptionGroupName.of(groupName),
                CanonicalOptionGroupId.of(groupId),
                inputType,
                sortOrder,
                values,
                DeletionStatus.active());
    }

    private ProductGroupDetailBundle buildBundle(
            String productName,
            OptionType optionType,
            List<SellerOptionGroup> optionGroups,
            List<SkuProduct> skuProducts) {

        Instant now = Instant.now();
        long productIdSeq = 500L;

        List<Product> products = new ArrayList<>();
        for (int i = 0; i < skuProducts.size(); i++) {
            SkuProduct sku = skuProducts.get(i);

            List<ProductOptionMapping> mappings = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : sku.optionMapping().entrySet()) {
                long groupId = entry.getKey();
                int valueIdx = entry.getValue();
                long valueId = groupId * 1000 + valueIdx;
                mappings.add(
                        ProductOptionMapping.reconstitute(
                                ProductOptionMappingId.of(productIdSeq * 10 + mappings.size()),
                                ProductId.of(productIdSeq),
                                SellerOptionValueId.of(valueId),
                                DeletionStatus.active()));
            }

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
                            i,
                            mappings,
                            now,
                            now));
        }

        ProductGroup group =
                ProductGroup.reconstitute(
                        ProductGroupId.of(PG_ID),
                        SellerId.of(25L),
                        BrandId.of(421L),
                        CategoryId.of(52L),
                        ShippingPolicyId.of(36L),
                        RefundPolicyId.of(35L),
                        ProductGroupName.of(productName),
                        optionType,
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
                        optionGroups,
                        now,
                        now);

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
                                NoticeFieldValue.of("블랙")),
                        ProductNoticeEntry.reconstitute(
                                ProductNoticeEntryId.of(3L),
                                ProductNoticeId.of(1L),
                                NoticeFieldId.of(102L),
                                NoticeFieldValue.of("FREE")),
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
                        DescriptionHtml.of("<p>옵션 타입별 테스트 상품</p>"),
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
                        optionType.name(),
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
