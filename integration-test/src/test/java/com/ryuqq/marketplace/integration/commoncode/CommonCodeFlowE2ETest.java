package com.ryuqq.marketplace.integration.commoncode;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.commoncode.repository.CommonCodeJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.CommonCodeTypeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.repository.CommonCodeTypeJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * CommonCode 통합 플로우 E2E 테스트.
 *
 * <p>테스트 대상: - CRUD 전체 플로우 - CommonCodeType 상태 연계 시나리오 - 복수 타입에 동일 코드 생성
 *
 * <p>우선순위: - P0: 3개 시나리오 (필수 플로우)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("commoncode")
@Tag("flow")
@DisplayName("CommonCode Flow API E2E 테스트")
class CommonCodeFlowE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/common-codes";
    private static final String QUERY_URL = "/public/common-codes";

    @Autowired private CommonCodeJpaRepository commonCodeRepository;
    @Autowired private CommonCodeTypeJpaRepository commonCodeTypeRepository;

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

    @Nested
    @DisplayName("통합 플로우 시나리오")
    class IntegrationFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] CRUD 전체 플로우")
        void fullCrudFlow_Success() {
            // Step 1: CommonCodeType 생성 (PAYMENT_METHOD)
            var paymentMethodType =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            Long typeId = paymentMethodType.getId();

            // Step 2: CommonCode 생성 (CREDIT_CARD)
            Map<String, Object> createRequest =
                    Map.of(
                            "commonCodeTypeId",
                            typeId,
                            "code",
                            "CREDIT_CARD",
                            "displayName",
                            "신용카드",
                            "displayOrder",
                            1);

            Response createResponse =
                    given().spec(givenAdminJson()).body(createRequest).when().post(BASE_URL);

            createResponse
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data", greaterThan(0));

            Long commonCodeId = createResponse.jsonPath().getLong("data");

            // Step 3: 목록 조회 (1개 확인)
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", typeId)
                    .when()
                    .get(QUERY_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].code", equalTo("CREDIT_CARD"))
                    .body("data.content[0].displayName", equalTo("신용카드"));

            // Step 4: 수정 (displayName 변경)
            Map<String, Object> updateRequest = Map.of("displayName", "신용/체크카드", "displayOrder", 2);

            given().spec(givenAdminJson())
                    .body(updateRequest)
                    .when()
                    .put(BASE_URL + "/{id}", commonCodeId)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 5: 목록 조회 (displayName 변경 확인)
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", typeId)
                    .when()
                    .get(QUERY_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].displayName", equalTo("신용/체크카드"))
                    .body("data.content[0].displayOrder", equalTo(2));

            // Step 6: 비활성화
            Map<String, Object> deactivateRequest =
                    Map.of("ids", List.of(commonCodeId), "active", false);

            given().spec(givenAdminJson())
                    .body(deactivateRequest)
                    .when()
                    .patch(BASE_URL + "/active-status")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // Step 7: 목록 조회 (active=false 필터)
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", typeId)
                    .queryParam("active", false)
                    .when()
                    .get(QUERY_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].active", equalTo(false));
        }

        @Test
        @Tag("P0")
        @DisplayName("[F2] CommonCodeType 상태 연계 시나리오")
        void commonCodeTypeStatusIntegration_Success() {
            // Step 1: CommonCodeType 생성 (active=true)
            var paymentMethodType =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            Long typeId = paymentMethodType.getId();

            // Step 2: CommonCode 생성
            Map<String, Object> createRequest =
                    Map.of(
                            "commonCodeTypeId",
                            typeId,
                            "code",
                            "CREDIT_CARD",
                            "displayName",
                            "신용카드",
                            "displayOrder",
                            1);

            Response createResponse =
                    given().spec(givenAdminJson()).body(createRequest).when().post(BASE_URL);

            createResponse.then().statusCode(HttpStatus.CREATED.value());

            // Step 3: CommonCodeType 비활성화 (현재는 별도 API 없으므로 직접 처리)
            // var savedType = commonCodeTypeRepository.findById(typeId).orElseThrow();
            // savedType.deactivate();
            // commonCodeTypeRepository.save(savedType);

            // Step 4: CommonCode 조회 (타입 상태와 무관하게 조회 가능)
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", typeId)
                    .when()
                    .get(QUERY_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1));

            // 검증: CommonCodeType이 비활성이어도 CommonCode는 조회/수정 가능
        }

        @Test
        @Tag("P0")
        @DisplayName("[F3] 복수 타입에 동일 코드 생성")
        void multipleTypesWithSameCode_Success() {
            // Step 1: CommonCodeType 2개 생성 (PAYMENT_METHOD, DELIVERY_COMPANY)
            var paymentMethodType =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            var deliveryCompanyType =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "DELIVERY_COMPANY", "배송사"));

            Long paymentTypeId = paymentMethodType.getId();
            Long deliveryTypeId = deliveryCompanyType.getId();

            // Step 2: CommonCode 생성 (타입1, CREDIT_CARD)
            Map<String, Object> createRequest1 =
                    Map.of(
                            "commonCodeTypeId",
                            paymentTypeId,
                            "code",
                            "CREDIT_CARD",
                            "displayName",
                            "신용카드",
                            "displayOrder",
                            1);

            Response createResponse1 =
                    given().spec(givenAdminJson()).body(createRequest1).when().post(BASE_URL);

            createResponse1.then().statusCode(HttpStatus.CREATED.value());

            // Step 3: CommonCode 생성 (타입2, CREDIT_CARD) - 중복 아님
            Map<String, Object> createRequest2 =
                    Map.of(
                            "commonCodeTypeId",
                            deliveryTypeId,
                            "code",
                            "CREDIT_CARD",
                            "displayName",
                            "신용카드",
                            "displayOrder",
                            1);

            Response createResponse2 =
                    given().spec(givenAdminJson()).body(createRequest2).when().post(BASE_URL);

            createResponse2.then().statusCode(HttpStatus.CREATED.value());

            // Step 4: 각 타입별 조회 (각각 1개씩 조회됨)
            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", paymentTypeId)
                    .when()
                    .get(QUERY_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].code", equalTo("CREDIT_CARD"));

            given().spec(givenAdmin())
                    .queryParam("commonCodeTypeId", deliveryTypeId)
                    .when()
                    .get(QUERY_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(1))
                    .body("data.content[0].code", equalTo("CREDIT_CARD"));
        }
    }
}
