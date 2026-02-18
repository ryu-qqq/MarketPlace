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
 * Category Preset Command 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - POST /category-presets - 카테고리 프리셋 등록 - PUT /category-presets/{id} - 카테고리 프리셋 수정 -
 * DELETE /category-presets - 카테고리 프리셋 벌크 비활성화
 *
 * <p>시나리오: - P0: 10개 시나리오 (필수 기능) - P1: 2개 시나리오 (중요 기능)
 *
 * <p>참고: DELETE API는 실제 삭제가 아닌 INACTIVE 비활성화 처리입니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("categorypreset")
@Tag("command")
@DisplayName("Category Preset Command API E2E 테스트")
class CategoryPresetCommandE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/category-presets";
    private static final String TEST_CATEGORY_CODE = "TEST-CAT-001";

    @Autowired private CategoryPresetJpaRepository categoryPresetRepository;
    @Autowired private SalesChannelJpaRepository salesChannelRepository;
    @Autowired private SalesChannelCategoryJpaRepository salesChannelCategoryRepository;
    @Autowired private ShopJpaRepository shopRepository;
    @Autowired private CategoryJpaRepository categoryRepository;

    private Long shopId;
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

        // Command 테스트용 의존성 생성
        var channel = salesChannelRepository.save(SalesChannelJpaEntityFixtures.activeEntity());
        // Shop의 salesChannelId가 실제 channel.getId()와 일치해야 함
        var shop =
                shopRepository.save(
                        ShopJpaEntityFixtures.activeEntityWithSalesChannelId(channel.getId()));
        // SalesChannelCategory 코드를 TEST_CATEGORY_CODE로 맞춤
        salesChannelCategoryRepository.save(
                SalesChannelCategoryJpaEntityFixtures.entityWithExternalCode(
                        channel.getId(), TEST_CATEGORY_CODE));

        // Category 데이터 저장 (validateInternalCategoriesExist 검증용)
        var c1 = categoryRepository.save(CategoryJpaEntityFixtures.newEntity());
        var c2 = categoryRepository.save(CategoryJpaEntityFixtures.newEntity());
        var c3 = categoryRepository.save(CategoryJpaEntityFixtures.newEntity());

        shopId = shop.getId();
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

    // ===== POST /category-presets - 카테고리 프리셋 등록 =====

    @Nested
    @DisplayName("POST /category-presets - 카테고리 프리셋 등록")
    class RegisterCategoryPresetTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1-1] 생성 성공 - 유효한 요청으로 생성")
        void registerCategoryPreset_validRequest_returns201() {
            // given
            Map<String, Object> request = createRegisterRequest("식품 - 과자류 전송용");

            // when
            Response response = given().spec(givenSuperAdmin()).body(request).when().post(BASE_URL);

            // then
            response.then().statusCode(HttpStatus.CREATED.value()).body("data.id", greaterThan(0));

            Long createdId = response.jsonPath().getLong("data.id");
            assertThat(categoryPresetRepository.findById(createdId)).isPresent();

            var saved = categoryPresetRepository.findById(createdId).orElseThrow();
            assertThat(saved.getPresetName()).isEqualTo("식품 - 과자류 전송용");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-2] 필수 필드 누락 - shopId null → 400")
        void registerCategoryPreset_missingShopId_returns400() {
            // given: shopId 누락
            Map<String, Object> request =
                    Map.of(
                            "presetName",
                            "식품 - 과자류 전송용",
                            "categoryCode",
                            TEST_CATEGORY_CODE,
                            "internalCategoryIds",
                            List.of(cat1Id, cat2Id));

            // when & then
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-3] 필수 필드 누락 - presetName blank → 400")
        void registerCategoryPreset_blankPresetName_returns400() {
            // given: presetName 빈 문자열
            Map<String, Object> request =
                    Map.of(
                            "shopId",
                            shopId,
                            "presetName",
                            "",
                            "categoryCode",
                            TEST_CATEGORY_CODE,
                            "internalCategoryIds",
                            List.of(cat1Id, cat2Id));

            // when & then
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-4] 필수 필드 누락 - internalCategoryIds 빈 리스트 → 400")
        void registerCategoryPreset_emptyInternalCategoryIds_returns400() {
            // given: internalCategoryIds 빈 리스트
            Map<String, Object> request =
                    Map.of(
                            "shopId",
                            shopId,
                            "presetName",
                            "식품 - 과자류 전송용",
                            "categoryCode",
                            TEST_CATEGORY_CODE,
                            "internalCategoryIds",
                            List.of());

            // when & then
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-5] 권한 없는 사용자 - superAdmin 아님 → 403")
        void registerCategoryPreset_notSuperAdmin_returns403() {
            // given
            Map<String, Object> request = createRegisterRequest("식품 - 과자류 전송용");

            // when & then
            given().spec(givenAuthenticatedUser())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== PUT /category-presets/{id} - 카테고리 프리셋 수정 =====

    @Nested
    @DisplayName("PUT /category-presets/{id} - 카테고리 프리셋 수정")
    class UpdateCategoryPresetTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2-1] 수정 성공 - 존재하는 프리셋 수정")
        void updateCategoryPreset_existingPreset_returns204() {
            // given: 기존 프리셋 생성
            var preset =
                    categoryPresetRepository.save(
                            CategoryPresetJpaEntityFixtures.activeEntityWithShopId(shopId));
            Long presetId = preset.getId();

            Map<String, Object> request = createUpdateRequest("수정된 과자류 전송용");

            // when
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{id}", presetId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // then: DB 검증
            var updated = categoryPresetRepository.findById(presetId).orElseThrow();
            assertThat(updated.getPresetName()).isEqualTo("수정된 과자류 전송용");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-2] 존재하지 않는 ID → 404")
        void updateCategoryPreset_nonExistingId_returns404() {
            // given: 존재하지 않는 ID
            Map<String, Object> request = createUpdateRequest("수정된 과자류 전송용");

            // when & then
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{id}", 99999L)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-3] 권한 없는 사용자 - superAdmin 아님 → 403")
        void updateCategoryPreset_notSuperAdmin_returns403() {
            // given: 기존 프리셋 생성
            var preset =
                    categoryPresetRepository.save(
                            CategoryPresetJpaEntityFixtures.activeEntityWithShopId(shopId));

            Map<String, Object> request = createUpdateRequest("수정된 과자류 전송용");

            // when & then
            given().spec(givenAuthenticatedUser())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{id}", preset.getId())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== DELETE /category-presets - 카테고리 프리셋 벌크 비활성화 =====

    @Nested
    @DisplayName("DELETE /category-presets - 카테고리 프리셋 벌크 비활성화")
    class DeleteCategoryPresetsTest {

        @Test
        @Tag("P0")
        @DisplayName("[C3-1] 삭제 성공 - 복수 프리셋 비활성화")
        void deleteCategoryPresets_validIds_returns200() {
            // given: 프리셋 2건 저장
            var preset1 =
                    categoryPresetRepository.save(
                            CategoryPresetJpaEntityFixtures.activeEntityWithShopId(shopId));
            var preset2 =
                    categoryPresetRepository.save(
                            CategoryPresetJpaEntityFixtures.activeEntityWithShopId(shopId));

            Map<String, Object> request = Map.of("ids", List.of(preset1.getId(), preset2.getId()));

            // when
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .delete(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.deletedCount", equalTo(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-2] 존재하지 않는 ID 포함 - 존재하는 것만 비활성화 후 200")
        void deleteCategoryPresets_nonExistingId_returns200() {
            // given: 존재하는 ID 1개 + 존재하지 않는 ID 1개
            var preset1 =
                    categoryPresetRepository.save(
                            CategoryPresetJpaEntityFixtures.activeEntityWithShopId(shopId));

            // 존재하지 않는 ID 99999L은 무시됨 (BrandPreset과 동일한 동작)
            Map<String, Object> request = Map.of("ids", List.of(preset1.getId(), 99999L));

            // when & then: 존재하는 것만 비활성화 후 200
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .delete(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.deletedCount", equalTo(1));
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-3] 빈 ID 리스트 → 400")
        void deleteCategoryPresets_emptyIds_returns400() {
            // given: ids 빈 리스트
            Map<String, Object> request = Map.of("ids", List.of());

            // when & then
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .delete(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C3-4] 권한 없는 사용자 - superAdmin 아님 → 403")
        void deleteCategoryPresets_notSuperAdmin_returns403() {
            // given: 프리셋 1건 저장
            var preset1 =
                    categoryPresetRepository.save(
                            CategoryPresetJpaEntityFixtures.activeEntityWithShopId(shopId));

            Map<String, Object> request = Map.of("ids", List.of(preset1.getId()));

            // when & then
            given().spec(givenAuthenticatedUser())
                    .body(request)
                    .when()
                    .delete(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
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
