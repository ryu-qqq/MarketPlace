package com.ryuqq.marketplace.integration.sellerapplication;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerBusinessInfoJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.seller.repository.SellerJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.SellerApplicationJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.repository.SellerApplicationJpaRepository;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.response.Response;
import java.util.HashMap;
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
 * Seller Application Command 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - POST /seller-applications - 입점 신청 - POST /seller-applications/{id}/approve - 입점 신청
 * 승인 - POST /seller-applications/{id}/reject - 입점 신청 거절
 *
 * <p>특징: - 복잡한 워크플로우 (신청 → 승인/거절) - 승인 시 Seller 생성 - 상태 전이 검증
 *
 * <p>시나리오: - P0: 15개 시나리오 (필수 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("sellerapplication")
@Tag("command")
@DisplayName("Seller Application Command API E2E 테스트")
class SellerApplicationCommandE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/seller-applications";

    @Autowired private SellerApplicationJpaRepository sellerApplicationRepository;

    @Autowired private SellerJpaRepository sellerRepository;

    @Autowired private SellerBusinessInfoJpaRepository sellerBusinessInfoRepository;

    @BeforeEach
    void setUp() {
        sellerApplicationRepository.deleteAll();
        sellerBusinessInfoRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        sellerApplicationRepository.deleteAll();
        sellerBusinessInfoRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    // ===== POST /seller-applications - 입점 신청 =====

    @Nested
    @DisplayName("POST /seller-applications - 입점 신청")
    class ApplySellerApplicationTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1-1] 신청 성공")
        void applySellerApplication_validRequest_returns201() {
            // given
            Map<String, Object> request = createApplyRequest();

            // when
            Response response =
                    given().spec(
                                    givenAuthenticatedUser()
                                            .header(
                                                    "X-User-Permissions",
                                                    "seller-application:write"))
                            .body(request)
                            .when()
                            .post(BASE_URL);

            // then
            response.then().statusCode(HttpStatus.CREATED.value());

            Long applicationId = response.jsonPath().getLong("data.applicationId");
            assertThat(applicationId).isNotNull();

            // DB 검증
            var application = sellerApplicationRepository.findById(applicationId).orElseThrow();
            assertThat(application.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-2] 필수 필드 누락 - companyName")
        void applySellerApplication_missingCompanyName_returns400() {
            // given
            Map<String, Object> request = createApplyRequest();
            @SuppressWarnings("unchecked")
            Map<String, Object> businessInfo = (Map<String, Object>) request.get("businessInfo");
            businessInfo.remove("companyName");

            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "seller-application:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-3] 필수 필드 누락 - registrationNumber")
        void applySellerApplication_missingRegistrationNumber_returns400() {
            // given
            Map<String, Object> request = createApplyRequest();
            @SuppressWarnings("unchecked")
            Map<String, Object> businessInfo = (Map<String, Object>) request.get("businessInfo");
            businessInfo.put("registrationNumber", "");

            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "seller-application:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-7] 권한 검증 - 비인증 사용자")
        void applySellerApplication_unauthenticated_returns401() {
            // given
            Map<String, Object> request = createApplyRequest();

            // when & then
            given().spec(givenUnauthenticated())
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-8] 권한 검증 - 인증된 일반 사용자")
        void applySellerApplication_authenticatedUser_returns201() {
            // given
            Map<String, Object> request = createApplyRequest();

            // when & then
            given().spec(
                            givenAuthenticatedUser()
                                    .header("X-User-Permissions", "seller-application:write"))
                    .body(request)
                    .when()
                    .post(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.CREATED.value());
        }
    }

    // ===== POST /seller-applications/{id}/approve - 입점 신청 승인 =====

    @Nested
    @DisplayName("POST /seller-applications/{id}/approve - 입점 신청 승인")
    class ApproveSellerApplicationTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2-1] 승인 성공")
        void approveSellerApplication_pendingApplication_returns200() {
            // given: PENDING 신청 생성
            var application =
                    sellerApplicationRepository.save(
                            SellerApplicationJpaEntityFixtures.pendingEntity());

            // when
            Response response =
                    given().spec(givenSuperAdmin())
                            .when()
                            .post(BASE_URL + "/{id}/approve", application.getId());

            // then
            response.then().statusCode(HttpStatus.OK.value());

            Long sellerId = response.jsonPath().getLong("data.sellerId");
            assertThat(sellerId).isNotNull();

            // DB 검증: Seller 생성 확인
            assertThat(sellerRepository.findById(sellerId)).isPresent();

            // DB 검증: 신청 상태 변경 확인
            var approved = sellerApplicationRepository.findById(application.getId()).orElseThrow();
            assertThat(approved.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
            assertThat(approved.getApprovedSellerId()).isEqualTo(sellerId);
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-2] PENDING 아닌 상태에서 승인")
        void approveSellerApplication_alreadyProcessed_returns409() {
            // given: 이미 승인된 신청
            var application =
                    sellerApplicationRepository.save(
                            SellerApplicationJpaEntityFixtures.approvedEntity(100L));

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .post(BASE_URL + "/{id}/approve", application.getId())
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-3] 존재하지 않는 신청 ID")
        void approveSellerApplication_nonExistingId_returns404() {
            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .post(BASE_URL + "/{id}/approve", 99999)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }

    // ===== POST /seller-applications/{id}/reject - 입점 신청 거절 =====

    @Nested
    @DisplayName("POST /seller-applications/{id}/reject - 입점 신청 거절")
    class RejectSellerApplicationTest {

        @Test
        @Tag("P0")
        @DisplayName("[C3-1] 거절 성공")
        void rejectSellerApplication_pendingApplication_returns204() {
            // given: PENDING 신청 생성
            var application =
                    sellerApplicationRepository.save(
                            SellerApplicationJpaEntityFixtures.pendingEntity());

            Map<String, Object> request = Map.of("rejectionReason", "사업자 등록번호 확인 불가");

            // when & then
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post(BASE_URL + "/{id}/reject", application.getId())
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // DB 검증
            var rejected = sellerApplicationRepository.findById(application.getId()).orElseThrow();
            assertThat(rejected.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
            assertThat(rejected.getRejectionReason()).isEqualTo("사업자 등록번호 확인 불가");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-2] 거절 사유 누락")
        void rejectSellerApplication_missingReason_returns400() {
            // given
            var application =
                    sellerApplicationRepository.save(
                            SellerApplicationJpaEntityFixtures.pendingEntity());

            Map<String, Object> request = Map.of("rejectionReason", "");

            // when & then
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post(BASE_URL + "/{id}/reject", application.getId())
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-3] PENDING 아닌 상태에서 거절")
        void rejectSellerApplication_alreadyProcessed_returns409() {
            // given: 이미 승인된 신청
            var application =
                    sellerApplicationRepository.save(
                            SellerApplicationJpaEntityFixtures.approvedEntity(100L));

            Map<String, Object> request = Map.of("rejectionReason", "테스트 사유");

            // when & then
            given().spec(givenSuperAdmin())
                    .body(request)
                    .when()
                    .post(BASE_URL + "/{id}/reject", application.getId())
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    // ===== 전체 플로우 시나리오 =====

    @Nested
    @DisplayName("전체 플로우 시나리오")
    class FullFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1] 신청 → 조회 → 승인 → Seller 생성 확인 (전체 흐름)")
        void fullFlow_applyToApproval() {
            // Step 1: 신청
            Map<String, Object> applyRequest = createApplyRequest();
            Response applyResponse =
                    given().spec(
                                    givenAuthenticatedUser()
                                            .header(
                                                    "X-User-Permissions",
                                                    "seller-application:write"))
                            .body(applyRequest)
                            .when()
                            .post(BASE_URL);

            applyResponse.then().statusCode(HttpStatus.CREATED.value());
            Long applicationId = applyResponse.jsonPath().getLong("data.applicationId");

            // Step 2: 목록 조회
            given().spec(givenSuperAdmin())
                    .queryParam("status", "PENDING")
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.totalElements", org.hamcrest.Matchers.greaterThanOrEqualTo(1));

            // Step 3: 상세 조회
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{id}", applicationId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", org.hamcrest.Matchers.equalTo("PENDING"));

            // Step 4: 승인
            Response approveResponse =
                    given().spec(givenSuperAdmin())
                            .when()
                            .post(BASE_URL + "/{id}/approve", applicationId);

            approveResponse.then().statusCode(HttpStatus.OK.value());
            Long sellerId = approveResponse.jsonPath().getLong("data.sellerId");

            // Step 5: 신청 상태 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{id}", applicationId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", org.hamcrest.Matchers.equalTo("APPROVED"))
                    .body(
                            "data.approvedSellerId",
                            org.hamcrest.Matchers.equalTo(sellerId.intValue()));

            // Step 6: Seller 생성 확인
            assertThat(sellerRepository.findById(sellerId)).isPresent();
        }

        @Test
        @Tag("P0")
        @DisplayName("[F2] 신청 → 거절 플로우")
        void fullFlow_applyToRejection() {
            // Step 1: 신청
            Map<String, Object> applyRequest = createApplyRequest();
            Response applyResponse =
                    given().spec(
                                    givenAuthenticatedUser()
                                            .header(
                                                    "X-User-Permissions",
                                                    "seller-application:write"))
                            .body(applyRequest)
                            .when()
                            .post(BASE_URL);

            applyResponse.then().statusCode(HttpStatus.CREATED.value());
            Long applicationId = applyResponse.jsonPath().getLong("data.applicationId");

            // Step 2: 거절
            Map<String, Object> rejectRequest = Map.of("rejectionReason", "서류 미비");
            given().spec(givenSuperAdmin())
                    .body(rejectRequest)
                    .when()
                    .post(BASE_URL + "/{id}/reject", applicationId)
                    .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());

            // Step 3: 거절 확인
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{id}", applicationId)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.status", org.hamcrest.Matchers.equalTo("REJECTED"))
                    .body("data.rejectionReason", org.hamcrest.Matchers.equalTo("서류 미비"));

            // Step 4: 재승인 시도 (409)
            given().spec(givenSuperAdmin())
                    .when()
                    .post(BASE_URL + "/{id}/approve", applicationId)
                    .then()
                    .statusCode(HttpStatus.CONFLICT.value());
        }
    }

    // ===== Helper Methods =====

    private Map<String, Object> createApplyRequest() {
        Map<String, Object> address = new HashMap<>();
        address.put("zipCode", "12345");
        address.put("line1", "서울시 강남구");
        address.put("line2", "테헤란로 123");

        Map<String, Object> sellerInfo = new HashMap<>();
        sellerInfo.put("sellerName", "테스트셀러");
        sellerInfo.put("displayName", "테스트 브랜드");

        Map<String, Object> businessInfo = new HashMap<>();
        businessInfo.put("registrationNumber", "123-45-67890");
        businessInfo.put("companyName", "테스트컴퍼니");
        businessInfo.put("representative", "홍길동");
        businessInfo.put("businessAddress", address);

        Map<String, Object> csContact = new HashMap<>();
        csContact.put("phone", "010-1234-5678");
        csContact.put("email", "contact@example.com");

        Map<String, Object> contactInfo = new HashMap<>();
        contactInfo.put("name", "김담당");
        contactInfo.put("phone", "010-1234-5678");
        contactInfo.put("email", "contact@example.com");

        Map<String, Object> settlementInfo = new HashMap<>();
        settlementInfo.put("bankName", "신한은행");
        settlementInfo.put("accountNumber", "110123456789");
        settlementInfo.put("accountHolderName", "홍길동");

        Map<String, Object> request = new HashMap<>();
        request.put("sellerInfo", sellerInfo);
        request.put("businessInfo", businessInfo);
        request.put("csContact", csContact);
        request.put("contactInfo", contactInfo);
        request.put("settlementInfo", settlementInfo);

        return request;
    }
}
