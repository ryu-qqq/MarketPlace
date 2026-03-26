package com.ryuqq.marketplace.integration.container;

import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.ryuqq.marketplace.application.seller.service.query.ResolveSellerIdByOrganizationService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Testcontainers 기반 E2E 테스트 Base 클래스.
 *
 * <p>MySQL + Redis Testcontainers 컨테이너를 사용하는 통합 테스트의 공통 설정입니다.
 *
 * <p>특징:
 *
 * <ul>
 *   <li>실제 MySQL 컨테이너 사용 (H2 대체)
 *   <li>실제 Redis 컨테이너 사용 (Redisson Mock 불필요)
 *   <li>외부 API 클라이언트만 Mock (TestContainersExternalMockConfig)
 *   <li>JVM당 1회 컨테이너 기동 (TestContainersConfig 싱글톤)
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("container")
@SpringBootTest(
        classes = TestContainersWebApplication.class,
        webEnvironment = RANDOM_PORT,
        properties = {"spring.main.allow-bean-definition-overriding=true"})
@Import(TestContainersExternalMockConfig.class)
@ActiveProfiles("test")
public abstract class ContainerE2ETestBase {

    @LocalServerPort protected int port;

    @Autowired private ResolveSellerIdByOrganizationService resolveSellerIdService;

    private static final String BASE_PATH = "/api/v1/market";

    @DynamicPropertySource
    static void configureContainers(DynamicPropertyRegistry registry) {
        TestContainersConfig.overrideProperties(registry);
    }

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = BASE_PATH;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        resolveSellerIdService.clearCache();
    }

    // ===== 인증 컨텍스트 헬퍼 메서드 =====

    /** SUPER_ADMIN 인증 헤더를 포함한 요청 시작. */
    protected RequestSpecification givenSuperAdmin() {
        return given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("X-User-Id", "super-admin-001")
                .header("X-Tenant-Id", "tenant-001")
                .header("X-Organization-Id", "org-admin-001")
                .header("X-User-Roles", "ROLE_SUPER_ADMIN")
                .header("X-User-Permissions", "*:*")
                .header("X-User-Email", "admin@marketplace.com");
    }

    /**
     * 셀러 사용자 인증 헤더를 포함한 요청 시작.
     *
     * @param organizationId 셀러의 조직 ID
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
                .header("X-User-Email", "seller@example.com");
    }

    /** 인증만 된 사용자 요청 시작 (특별한 권한 없음). */
    protected RequestSpecification givenAuthenticatedUser() {
        return given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("X-User-Id", "user-001")
                .header("X-Tenant-Id", "tenant-001")
                .header("X-User-Roles", "ROLE_USER")
                .header("X-User-Permissions", "")
                .header("X-User-Email", "user@example.com");
    }

    /**
     * 특정 권한을 가진 인증 사용자 요청 시작.
     *
     * @param permissions 부여할 권한 목록
     */
    protected RequestSpecification givenWithPermission(String... permissions) {
        return given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("X-User-Id", "user-001")
                .header("X-Tenant-Id", "tenant-001")
                .header("X-User-Roles", "ROLE_USER")
                .header("X-User-Permissions", String.join(",", permissions))
                .header("X-User-Email", "user@example.com");
    }

    /** 비인증 요청 시작 (인증 헤더 없음). */
    protected RequestSpecification givenUnauthenticated() {
        return given().contentType(ContentType.JSON).accept(ContentType.JSON);
    }
}
