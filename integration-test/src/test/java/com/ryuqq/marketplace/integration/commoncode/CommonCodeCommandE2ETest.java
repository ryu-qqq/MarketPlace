package com.ryuqq.marketplace.integration.commoncode;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.commoncode.CommonCodeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.CommonCodeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.repository.CommonCodeJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.CommonCodeTypeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.repository.CommonCodeTypeJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * CommonCode Command 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - POST /admin/common-codes - 공통 코드 등록 - PUT /admin/common-codes/{id} - 공통 코드 수정 -
 * PATCH /admin/common-codes/active-status - 활성화 상태 변경
 *
 * <p>우선순위: - P0: 15개 시나리오 (필수 기능) - P1: 8개 시나리오 (중요 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("commoncode")
@Tag("command")
@DisplayName("CommonCode Command API E2E 테스트")
class CommonCodeCommandE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/common-codes";

    @Autowired private CommonCodeJpaRepository commonCodeRepository;
    @Autowired private CommonCodeTypeJpaRepository commonCodeTypeRepository;

    private Long defaultCommonCodeTypeId;

    @BeforeEach
    void setUp() {
        // 전체 데이터 초기화
        commonCodeRepository.deleteAll();
        commonCodeTypeRepository.deleteAll();

        // CommonCodeType 사전 데이터 생성
        var paymentMethodType =
                commonCodeTypeRepository.save(
                        CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                "PAYMENT_METHOD", "결제수단"));
        defaultCommonCodeTypeId = paymentMethodType.getId();
    }

    @AfterEach
    void tearDown() {
        commonCodeRepository.deleteAll();
        commonCodeTypeRepository.deleteAll();
    }

    // ===== POST /admin/common-codes - 공통 코드 등록 =====

    @Nested
    @DisplayName("POST /admin/common-codes - 공통 코드 등록")
    class RegisterCommonCodeTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1-1] 생성 성공 - 유효한 요청으로 생성")
        void registerCommonCode_ValidRequest_Returns201() {
            // given
            Map<String, Object> request =
                    Map.of(
                            "commonCodeTypeId",
                            defaultCommonCodeTypeId,
                            "code",
                            "CREDIT_CARD",
                            "displayName",
                            "신용카드",
                            "displayOrder",
                            1);

            // when
            Response response = given().spec(givenAdminJson()).body(request).when().post(BASE_URL);

            // then
            response.then().statusCode(HttpStatus.CREATED.value()).body("data", greaterThan(0));

            Long createdId = response.jsonPath().getLong("data");
            assertThat(commonCodeRepository.findById(createdId)).isPresent();

            var saved = commonCodeRepository.findById(createdId).orElseThrow();
            assertThat(saved.isActive()).isTrue();
            assertThat(saved.getDeletedAt()).isNull();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-2] 필수 필드 누락 - commonCodeTypeId null → 400")
        void registerCommonCode_MissingTypeId_Returns400() {
            // given: commonCodeTypeId 누락
            Map<String, Object> request =
                    Map.of(
                            "code", "CREDIT_CARD",
                            "displayName", "신용카드",
                            "displayOrder", 1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-3] 필수 필드 누락 - code blank → 400")
        void registerCommonCode_BlankCode_Returns400() {
            // given: code가 빈 문자열
            Map<String, Object> request =
                    Map.of(
                            "commonCodeTypeId",
                            defaultCommonCodeTypeId,
                            "code",
                            "",
                            "displayName",
                            "신용카드",
                            "displayOrder",
                            1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-4] 필수 필드 누락 - displayName blank → 400")
        void registerCommonCode_BlankDisplayName_Returns400() {
            // given: displayName이 빈 문자열
            Map<String, Object> request =
                    Map.of(
                            "commonCodeTypeId",
                            defaultCommonCodeTypeId,
                            "code",
                            "CREDIT_CARD",
                            "displayName",
                            "",
                            "displayOrder",
                            1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-5] 잘못된 범위 - displayOrder 음수 → 400")
        void registerCommonCode_NegativeDisplayOrder_Returns400() {
            // given: displayOrder 음수
            Map<String, Object> request =
                    Map.of(
                            "commonCodeTypeId",
                            defaultCommonCodeTypeId,
                            "code",
                            "CREDIT_CARD",
                            "displayName",
                            "신용카드",
                            "displayOrder",
                            -1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-6] 존재하지 않는 타입 → 404")
        void registerCommonCode_NonExistingTypeId_Returns404() {
            // given: 존재하지 않는 타입 ID
            Map<String, Object> request =
                    Map.of(
                            "commonCodeTypeId",
                            99999L,
                            "code",
                            "CREDIT_CARD",
                            "displayName",
                            "신용카드",
                            "displayOrder",
                            1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(
                            anyOf(
                                    equalTo(HttpStatus.NOT_FOUND.value()),
                                    equalTo(HttpStatus.BAD_REQUEST.value())));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-7] 중복 코드 → 409 Conflict")
        void registerCommonCode_DuplicateCode_Returns409() {
            // given: 동일 코드가 이미 존재
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));

            Map<String, Object> request =
                    Map.of(
                            "commonCodeTypeId",
                            defaultCommonCodeTypeId,
                            "code",
                            "CREDIT_CARD",
                            "displayName",
                            "신용카드2",
                            "displayOrder",
                            2);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-8] 다른 타입에서 동일 코드 - 허용됨")
        void registerCommonCode_SameCodeDifferentType_Returns201() {
            // given: 타입1에 CREDIT_CARD 존재
            commonCodeRepository.save(
                    CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                            defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));

            // 타입2 생성
            var deliveryType =
                    commonCodeTypeRepository.save(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "DELIVERY_COMPANY", "배송사"));

            Map<String, Object> request =
                    Map.of(
                            "commonCodeTypeId",
                            deliveryType.getId(),
                            "code",
                            "CREDIT_CARD",
                            "displayName",
                            "신용카드",
                            "displayOrder",
                            1);

            // when & then: 다른 타입이므로 생성 성공
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("data", greaterThan(0));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-9] 코드값 소문자 허용 - 패턴 검증 미적용")
        void registerCommonCode_LowercaseCode_Returns201() {
            // given: 소문자 코드 (현재 패턴 검증 없음, 등록 성공)
            Map<String, Object> request =
                    Map.of(
                            "commonCodeTypeId",
                            defaultCommonCodeTypeId,
                            "code",
                            "creditCard",
                            "displayName",
                            "신용카드",
                            "displayOrder",
                            1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-10] 코드값 패턴 위반 - 숫자로 시작 → 400")
        void registerCommonCode_InvalidPattern_NumberStart_Returns400() {
            // given: 숫자로 시작하는 코드
            Map<String, Object> request =
                    Map.of(
                            "commonCodeTypeId",
                            defaultCommonCodeTypeId,
                            "code",
                            "1CARD",
                            "displayName",
                            "신용카드",
                            "displayOrder",
                            1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    // ===== PUT /admin/common-codes/{id} - 공통 코드 수정 =====

    @Nested
    @DisplayName("PUT /admin/common-codes/{id} - 공통 코드 수정")
    class UpdateCommonCodeTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2-1] 수정 성공 - 존재하는 리소스 수정")
        void updateCommonCode_ExistingResource_Returns200() {
            // given: 기존 코드 생성
            var saved =
                    commonCodeRepository.save(
                            CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                    defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));
            Long savedId = saved.getId();

            Map<String, Object> request = Map.of("displayName", "신용/체크카드", "displayOrder", 2);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{id}", savedId)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // then: DB 검증
            var updated = commonCodeRepository.findById(savedId).orElseThrow();
            assertThat(updated.getDisplayName()).isEqualTo("신용/체크카드");
            assertThat(updated.getDisplayOrder()).isEqualTo(2);
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-2] 존재하지 않는 리소스 → 404")
        void updateCommonCode_NonExistingResource_Returns404() {
            // given: 존재하지 않는 ID
            Long nonExistingId = 99999L;

            Map<String, Object> request = Map.of("displayName", "신용카드", "displayOrder", 1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{id}", nonExistingId)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-3] 필수 필드 누락 - displayName blank → 400")
        void updateCommonCode_BlankDisplayName_Returns400() {
            // given: 기존 코드 생성
            var saved =
                    commonCodeRepository.save(
                            CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                    defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));

            Map<String, Object> request = Map.of("displayName", "", "displayOrder", 1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{id}", saved.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-4] 잘못된 범위 - displayOrder 음수 → 400")
        void updateCommonCode_NegativeDisplayOrder_Returns400() {
            // given: 기존 코드 생성
            var saved =
                    commonCodeRepository.save(
                            CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                    defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));

            Map<String, Object> request = Map.of("displayName", "신용카드", "displayOrder", -1);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{id}", saved.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C2-5] 삭제된 코드 수정 시도 → 404")
        void updateCommonCode_DeletedCode_Returns404() {
            // given: 삭제된 코드
            Instant now = Instant.now();
            var deletedCode =
                    CommonCodeJpaEntity.create(
                            null,
                            defaultCommonCodeTypeId,
                            "CREDIT_CARD",
                            "신용카드",
                            1,
                            false,
                            now,
                            now,
                            now); // deletedAt 설정
            var saved = commonCodeRepository.save(deletedCode);

            Map<String, Object> request = Map.of("displayName", "신용카드", "displayOrder", 1);

            // when & then: Soft Delete 필터링으로 404
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{id}", saved.getId())
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C2-6] 코드는 수정 불가 - code는 불변")
        void updateCommonCode_CodeUnchanged_Returns200() {
            // given: 기존 코드 생성
            var saved =
                    commonCodeRepository.save(
                            CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                    defaultCommonCodeTypeId, "CREDIT_CARD", "신용카드"));
            String originalCode = saved.getCode();

            Map<String, Object> request = Map.of("displayName", "수정된이름", "displayOrder", 2);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .put(BASE_URL + "/{id}", saved.getId())
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // then: code는 변경 안 됨
            var updated = commonCodeRepository.findById(saved.getId()).orElseThrow();
            assertThat(updated.getCode()).isEqualTo(originalCode);
        }
    }

    // ===== PATCH /admin/common-codes/active-status - 활성화 상태 변경 =====

    @Nested
    @DisplayName("PATCH /admin/common-codes/active-status - 활성화 상태 변경")
    class ChangeActiveStatusTest {

        @Test
        @Tag("P0")
        @DisplayName("[C3-1] 활성화 성공 - 비활성 코드 활성화")
        void changeActiveStatus_Activate_Returns200() {
            // given: 비활성 코드 2개
            Instant now = Instant.now();
            var code1 =
                    commonCodeRepository.save(
                            CommonCodeJpaEntity.create(
                                    null,
                                    defaultCommonCodeTypeId,
                                    "CODE_1",
                                    "코드1",
                                    1,
                                    false,
                                    now,
                                    now,
                                    null));
            var code2 =
                    commonCodeRepository.save(
                            CommonCodeJpaEntity.create(
                                    null,
                                    defaultCommonCodeTypeId,
                                    "CODE_2",
                                    "코드2",
                                    1,
                                    false,
                                    now,
                                    now,
                                    null));

            Map<String, Object> request =
                    Map.of("ids", List.of(code1.getId(), code2.getId()), "active", true);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/active-status")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // then: DB 검증
            var updated1 = commonCodeRepository.findById(code1.getId()).orElseThrow();
            var updated2 = commonCodeRepository.findById(code2.getId()).orElseThrow();
            assertThat(updated1.isActive()).isTrue();
            assertThat(updated2.isActive()).isTrue();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-2] 비활성화 성공 - 활성 코드 비활성화")
        void changeActiveStatus_Deactivate_Returns200() {
            // given: 활성 코드 2개
            var code1 =
                    commonCodeRepository.save(
                            CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                    defaultCommonCodeTypeId, "CODE_1", "코드1"));
            var code2 =
                    commonCodeRepository.save(
                            CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                    defaultCommonCodeTypeId, "CODE_2", "코드2"));

            Map<String, Object> request =
                    Map.of("ids", List.of(code1.getId(), code2.getId()), "active", false);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/active-status")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // then: DB 검증
            var updated1 = commonCodeRepository.findById(code1.getId()).orElseThrow();
            var updated2 = commonCodeRepository.findById(code2.getId()).orElseThrow();
            assertThat(updated1.isActive()).isFalse();
            assertThat(updated2.isActive()).isFalse();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-3] 필수 필드 누락 - ids empty → 400")
        void changeActiveStatus_EmptyIds_Returns400() {
            // given: ids가 빈 리스트
            Map<String, Object> request = Map.of("ids", List.of(), "active", true);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/active-status")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-4] 필수 필드 누락 - active null → 400")
        void changeActiveStatus_NullActive_Returns400() {
            // given: active 누락
            var code1 =
                    commonCodeRepository.save(
                            CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                    defaultCommonCodeTypeId, "CODE_1", "코드1"));

            Map<String, Object> request = Map.of("ids", List.of(code1.getId()));

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/active-status")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-5] 존재하지 않는 ID 포함 → 404")
        void changeActiveStatus_NonExistingId_Returns404() {
            // given: 존재하는 ID 1개 + 존재하지 않는 ID 1개
            var code1 =
                    commonCodeRepository.save(
                            CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                    defaultCommonCodeTypeId, "CODE_1", "코드1"));
            Long nonExistingId = 99999L;

            Map<String, Object> request =
                    Map.of("ids", List.of(code1.getId(), nonExistingId), "active", true);

            // when & then
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/active-status")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C3-6] 일부만 존재 - 실패 전 상태 롤백 확인")
        void changeActiveStatus_PartialExist_RollbackOnFailure() {
            // given: 존재하는 ID 1개 (비활성), 존재하지 않는 ID 1개
            Instant now = Instant.now();
            var code1 =
                    commonCodeRepository.save(
                            CommonCodeJpaEntity.create(
                                    null,
                                    defaultCommonCodeTypeId,
                                    "CODE_1",
                                    "코드1",
                                    1,
                                    false,
                                    now,
                                    now,
                                    null));
            Long nonExistingId = 99999L;

            Map<String, Object> request =
                    Map.of("ids", List.of(code1.getId(), nonExistingId), "active", true);

            // when
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/active-status")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());

            // then: 롤백 확인 (code1의 상태가 변경되지 않음)
            var unchanged = commonCodeRepository.findById(code1.getId()).orElseThrow();
            assertThat(unchanged.isActive()).isFalse();
        }

        @Test
        @Tag("P1")
        @DisplayName("[C3-7] 중복 ID 포함 - 중복 제거 처리")
        void changeActiveStatus_DuplicateIds_HandlesCorrectly() {
            // given: 동일 ID를 여러 번 포함
            var code1 =
                    commonCodeRepository.save(
                            CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                    defaultCommonCodeTypeId, "CODE_1", "코드1"));

            Map<String, Object> request =
                    Map.of(
                            "ids",
                            List.of(code1.getId(), code1.getId(), code1.getId()),
                            "active",
                            true);

            // when & then: 중복은 내부에서 처리됨
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/active-status")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            var updated = commonCodeRepository.findById(code1.getId()).orElseThrow();
            assertThat(updated.isActive()).isTrue();
        }

        @Test
        @Tag("P1")
        @DisplayName("[C3-8] 삭제된 코드 포함 → 404")
        void changeActiveStatus_DeletedCode_Returns404() {
            // given: 활성 코드 1개, 삭제된 코드 1개
            Instant now = Instant.now();
            var code1 =
                    commonCodeRepository.save(
                            CommonCodeJpaEntityFixtures.newEntityWithTypeIdAndCode(
                                    defaultCommonCodeTypeId, "CODE_1", "코드1"));
            var code2 =
                    commonCodeRepository.save(
                            CommonCodeJpaEntity.create(
                                    null,
                                    defaultCommonCodeTypeId,
                                    "CODE_2",
                                    "코드2",
                                    1,
                                    false,
                                    now,
                                    now,
                                    now)); // deletedAt 설정

            Map<String, Object> request =
                    Map.of("ids", List.of(code1.getId(), code2.getId()), "active", true);

            // when & then: Soft Delete 필터링으로 404
            given().spec(givenAdminJson())
                    .body(request)
                    .when()
                    .patch(BASE_URL + "/active-status")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }
}
