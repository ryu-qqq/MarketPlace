package com.ryuqq.marketplace.integration.productgroup;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
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
 * ProductGroup 전체 플로우 E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>C1→Q2: 단건 등록 → 상세 조회 (8개 서브 Aggregate 검증)
 *   <li>C1→C3→Q2: 등록 → 전체 수정 → 조회 (diff 기반 이미지/옵션/상품 교체 검증)
 *   <li>C1→C5: 등록 → 배치 상태 변경 (상태 전이 검증)
 *   <li>C4: 기본 정보 수정 → 이미지/옵션 변경 없음 확인
 * </ul>
 */
@Tag("e2e")
@Tag("productgroup")
@Tag("flow")
@DisplayName("ProductGroup Flow E2E 테스트")
class ProductGroupFlowE2ETest extends E2ETestBase {

    private static final String PRODUCT_GROUPS = "/product-groups";
    private static final String PRODUCT_GROUPS_ID = "/product-groups/{productGroupId}";
    private static final String PRODUCT_GROUPS_STATUS = "/product-groups/status";
    private static final String PRODUCT_GROUPS_BASIC_INFO =
            "/product-groups/{productGroupId}/basic-info";

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

    @BeforeEach
    void setUp() {
        cleanUp();
        seedReferenceData();
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

        var field1 = NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(noticeCategoryId);
        var field2 = NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(noticeCategoryId);
        noticeFieldRepository.save(field1);
        noticeFieldRepository.save(field2);
    }

    // ===== 요청 생성 헬퍼 =====

    private Map<String, Object> createRegisterRequest() {
        Map<String, Object> request = new HashMap<>();
        request.put("sellerId", sellerId);
        request.put("brandId", brandId);
        request.put("categoryId", categoryId);
        request.put("shippingPolicyId", shippingPolicyId);
        request.put("refundPolicyId", refundPolicyId);
        request.put("productGroupName", "테스트 상품그룹");
        request.put("optionType", "COMBINATION");

        request.put(
                "images",
                List.of(
                        Map.of(
                                "originUrl", "https://example.com/img1.jpg",
                                "imageType", "THUMBNAIL",
                                "sortOrder", 0),
                        Map.of(
                                "originUrl", "https://example.com/img2.jpg",
                                "imageType", "DETAIL",
                                "sortOrder", 1)));

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
                                        Map.of("optionValueName", "S", "sortOrder", 0),
                                        Map.of("optionValueName", "M", "sortOrder", 1)))));

        request.put(
                "products",
                List.of(
                        Map.of(
                                "skuCode", "SKU-RED-S",
                                "regularPrice", 50000,
                                "currentPrice", 45000,
                                "stockQuantity", 100,
                                "sortOrder", 0,
                                "optionIndices", List.of(0, 0)),
                        Map.of(
                                "skuCode", "SKU-RED-M",
                                "regularPrice", 50000,
                                "currentPrice", 45000,
                                "stockQuantity", 80,
                                "sortOrder", 1,
                                "optionIndices", List.of(0, 1)),
                        Map.of(
                                "skuCode", "SKU-BLUE-S",
                                "regularPrice", 55000,
                                "currentPrice", 50000,
                                "stockQuantity", 60,
                                "sortOrder", 2,
                                "optionIndices", List.of(1, 0)),
                        Map.of(
                                "skuCode", "SKU-BLUE-M",
                                "regularPrice", 55000,
                                "currentPrice", 50000,
                                "stockQuantity", 40,
                                "sortOrder", 3,
                                "optionIndices", List.of(1, 1))));

        request.put("description", Map.of("content", "<p>테스트 상품 상세설명</p>"));

        var noticeFields = noticeFieldRepository.findAll();
        request.put(
                "notice",
                Map.of(
                        "noticeCategoryId",
                        noticeCategoryId,
                        "entries",
                        List.of(
                                Map.of(
                                        "noticeFieldId",
                                        noticeFields.get(0).getId(),
                                        "fieldValue",
                                        "면 100%"),
                                Map.of(
                                        "noticeFieldId",
                                        noticeFields.get(1).getId(),
                                        "fieldValue",
                                        "대한민국"))));

        return request;
    }

    // ===== 플로우 테스트 =====

    @Nested
    @DisplayName("C1→Q2: 등록 → 상세 조회")
    class RegisterAndGetDetailTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-1] 단건 등록 후 상세 조회 - 8개 서브 Aggregate 모두 검증")
        void registerAndGetDetail_AllSubAggregates_Verified() {
            // ===== Step 1: POST - 상품그룹 등록 =====
            Map<String, Object> request = createRegisterRequest();

            Response createResponse =
                    given().spec(givenSuperAdmin()).body(request).when().post(PRODUCT_GROUPS);

            createResponse.then().statusCode(HttpStatus.CREATED.value());
            Long productGroupId = createResponse.jsonPath().getLong("productGroupId");
            assertThat(productGroupId).isNotNull().isGreaterThan(0);

            // ===== Step 2: GET - 상세 조회로 모든 서브 Aggregate 검증 =====
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    // 기본 정보
                    .body("data.id", equalTo(productGroupId.intValue()))
                    .body("data.sellerId", equalTo(sellerId.intValue()))
                    .body("data.brandId", equalTo(brandId.intValue()))
                    .body("data.categoryId", equalTo(categoryId.intValue()))
                    .body("data.productGroupName", equalTo("테스트 상품그룹"))
                    .body("data.optionType", equalTo("COMBINATION"))
                    .body("data.status", equalTo("DRAFT"))
                    // 이미지 (2개)
                    .body("data.images.size()", equalTo(2))
                    .body("data.images[0].originUrl", equalTo("https://example.com/img1.jpg"))
                    .body("data.images[0].imageType", equalTo("THUMBNAIL"))
                    .body("data.images[1].originUrl", equalTo("https://example.com/img2.jpg"))
                    .body("data.images[1].imageType", equalTo("DETAIL"))
                    // 옵션 그룹 (2개) + 옵션 값 (각 2개)
                    .body("data.optionProductMatrix.optionGroups.size()", equalTo(2))
                    .body(
                            "data.optionProductMatrix.optionGroups[0].optionValues.size()",
                            equalTo(2))
                    .body(
                            "data.optionProductMatrix.optionGroups[1].optionValues.size()",
                            equalTo(2))
                    // 상품(SKU) (4개 = 2x2 조합)
                    .body("data.optionProductMatrix.products.size()", equalTo(4))
                    .body("data.optionProductMatrix.products[0].skuCode", equalTo("SKU-RED-S"))
                    .body("data.optionProductMatrix.products[0].regularPrice", equalTo(50000))
                    .body("data.optionProductMatrix.products[0].currentPrice", equalTo(45000))
                    .body("data.optionProductMatrix.products[0].stockQuantity", equalTo(100))
                    // 설명
                    .body("data.description", notNullValue())
                    .body("data.description.content", containsString("테스트 상품 상세설명"))
                    // 고시정보
                    .body("data.productNotice", notNullValue())
                    .body("data.productNotice.entries.size()", equalTo(2));

            // ===== DB 상태 추가 검증 =====
            assertThat(productGroupRepository.findById(productGroupId)).isPresent();
            assertThat(productGroupImageRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
            assertThat(productRepository.findAll()).hasSizeGreaterThanOrEqualTo(4);
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-2] 등록 시 유효하지 않은 FK - 400 Bad Request")
        void register_InvalidForeignKey_Returns400() {
            Map<String, Object> request = createRegisterRequest();
            request.put("brandId", 999999L);

            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post(PRODUCT_GROUPS)
                    .then()
                    .statusCode(
                            anyOf(
                                    equalTo(HttpStatus.BAD_REQUEST.value()),
                                    equalTo(HttpStatus.NOT_FOUND.value())));
        }
    }

    @Nested
    @DisplayName("C1→C3→Q2: 등록 → 전체 수정 → 조회")
    class RegisterUpdateAndGetDetailTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-3] 등록 후 전체 수정 - diff 기반 이미지/옵션/상품 교체 검증")
        void registerThenFullUpdate_DiffBased_Verified() {
            // ===== Step 1: POST - 상품그룹 등록 =====
            Map<String, Object> registerRequest = createRegisterRequest();

            Response createResponse =
                    given().spec(givenSuperAdmin())
                            .body(registerRequest)
                            .when()
                            .post(PRODUCT_GROUPS);

            createResponse.then().statusCode(HttpStatus.CREATED.value());
            Long productGroupId = createResponse.jsonPath().getLong("productGroupId");

            // ===== Step 2: GET - 기존 데이터 ID 조회 (diff 수정을 위해 ID 필요) =====
            Response detailResponse =
                    given().spec(givenSuperAdmin()).when().get(PRODUCT_GROUPS_ID, productGroupId);

            detailResponse.then().statusCode(HttpStatus.OK.value());

            // 기존 이미지 ID, 옵션 그룹 ID, 상품 ID 추출
            List<Integer> existingImageIds = detailResponse.jsonPath().getList("data.images.id");
            List<Integer> existingOptionGroupIds =
                    detailResponse.jsonPath().getList("data.optionProductMatrix.optionGroups.id");
            List<Map<String, Object>> existingOptionGroups =
                    detailResponse.jsonPath().getList("data.optionProductMatrix.optionGroups");
            List<Integer> existingProductIds =
                    detailResponse.jsonPath().getList("data.optionProductMatrix.products.id");

            // ===== Step 3: PUT - 전체 수정 =====
            Map<String, Object> updateRequest = new HashMap<>();
            updateRequest.put("brandId", brandId);
            updateRequest.put("categoryId", categoryId);
            updateRequest.put("shippingPolicyId", shippingPolicyId);
            updateRequest.put("refundPolicyId", refundPolicyId);
            updateRequest.put("productGroupName", "수정된 상품그룹");
            updateRequest.put("optionType", "COMBINATION");

            // 이미지: 기존 1개 유지 + 1개 신규 추가 (기존 2번째 제거)
            updateRequest.put(
                    "images",
                    List.of(
                            Map.of(
                                    "originUrl", "https://example.com/img1.jpg",
                                    "imageType", "THUMBNAIL",
                                    "sortOrder", 0),
                            Map.of(
                                    "originUrl", "https://example.com/img3_new.jpg",
                                    "imageType", "DETAIL",
                                    "sortOrder", 1)));

            // 옵션 그룹: 기존 2개 유지하되 옵션값 변경
            List<Map<String, Object>> optionValues0 =
                    detailResponse
                            .jsonPath()
                            .getList("data.optionProductMatrix.optionGroups[0].optionValues");
            List<Map<String, Object>> optionValues1 =
                    detailResponse
                            .jsonPath()
                            .getList("data.optionProductMatrix.optionGroups[1].optionValues");

            updateRequest.put(
                    "optionGroups",
                    List.of(
                            Map.of(
                                    "sellerOptionGroupId",
                                    existingOptionGroupIds.get(0),
                                    "optionGroupName",
                                    "색상",
                                    "optionValues",
                                    List.of(
                                            Map.of(
                                                    "sellerOptionValueId",
                                                    optionValues0.get(0).get("id"),
                                                    "optionValueName",
                                                    "빨강",
                                                    "sortOrder",
                                                    0),
                                            Map.of(
                                                    "sellerOptionValueId",
                                                    optionValues0.get(1).get("id"),
                                                    "optionValueName",
                                                    "파랑",
                                                    "sortOrder",
                                                    1))),
                            Map.of(
                                    "sellerOptionGroupId",
                                    existingOptionGroupIds.get(1),
                                    "optionGroupName",
                                    "사이즈",
                                    "optionValues",
                                    List.of(
                                            Map.of(
                                                    "sellerOptionValueId",
                                                    optionValues1.get(0).get("id"),
                                                    "optionValueName",
                                                    "S",
                                                    "sortOrder",
                                                    0),
                                            Map.of(
                                                    "sellerOptionValueId",
                                                    optionValues1.get(1).get("id"),
                                                    "optionValueName",
                                                    "M",
                                                    "sortOrder",
                                                    1)))));

            // 상품: 기존 4개 중 2개 유지, 2개 변경 (가격/재고 수정)
            updateRequest.put(
                    "products",
                    List.of(
                            Map.of(
                                    "productId", existingProductIds.get(0),
                                    "skuCode", "SKU-RED-S",
                                    "regularPrice", 60000,
                                    "currentPrice", 55000,
                                    "stockQuantity", 150,
                                    "sortOrder", 0,
                                    "optionIndices", List.of(0, 0)),
                            Map.of(
                                    "productId", existingProductIds.get(1),
                                    "skuCode", "SKU-RED-M",
                                    "regularPrice", 60000,
                                    "currentPrice", 55000,
                                    "stockQuantity", 120,
                                    "sortOrder", 1,
                                    "optionIndices", List.of(0, 1)),
                            Map.of(
                                    "productId", existingProductIds.get(2),
                                    "skuCode", "SKU-BLUE-S",
                                    "regularPrice", 65000,
                                    "currentPrice", 60000,
                                    "stockQuantity", 80,
                                    "sortOrder", 2,
                                    "optionIndices", List.of(1, 0)),
                            Map.of(
                                    "productId", existingProductIds.get(3),
                                    "skuCode", "SKU-BLUE-M",
                                    "regularPrice", 65000,
                                    "currentPrice", 60000,
                                    "stockQuantity", 50,
                                    "sortOrder", 3,
                                    "optionIndices", List.of(1, 1))));

            // 설명 수정
            updateRequest.put("description", Map.of("content", "<p>수정된 상품 상세설명</p>"));

            // 고시정보 수정
            var noticeFields = noticeFieldRepository.findAll();
            updateRequest.put(
                    "notice",
                    Map.of(
                            "noticeCategoryId",
                            noticeCategoryId,
                            "entries",
                            noticeFields.stream()
                                    .map(
                                            f ->
                                                    Map.of(
                                                            "noticeFieldId",
                                                            f.getId(),
                                                            "fieldValue",
                                                            "수정된 값"))
                                    .toList()));

            given().spec(givenSuperAdmin())
                    .body(updateRequest)
                    .when()
                    .put(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // ===== Step 4: GET - 수정 내용 검증 =====
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    // 기본 정보 수정 확인
                    .body("data.productGroupName", equalTo("수정된 상품그룹"))
                    // 이미지 교체 확인 (2개, 두번째가 새 이미지)
                    .body("data.images.size()", equalTo(2))
                    .body(
                            "data.images.find { it.sortOrder == 1 }.originUrl",
                            equalTo("https://example.com/img3_new.jpg"))
                    // 상품 가격 수정 확인
                    .body("data.optionProductMatrix.products[0].regularPrice", equalTo(60000))
                    .body("data.optionProductMatrix.products[0].currentPrice", equalTo(55000))
                    .body("data.optionProductMatrix.products[0].stockQuantity", equalTo(150))
                    // 설명 수정 확인
                    .body("data.description.content", containsString("수정된 상품 상세설명"));
        }
    }

    @Nested
    @DisplayName("C1→C5: 등록 → 배치 상태 변경")
    class RegisterAndBatchStatusChangeTest {

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-4] 등록 후 DRAFT→ACTIVE 상태 전이 성공")
        void registerThenChangeStatus_DraftToActive_Success() {
            // ===== Step 1: POST - 등록 (DRAFT 상태) =====
            Response createResponse =
                    given().spec(givenSuperAdmin())
                            .body(createRegisterRequest())
                            .when()
                            .post(PRODUCT_GROUPS);

            createResponse.then().statusCode(HttpStatus.CREATED.value());
            Long productGroupId = createResponse.jsonPath().getLong("productGroupId");

            // ===== Step 2: PATCH - 상태 변경 (DRAFT → ACTIVE) =====
            Map<String, Object> statusRequest =
                    Map.of("productGroupIds", List.of(productGroupId), "targetStatus", "ACTIVE");

            given().spec(givenSellerUser(sellerOrganizationId, "product-group:write"))
                    .body(statusRequest)
                    .when()
                    .patch(PRODUCT_GROUPS_STATUS)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // ===== Step 3: GET - 상태 변경 확인 =====
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", equalTo("ACTIVE"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[FLOW-5] ACTIVE→INACTIVE→ACTIVE 상태 전이 사이클")
        void statusTransitionCycle_ActiveInactiveActive_Success() {
            // Step 1: 등록
            Response createResponse =
                    given().spec(givenSuperAdmin())
                            .body(createRegisterRequest())
                            .when()
                            .post(PRODUCT_GROUPS);

            Long productGroupId = createResponse.jsonPath().getLong("productGroupId");

            // Step 2: DRAFT → ACTIVE
            given().spec(givenSellerUser(sellerOrganizationId, "product-group:write"))
                    .body(
                            Map.of(
                                    "productGroupIds",
                                    List.of(productGroupId),
                                    "targetStatus",
                                    "ACTIVE"))
                    .when()
                    .patch(PRODUCT_GROUPS_STATUS)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 3: ACTIVE → INACTIVE
            given().spec(givenSellerUser(sellerOrganizationId, "product-group:write"))
                    .body(
                            Map.of(
                                    "productGroupIds",
                                    List.of(productGroupId),
                                    "targetStatus",
                                    "INACTIVE"))
                    .when()
                    .patch(PRODUCT_GROUPS_STATUS)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 4: INACTIVE → ACTIVE
            given().spec(givenSellerUser(sellerOrganizationId, "product-group:write"))
                    .body(
                            Map.of(
                                    "productGroupIds",
                                    List.of(productGroupId),
                                    "targetStatus",
                                    "ACTIVE"))
                    .when()
                    .patch(PRODUCT_GROUPS_STATUS)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 검증
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", equalTo("ACTIVE"));
        }

        @Test
        @DisplayName("[FLOW-6] 복수 상품그룹 배치 상태 변경")
        void batchStatusChange_MultipleProductGroups_Success() {
            // 2개 상품그룹 등록
            Response first =
                    given().spec(givenSuperAdmin())
                            .body(createRegisterRequest())
                            .when()
                            .post(PRODUCT_GROUPS);
            Long id1 = first.jsonPath().getLong("productGroupId");

            Response second =
                    given().spec(givenSuperAdmin())
                            .body(createRegisterRequest())
                            .when()
                            .post(PRODUCT_GROUPS);
            Long id2 = second.jsonPath().getLong("productGroupId");

            // 배치 상태 변경
            given().spec(givenSellerUser(sellerOrganizationId, "product-group:write"))
                    .body(Map.of("productGroupIds", List.of(id1, id2), "targetStatus", "ACTIVE"))
                    .when()
                    .patch(PRODUCT_GROUPS_STATUS)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // 각각 조회 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, id1)
                    .then()
                    .body("data.status", equalTo("ACTIVE"));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, id2)
                    .then()
                    .body("data.status", equalTo("ACTIVE"));
        }
    }

    @Nested
    @DisplayName("C4: 기본 정보 수정")
    class UpdateBasicInfoTest {

        @Test
        @DisplayName("[FLOW-7] 기본 정보만 수정 - 이미지/옵션/상품 변경 없음 확인")
        void updateBasicInfo_OnlyNameChanged_OtherSubAggregatesUnchanged() {
            // Step 1: 등록
            Response createResponse =
                    given().spec(givenSuperAdmin())
                            .body(createRegisterRequest())
                            .when()
                            .post(PRODUCT_GROUPS);
            Long productGroupId = createResponse.jsonPath().getLong("productGroupId");

            // Step 2: 기본 정보 수정
            Map<String, Object> basicInfoRequest = new HashMap<>();
            basicInfoRequest.put("productGroupName", "기본 정보만 수정됨");
            basicInfoRequest.put("brandId", brandId);
            basicInfoRequest.put("categoryId", categoryId);
            basicInfoRequest.put("shippingPolicyId", shippingPolicyId);
            basicInfoRequest.put("refundPolicyId", refundPolicyId);

            given().spec(givenSuperAdmin())
                    .body(basicInfoRequest)
                    .when()
                    .patch(PRODUCT_GROUPS_BASIC_INFO, productGroupId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 3: 조회 - 이름은 변경, 나머지 유지 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.productGroupName", equalTo("기본 정보만 수정됨"))
                    // 이미지 변경 없음
                    .body("data.images.size()", equalTo(2))
                    // 옵션 변경 없음
                    .body("data.optionProductMatrix.optionGroups.size()", equalTo(2))
                    // 상품 변경 없음
                    .body("data.optionProductMatrix.products.size()", equalTo(4));
        }
    }

    @Nested
    @DisplayName("인증/인가 테스트")
    @Tag("auth")
    class AuthorizationTest {

        @Test
        @DisplayName("[AUTH-1] 비인증 요청으로 등록 시도 - 401")
        void register_Unauthenticated_Returns401() {
            given().spec(givenUnauthenticated())
                    .body(createRegisterRequest())
                    .when()
                    .post(PRODUCT_GROUPS)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("[AUTH-2] 비인증 요청으로 상세 조회 시도 - 401")
        void getDetail_Unauthenticated_Returns401() {
            given().spec(givenUnauthenticated())
                    .when()
                    .get(PRODUCT_GROUPS_ID, 1L)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
