package com.ryuqq.marketplace.integration.brandpreset;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.brand.BrandJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.BrandPresetJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository.BrandPresetJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.SalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository.SalesChannelJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.SalesChannelBrandJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.repository.SalesChannelBrandJpaRepository;
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
 * Brand Preset Command 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - POST /brand-presets - 브랜드 프리셋 등록 - PUT /brand-presets/{id} - 브랜드 프리셋 수정 - DELETE
 * /brand-presets - 브랜드 프리셋 벌크 삭제
 *
 * <p>시나리오: - P0: 10개 시나리오 (필수 기능) - P1: 2개 시나리오 (중요 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("brandpreset")
@Tag("command")
@DisplayName("Brand Preset Command API E2E 테스트")
class BrandPresetCommandE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/brand-presets";

    @Autowired private BrandPresetJpaRepository brandPresetRepository;
    @Autowired private SalesChannelJpaRepository salesChannelRepository;
    @Autowired private SalesChannelBrandJpaRepository salesChannelBrandRepository;
    @Autowired private ShopJpaRepository shopRepository;
    @Autowired private BrandJpaRepository brandRepository;

    private Long shopId;
    private Long salesChannelBrandId;
    private Long brand1Id;
    private Long brand2Id;
    private Long brand3Id;

    @BeforeEach
    void setUp() {
        brandPresetRepository.deleteAll();
        salesChannelBrandRepository.deleteAll();
        shopRepository.deleteAll();
        salesChannelRepository.deleteAll();
        brandRepository.deleteAll();

        // Command 테스트용 의존성 생성 (등록 시 shopId, salesChannelBrandId 필요)
        var channel = salesChannelRepository.save(SalesChannelJpaEntityFixtures.activeEntity());
        // Shop의 salesChannelId가 실제 channel.getId()와 일치해야 함
        var shop =
                shopRepository.save(
                        ShopJpaEntityFixtures.activeEntityWithSalesChannelId(channel.getId()));
        var scBrand =
                salesChannelBrandRepository.save(
                        SalesChannelBrandJpaEntityFixtures.activeEntityWithSalesChannel(
                                channel.getId()));

        // Brand 데이터 저장 (validateInternalBrandsExist 검증용)
        var b1 = brandRepository.save(BrandJpaEntityFixtures.newEntity());
        var b2 = brandRepository.save(BrandJpaEntityFixtures.newEntity());
        var b3 = brandRepository.save(BrandJpaEntityFixtures.newEntity());

        shopId = shop.getId();
        salesChannelBrandId = scBrand.getId();
        brand1Id = b1.getId();
        brand2Id = b2.getId();
        brand3Id = b3.getId();
    }

    @AfterEach
    void tearDown() {
        brandPresetRepository.deleteAll();
        salesChannelBrandRepository.deleteAll();
        shopRepository.deleteAll();
        salesChannelRepository.deleteAll();
        brandRepository.deleteAll();
    }

    // ===== POST /brand-presets - 브랜드 프리셋 등록 =====

    @Nested
    @DisplayName("POST /brand-presets - 브랜드 프리셋 등록")
    class RegisterBrandPresetTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1-1] 생성 성공 - 유효한 요청으로 생성")
        void registerBrandPreset_validRequest_returns201() {
            // given
            Map<String, Object> request = createRegisterRequest("나이키 전송용");

            // when
            Response response = given().spec(givenSuperAdmin()).body(request).when().post(BASE_URL);

            // then
            response.then().statusCode(HttpStatus.CREATED.value()).body("data.id", greaterThan(0));

            Long createdId = response.jsonPath().getLong("data.id");
            assertThat(brandPresetRepository.findById(createdId)).isPresent();

            var saved = brandPresetRepository.findById(createdId).orElseThrow();
            assertThat(saved.getPresetName()).isEqualTo("나이키 전송용");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-2] 필수 필드 누락 - shopId null → 400")
        void registerBrandPreset_missingShopId_returns400() {
            // given: shopId 누락
            Map<String, Object> request =
                    Map.of(
                            "salesChannelBrandId",
                            salesChannelBrandId,
                            "presetName",
                            "나이키 전송용",
                            "internalBrandIds",
                            List.of(brand1Id, brand2Id));

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
        void registerBrandPreset_blankPresetName_returns400() {
            // given: presetName 빈 문자열
            Map<String, Object> request =
                    Map.of(
                            "shopId",
                            shopId,
                            "salesChannelBrandId",
                            salesChannelBrandId,
                            "presetName",
                            "",
                            "internalBrandIds",
                            List.of(brand1Id, brand2Id));

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
        @DisplayName("[C1-4] 필수 필드 누락 - internalBrandIds 빈 리스트 → 400")
        void registerBrandPreset_emptyInternalBrandIds_returns400() {
            // given: internalBrandIds 빈 리스트
            Map<String, Object> request =
                    Map.of(
                            "shopId",
                            shopId,
                            "salesChannelBrandId",
                            salesChannelBrandId,
                            "presetName",
                            "나이키 전송용",
                            "internalBrandIds",
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
        void registerBrandPreset_notSuperAdmin_returns403() {
            // given
            Map<String, Object> request = createRegisterRequest("나이키 전송용");

            // when & then
            given().spec(givenAuthenticatedUser())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== PUT /brand-presets/{id} - 브랜드 프리셋 수정 =====

    @Nested
    @DisplayName("PUT /brand-presets/{id} - 브랜드 프리셋 수정")
    class UpdateBrandPresetTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2-1] 수정 성공 - 존재하는 프리셋 수정")
        void updateBrandPreset_existingPreset_returns204() {
            // given: 기존 프리셋 생성
            var preset =
                    brandPresetRepository.save(
                            BrandPresetJpaEntityFixtures.activeEntityWithShopId(shopId));
            Long presetId = preset.getId();

            Map<String, Object> request = createUpdateRequest("수정된 프리셋 이름");

            // when
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{id}", presetId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // then: DB 검증
            var updated = brandPresetRepository.findById(presetId).orElseThrow();
            assertThat(updated.getPresetName()).isEqualTo("수정된 프리셋 이름");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-2] 존재하지 않는 ID → 404")
        void updateBrandPreset_nonExistingId_returns404() {
            // given: 존재하지 않는 ID
            Map<String, Object> request = createUpdateRequest("수정된 프리셋 이름");

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
        void updateBrandPreset_notSuperAdmin_returns403() {
            // given: 기존 프리셋 생성
            var preset =
                    brandPresetRepository.save(
                            BrandPresetJpaEntityFixtures.activeEntityWithShopId(shopId));

            Map<String, Object> request = createUpdateRequest("수정된 프리셋 이름");

            // when & then
            given().spec(givenAuthenticatedUser())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{id}", preset.getId())
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== DELETE /brand-presets - 브랜드 프리셋 벌크 삭제 =====

    @Nested
    @DisplayName("DELETE /brand-presets - 브랜드 프리셋 벌크 삭제")
    class DeleteBrandPresetsTest {

        @Test
        @Tag("P0")
        @DisplayName("[C3-1] 삭제 성공 - 복수 프리셋 삭제")
        void deleteBrandPresets_validIds_returns200() {
            // given: 프리셋 2건 저장
            var preset1 =
                    brandPresetRepository.save(
                            BrandPresetJpaEntityFixtures.activeEntityWithShopId(shopId));
            var preset2 =
                    brandPresetRepository.save(
                            BrandPresetJpaEntityFixtures.activeEntityWithShopId(shopId));

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
        @DisplayName("[C3-2] 존재하지 않는 ID 포함 - 존재하는 것만 삭제 후 200")
        void deleteBrandPresets_nonExistingId_returns200() {
            // given: 존재하는 ID 1개 + 존재하지 않는 ID 1개
            var preset1 =
                    brandPresetRepository.save(
                            BrandPresetJpaEntityFixtures.activeEntityWithShopId(shopId));

            Map<String, Object> request = Map.of("ids", List.of(preset1.getId()));

            // when & then: 존재하는 것만 삭제
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
        void deleteBrandPresets_emptyIds_returns400() {
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
        void deleteBrandPresets_notSuperAdmin_returns403() {
            // given: 프리셋 1건 저장
            var preset1 =
                    brandPresetRepository.save(
                            BrandPresetJpaEntityFixtures.activeEntityWithShopId(shopId));

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
                "shopId", shopId,
                "salesChannelBrandId", salesChannelBrandId,
                "presetName", presetName,
                "internalBrandIds", List.of(brand1Id, brand2Id, brand3Id));
    }

    private Map<String, Object> createUpdateRequest(String presetName) {
        return Map.of(
                "presetName", presetName,
                "salesChannelBrandId", salesChannelBrandId,
                "internalBrandIds", List.of(brand1Id, brand2Id));
    }
}
