package com.ryuqq.marketplace.integration.container;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * Testcontainers 인프라 Smoke 테스트.
 *
 * <p>MySQL + Redis 컨테이너 기동 확인 및 기본 Health Check를 수행합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("container")
@DisplayName("Testcontainers 인프라 Smoke 테스트")
class ContainerSmokeTest extends ContainerE2ETestBase {

    @Test
    @DisplayName("[SMOKE-1] MySQL 컨테이너 기동 확인")
    void mysqlContainer_IsRunning() {
        assertThat(TestContainersConfig.MYSQL.isRunning()).as("MySQL 컨테이너가 실행 중이어야 합니다.").isTrue();
    }

    @Test
    @DisplayName("[SMOKE-2] Redis 컨테이너 기동 확인")
    void redisContainer_IsRunning() {
        assertThat(TestContainersConfig.REDIS.isRunning()).as("Redis 컨테이너가 실행 중이어야 합니다.").isTrue();
    }

    @Test
    @DisplayName("[SMOKE-3] MySQL JDBC URL 확인")
    void mysqlContainer_HasValidJdbcUrl() {
        String jdbcUrl = TestContainersConfig.MYSQL.getJdbcUrl();
        assertThat(jdbcUrl).as("MySQL JDBC URL이 유효해야 합니다.").isNotBlank().contains("mysql");
    }

    @Test
    @DisplayName("[SMOKE-4] Redis 포트 매핑 확인")
    void redisContainer_HasMappedPort() {
        int mappedPort = TestContainersConfig.REDIS.getFirstMappedPort();
        assertThat(mappedPort).as("Redis 매핑 포트가 유효해야 합니다.").isGreaterThan(0);
    }

    @Test
    @DisplayName("[SMOKE-5] Spring Boot 기동 확인 - 인증 없는 요청 401 반환")
    void springBoot_IsRunning_Returns401ForUnauthenticated() {
        given().when()
                .get("/api/v1/market/qnas")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
