package com.ryuqq.marketplace.integration.product;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.brand.BrandJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.category.CategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.category.repository.CategoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.notice.NoticeCategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.notice.NoticeFieldJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.notice.repository.NoticeCategoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.notice.repository.NoticeFieldJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.product.repository.ProductJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.product.repository.ProductOptionMappingJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.ProductGroupJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.SellerOptionGroupJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.SellerOptionValueJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.DescriptionImageJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.ProductGroupDescriptionJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.repository.ProductGroupImageJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository.ProductNoticeEntryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository.ProductNoticeJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.RefundPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.repository.RefundPolicyJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.ShippingPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.repository.ShippingPolicyJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Product Command E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>C1: PATCH /products/{id}/price - 가격 수정
 *   <li>C2: PATCH /products/{id}/stock - 재고 수정
 *   <li>C3: PATCH /products/product-groups/{id}/status - 배치 상태 변경
 *   <li>C4: PATCH /products/product-groups/{id} - 상품 일괄 수정 (dual domain diff)
 * </ul>
 *
 * <p>선행 조건: productgroup C1으로 상품그룹 등록 후 product 수정
 */
@Tag("e2e")
@Tag("product")
@Tag("command")
@DisplayName("Product Command E2E 테스트")
class ProductCommandE2ETest extends E2ETestBase {

    private static final String PRODUCT_GROUPS = "/product-groups";
    private static final String PRODUCT_GROUPS_ID = "/product-groups/{productGroupId}";
    private static final String PRODUCTS_PRICE = "/products/{productId}/price";
    private static final String PRODUCTS_STOCK = "/products/{productId}/stock";
    private static final String PRODUCTS_STATUS =
            "/products/product-groups/{productGroupId}/status";
    private static final String PRODUCTS_BATCH_UPDATE = "/products/product-groups/{productGroupId}";

    @Autowired private SellerJpaRepository sellerRepository;
    @Autowired private BrandJpaRepository brandRepository;
    @Autowired private CategoryJpaRepository categoryRepository;
    @Autowired private ShippingPolicyJpaRepository shippingPolicyRepository;
    @Autowired private RefundPolicyJpaRepository refundPolicyRepository;
    @Autowired private NoticeCategoryJpaRepository noticeCategoryRepository;
    @Autowired private NoticeFieldJpaRepository noticeFieldRepository;
    @Autowired private ProductGroupJpaRepository productGroupRepository;
    @Autowired private ProductGroupImageJpaRepository productGroupImageRepository;
    @Autowired private SellerOptionGroupJpaRepository sellerOptionGroupRepository;
    @Autowired private SellerOptionValueJpaRepository sellerOptionValueRepository;
    @Autowired private ProductJpaRepository productRepository;
    @Autowired private ProductOptionMappingJpaRepository productOptionMappingRepository;
    @Autowired private ProductGroupDescriptionJpaRepository descriptionRepository;
    @Autowired private DescriptionImageJpaRepository descriptionImageRepository;
    @Autowired private ProductNoticeJpaRepository productNoticeRepository;
    @Autowired private ProductNoticeEntryJpaRepository productNoticeEntryRepository;

    private Long sellerId;
    private String sellerOrganizationId;
    private Long brandId;
    private Long categoryId;
    private Long shippingPolicyId;
    private Long refundPolicyId;
    private Long noticeCategoryId;
    private Long productGroupId;
    private List<Integer> productIds;

    @BeforeEach
    void setUp() {
        cleanUp();
        seedReferenceData();
        registerProductGroup();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        productOptionMappingRepository.deleteAll();
        productRepository.deleteAll();
        sellerOptionValueRepository.deleteAll();
        sellerOptionGroupRepository.deleteAll();
        descriptionImageRepository.deleteAll();
        descriptionRepository.deleteAll();
        productNoticeEntryRepository.deleteAll();
        productNoticeRepository.deleteAll();
        productGroupImageRepository.deleteAll();
        productGroupRepository.deleteAll();
        noticeFieldRepository.deleteAll();
        noticeCategoryRepository.deleteAll();
        shippingPolicyRepository.deleteAll();
        refundPolicyRepository.deleteAll();
        brandRepository.deleteAll();
        categoryRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    private void seedReferenceData() {
        var seller = sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithAuth());
        sellerId = seller.getId();
        sellerOrganizationId = seller.getAuthOrganizationId();
        brandId = brandRepository.save(BrandJpaEntityFixtures.newEntity()).getId();
        categoryId = categoryRepository.save(CategoryJpaEntityFixtures.newEntity()).getId();
        shippingPolicyId =
                shippingPolicyRepository
                        .save(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId))
                        .getId();
        refundPolicyId =
                refundPolicyRepository
                        .save(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId))
                        .getId();
        var noticeCategory =
                noticeCategoryRepository.save(NoticeCategoryJpaEntityFixtures.newEntity());
        noticeCategoryId = noticeCategory.getId();
        noticeFieldRepository.save(
                NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(noticeCategoryId));
        noticeFieldRepository.save(
                NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(noticeCategoryId));
    }

    private void registerProductGroup() {
        Map<String, Object> request = buildRegisterRequest();

        Response response =
                given().spec(givenSuperAdmin()).body(request).when().post(PRODUCT_GROUPS);

        response.then().statusCode(HttpStatus.CREATED.value());
        productGroupId = response.jsonPath().getLong("productGroupId");

        // 상품 ID 조회
        Response detail =
                given().spec(givenSuperAdmin()).when().get(PRODUCT_GROUPS_ID, productGroupId);
        productIds = detail.jsonPath().getList("data.optionProductMatrix.products.id");
    }

    private Map<String, Object> buildRegisterRequest() {
        Map<String, Object> request = new HashMap<>();
        request.put("sellerId", sellerId);
        request.put("brandId", brandId);
        request.put("categoryId", categoryId);
        request.put("shippingPolicyId", shippingPolicyId);
        request.put("refundPolicyId", refundPolicyId);
        request.put("productGroupName", "상품 테스트용 그룹");
        request.put("optionType", "COMBINATION");

        request.put(
                "images",
                List.of(
                        Map.of(
                                "originUrl", "https://example.com/img.jpg",
                                "imageType", "THUMBNAIL",
                                "sortOrder", 0)));

        request.put(
                "optionGroups",
                List.of(
                        Map.of(
                                "optionGroupName",
                                "색상",
                                "optionValues",
                                List.of(
                                        Map.of("optionValueName", "빨강", "sortOrder", 0),
                                        Map.of("optionValueName", "파랑", "sortOrder", 1))),
                        Map.of(
                                "optionGroupName",
                                "사이즈",
                                "optionValues",
                                List.of(
                                        Map.of("optionValueName", "M", "sortOrder", 0),
                                        Map.of("optionValueName", "L", "sortOrder", 1)))));

        request.put(
                "products",
                List.of(
                        Map.of(
                                "skuCode", "SKU-RED-M",
                                "regularPrice", 30000,
                                "currentPrice", 25000,
                                "stockQuantity", 100,
                                "sortOrder", 0,
                                "optionIndices", List.of(0, 0)),
                        Map.of(
                                "skuCode", "SKU-RED-L",
                                "regularPrice", 30000,
                                "currentPrice", 25000,
                                "stockQuantity", 80,
                                "sortOrder", 1,
                                "optionIndices", List.of(0, 1)),
                        Map.of(
                                "skuCode", "SKU-BLUE-M",
                                "regularPrice", 30000,
                                "currentPrice", 25000,
                                "stockQuantity", 90,
                                "sortOrder", 2,
                                "optionIndices", List.of(1, 0)),
                        Map.of(
                                "skuCode", "SKU-BLUE-L",
                                "regularPrice", 30000,
                                "currentPrice", 25000,
                                "stockQuantity", 70,
                                "sortOrder", 3,
                                "optionIndices", List.of(1, 1))));

        request.put("description", Map.of("content", "<p>상세 설명</p>"));

        var noticeFields = noticeFieldRepository.findAll();
        request.put(
                "notice",
                Map.of(
                        "noticeCategoryId",
                        noticeCategoryId,
                        "entries",
                        noticeFields.stream()
                                .map(f -> Map.of("noticeFieldId", f.getId(), "fieldValue", "테스트 값"))
                                .toList()));
        return request;
    }

    // ===== C1: 가격 수정 =====

    @Nested
    @DisplayName("PATCH /products/{id}/price - 가격 수정")
    class UpdatePriceTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1-01] 가격 수정 성공")
        void updatePrice_ValidRequest_Returns204() {
            Long productId = productIds.get(0).longValue();

            given().spec(givenSuperAdmin())
                    .body(Map.of("regularPrice", 40000, "currentPrice", 35000))
                    .when()
                    .patch(PRODUCTS_PRICE, productId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 조회로 검증
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .body(
                            "data.optionProductMatrix.products.find { it.id == "
                                    + productId
                                    + " }.regularPrice",
                            equalTo(40000))
                    .body(
                            "data.optionProductMatrix.products.find { it.id == "
                                    + productId
                                    + " }.currentPrice",
                            equalTo(35000));
        }

        @Test
        @DisplayName("[C1-02] 판매가가 정가보다 높으면 400")
        void updatePrice_CurrentPriceExceedsRegular_Returns400() {
            Long productId = productIds.get(0).longValue();

            given().spec(givenSuperAdmin())
                    .body(Map.of("regularPrice", 30000, "currentPrice", 50000))
                    .when()
                    .patch(PRODUCTS_PRICE, productId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("[C1-03] 존재하지 않는 상품 ID - 404")
        void updatePrice_NonExistentProduct_Returns404() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("regularPrice", 40000, "currentPrice", 35000))
                    .when()
                    .patch(PRODUCTS_PRICE, 999999L)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // ===== C2: 재고 수정 =====

    @Nested
    @DisplayName("PATCH /products/{id}/stock - 재고 수정")
    class UpdateStockTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2-01] 재고 수정 성공")
        void updateStock_ValidRequest_Returns204() {
            Long productId = productIds.get(0).longValue();

            given().spec(givenSuperAdmin())
                    .body(Map.of("stockQuantity", 200))
                    .when()
                    .patch(PRODUCTS_STOCK, productId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 조회로 검증
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .body(
                            "data.optionProductMatrix.products.find { it.id == "
                                    + productId
                                    + " }.stockQuantity",
                            equalTo(200));
        }

        @Test
        @DisplayName("[C2-02] 재고 0으로 설정 성공")
        void updateStock_ZeroQuantity_Returns204() {
            Long productId = productIds.get(0).longValue();

            given().spec(givenSuperAdmin())
                    .body(Map.of("stockQuantity", 0))
                    .when()
                    .patch(PRODUCTS_STOCK, productId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }
    }

    // ===== C3: 배치 상태 변경 =====

    @Nested
    @DisplayName("PATCH /products/product-groups/{id}/status - 배치 상태 변경")
    class BatchChangeStatusTest {

        @Test
        @Tag("P0")
        @DisplayName("[C3-01] 배치 상태 변경 성공")
        void batchChangeStatus_ValidRequest_Returns204() {
            List<Long> ids = productIds.stream().map(Integer::longValue).toList();

            given().spec(givenSellerUser(sellerOrganizationId, "product:write"))
                    .body(Map.of("productIds", ids, "targetStatus", "INACTIVE"))
                    .when()
                    .patch(PRODUCTS_STATUS, productGroupId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 조회로 검증
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .body(
                            "data.optionProductMatrix.products.findAll { it.status == 'INACTIVE'"
                                    + " }.size()",
                            equalTo(productIds.size()));
        }
    }

    // ===== C4: 상품 일괄 수정 (dual domain diff) =====

    @Nested
    @DisplayName("PATCH /products/product-groups/{id} - 상품+옵션 일괄 수정")
    class BatchUpdateProductsTest {

        @Test
        @Tag("P0")
        @DisplayName("[C4-01] 상품 일괄 수정 - 가격/재고 변경")
        void batchUpdateProducts_PriceAndStockChanged_Returns204() {
            // 기존 옵션/상품 ID 조회
            Response detail =
                    given().spec(givenSuperAdmin()).when().get(PRODUCT_GROUPS_ID, productGroupId);

            List<Map<String, Object>> optionGroups =
                    detail.jsonPath().getList("data.optionProductMatrix.optionGroups");
            List<Map<String, Object>> products =
                    detail.jsonPath().getList("data.optionProductMatrix.products");

            // 옵션 그룹 구성 (기존 ID 유지)
            List<Map<String, Object>> optionGroupsReq =
                    optionGroups.stream()
                            .map(
                                    og -> {
                                        @SuppressWarnings("unchecked")
                                        List<Map<String, Object>> ovs =
                                                (List<Map<String, Object>>) og.get("optionValues");
                                        return Map.<String, Object>of(
                                                "sellerOptionGroupId", og.get("id"),
                                                "optionGroupName", og.get("optionGroupName"),
                                                "optionValues",
                                                        ovs.stream()
                                                                .map(
                                                                        ov ->
                                                                                Map
                                                                                        .<String,
                                                                                                Object>
                                                                                                of(
                                                                                                        "sellerOptionValueId",
                                                                                                        ov
                                                                                                                .get(
                                                                                                                        "id"),
                                                                                                        "optionValueName",
                                                                                                        ov
                                                                                                                .get(
                                                                                                                        "optionValueName"),
                                                                                                        "sortOrder",
                                                                                                        ov
                                                                                                                .get(
                                                                                                                        "sortOrder")))
                                                                .toList());
                                    })
                            .toList();

            // optionValueId → sortOrder 매핑 구성
            Map<Object, Integer> valueSortOrderMap = new HashMap<>();
            for (Map<String, Object> og : optionGroups) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> ovs = (List<Map<String, Object>>) og.get("optionValues");
                for (Map<String, Object> ov : ovs) {
                    valueSortOrderMap.put(ov.get("id"), ((Number) ov.get("sortOrder")).intValue());
                }
            }

            // 상품 구성 (가격/재고 변경)
            List<Map<String, Object>> productsReq =
                    products.stream()
                            .map(
                                    p -> {
                                        @SuppressWarnings("unchecked")
                                        List<Integer> optionIndices =
                                                ((List<Map<String, Object>>) p.get("options"))
                                                        .stream()
                                                                .map(
                                                                        o ->
                                                                                valueSortOrderMap
                                                                                        .getOrDefault(
                                                                                                o
                                                                                                        .get(
                                                                                                                "sellerOptionValueId"),
                                                                                                0))
                                                                .toList();
                                        return Map.<String, Object>of(
                                                "productId",
                                                p.get("id"),
                                                "skuCode",
                                                p.get("skuCode"),
                                                "regularPrice",
                                                99000,
                                                "currentPrice",
                                                89000,
                                                "stockQuantity",
                                                999,
                                                "sortOrder",
                                                p.get("sortOrder"),
                                                "optionIndices",
                                                optionIndices);
                                    })
                            .toList();

            Map<String, Object> updateRequest =
                    Map.of(
                            "optionGroups", optionGroupsReq,
                            "products", productsReq);

            given().spec(givenSuperAdmin())
                    .body(updateRequest)
                    .when()
                    .patch(PRODUCTS_BATCH_UPDATE, productGroupId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 검증
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .body("data.optionProductMatrix.products[0].regularPrice", equalTo(99000))
                    .body("data.optionProductMatrix.products[0].currentPrice", equalTo(89000))
                    .body("data.optionProductMatrix.products[0].stockQuantity", equalTo(999));
        }

        @Test
        @DisplayName("[C4-02] 신규 옵션 값 추가 + 신규 상품 추가")
        void batchUpdateProducts_AddNewOptionAndProduct_Returns204() {
            Response detail =
                    given().spec(givenSuperAdmin()).when().get(PRODUCT_GROUPS_ID, productGroupId);

            List<Map<String, Object>> optionGroups =
                    detail.jsonPath().getList("data.optionProductMatrix.optionGroups");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> existingValues =
                    (List<Map<String, Object>>) optionGroups.get(0).get("optionValues");

            // 기존 옵션 2개 유지 + 신규 옵션 값 1개 추가 ("초록")
            List<Map<String, Object>> optionValuesReq =
                    new java.util.ArrayList<>(
                            existingValues.stream()
                                    .map(
                                            ov ->
                                                    Map.<String, Object>of(
                                                            "sellerOptionValueId",
                                                            ov.get("id"),
                                                            "optionValueName",
                                                            ov.get("optionValueName"),
                                                            "sortOrder",
                                                            ov.get("sortOrder")))
                                    .toList());
            optionValuesReq.add(Map.of("optionValueName", "초록", "sortOrder", 2));

            List<Map<String, Object>> optionGroupsReq =
                    List.of(
                            Map.of(
                                    "sellerOptionGroupId",
                                    optionGroups.get(0).get("id"),
                                    "optionGroupName",
                                    "색상",
                                    "optionValues",
                                    optionValuesReq));

            // 기존 상품 2개 + 신규 상품 1개 (초록)
            List<Map<String, Object>> productsReq = new java.util.ArrayList<>();
            productsReq.add(
                    Map.of(
                            "productId", productIds.get(0),
                            "skuCode", "SKU-RED",
                            "regularPrice", 30000,
                            "currentPrice", 25000,
                            "stockQuantity", 100,
                            "sortOrder", 0,
                            "optionIndices", List.of(0)));
            productsReq.add(
                    Map.of(
                            "productId", productIds.get(1),
                            "skuCode", "SKU-BLUE",
                            "regularPrice", 30000,
                            "currentPrice", 25000,
                            "stockQuantity", 80,
                            "sortOrder", 1,
                            "optionIndices", List.of(1)));
            productsReq.add(
                    Map.of(
                            "skuCode", "SKU-GREEN",
                            "regularPrice", 35000,
                            "currentPrice", 30000,
                            "stockQuantity", 50,
                            "sortOrder", 2,
                            "optionIndices", List.of(2)));

            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "optionGroups", optionGroupsReq,
                                    "products", productsReq))
                    .when()
                    .patch(PRODUCTS_BATCH_UPDATE, productGroupId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 검증: 3개 상품 존재
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .body("data.optionProductMatrix.products.size()", greaterThanOrEqualTo(3));
        }
    }
}
