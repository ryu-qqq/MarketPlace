package com.ryuqq.marketplace.integration;

import static io.restassured.RestAssured.given;

import com.ryuqq.marketplace.bootstrap.MarketPlaceApplication;
import com.ryuqq.marketplace.integration.config.StubExternalClientConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * E2E 테스트 Base 클래스.
 *
 * <p>REST Assured를 사용한 End-to-End 통합 테스트의 공통 설정을 제공합니다.
 *
 * <p>특징: - 실제 Spring Boot 애플리케이션 전체 컨텍스트 로드 - H2 In-Memory Database 사용 (test 프로파일) - REST Assured를
 * 통한 HTTP 요청/응답 검증 - 트랜잭션 자동 롤백 (선택 시 @Transactional 추가)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@SpringBootTest(
        classes = MarketPlaceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(StubExternalClientConfig.class)
@ActiveProfiles("test")
public abstract class E2ETestBase {

    @LocalServerPort protected int port;

    private static final String BASE_PATH = "/api/v1/market";

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = BASE_PATH;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // ===== 인증 컨텍스트 헬퍼 메서드 =====

    /**
     * SUPER_ADMIN 인증 헤더를 포함한 요청 시작.
     *
     * <p>GatewayAuthenticationFilter가 X-User-* 헤더를 파싱하여 SUPER_ADMIN SecurityContext를 생성합니다.
     */
    protected RequestSpecification givenSuperAdmin() {
        return given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("X-User-Id", "super-admin-001")
                .header("X-Tenant-Id", "tenant-001")
                .header("X-Organization-Id", "org-admin-001")
                .header("X-User-Roles", "ROLE_SUPER_ADMIN")
                .header("X-User-Permissions", "*:*")
                .header("X-User-Email", "admin@marketplace.com")
                .header("X-Authenticated", "true");
    }

    /**
     * 셀러 사용자 인증 헤더를 포함한 요청 시작.
     *
     * @param organizationId 셀러의 조직 ID (seller.auth_organization_id와 매핑)
     * @param permissions 부여할 권한 목록
     */
    protected RequestSpecification givenSellerUser(String organizationId, String... permissions) {
        return given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("X-User-Id", "seller-user-001")
                .header("X-Tenant-Id", "tenant-001")
                .header("X-Organization-Id", organizationId)
                .header("X-User-Roles", "ROLE_USER")
                .header("X-User-Permissions", String.join(",", permissions))
                .header("X-User-Email", "seller@example.com")
                .header("X-Authenticated", "true");
    }

    /**
     * 인증만 된 사용자 요청 시작 (특별한 권한 없음).
     *
     * <p>{@code @access.authenticated()} 검사는 통과하지만, {@code @access.superAdmin()}이나 권한 기반 검사는 실패합니다.
     */
    protected RequestSpecification givenAuthenticatedUser() {
        return given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("X-User-Id", "user-001")
                .header("X-Tenant-Id", "tenant-001")
                .header("X-User-Roles", "ROLE_USER")
                .header("X-User-Permissions", "")
                .header("X-User-Email", "user@example.com")
                .header("X-Authenticated", "true");
    }

    /**
     * 비인증 요청 시작 (인증 헤더 없음).
     *
     * <p>SecurityConfig의 {@code anyRequest().authenticated()} 규칙에 의해 permitAll이 아닌 엔드포인트는 401
     * Unauthorized를 반환합니다.
     */
    protected RequestSpecification givenUnauthenticated() {
        return given().contentType(ContentType.JSON).accept(ContentType.JSON);
    }

    /**
     * Admin API 요청 시작 (SUPER_ADMIN 인증 헤더 포함).
     *
     * @deprecated {@link #givenSuperAdmin()} 사용을 권장합니다.
     */
    @Deprecated
    protected RequestSpecification givenAdmin() {
        return givenSuperAdmin();
    }

    /**
     * JSON Body를 포함한 Admin API 요청 시작.
     *
     * @deprecated {@link #givenSuperAdmin()} 사용을 권장합니다.
     */
    @Deprecated
    protected RequestSpecification givenAdminJson() {
        return givenSuperAdmin();
    }
}
