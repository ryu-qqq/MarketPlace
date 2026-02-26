package com.ryuqq.marketplace.integration.commoncodetype;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.commoncode.CommonCodeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.repository.CommonCodeJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.CommonCodeTypeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.CommonCodeTypeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.repository.CommonCodeTypeJpaRepository;
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
 * CommonCodeType Command 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - POST /api/v1/market/common-code-types - 공통 코드 타입 등록 - PUT
 * /api/v1/market/common-code-types/{id} - 공통 코드 타입 수정 - PATCH
 * /api/v1/market/common-code-types/active-status - 공통 코드 타입 상태 변경
 *
 * <p>우선순위: - P0: 22개 시나리오 (필수 기능) - P1: 8개 시나리오 (중요 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("commoncodetype")
@Tag("command")
@DisplayName("CommonCodeType Command API E2E 테스트")
class CommonCodeTypeCommandE2ETest extends E2ETestBase {

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

    // ===== POST /api/v1/market/common-code-types - 등록 =====

    @Nested
    @DisplayName("POST /api/v1/market/common-code-types - 등록")
    class RegisterCommonCodeTypeTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-01] 생성 성공 - 유효한 요청으로 생성")
        void registerCommonCodeType_ValidRequest_Returns201() {
            // given
            Map<String, Object> request = createRegisterRequest();

            // when
            Response response = given().spec(givenAdminJson()).body(request).when().post(BASE_PATH);

            // then
            response.then().statusCode(HttpStatus.CREATED.value());

            Long createdId = response.jsonPath().getLong("data");
            assertThat(createdId).isNotNull().isGreaterThan(0);

            // DB 검증
            CommonCodeTypeJpaEntity saved =
                    commonCodeTypeRepository.findById(createdId).orElseThrow();
            assertThat(saved.getCode()).isEqualTo("PAYMENT_METHOD");
            assertThat(saved.getName()).isEqualTo("결제수단");
            assertThat(saved.getDescription()).isEqualTo("결제 시 사용 가능한 결제수단 목록");
            assertThat(saved.getDisplayOrder()).isEqualTo(1);
            assertThat(saved.isActive()).isTrue();
            assertThat(saved.getCreatedAt()).isNotNull();
            assertThat(saved.getUpdatedAt()).isNotNull();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-02] 생성 성공 - description 없이 생성")
        void registerCommonCodeType_WithoutDescription_Returns201() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "code", "DELIVERY_COMPANY",
                            "name", "배송사",
                            "displayOrder", 2);

            // when
            Response response = given().spec(givenAdminJson()).body(request).when().post(BASE_PATH);

            // then
            response.then().statusCode(HttpStatus.CREATED.value());

            Long createdId = response.jsonPath().getLong("data");

            // DB 검증
            CommonCodeTypeJpaEntity saved =
                    commonCodeTypeRepository.findById(createdId).orElseThrow();
            assertThat(saved.getDescription()).isNullOrEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-03] 필수 필드 누락 - code 누락 시 400")
        void registerCommonCodeType_MissingCode_Returns400() {
            // given
            Map<String, Object> request = Map.of("name", "결제수단", "displayOrder", 1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-04] 필수 필드 누락 - name 누락 시 400")
        void registerCommonCodeType_MissingName_Returns400() {
            // given
            Map<String, Object> request = Map.of("code", "PAYMENT_METHOD", "displayOrder", 1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-05] 필수 필드 누락 - displayOrder 누락 시 400")
        void registerCommonCodeType_MissingDisplayOrder_Returns400() {
            // given
            Map<String, Object> request = Map.of("code", "PAYMENT_METHOD", "name", "결제수단");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C1-06] 중복 코드 - 동일 code 등록 시 409")
        void registerCommonCodeType_DuplicateCode_Returns409() {
            // given
            commonCodeTypeRepository.save(
                    CommonCodeTypeJpaEntityFixtures.newEntityWithCode("PAYMENT_METHOD", "결제수단"));

            Map<String, Object> request =
                    Map.of(
                            "code", "PAYMENT_METHOD",
                            "name", "결제수단2",
                            "displayOrder", 2);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());

            // DB 검증: 기존 1건만 존재
            long count = commonCodeTypeRepository.count();
            assertThat(count).isEqualTo(1);
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-C1-07] 잘못된 displayOrder - 음수 값 시 400")
        void registerCommonCodeType_NegativeDisplayOrder_Returns400() {
            // given
            Map<String, Object> request =
                    Map.of("code", "PAYMENT_METHOD", "name", "결제수단", "displayOrder", -1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-C1-08] 빈 문자열 - code가 빈 문자열일 때 400")
        void registerCommonCodeType_EmptyCode_Returns400() {
            // given
            Map<String, Object> request = Map.of("code", "", "name", "결제수단", "displayOrder", 1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-C1-09] 빈 문자열 - name이 빈 문자열일 때 400")
        void registerCommonCodeType_EmptyName_Returns400() {
            // given
            Map<String, Object> request =
                    Map.of("code", "PAYMENT_METHOD", "name", "", "displayOrder", 1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== PUT /api/v1/market/common-code-types/{id} - 수정 =====

    @Nested
    @DisplayName("PUT /api/v1/market/common-code-types/{id} - 수정")
    class UpdateCommonCodeTypeTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-01] 수정 성공 - 존재하는 리소스 수정")
        void updateCommonCodeType_ExistingResource_Returns200() {
            // given
            var entity =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            Long id = entity.getId();

            Map<String, Object> request =
                    Map.of(
                            "name", "결제수단_수정",
                            "description", "수정된 설명",
                            "displayOrder", 2);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_PATH + "/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // DB 검증
            var updated = commonCodeTypeRepository.findById(id).orElseThrow();
            assertThat(updated.getName()).isEqualTo("결제수단_수정");
            assertThat(updated.getDescription()).isEqualTo("수정된 설명");
            assertThat(updated.getDisplayOrder()).isEqualTo(2);
            assertThat(updated.getCode()).isEqualTo("PAYMENT_METHOD"); // 코드는 변경 불가
            assertThat(updated.getUpdatedAt()).isAfter(updated.getCreatedAt());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-02] 수정 성공 - description을 null로 변경")
        void updateCommonCodeType_NullDescription_Returns200() {
            // given
            var entity =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            Long id = entity.getId();

            Map<String, Object> request = Map.of("name", "결제수단", "displayOrder", 1);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_PATH + "/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // DB 검증
            var updated = commonCodeTypeRepository.findById(id).orElseThrow();
            assertThat(updated.getDescription()).isNullOrEmpty();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-03] 존재하지 않는 리소스 - 없는 ID 수정 시 404")
        void updateCommonCodeType_NonExistingId_Returns404() {
            // given
            Long nonExistingId = 99999L;
            Map<String, Object> request = Map.of("name", "결제수단", "displayOrder", 1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_PATH + "/{id}", nonExistingId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-04] 필수 필드 누락 - name 누락 시 400")
        void updateCommonCodeType_MissingName_Returns400() {
            // given
            var entity =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            Long id = entity.getId();

            Map<String, Object> request = Map.of("displayOrder", 1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_PATH + "/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C2-05] 필수 필드 누락 - displayOrder 누락 시 400")
        void updateCommonCodeType_MissingDisplayOrder_Returns400() {
            // given
            var entity =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            Long id = entity.getId();

            Map<String, Object> request = Map.of("name", "결제수단");

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_PATH + "/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-C2-08] 잘못된 displayOrder - 음수 값 시 400")
        void updateCommonCodeType_NegativeDisplayOrder_Returns400() {
            // given
            var entity =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            Long id = entity.getId();

            Map<String, Object> request = Map.of("name", "결제수단", "displayOrder", -1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_PATH + "/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-C2-09] 빈 문자열 - name이 빈 문자열일 때 400")
        void updateCommonCodeType_EmptyName_Returns400() {
            // given
            var entity =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            Long id = entity.getId();

            Map<String, Object> request = Map.of("name", "", "displayOrder", 1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_PATH + "/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== PATCH /api/v1/market/common-code-types/active-status - 상태 변경 =====

    @Nested
    @DisplayName("PATCH /api/v1/market/common-code-types/active-status - 상태 변경")
    class ChangeActiveStatusTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-01] 활성화 성공 - 단일 항목 활성화")
        void changeActiveStatus_ActivateSingle_Returns200() {
            // given
            var entity =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newInactiveEntity());
            Long id = entity.getId();

            Map<String, Object> request = Map.of("ids", List.of(id), "active", true);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // DB 검증
            var updated = commonCodeTypeRepository.findById(id).orElseThrow();
            assertThat(updated.isActive()).isTrue();
            assertThat(updated.getUpdatedAt()).isNotNull();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-02] 비활성화 성공 - 단일 항목 비활성화 (하위 공통 코드 없음)")
        void changeActiveStatus_DeactivateSingle_NoCommonCode_Returns200() {
            // given
            var entity =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            Long id = entity.getId();

            Map<String, Object> request = Map.of("ids", List.of(id), "active", false);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // DB 검증
            var updated = commonCodeTypeRepository.findById(id).orElseThrow();
            assertThat(updated.isActive()).isFalse();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-03] 일괄 활성화 - 여러 항목 동시 활성화")
        void changeActiveStatus_ActivateMultiple_Returns200() {
            // given
            var entity1 =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newInactiveEntity());
            var entity2 =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newInactiveEntity());
            var entity3 =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newInactiveEntity());

            List<Long> ids = List.of(entity1.getId(), entity2.getId(), entity3.getId());

            Map<String, Object> request = Map.of("ids", ids, "active", true);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // DB 검증
            List<CommonCodeTypeJpaEntity> updated = commonCodeTypeRepository.findAllById(ids);
            assertThat(updated).allMatch(CommonCodeTypeJpaEntity::isActive);
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-04] 일괄 비활성화 - 여러 항목 동시 비활성화 (하위 공통 코드 없음)")
        void changeActiveStatus_DeactivateMultiple_NoCommonCode_Returns200() {
            // given
            var entity1 =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode("TYPE_1", "타입1"));
            var entity2 =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode("TYPE_2", "타입2"));
            var entity3 =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode("TYPE_3", "타입3"));

            List<Long> ids = List.of(entity1.getId(), entity2.getId(), entity3.getId());

            Map<String, Object> request = Map.of("ids", ids, "active", false);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // DB 검증
            List<CommonCodeTypeJpaEntity> updated = commonCodeTypeRepository.findAllById(ids);
            assertThat(updated).allMatch(entity -> !entity.isActive());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-05] 비활성화 실패 - 활성화된 하위 공통 코드 존재 시 400")
        void changeActiveStatus_DeactivateWithActiveCommonCode_Returns400() {
            // given
            var codeType =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            Long typeId = codeType.getId();

            // 해당 타입의 활성 공통 코드 생성
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            typeId, "CREDIT_CARD", "신용카드"));

            Map<String, Object> request = Map.of("ids", List.of(typeId), "active", false);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());

            // DB 검증: 변경되지 않음
            var entity = commonCodeTypeRepository.findById(typeId).orElseThrow();
            assertThat(entity.isActive()).isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-06] 비활성화 성공 - 비활성화된 하위 공통 코드만 존재")
        void changeActiveStatus_DeactivateWithInactiveCommonCode_Returns200() {
            // given
            var codeType =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));
            Long typeId = codeType.getId();

            // 비활성화된 공통 코드만 생성
            commonCodeRepository.save(CommonCodeJpaEntityFixtures.newInactiveEntity());

            Map<String, Object> request = Map.of("ids", List.of(typeId), "active", false);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // DB 검증
            var updated = commonCodeTypeRepository.findById(typeId).orElseThrow();
            assertThat(updated.isActive()).isFalse();
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-08] 필수 필드 누락 - ids 누락 시 400")
        void changeActiveStatus_MissingIds_Returns400() {
            // given
            Map<String, Object> request = Map.of("active", true);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-09] 필수 필드 누락 - active 누락 시 400")
        void changeActiveStatus_MissingActive_Returns400() {
            // given
            Map<String, Object> request = Map.of("ids", List.of(1L));

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[TC-C3-10] 빈 배열 - ids가 빈 배열일 때 400")
        void changeActiveStatus_EmptyIds_Returns400() {
            // given
            Map<String, Object> request = Map.of("ids", List.of(), "active", true);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[TC-C3-11] 존재하지 않는 ID - 일부 ID가 존재하지 않을 때 404")
        void changeActiveStatus_NonExistingId_Returns404() {
            // given
            var entity =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "PAYMENT_METHOD", "결제수단"));

            Map<String, Object> request =
                    Map.of("ids", List.of(entity.getId(), 99999L), "active", true);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_PATH + "/active-status")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // ===== Helper Methods =====

    private Map<String, Object> createRegisterRequest() {
        return Map.of(
                "code", "PAYMENT_METHOD",
                "name", "결제수단",
                "description", "결제 시 사용 가능한 결제수단 목록",
                "displayOrder", 1);
    }
}
