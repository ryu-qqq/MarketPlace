package com.ryuqq.marketplace.integration.categorypreset;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.CategoryPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository.CategoryPresetJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.SalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository.SalesChannelJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.SalesChannelCategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.repository.SalesChannelCategoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.repository.ShopJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Category Preset Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /category-presets - 카테고리 프리셋 목록 조회 - GET /category-presets/{id} - 카테고리 프리셋 상세 조회
 *
 * <p>시나리오: - P0: 8개 시나리오 (필수 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("categorypreset")
@Tag("query")
@DisplayName("Category Preset Query API E2E 테스트")
class CategoryPresetQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/category-presets";

    @Autowired private CategoryPresetJpaRepository categoryPresetRepository;
    @Autowired private SalesChannelJpaRepository salesChannelRepository;
    @Autowired private SalesChannelCategoryJpaRepository salesChannelCategoryRepository;
    @Autowired private ShopJpaRepository shopRepository;

    private Long shopId;
    private Long salesChannelCategoryId;

    @BeforeEach
    void setUp() {
        categoryPresetRepository.deleteAll();
        salesChannelCategoryRepository.deleteAll();
        shopRepository.deleteAll();
        salesChannelRepository.deleteAll();

        // CategoryPreset QueryDSL JOIN 의존성 생성
        // JOIN 경로: categoryPreset.shopId → shop.id, shop.salesChannelId → salesChannel.id
        var channel = salesChannelRepository.save(SalesChannelJpaEntityFixtures.activeEntity());
        Instant now = Instant.now();
        var shop =
                shopRepository.save(
                        ShopJpaEntity.create(
                                null,
                                channel.getId(),
                                "테스트 외부몰",
                                "test-account",
                                "ACTIVE",
                                null,
                                null,
                                null,
                                null,
                                null,
                                now,
                                now,
                                null));
        var scCategory =
                salesChannelCategoryRepository.save(
                        SalesChannelCategoryJpaEntityFixtures.entityWithSalesChannel(
                                channel.getId()));
        shopId = shop.getId();
        salesChannelCategoryId = scCategory.getId();
    }

    @AfterEach
    void tearDown() {
        categoryPresetRepository.deleteAll();
        salesChannelCategoryRepository.deleteAll();
        shopRepository.deleteAll();
        salesChannelRepository.deleteAll();
    }

    private CategoryPresetJpaEntity savePreset() {
        Instant now = Instant.now();
        return categoryPresetRepository.save(
                CategoryPresetJpaEntity.create(
                        null, shopId, salesChannelCategoryId, "테스트 카테고리 프리셋", "ACTIVE", now, now));
    }

    // ===== GET /category-presets - 카테고리 프리셋 목록 조회 =====

    @Nested
    @DisplayName("GET /category-presets - 카테고리 프리셋 목록 조회")
    class SearchCategoryPresetsTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-1] 데이터 존재 시 정상 조회")
        void searchCategoryPresets_withData_returnsOk() {
            // given: 5건 저장
            for (int i = 0; i < 5; i++) {
                savePreset();
            }

            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "category-preset:read"))
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(5))
                    .body("data.totalElements", equalTo(5));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-2] 데이터 없을 때 빈 목록")
        void searchCategoryPresets_noData_returnsEmptyList() {
            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "category-preset:read"))
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", empty())
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-5] 권한 없는 사용자 접근")
        void searchCategoryPresets_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-6] 인증된 일반 사용자 접근")
        void searchCategoryPresets_authenticatedUser_returns200() {
            // given
            savePreset();

            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "category-preset:read"))
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(greaterThanOrEqualTo(1)));
        }
    }

    // ===== GET /category-presets/{id} - 카테고리 프리셋 상세 조회 =====

    @Nested
    @DisplayName("GET /category-presets/{id} - 카테고리 프리셋 상세 조회")
    class GetCategoryPresetTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q2-1] 존재하는 ID로 상세 조회")
        void getCategoryPreset_existingId_returns200() {
            // given
            var preset = savePreset();

            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "category-preset:read"))
                    .when()
                    .get(BASE_URL + "/{id}", preset.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.id", equalTo(preset.getId().intValue()));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q2-2] 존재하지 않는 ID → 404")
        void getCategoryPreset_nonExistingId_returns404() {
            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "category-preset:read"))
                    .when()
                    .get(BASE_URL + "/{id}", 99999)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // ===== 전체 플로우 시나리오 =====

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F2] 목록 조회 → 상세 조회 플로우")
        void fullFlow_listToDetail() {
            // Step 1: 사전 데이터 저장 (3건)
            var preset1 = savePreset();
            var preset2 = savePreset();
            var preset3 = savePreset();

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
}
