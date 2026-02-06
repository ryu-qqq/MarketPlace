package com.ryuqq.marketplace.integration.commoncodetype;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.commoncode.CommonCodeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.CommonCodeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.repository.CommonCodeJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.repository.CommonCodeTypeJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.time.Instant;
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
 * CommonCodeType 통합 플로우 E2E 테스트.
 *
 * <p>테스트 대상: - 전체 CRUD 플로우 - 비활성화 제약 플로우
 *
 * <p>우선순위: - P0: 2개 시나리오 (필수 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("commoncodetype")
@Tag("flow")
@DisplayName("CommonCodeType 통합 플로우 E2E 테스트")
class CommonCodeTypeFlowE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/common-code-types";

    @Autowired private CommonCodeTypeJpaRepository commonCodeTypeRepository;
    @Autowired private CommonCodeJpaRepository commonCodeRepository;

    @BeforeEach
    void setUp() {
        commonCodeRepository.deleteAll();
        commonCodeTypeRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        commonCodeRepository.deleteAll();
        commonCodeTypeRepository.deleteAll();
    }

    // ===== 전체 CRUD 플로우 =====

    @Nested
    @DisplayName("전체 CRUD 플로우")
    class FullCrudFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-F1-01] 전체 CRUD 플로우 - 생성 → 조회 → 수정 → 비활성화")
        void fullCrudFlow_CreateReadUpdateDeactivate_Success() {
            // Step 1: 생성
            Map<String, Object> createRequest =
                    Map.of(
                            "code", "TEST_TYPE",
                            "name", "테스트타입",
                            "description", "테스트용",
                            "displayOrder", 1);

            Response createResponse =
                    given().spec(givenAdminJson()).body(createRequest).when().post(BASE_PATH);

            createResponse.then().statusCode(HttpStatus.CREATED.value());

            Long createdId = createResponse.jsonPath().getLong("data");

            // Step 2: 목록 조회로 생성 확인
            given().spec(givenAdmin())
                    .queryParam("searchWord", "TEST_TYPE")
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1))
                    .body("data.content[0].code", equalTo("TEST_TYPE"))
                    .body("data.content[0].active", equalTo(true));

            // Step 3: 수정
            Map<String, Object> updateRequest =
                    Map.of(
                            "name", "테스트타입_수정",
                            "description", "수정된 설명",
                            "displayOrder", 2);

            given().spec(givenAdminJson())
                    .body(updateRequest)
                    .when()
                    .put(BASE_PATH + "/{id}", createdId)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 4: 목록 조회로 수정 확인
            given().spec(givenAdmin())
                    .queryParam("searchWord", "TEST_TYPE")
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content[0].name", equalTo("테스트타입_수정"))
                    .body("data.content[0].description", equalTo("수정된 설명"))
                    .body("data.content[0].displayOrder", equalTo(2));

            // Step 5: 비활성화
            Map<String, Object> deactivateRequest =
                    Map.of("ids", List.of(createdId), "active", false);

            given().spec(givenAdminJson())
                    .body(deactivateRequest)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 6: 비활성화 확인
            given().spec(givenAdmin())
                    .queryParam("active", false)
                    .queryParam("searchWord", "TEST_TYPE")
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(1))
                    .body("data.content[0].active", equalTo(false));
        }
    }

    // ===== 비활성화 제약 플로우 =====

    @Nested
    @DisplayName("비활성화 제약 플로우")
    class DeactivationConstraintFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-F2-01] 공통 코드 존재 시 타입 비활성화 제약")
        void deactivationConstraintFlow_WithActiveCommonCode_FailThenSucceed() {
            // Step 1: 공통 코드 타입 생성
            Map<String, Object> createRequest =
                    Map.of(
                            "code", "PAYMENT_METHOD",
                            "name", "결제수단",
                            "displayOrder", 1);

            Response createResponse =
                    given().spec(givenAdminJson()).body(createRequest).when().post(BASE_PATH);

            createResponse.then().statusCode(HttpStatus.CREATED.value());

            Long typeId = createResponse.jsonPath().getLong("data");

            // Step 2: 활성 공통 코드 생성 (직접 DB 삽입)
            CommonCodeJpaEntity commonCode =
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            typeId, "CREDIT_CARD", "신용카드");
            commonCodeRepository.save(commonCode);
            Long commonCodeId = commonCode.getId();

            // Step 3: 타입 비활성화 시도 (실패 예상)
            Map<String, Object> deactivateRequest = Map.of("ids", List.of(typeId), "active", false);

            given().spec(givenAdminJson())
                    .body(deactivateRequest)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            // Step 4: 공통 코드 비활성화 (직접 DB 업데이트)
            CommonCodeJpaEntity retrieved =
                    commonCodeRepository.findById(commonCodeId).orElseThrow();
            // 비활성화된 새 엔티티 생성
            CommonCodeJpaEntity deactivated =
                    CommonCodeJpaEntity.create(
                            retrieved.getId(),
                            retrieved.getCommonCodeTypeId(),
                            retrieved.getCode(),
                            retrieved.getDisplayName(),
                            retrieved.getDisplayOrder(),
                            false, // 비활성화
                            retrieved.getCreatedAt(),
                            Instant.now(),
                            null);
            commonCodeRepository.save(deactivated);

            // Step 5: 타입 비활성화 재시도 (성공 예상)
            given().spec(givenAdminJson())
                    .body(deactivateRequest)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 6: 비활성화 확인
            given().spec(givenAdmin())
                    .queryParam("active", false)
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.findAll { it.id == " + typeId + " }.size()", equalTo(1))
                    .body("data.content.findAll { it.active == false }.size()", equalTo(1));
        }
    }
}
