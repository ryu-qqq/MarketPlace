package com.ryuqq.marketplace.integration.legacy;

import static io.restassured.RestAssured.given;

import com.ryuqq.marketplace.bootstrap.legacy.LegacyApiApplication;
import com.ryuqq.marketplace.integration.config.StubExternalClientConfig;
import com.ryuqq.marketplace.integration.legacy.config.LegacyStubConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * 레거시 API E2E 테스트 Base 클래스.
 *
 * <p>REST Assured를 사용한 레거시 API End-to-End 통합 테스트의 공통 설정을 제공합니다.
 *
 * <p>특징:
 *
 * <ul>
 *   <li>LegacyApiApplication 컨텍스트 로드
 *   <li>H2 In-Memory Database 사용 (test 프로파일)
 *   <li>레거시 JWT 인증: Authorization: Bearer 헤더
 *   <li>Stub LegacyTokenClient: 모든 토큰 유효 처리, sellerId=10 / role=MASTER / email=stub@example.com 반환
 *   <li>basePath: /api/v1/legacy (E2ETestBase의 /api/v1/market와 독립)
 * </ul>
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@SpringBootTest(
        classes = LegacyApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
            // LegacyJpaConfig와 LegacyApiApplication의 @EnableJpaRepositories 중복 등록 허용
            "spring.main.allow-bean-definition-overriding=true",
            // bootstrap-legacy-api의 config.import(redis.yml, authhub.yml 등) 오버라이드
            // 외부 서비스 AutoConfiguration 비활성화
            "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration,"
                + "org.redisson.spring.starter.RedissonAutoConfigurationV2,"
                + "com.ryuqq.authhub.sdk.autoconfigure.AuthHubAutoConfiguration,"
                + "com.ryuqq.fileflow.sdk.autoconfigure.FileFlowAutoConfiguration"
        })
@Import({StubExternalClientConfig.class, LegacyStubConfig.class})
@ActiveProfiles("test")
@TestPropertySource(
        properties = {
            // 레거시 JPA 설정 활성화 (LegacyJpaConfig @ConditionalOnProperty 게이트 해제)
            "persistence.legacy.enabled=true",
            // 레거시 DataSource는 기본 H2와 동일하게 사용 (HikariCP는 jdbcUrl 키를 사용)
            "persistence.legacy.datasource.driver-class-name=org.h2.Driver",
            "persistence.legacy.datasource.jdbc-url=jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
            "persistence.legacy.datasource.username=sa",
            "persistence.legacy.datasource.password=",
            // H2 환경: MySQL 전용 마이그레이션 스크립트 실행 억제 (빈 위치 → 마이그레이션 없음)
            "persistence.legacy.flyway.locations=classpath:db/empty-legacy-migrations",
            // H2 환경: Hibernate DDL-auto로 스키마 생성 (마이그레이션 대체)
            "persistence.legacy.jpa.ddl-auto=create-drop"
        })
public abstract class LegacyE2ETestBase {

    @LocalServerPort protected int port;

    private static final String STUB_TOKEN = "stub-token";

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
     * <p>LegacyJwtAuthenticationFilter가 Authorization: Bearer 헤더를 파싱하여 LegacyAuthContextHolder에
     * sellerId=10, role=MASTER, email=stub@example.com 를 세팅합니다. StubLegacyTokenClient가 모든 토큰을
     * valid로 처리합니다.
     */
    protected RequestSpecification givenLegacyAuth() {
        return given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Authorization", "Bearer " + STUB_TOKEN);
    }

    /**
     * 비인증 요청 시작 (인증 헤더 없음).
     *
     * <p>LegacySecurityConfig의 anyRequest().authenticated() 규칙에 의해 /api/v1/legacy/auth/** 외 경로는 401
     * Unauthorized를 반환합니다.
     */
    protected RequestSpecification givenUnauthenticated() {
        return given().contentType(ContentType.JSON).accept(ContentType.JSON);
    }
}
