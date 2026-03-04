package com.ryuqq.marketplace.integration.selleradmin;

import com.ryuqq.marketplace.integration.E2ETestBase;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * SellerAdminAuthE2ETest - 셀러 관리자 가입 신청 인증/인가 E2E 테스트.
 *
 * <p>SellerAdmin 도메인의 인증/인가 접근 제어만 검증합니다.
 *
 * <p>AuthHub SDK 의존성이 강하므로 비즈니스 로직 테스트는 생략하고, 모든 엔드포인트에 대해 다음 시나리오만 테스트합니다: - 토큰 없이 요청 → 401 -
 * superAdmin 전용 엔드포인트: 일반 사용자 → 403 - myselfOr 엔드포인트: 다른 사용자 → 403, 본인은 통과 - authenticated 엔드포인트:
 * 인증만 되면 통과
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("selleradmin")
@Tag("e2e")
@Tag("auth")
@DisplayName("셀러 관리자 가입 신청 인증/인가 E2E 테스트")
public class SellerAdminAuthE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/seller-admin-applications";

    // ===== 1. GET /admin/seller-admin-applications - 목록 조회 =====

    @Nested
    @DisplayName("GET /admin/seller-admin-applications - 목록 조회")
    class SearchTest {

        @Test
        @DisplayName("SC-Q1-02: 토큰 없이 요청 시 401")
        void search_WithoutToken_ShouldReturn401() {
            givenUnauthenticated()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("SC-Q1-03: 슈퍼어드민 아닌 사용자 접근 시 403")
        void search_WithoutSuperAdminRole_ShouldReturn403() {
            givenAuthenticatedUser()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("SC-Q1-01: 슈퍼어드민은 접근 가능 (200)")
        void search_WithSuperAdmin_ShouldReturn200() {
            givenSuperAdmin()
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_PATH)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }
    }

    // ===== 2. GET /admin/seller-admin-applications/{id} - 상세 조회 =====

    @Nested
    @DisplayName("GET /admin/seller-admin-applications/{id} - 상세 조회")
    class GetDetailTest {

        private static final String DETAIL_PATH = BASE_PATH + "/{sellerAdminId}";
        private static final String SELLER_ADMIN_ID_1 = "01933000-0000-7000-8000-000000000001";
        private static final String SELLER_ADMIN_ID_2 = "01933000-0000-7000-8000-000000000002";

        @Test
        @DisplayName("SC-Q2-04: 토큰 없이 요청 시 401")
        void get_WithoutToken_ShouldReturn401() {
            givenUnauthenticated()
                    .when()
                    .get(DETAIL_PATH, SELLER_ADMIN_ID_1)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("SC-Q2-03: 다른 관리자의 정보 조회 시 403 (권한 없음)")
        void get_OtherAdminWithoutPermission_ShouldReturn403() {
            // 일반 사용자 (id1)가 id2의 정보 조회 시도
            givenSellerUser("org-001")
                    .when()
                    .get(DETAIL_PATH, SELLER_ADMIN_ID_2)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("SC-Q2-02: 슈퍼어드민은 모든 정보 조회 가능 (200 or 404)")
        void get_WithSuperAdmin_ShouldReturnValidResponse() {
            givenSuperAdmin()
                    .when()
                    .get(DETAIL_PATH, SELLER_ADMIN_ID_1)
                    .then()
                    .statusCode(
                            org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is(HttpStatus.OK.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.NOT_FOUND.value())));
        }

        @Test
        @DisplayName("SC-Q2-06: 'seller-admin:read' 권한으로 타인 조회 가능")
        void get_WithReadPermission_ShouldReturnValidResponse() {
            givenSellerUser("org-001", "seller-admin:read")
                    .when()
                    .get(DETAIL_PATH, SELLER_ADMIN_ID_2)
                    .then()
                    .statusCode(
                            org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is(HttpStatus.OK.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.NOT_FOUND.value())));
        }
    }

    // ===== 3. POST /admin/seller-admin-applications - 가입 신청 =====

    @Nested
    @DisplayName("POST /admin/seller-admin-applications - 가입 신청")
    class ApplyTest {

        @Test
        @DisplayName("SC-C1-02: 토큰 없이 가입 신청도 허용 (공개 API)")
        void apply_WithoutToken_ShouldBeAllowed() {
            Map<String, Object> request =
                    Map.of(
                            "sellerId", 1L,
                            "loginId", "newadmin@test.com",
                            "name", "신규관리자",
                            "phoneNumber", "010-1111-2222",
                            "password", "Password123!");

            givenUnauthenticated()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(
                            org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is(HttpStatus.CREATED.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.BAD_REQUEST.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.NOT_FOUND.value())));
        }

        @Test
        @DisplayName("SC-C1-01: 인증된 사용자는 접근 가능 (201 or 400)")
        void apply_WithAuthenticated_ShouldReturnValidResponse() {
            Map<String, Object> request =
                    Map.of(
                            "sellerId", 1L,
                            "loginId", "newadmin@test.com",
                            "name", "신규관리자",
                            "phoneNumber", "010-1111-2222",
                            "password", "Password123!");

            givenAuthenticatedUser()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(BASE_PATH)
                    .then()
                    .statusCode(
                            org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is(HttpStatus.CREATED.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.BAD_REQUEST.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.NOT_FOUND.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.CONFLICT.value())));
        }
    }

    // ===== 4. POST /admin/seller-admin-applications/{id}/approve - 승인 =====

    @Nested
    @DisplayName("POST /admin/seller-admin-applications/{id}/approve - 승인")
    class ApproveTest {

        private static final String APPROVE_PATH = BASE_PATH + "/{sellerAdminId}/approve";
        private static final String SELLER_ADMIN_ID = "01933000-0000-7000-8000-000000000001";

        @Test
        @DisplayName("SC-C2-02: 토큰 없이 승인 시 401")
        void approve_WithoutToken_ShouldReturn401() {
            givenUnauthenticated()
                    .when()
                    .post(APPROVE_PATH, SELLER_ADMIN_ID)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("SC-C2-03: 일반 사용자가 승인 시 403")
        void approve_WithoutSuperAdminRole_ShouldReturn403() {
            givenAuthenticatedUser()
                    .when()
                    .post(APPROVE_PATH, SELLER_ADMIN_ID)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("SC-C2-01: 슈퍼어드민은 승인 가능 (200 or 404 or 409)")
        void approve_WithSuperAdmin_ShouldReturnValidResponse() {
            givenSuperAdmin()
                    .when()
                    .post(APPROVE_PATH, SELLER_ADMIN_ID)
                    .then()
                    .statusCode(
                            org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is(HttpStatus.OK.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.NOT_FOUND.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.CONFLICT.value())));
        }
    }

    // ===== 5. POST /admin/seller-admin-applications/{id}/reject - 거절 =====

    @Nested
    @DisplayName("POST /admin/seller-admin-applications/{id}/reject - 거절")
    class RejectTest {

        private static final String REJECT_PATH = BASE_PATH + "/{sellerAdminId}/reject";
        private static final String SELLER_ADMIN_ID = "01933000-0000-7000-8000-000000000001";

        @Test
        @DisplayName("SC-C3-02: 토큰 없이 거절 시 401")
        void reject_WithoutToken_ShouldReturn401() {
            givenUnauthenticated()
                    .when()
                    .post(REJECT_PATH, SELLER_ADMIN_ID)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("SC-C3-03: 일반 사용자가 거절 시 403")
        void reject_WithoutSuperAdminRole_ShouldReturn403() {
            givenAuthenticatedUser()
                    .when()
                    .post(REJECT_PATH, SELLER_ADMIN_ID)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("SC-C3-01: 슈퍼어드민은 거절 가능 (204 or 404 or 409)")
        void reject_WithSuperAdmin_ShouldReturnValidResponse() {
            givenSuperAdmin()
                    .when()
                    .post(REJECT_PATH, SELLER_ADMIN_ID)
                    .then()
                    .statusCode(
                            org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is(HttpStatus.NO_CONTENT.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.NOT_FOUND.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.CONFLICT.value())));
        }
    }

    // ===== 6. POST /admin/seller-admin-applications/bulk-approve - 일괄 승인 =====

    @Nested
    @DisplayName("POST /admin/seller-admin-applications/bulk-approve - 일괄 승인")
    class BulkApproveTest {

        private static final String BULK_APPROVE_PATH = BASE_PATH + "/bulk-approve";

        @Test
        @DisplayName("SC-C4-02: 토큰 없이 일괄 승인 시 401")
        void bulkApprove_WithoutToken_ShouldReturn401() {
            Map<String, Object> request = Map.of("sellerAdminIds", List.of("id1", "id2", "id3"));

            givenUnauthenticated()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(BULK_APPROVE_PATH)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("SC-C4-03: 일반 사용자가 일괄 승인 시 403")
        void bulkApprove_WithoutSuperAdminRole_ShouldReturn403() {
            Map<String, Object> request = Map.of("sellerAdminIds", List.of("id1", "id2", "id3"));

            givenAuthenticatedUser()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(BULK_APPROVE_PATH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("SC-C4-01: 슈퍼어드민은 일괄 승인 가능 (200 or 400)")
        void bulkApprove_WithSuperAdmin_ShouldReturnValidResponse() {
            Map<String, Object> request =
                    Map.of("sellerAdminIds", List.of("01933000-0000-7000-8000-000000000001"));

            givenSuperAdmin()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(BULK_APPROVE_PATH)
                    .then()
                    .statusCode(
                            org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is(HttpStatus.OK.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.BAD_REQUEST.value())));
        }
    }

    // ===== 7. POST /admin/seller-admin-applications/bulk-reject - 일괄 거절 =====

    @Nested
    @DisplayName("POST /admin/seller-admin-applications/bulk-reject - 일괄 거절")
    class BulkRejectTest {

        private static final String BULK_REJECT_PATH = BASE_PATH + "/bulk-reject";

        @Test
        @DisplayName("SC-C5-02: 토큰 없이 일괄 거절 시 401")
        void bulkReject_WithoutToken_ShouldReturn401() {
            Map<String, Object> request = Map.of("sellerAdminIds", List.of("id1", "id2", "id3"));

            givenUnauthenticated()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(BULK_REJECT_PATH)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("SC-C5-03: 일반 사용자가 일괄 거절 시 403")
        void bulkReject_WithoutSuperAdminRole_ShouldReturn403() {
            Map<String, Object> request = Map.of("sellerAdminIds", List.of("id1", "id2", "id3"));

            givenAuthenticatedUser()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(BULK_REJECT_PATH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("SC-C5-01: 슈퍼어드민은 일괄 거절 가능 (204 or 400 or 404 or 409)")
        void bulkReject_WithSuperAdmin_ShouldReturnValidResponse() {
            Map<String, Object> request =
                    Map.of("sellerAdminIds", List.of("01933000-0000-7000-8000-000000000001"));

            givenSuperAdmin()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post(BULK_REJECT_PATH)
                    .then()
                    .statusCode(
                            org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is(HttpStatus.NO_CONTENT.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.BAD_REQUEST.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.NOT_FOUND.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.CONFLICT.value())));
        }
    }

    // ===== 8. POST /admin/seller-admin-applications/{id}/reset-password - 비밀번호 초기화 =====

    @Nested
    @DisplayName("POST /admin/seller-admin-applications/{id}/reset-password - 비밀번호 초기화")
    class ResetPasswordTest {

        private static final String RESET_PASSWORD_PATH =
                BASE_PATH + "/{sellerAdminId}/reset-password";
        private static final String SELLER_ADMIN_ID = "01933000-0000-7000-8000-000000000001";

        @Test
        @DisplayName("SC-C6-02: 토큰 없이 비밀번호 초기화 시 401")
        void resetPassword_WithoutToken_ShouldReturn401() {
            givenUnauthenticated()
                    .when()
                    .post(RESET_PASSWORD_PATH, SELLER_ADMIN_ID)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("SC-C6-03: 일반 사용자가 비밀번호 초기화 시 403")
        void resetPassword_WithoutSuperAdminRole_ShouldReturn403() {
            givenAuthenticatedUser()
                    .when()
                    .post(RESET_PASSWORD_PATH, SELLER_ADMIN_ID)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("SC-C6-01: 슈퍼어드민은 비밀번호 초기화 가능 (204 or 400 or 404)")
        void resetPassword_WithSuperAdmin_ShouldReturnValidResponse() {
            givenSuperAdmin()
                    .when()
                    .post(RESET_PASSWORD_PATH, SELLER_ADMIN_ID)
                    .then()
                    .statusCode(
                            org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is(HttpStatus.NO_CONTENT.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.BAD_REQUEST.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.NOT_FOUND.value())));
        }
    }

    // ===== 9. PATCH /admin/seller-admin-applications/{id}/change-password - 비밀번호 변경 =====

    @Nested
    @DisplayName("PATCH /admin/seller-admin-applications/{id}/change-password - 비밀번호 변경")
    class ChangePasswordTest {

        private static final String CHANGE_PASSWORD_PATH =
                BASE_PATH + "/{sellerAdminId}/change-password";
        private static final String SELLER_ADMIN_ID_1 = "01933000-0000-7000-8000-000000000001";
        private static final String SELLER_ADMIN_ID_2 = "01933000-0000-7000-8000-000000000002";

        @Test
        @DisplayName("SC-C7-04: 토큰 없이 비밀번호 변경 시 401")
        void changePassword_WithoutToken_ShouldReturn401() {
            Map<String, Object> request = Map.of("newPassword", "NewPassword123!");

            givenUnauthenticated()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch(CHANGE_PASSWORD_PATH, SELLER_ADMIN_ID_1)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @DisplayName("SC-C7-03: 다른 관리자의 비밀번호 변경 시 403 (권한 없음)")
        void changePassword_OtherAdminWithoutPermission_ShouldReturn403() {
            Map<String, Object> request = Map.of("newPassword", "NewPassword123!");

            // 일반 사용자 (id1)가 id2의 비밀번호 변경 시도
            givenSellerUser("org-001")
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch(CHANGE_PASSWORD_PATH, SELLER_ADMIN_ID_2)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @DisplayName("SC-C7-02: 슈퍼어드민은 모든 관리자 비밀번호 변경 가능 (204 or 400 or 404)")
        void changePassword_WithSuperAdmin_ShouldReturnValidResponse() {
            Map<String, Object> request = Map.of("newPassword", "NewPassword123!");

            givenSuperAdmin()
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch(CHANGE_PASSWORD_PATH, SELLER_ADMIN_ID_1)
                    .then()
                    .statusCode(
                            org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is(HttpStatus.NO_CONTENT.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.BAD_REQUEST.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.NOT_FOUND.value())));
        }

        @Test
        @DisplayName("SC-C7-07: 'seller-admin:manage' 권한으로 타인 비밀번호 변경 가능")
        void changePassword_WithManagePermission_ShouldReturnValidResponse() {
            Map<String, Object> request = Map.of("newPassword", "NewPassword123!");

            givenSellerUser("org-001", "seller-admin:manage")
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .patch(CHANGE_PASSWORD_PATH, SELLER_ADMIN_ID_2)
                    .then()
                    .statusCode(
                            org.hamcrest.Matchers.anyOf(
                                    org.hamcrest.Matchers.is(HttpStatus.NO_CONTENT.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.BAD_REQUEST.value()),
                                    org.hamcrest.Matchers.is(HttpStatus.NOT_FOUND.value())));
        }
    }
}
