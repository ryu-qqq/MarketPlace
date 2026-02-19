package com.ryuqq.marketplace.integration.productgroup;

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
 * ProductGroup 하위 도메인(Description, Image, Notice) 개별 수정 및 배치 등록 E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>Description: PUT /{id}/description → GET /{id}/description/publish-status
 *   <li>Image: PUT /{id}/images → GET /{id}/images/upload-status
 *   <li>Notice: PUT /{id}/notice
 *   <li>Batch: POST /product-groups/batch
 * </ul>
 */
@Tag("e2e")
@Tag("productgroup")
@Tag("subdomain")
@DisplayName("ProductGroup 하위 도메인 E2E 테스트")
class ProductGroupSubDomainE2ETest extends E2ETestBase {

    private static final String PRODUCT_GROUPS = "/product-groups";
    private static final String PRODUCT_GROUPS_ID = "/product-groups/{productGroupId}";
    private static final String DESCRIPTION = "/product-groups/{productGroupId}/description";
    private static final String DESCRIPTION_PUBLISH_STATUS =
            "/product-groups/{productGroupId}/description/publish-status";
    private static final String IMAGES = "/product-groups/{productGroupId}/images";
    private static final String IMAGES_UPLOAD_STATUS =
            "/product-groups/{productGroupId}/images/upload-status";
    private static final String NOTICE = "/product-groups/{productGroupId}/notice";
    private static final String BATCH = "/product-groups/batch";

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
        sellerId = sellerRepository.save(SellerJpaEntityFixtures.activeEntityWithAuth()).getId();
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

    /** 상품그룹을 API로 등록하고 ID를 반환합니다. */
    private Long registerProductGroup() {
        Map<String, Object> request = createRegisterRequest();
        Response response =
                given().spec(givenSuperAdmin()).body(request).when().post(PRODUCT_GROUPS);
        response.then().statusCode(HttpStatus.CREATED.value());
        return response.jsonPath().getLong("productGroupId");
    }

    private Map<String, Object> createRegisterRequest() {
        var noticeFields = noticeFieldRepository.findAll();

        Map<String, Object> request = new HashMap<>();
        request.put("sellerId", sellerId);
        request.put("brandId", brandId);
        request.put("categoryId", categoryId);
        request.put("shippingPolicyId", shippingPolicyId);
        request.put("refundPolicyId", refundPolicyId);
        request.put("productGroupName", "서브도메인 테스트 상품그룹");
        request.put("optionType", "SINGLE");
        request.put(
                "images",
                List.of(
                        Map.of(
                                "originUrl",
                                "https://example.com/img1.jpg",
                                "imageType",
                                "THUMBNAIL",
                                "sortOrder",
                                0)));
        request.put(
                "optionGroups",
                List.of(
                        Map.of(
                                "optionGroupName",
                                "색상",
                                "optionValues",
                                List.of(Map.of("optionValueName", "빨강", "sortOrder", 0)))));
        request.put(
                "products",
                List.of(
                        Map.of(
                                "skuCode",
                                "SKU-SUB-001",
                                "regularPrice",
                                30000,
                                "currentPrice",
                                25000,
                                "stockQuantity",
                                50,
                                "sortOrder",
                                0,
                                "optionIndices",
                                List.of(0))));
        request.put("description", Map.of("content", "<p>초기 상세설명</p>"));
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

    // ===== Description 테스트 =====

    @Nested
    @DisplayName("Description: 상세 설명 수정 → 발행 상태 조회")
    class DescriptionTest {

        @Test
        @Tag("P1")
        @DisplayName("[DESC-1] 상세 설명 수정 후 발행 상태 조회")
        void updateDescription_ThenGetPublishStatus_Success() {
            Long productGroupId = registerProductGroup();

            // Step 1: PUT - 상세 설명 수정
            given().spec(givenSuperAdmin())
                    .body(Map.of("content", "<p>수정된 상세 설명입니다</p>"))
                    .when()
                    .put(DESCRIPTION, productGroupId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 2: GET - 발행 상태 조회
            given().spec(givenSuperAdmin())
                    .when()
                    .get(DESCRIPTION_PUBLISH_STATUS, productGroupId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.productGroupId", equalTo(productGroupId.intValue()))
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[DESC-2] 빈 content로 수정 시 400 Bad Request")
        void updateDescription_EmptyContent_Returns400() {
            Long productGroupId = registerProductGroup();

            given().spec(givenSuperAdmin())
                    .body(Map.of("content", ""))
                    .when()
                    .put(DESCRIPTION, productGroupId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @DisplayName("[DESC-3] 존재하지 않는 상품그룹 상세 설명 수정 시 404")
        void updateDescription_NonExistentProductGroup_Returns404() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("content", "<p>설명</p>"))
                    .when()
                    .put(DESCRIPTION, 999999L)
                    .then()
                    .statusCode(
                            anyOf(
                                    equalTo(HttpStatus.NOT_FOUND.value()),
                                    equalTo(HttpStatus.BAD_REQUEST.value())));
        }
    }

    // ===== Image 테스트 =====

    @Nested
    @DisplayName("Image: 이미지 수정 → 업로드 상태 조회")
    class ImageTest {

        @Test
        @Tag("P1")
        @DisplayName("[IMG-1] 이미지 전체 교체 후 업로드 상태 조회")
        void updateImages_ThenGetUploadStatus_Success() {
            Long productGroupId = registerProductGroup();

            // Step 1: PUT - 이미지 전체 교체 (기존 1개 → 3개로 변경)
            Map<String, Object> imageRequest =
                    Map.of(
                            "images",
                            List.of(
                                    Map.of(
                                            "imageType",
                                            "THUMBNAIL",
                                            "originUrl",
                                            "https://example.com/new_main.jpg",
                                            "sortOrder",
                                            0),
                                    Map.of(
                                            "imageType",
                                            "DETAIL",
                                            "originUrl",
                                            "https://example.com/new_sub1.jpg",
                                            "sortOrder",
                                            1),
                                    Map.of(
                                            "imageType",
                                            "DETAIL",
                                            "originUrl",
                                            "https://example.com/new_sub2.jpg",
                                            "sortOrder",
                                            2)));

            given().spec(givenSuperAdmin())
                    .body(imageRequest)
                    .when()
                    .put(IMAGES, productGroupId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 2: GET - 업로드 상태 조회
            given().spec(givenSuperAdmin())
                    .when()
                    .get(IMAGES_UPLOAD_STATUS, productGroupId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.productGroupId", equalTo(productGroupId.intValue()))
                    .body("data", notNullValue());

            // Step 3: 상세 조회로 이미지 교체 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.images.size()", equalTo(3))
                    .body("data.images[0].originUrl", equalTo("https://example.com/new_main.jpg"));
        }

        @Test
        @DisplayName("[IMG-2] 빈 이미지 목록으로 수정 시 - images null")
        void updateImages_NullImages_Returns400() {
            Long productGroupId = registerProductGroup();

            given().spec(givenSuperAdmin())
                    .body("{}")
                    .when()
                    .put(IMAGES, productGroupId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== Notice 테스트 =====

    @Nested
    @DisplayName("Notice: 고시정보 수정")
    class NoticeTest {

        @Test
        @Tag("P1")
        @DisplayName("[NOTICE-1] 고시정보 수정 → 상세 조회로 변경 확인")
        void updateNotice_ThenGetDetail_Updated() {
            Long productGroupId = registerProductGroup();
            var noticeFields = noticeFieldRepository.findAll();

            // Step 1: PUT - 고시정보 수정
            Map<String, Object> noticeRequest =
                    Map.of(
                            "noticeCategoryId",
                            noticeCategoryId,
                            "entries",
                            List.of(
                                    Map.of(
                                            "noticeFieldId",
                                            noticeFields.get(0).getId(),
                                            "fieldValue",
                                            "폴리에스터 100%"),
                                    Map.of(
                                            "noticeFieldId",
                                            noticeFields.get(1).getId(),
                                            "fieldValue",
                                            "중국")));

            given().spec(givenSuperAdmin())
                    .body(noticeRequest)
                    .when()
                    .put(NOTICE, productGroupId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 2: GET - 상세 조회로 고시정보 변경 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(PRODUCT_GROUPS_ID, productGroupId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.productNotice", notNullValue())
                    .body("data.productNotice.entries.size()", equalTo(2));
        }

        @Test
        @DisplayName("[NOTICE-2] noticeCategoryId null로 수정 시 400")
        void updateNotice_NullCategoryId_Returns400() {
            Long productGroupId = registerProductGroup();

            Map<String, Object> noticeRequest = new HashMap<>();
            noticeRequest.put("noticeCategoryId", null);
            noticeRequest.put("entries", List.of());

            given().spec(givenSuperAdmin())
                    .body(noticeRequest)
                    .when()
                    .put(NOTICE, productGroupId)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== Batch Registration 테스트 =====

    @Nested
    @DisplayName("Batch: 상품그룹 배치 등록")
    class BatchRegistrationTest {

        @Test
        @Tag("P1")
        @DisplayName("[BATCH-1] 배치 등록 - 2건 모두 성공")
        void batchRegister_AllSuccess() {
            Map<String, Object> item1 = createRegisterRequest();
            item1.put("productGroupName", "배치 상품그룹 1");

            Map<String, Object> item2 = createRegisterRequest();
            item2.put("productGroupName", "배치 상품그룹 2");

            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of(item1, item2)))
                    .when()
                    .post(BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalCount", equalTo(2))
                    .body("successCount", equalTo(2))
                    .body("failureCount", equalTo(0))
                    .body("results.size()", equalTo(2))
                    .body("results[0].success", equalTo(true))
                    .body("results[0].productGroupId", notNullValue())
                    .body("results[1].success", equalTo(true))
                    .body("results[1].productGroupId", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[BATCH-2] 배치 등록 - 유효하지 않은 FK 포함 시 partial failure")
        void batchRegister_InvalidFk_PartialFailure() {
            Map<String, Object> validItem = createRegisterRequest();
            validItem.put("productGroupName", "유효한 상품그룹");

            Map<String, Object> invalidItem = createRegisterRequest();
            invalidItem.put("productGroupName", "유효하지 않은 상품그룹");
            invalidItem.put("brandId", 999999L);

            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of(validItem, invalidItem)))
                    .when()
                    .post(BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("totalCount", equalTo(2))
                    .body("successCount", equalTo(1))
                    .body("failureCount", equalTo(1))
                    .body("results.find { it.index == 0 }.success", equalTo(true))
                    .body("results.find { it.index == 1 }.success", equalTo(false))
                    .body("results.find { it.index == 1 }.errorMessage", notNullValue());
        }

        @Test
        @DisplayName("[BATCH-3] 빈 items 목록으로 배치 등록 시 400")
        void batchRegister_EmptyItems_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("items", List.of()))
                    .when()
                    .post(BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }
}
