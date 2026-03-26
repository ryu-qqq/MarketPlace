package com.ryuqq.marketplace.adapter.out.client.naver;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceProductMapper;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
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
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import com.ryuqq.marketplace.domain.product.vo.SkuCode;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupDescriptionId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.CdnPath;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * 카테고리별 상품 등록 → 수정 → 삭제 통합 테스트.
 *
 * <p>각 notice 타입(WEAR, SHOES, BAG, FASHION_ITEMS, FURNITURE, KIDS, ETC)별로 매퍼를 통해 등록 요청을 생성하고, 네이버
 * API에 실제로 등록/수정/삭제까지 수행합니다.
 */
@Tag("external-integration")
@DisplayName("카테고리별 등록/수정/삭제 통합 테스트")
class NaverCategoryRegistrationTest {

    private static final long NAVER_CATEGORY_ID = 50000973L;
    private static final String IMAGE_URL =
            "https://shop-phinf.pstatic.net/20260313_294/17733696875206wuci_JPEG/288351393492979_1382318577.jpg";

    private final NaverCommerceProductMapper mapper = new NaverCommerceProductMapper();
    private final ObjectMapper objectMapper =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    @DisplayName("전체 카테고리 등록 → 수정 → 삭제")
    void registerUpdateDeleteAllCategories() throws Exception {
        HttpClient httpClient = HttpClient.newHttpClient();
        String token = NaverAuthHelper.getAccessToken(httpClient, new ObjectMapper());
        System.out.println("[OK] 토큰 발급 성공\n");

        List<CategoryTestCase> testCases =
                List.of(
                        new CategoryTestCase(
                                "CLOTHING",
                                CategoryGroup.CLOTHING,
                                "테스트 의류 상품",
                                List.of(
                                        f("material", "면 100%"),
                                        f("color", "블랙"),
                                        f("size", "FREE"),
                                        f("manufacturer", "테스트"),
                                        f("made_in", "중국"),
                                        f("wash_care", "손세탁"))),
                        new CategoryTestCase(
                                "SHOES",
                                CategoryGroup.SHOES,
                                "테스트 신발 상품",
                                List.of(
                                        f("material_upper", "가죽"),
                                        f("material_sole", "고무"),
                                        f("color", "브라운"),
                                        f("size", "270mm"),
                                        f("manufacturer", "테스트"),
                                        f("made_in", "이탈리아"))),
                        new CategoryTestCase(
                                "BAGS",
                                CategoryGroup.BAGS,
                                "테스트 가방 상품",
                                List.of(
                                        f("type", "크로스백"),
                                        f("material", "소가죽"),
                                        f("color", "블랙"),
                                        f("size", "30x20x10cm"),
                                        f("manufacturer", "테스트"),
                                        f("made_in", "한국"))),
                        new CategoryTestCase(
                                "ACCESSORIES",
                                CategoryGroup.ACCESSORIES,
                                "테스트 패션잡화 상품",
                                List.of(
                                        f("type", "벨트"),
                                        f("material", "소가죽"),
                                        f("size", "FREE"),
                                        f("manufacturer", "테스트"),
                                        f("made_in", "중국"))),
                        new CategoryTestCase(
                                "FURNITURE",
                                CategoryGroup.FURNITURE,
                                "테스트 가구 상품",
                                List.of(
                                        f("product_name", "원목 책상"),
                                        f("material", "원목"),
                                        f("color", "내추럴"),
                                        f("size", "120x60x75cm"),
                                        f("manufacturer", "테스트"),
                                        f("certification", "해당없음"))),
                        new CategoryTestCase(
                                "BABY_KIDS",
                                CategoryGroup.BABY_KIDS,
                                "테스트 유아용품 상품",
                                List.of(
                                        f("product_name", "유아 장난감"),
                                        f("model", "TOY-001"),
                                        f("material", "ABS"),
                                        f("color", "멀티"),
                                        f("size", "20x15x10cm"),
                                        f("manufacturer", "테스트"),
                                        f("caution", "3세 이상"),
                                        f("age_range", "3~7세"))),
                        new CategoryTestCase(
                                "DIGITAL",
                                CategoryGroup.DIGITAL,
                                "테스트 디지털 상품",
                                List.of(
                                        f("product_name", "블루투스 이어폰"), f("manufacturer", "테스트"),
                                        f("made_in", "중국"), f("certification", "해당없음"))));

        List<Long> registeredProductNos = new ArrayList<>();
        List<String> failedCategories = new ArrayList<>();

        // ===== 1. 등록 =====
        System.out.println("========== 1. 카테고리별 등록 ==========\n");
        for (CategoryTestCase tc : testCases) {
            System.out.printf("--- [%s] %s ---%n", tc.code, tc.name);
            try {
                ProductGroupDetailBundle bundle = buildBundle(tc);
                NaverProductRegistrationRequest request =
                        mapper.toRegistrationRequest(
                                ProductGroupSyncData.from(bundle), NAVER_CATEGORY_ID, null);

                String json = objectMapper.writeValueAsString(request);
                HttpResponse<String> resp =
                        postJson(
                                httpClient, token, NaverAuthHelper.BASE_URL + "/v2/products", json);

                System.out.println("  Status: " + resp.statusCode());
                if (resp.statusCode() == 200) {
                    JsonNode body = objectMapper.readTree(resp.body());
                    long originProductNo = body.path("originProductNo").asLong();
                    registeredProductNos.add(originProductNo);
                    System.out.println("  [PASS] originProductNo: " + originProductNo);
                } else {
                    failedCategories.add(tc.code);
                    System.out.println("  [FAIL] " + resp.body());
                }
            } catch (Exception e) {
                failedCategories.add(tc.code);
                System.out.println("  [ERROR] " + e.getMessage());
            }
            System.out.println();
            Thread.sleep(500);
        }

        System.out.printf("등록 결과: %d/%d 성공%n%n", registeredProductNos.size(), testCases.size());

        // ===== 2. 수정 =====
        if (!registeredProductNos.isEmpty()) {
            System.out.println("========== 2. 상품 수정 테스트 ==========\n");
            long targetProductNo = registeredProductNos.get(0);
            CategoryTestCase firstCase = testCases.get(0);

            // 이름과 가격 변경
            ProductGroupDetailBundle updatedBundle =
                    buildBundle(
                            firstCase.code,
                            firstCase.categoryGroup,
                            firstCase.name + " [수정됨]",
                            25000,
                            firstCase.fields);
            NaverProductRegistrationRequest updateRequest =
                    mapper.toRegistrationRequest(
                            ProductGroupSyncData.from(updatedBundle), NAVER_CATEGORY_ID, null);

            String updateJson = objectMapper.writeValueAsString(updateRequest);
            HttpResponse<String> updateResp =
                    putJson(
                            httpClient,
                            token,
                            NaverAuthHelper.BASE_URL
                                    + "/v2/products/origin-products/"
                                    + targetProductNo,
                            updateJson);

            System.out.println("  수정 대상: " + targetProductNo);
            System.out.println("  Status: " + updateResp.statusCode());
            if (updateResp.statusCode() == 200) {
                JsonNode body = objectMapper.readTree(updateResp.body());
                String updatedName = body.path("originProduct").path("name").asText();
                int updatedPrice = body.path("originProduct").path("salePrice").asInt();
                System.out.println("  [PASS] 이름: " + updatedName + ", 가격: " + updatedPrice);
                assertThat(updatedName).contains("[수정됨]");
                assertThat(updatedPrice).isEqualTo(25000);
            } else {
                System.out.println("  [FAIL] " + updateResp.body());
            }
            System.out.println();
        }

        // ===== 3. 삭제 =====
        System.out.println("========== 3. 전체 삭제 ==========\n");
        int deleteSuccess = 0;
        for (Long productNo : registeredProductNos) {
            HttpRequest deleteReq =
                    HttpRequest.newBuilder()
                            .uri(
                                    URI.create(
                                            NaverAuthHelper.BASE_URL
                                                    + "/v2/products/origin-products/"
                                                    + productNo))
                            .header("Authorization", "Bearer " + token)
                            .DELETE()
                            .build();
            HttpResponse<String> deleteResp =
                    httpClient.send(deleteReq, HttpResponse.BodyHandlers.ofString());
            if (deleteResp.statusCode() == 200) {
                deleteSuccess++;
                System.out.println("  [OK] 삭제: " + productNo);
            } else {
                System.out.println("  [FAIL] 삭제 실패 " + productNo + ": " + deleteResp.body());
            }
            Thread.sleep(500);
        }
        System.out.printf("%n삭제 결과: %d/%d 성공%n", deleteSuccess, registeredProductNos.size());

        // ===== 최종 결과 =====
        System.out.println("\n========== 최종 결과 ==========");
        System.out.printf(
                "등록: %d/%d | 수정: %s | 삭제: %d/%d%n",
                registeredProductNos.size(),
                testCases.size(),
                registeredProductNos.isEmpty() ? "SKIP" : "DONE",
                deleteSuccess,
                registeredProductNos.size());
        if (!failedCategories.isEmpty()) {
            System.out.println("실패 카테고리: " + failedCategories);
        }

        assertThat(failedCategories).as("모든 카테고리 등록 성공").isEmpty();
        assertThat(deleteSuccess).as("모든 상품 삭제 성공").isEqualTo(registeredProductNos.size());
    }

    // ===== Helper =====

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

    private record FieldEntry(String code, String value) {}

    private static FieldEntry f(String code, String value) {
        return new FieldEntry(code, value);
    }

    private record CategoryTestCase(
            String code, CategoryGroup categoryGroup, String name, List<FieldEntry> fields) {}

    private ProductGroupDetailBundle buildBundle(CategoryTestCase tc) {
        return buildBundle(tc.code, tc.categoryGroup, tc.name, 12000, tc.fields);
    }

    private ProductGroupDetailBundle buildBundle(
            String categoryCode,
            CategoryGroup categoryGroup,
            String productName,
            int price,
            List<FieldEntry> fields) {

        Instant now = Instant.now();
        long pgId = 99000L;

        ProductGroup group =
                ProductGroup.reconstitute(
                        ProductGroupId.of(pgId),
                        SellerId.of(25L),
                        BrandId.of(421L),
                        CategoryId.of(52L),
                        ShippingPolicyId.of(36L),
                        RefundPolicyId.of(35L),
                        ProductGroupName.of(productName),
                        OptionType.NONE,
                        ProductGroupStatus.ACTIVE,
                        List.of(
                                ProductGroupImage.reconstitute(
                                        ProductGroupImageId.of(1L),
                                        ProductGroupId.of(pgId),
                                        ImageUrl.of(IMAGE_URL),
                                        ImageUrl.of(IMAGE_URL),
                                        ImageType.THUMBNAIL,
                                        0,
                                        DeletionStatus.active())),
                        List.of(),
                        now,
                        now);

        List<Product> products =
                List.of(
                        Product.reconstitute(
                                ProductId.of(1L),
                                ProductGroupId.of(pgId),
                                SkuCode.of(null),
                                Money.of(price + 3000),
                                Money.of(price),
                                Money.of(price),
                                20,
                                50,
                                ProductStatus.ACTIVE,
                                1,
                                List.of(),
                                now,
                                now));

        // Notice
        long fieldIdSeq = 100L;
        List<NoticeField> noticeFields = new ArrayList<>();
        List<ProductNoticeEntry> entries = new ArrayList<>();
        long entryIdSeq = 1000L;

        for (FieldEntry fe : fields) {
            long fid = fieldIdSeq++;
            noticeFields.add(
                    NoticeField.reconstitute(
                            NoticeFieldId.of(fid),
                            NoticeFieldCode.of(fe.code),
                            NoticeFieldName.of(fe.code),
                            true,
                            (int) (fid - 99)));
            entries.add(
                    ProductNoticeEntry.reconstitute(
                            ProductNoticeEntryId.of(entryIdSeq++), ProductNoticeId.of(1L),
                            NoticeFieldId.of(fid), NoticeFieldValue.of(fe.value)));
        }

        NoticeCategory noticeCategory =
                NoticeCategory.reconstitute(
                        NoticeCategoryId.of(1L),
                        NoticeCategoryCode.of(categoryCode),
                        NoticeCategoryName.of(categoryCode, categoryCode),
                        categoryGroup,
                        true,
                        noticeFields,
                        now,
                        now);

        ProductNotice productNotice =
                ProductNotice.reconstitute(
                        ProductNoticeId.of(1L),
                        ProductGroupId.of(pgId),
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
                        ProductGroupId.of(pgId),
                        DescriptionHtml.of("<p>카테고리별 등록 테스트 상품입니다 - " + categoryCode + "</p>"),
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
                        pgId,
                        25L,
                        "테스트 셀러",
                        421L,
                        "테스트 브랜드",
                        52L,
                        "테스트 카테고리",
                        "테스트",
                        "52",
                        productName,
                        "NONE",
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
