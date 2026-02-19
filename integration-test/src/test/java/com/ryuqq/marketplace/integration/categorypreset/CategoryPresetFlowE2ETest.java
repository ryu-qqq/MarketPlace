package com.ryuqq.marketplace.integration.categorypreset;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.category.CategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.category.repository.CategoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.CategoryPresetJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository.CategoryPresetJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.SalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository.SalesChannelJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.SalesChannelCategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.repository.SalesChannelCategoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shop.ShopJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
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
 * Category Preset 전체 플로우 E2E 테스트.
 *
 * <p>테스트 대상: CRUD 전체 플로우 및 목록 → 상세 조회 플로우
 *
 * <p>시나리오: - F1: CRUD 전체 플로우 (P0) - F2: 목록 조회 → 상세 조회 플로우 (P1)
 *
 * <p>참고: DELETE API는 실제 삭제가 아닌 INACTIVE 비활성화 처리입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("categorypreset")
@Tag("flow")
@DisplayName("Category Preset 전체 플로우 E2E 테스트")
class CategoryPresetFlowE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/category-presets";
    private static final String TEST_CATEGORY_CODE = "TEST-CAT-001";

    @Autowired private CategoryPresetJpaRepository categoryPresetRepository;
    @Autowired private SalesChannelJpaRepository salesChannelRepository;
    @Autowired private SalesChannelCategoryJpaRepository salesChannelCategoryRepository;
    @Autowired private ShopJpaRepository shopRepository;
    @Autowired private CategoryJpaRepository categoryRepository;

    private Long shopId;
    private Long salesChannelCategoryId;
    private Long cat1Id;
    private Long cat2Id;
    private Long cat3Id;

    @BeforeEach
    void setUp() {
        categoryPresetRepository.deleteAll();
        salesChannelCategoryRepository.deleteAll();
        shopRepository.deleteAll();
        salesChannelRepository.deleteAll();
        categoryRepository.deleteAll();

        var channel = salesChannelRepository.save(SalesChannelJpaEntityFixtures.activeEntity());
        // Shop의 salesChannelId가 실제 channel.getId()와 일치해야 함
        var shop =
                shopRepository.save(
                        ShopJpaEntityFixtures.activeEntityWithSalesChannelId(channel.getId()));
        // SalesChannelCategory 코드를 TEST_CATEGORY_CODE로 맞춤
        var scCategory =
                salesChannelCategoryRepository.save(
                        SalesChannelCategoryJpaEntityFixtures.entityWithExternalCode(
                                channel.getId(), TEST_CATEGORY_CODE));

        // Category 데이터 저장 (validateInternalCategoriesExist 검증용)
        var c1 = categoryRepository.save(CategoryJpaEntityFixtures.newEntity());
        var c2 = categoryRepository.save(CategoryJpaEntityFixtures.newEntity());
        var c3 = categoryRepository.save(CategoryJpaEntityFixtures.newEntity());

        shopId = shop.getId();
        salesChannelCategoryId = scCategory.getId();
        cat1Id = c1.getId();
        cat2Id = c2.getId();
        cat3Id = c3.getId();
    }

    @AfterEach
    void tearDown() {
        categoryPresetRepository.deleteAll();
        salesChannelCategoryRepository.deleteAll();
        shopRepository.deleteAll();
        salesChannelRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    // ===== 전체 플로우 시나리오 =====

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] CRUD 전체 플로우 - 생성 → 조회 → 수정 → 비활성화")
        void fullFlow_crudCycle() {
            // Step 1: 생성
            Map<String, Object> registerRequest = createRegisterRequest("식품 - 과자류 전송용");
            Response createResponse =
                    given().spec(givenSuperAdmin()).body(registerRequest).when().post(BASE_URL);

            createResponse.then().statusCode(HttpStatus.CREATED.value());
            Long createdId = createResponse.jsonPath().getLong("data.id");
            assertThat(categoryPresetRepository.findById(createdId)).isPresent();

            // Step 2: 목록 조회 - 1건 존재
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "category-preset:read"))
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", equalTo(1));

            // Step 3: 상세 조회
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "category-preset:read"))
                    .when()
                    .get(BASE_URL + "/{id}", createdId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.id", equalTo(createdId.intValue()));

            // Step 4: 수정
            Map<String, Object> updateRequest = createUpdateRequest("수정된 과자류 전송용");
            given().spec(givenSuperAdmin())
                    .body(updateRequest)
                    .when()
                    .put(BASE_URL + "/{id}", createdId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 5: 수정 확인
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "category-preset:read"))
                    .when()
                    .get(BASE_URL + "/{id}", createdId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.presetName", equalTo("수정된 과자류 전송용"));

            // Step 6: 비활성화 (DELETE API = INACTIVE 처리)
            Map<String, Object> deleteRequest = Map.of("ids", List.of(createdId));
            given().spec(givenSuperAdmin())
                    .body(deleteRequest)
                    .when()
                    .delete(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.deletedCount", equalTo(1));

            // Step 7: 비활성화 확인 - DB에서 status가 INACTIVE로 변경됨
            var deactivated = categoryPresetRepository.findById(createdId).orElseThrow();
            assertThat(deactivated.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @Tag("P1")
        @DisplayName("[F2] 목록 조회 → 상세 조회 플로우")
        void fullFlow_listToDetail() {
            // Step 1: 사전 데이터 저장 (3건) - shopId와 salesChannelCategoryId 모두 실제 FK로 설정
            categoryPresetRepository.save(
                    CategoryPresetJpaEntityFixtures.activeEntityWithShopAndSalesChannelCategory(
                            shopId, salesChannelCategoryId));
            categoryPresetRepository.save(
                    CategoryPresetJpaEntityFixtures.activeEntityWithShopAndSalesChannelCategory(
                            shopId, salesChannelCategoryId));
            categoryPresetRepository.save(
                    CategoryPresetJpaEntityFixtures.activeEntityWithShopAndSalesChannelCategory(
                            shopId, salesChannelCategoryId));

            // Step 2: 목록 조회
            var listResponse =
                    given().spec(
                                    givenAuthenticatedUser()
                                            .header("X-User-Permissions", "category-preset:read"))
                            .when()
                            .get(BASE_URL)
                            .then()
                            .statusCode(HttpStatus.OK.value())
                            .body("data.totalElements", equalTo(3))
                            .extract()
                            .response();

            // Step 3: 첫 번째 ID 추출
            Long extractedId = listResponse.jsonPath().getLong("data.content[0].id");

            // Step 4: 상세 조회
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "category-preset:read"))
                    .when()
                    .get(BASE_URL + "/{id}", extractedId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.id", equalTo(extractedId.intValue()));

            // DB 일관성 검증
            assertThat(categoryPresetRepository.findById(extractedId)).isPresent();
        }
    }

    // ===== Helper Methods =====

    private Map<String, Object> createRegisterRequest(String presetName) {
        return Map.of(
                "shopId",
                shopId,
                "presetName",
                presetName,
                "categoryCode",
                TEST_CATEGORY_CODE,
                "internalCategoryIds",
                List.of(cat1Id, cat2Id, cat3Id));
    }

    private Map<String, Object> createUpdateRequest(String presetName) {
        return Map.of(
                "presetName",
                presetName,
                "categoryCode",
                TEST_CATEGORY_CODE,
                "internalCategoryIds",
                List.of(cat1Id, cat2Id));
    }
}
