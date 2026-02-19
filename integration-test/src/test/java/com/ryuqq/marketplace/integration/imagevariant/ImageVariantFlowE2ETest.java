package com.ryuqq.marketplace.integration.imagevariant;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.repository.ImageTransformOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.ImageVariantJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.repository.ImageVariantJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
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
 * ImageVariant 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - POST /image-variants/product-groups/{productGroupId}/transform - 수동 변환 요청 (202) -
 * GET /image-variants/product-groups/{productGroupId}/images/{imageId} - Variant 목록 조회
 *
 * <p>Phase 4 (LOW): @PreAuthorize("@access.isSellerOwnerOr(...)") 권한 체크
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("image-variant")
@Tag("flow")
@DisplayName("[E2E] ImageVariant 통합 플로우 테스트")
class ImageVariantFlowE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/image-variants";

    @Autowired private ImageVariantJpaRepository imageVariantRepository;
    @Autowired private ImageTransformOutboxJpaRepository imageTransformOutboxRepository;

    @BeforeEach
    void setUp() {
        imageVariantRepository.deleteAll();
        imageTransformOutboxRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        imageVariantRepository.deleteAll();
        imageTransformOutboxRepository.deleteAll();
    }

    // ===== POST /image-variants/product-groups/{id}/transform =====

    @Nested
    @DisplayName("POST /image-variants/product-groups/{id}/transform - 수동 변환 요청")
    class RequestTransformTest {

        @Test
        @Tag("P1")
        @DisplayName("[C1-1] SuperAdmin 변환 요청 → 202 Accepted")
        void requestTransform_superAdmin_returns202() {
            // given: 존재하지 않는 productGroupId로도 202 반환 (업로드 이미지 없으면 빈 처리)
            Map<String, Object> request = new HashMap<>();
            request.put("variantTypes", List.of("SMALL_WEBP", "MEDIUM_WEBP"));

            // when & then
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post(BASE_URL + "/product-groups/{productGroupId}/transform", 99999)
                    .then()
                    .statusCode(HttpStatus.ACCEPTED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-2] Request body 없이 요청 → 202 Accepted (전체 타입 대상)")
        void requestTransform_noBody_returns202() {
            // when & then: body 없이 요청 (전체 Variant 타입 대상)
            given().spec(givenSuperAdmin())
                    .when()
                    .post(BASE_URL + "/product-groups/{productGroupId}/transform", 99999)
                    .then()
                    .statusCode(HttpStatus.ACCEPTED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-3] 비인증 사용자 → 401 Unauthorized")
        void requestTransform_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .post(BASE_URL + "/product-groups/{productGroupId}/transform", 1)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // ===== GET /image-variants/product-groups/{id}/images/{imageId} =====

    @Nested
    @DisplayName("GET /image-variants/product-groups/{id}/images/{imageId} - Variant 목록 조회")
    class GetVariantsByImageIdTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-1] 데이터 존재 시 Variant 목록 조회")
        void getVariantsByImageId_withData_returnsVariantList() {
            // given: sourceImageId=200L인 Variant 3건 저장
            Long sourceImageId = 200L;
            imageVariantRepository.save(
                    ImageVariantJpaEntityFixtures.newEntityWithSourceImageId(sourceImageId));
            imageVariantRepository.save(
                    ImageVariantJpaEntityFixtures.newEntityWithSourceImageId(sourceImageId));
            imageVariantRepository.save(
                    ImageVariantJpaEntityFixtures.newEntityWithSourceImageId(sourceImageId));

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(
                            BASE_URL + "/product-groups/{productGroupId}/images/{imageId}",
                            1,
                            sourceImageId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.size()", equalTo(3))
                    .body("data[0].variantType", notNullValue())
                    .body("data[0].variantUrl", notNullValue())
                    .body("data[0].width", notNullValue())
                    .body("data[0].height", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-2] 데이터 없을 때 빈 목록")
        void getVariantsByImageId_noData_returnsEmptyList() {
            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/product-groups/{productGroupId}/images/{imageId}", 1, 99999)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", empty());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-3] 다른 sourceImageId의 Variant는 조회되지 않음")
        void getVariantsByImageId_differentSourceImageId_notIncluded() {
            // given: sourceImageId=300L에 2건, sourceImageId=301L에 1건
            imageVariantRepository.save(
                    ImageVariantJpaEntityFixtures.newEntityWithSourceImageId(300L));
            imageVariantRepository.save(
                    ImageVariantJpaEntityFixtures.newEntityWithSourceImageId(300L));
            imageVariantRepository.save(
                    ImageVariantJpaEntityFixtures.newEntityWithSourceImageId(301L));

            // when & then: sourceImageId=300L 조회 → 2건만
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/product-groups/{productGroupId}/images/{imageId}", 1, 300)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.size()", equalTo(2));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-4] 비인증 사용자 → 401 Unauthorized")
        void getVariantsByImageId_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL + "/product-groups/{productGroupId}/images/{imageId}", 1, 100)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
