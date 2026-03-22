package com.ryuqq.marketplace.integration.container;

import static io.restassured.RestAssured.given;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.ryuqq.marketplace.bootstrap.legacy.LegacyApiApplication;
import com.ryuqq.marketplace.integration.legacy.config.LegacyStubConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

/**
 * 레거시 API Testcontainers 기반 E2E 테스트 Base 클래스.
 *
 * <p>MySQL + Redis Testcontainers를 사용하는 레거시 API 통합 테스트의 공통 설정입니다.
 *
 * <p>특징:
 *
 * <ul>
 *   <li>LegacyApiApplication 컨텍스트 로드
 *   <li>실제 MySQL 컨테이너 사용 (레거시 DataSource도 동일 컨테이너)
 *   <li>실제 Redis 컨테이너 사용 (Redisson Mock 불필요)
 *   <li>레거시 JWT 인증: Authorization: Bearer 헤더
 *   <li>basePath: "" (레거시 전용 경로)
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("container")
@SpringBootTest(
        classes = LegacyApiApplication.class,
        webEnvironment = RANDOM_PORT,
        properties = {
            "spring.main.allow-bean-definition-overriding=true",
            "spring.autoconfigure.exclude="
                    + "com.ryuqq.authhub.sdk.autoconfigure.AuthHubAutoConfiguration,"
                    + "com.ryuqq.fileflow.sdk.autoconfigure.FileFlowAutoConfiguration"
        })
@Import({TestContainersExternalMockConfig.class, LegacyStubConfig.class})
@ActiveProfiles("test")
@TestPropertySource(
        properties = {
            // 레거시 JPA 설정 활성화
            "persistence.legacy.enabled=true",
            // 레거시 DataSource → Testcontainers MySQL 사용 (DynamicPropertySource에서 오버라이드)
            // Flyway 비활성화 (ddl-auto create 사용)
            "persistence.legacy.flyway.locations=classpath:db/empty-legacy-migrations",
            "persistence.legacy.jpa.ddl-auto=create-drop"
        })
public abstract class ContainerLegacyE2ETestBase {

    @LocalServerPort protected int port;

    private static final String STUB_TOKEN = "stub-token";

    @DynamicPropertySource
    static void configureContainers(DynamicPropertyRegistry registry) {
        TestContainersConfig.overrideProperties(registry);

        // 레거시 DataSource도 동일한 MySQL 컨테이너 사용
        registry.add(
                "persistence.legacy.datasource.driver-class-name",
                () -> "com.mysql.cj.jdbc.Driver");
        registry.add(
                "persistence.legacy.datasource.jdbc-url",
                () -> TestContainersConfig.MYSQL.getJdbcUrl());
        registry.add(
                "persistence.legacy.datasource.username", TestContainersConfig.MYSQL::getUsername);
        registry.add(
                "persistence.legacy.datasource.password", TestContainersConfig.MYSQL::getPassword);
    }

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // ===== 인증 컨텍스트 헬퍼 메서드 =====

    /**
     * 레거시 인증 헤더를 포함한 요청 시작.
     *
     * <p>StubLegacyTokenClient가 모든 토큰을 valid로 처리합니다. sellerId=10, role=MASTER,
     * email=stub@example.com 반환.
     */
    protected RequestSpecification givenLegacyAuth() {
        return given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + STUB_TOKEN);
    }

    /** 비인증 요청 시작 (인증 헤더 없음). */
    protected RequestSpecification givenUnauthenticated() {
        return given().contentType(ContentType.JSON).accept(ContentType.JSON);
    }
}
