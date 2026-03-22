package com.ryuqq.marketplace.integration.container;

import com.redis.testcontainers.RedisContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Testcontainers 공유 인프라 설정 (싱글톤).
 *
 * <p>MySQL + Redis 컨테이너를 JVM 수명 동안 한 번만 기동하고 모든 테스트에서 공유합니다.
 *
 * @see <a href="https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers">
 *     Singleton Containers</a>
 */
public final class TestContainersConfig {

    private static final Logger log = LoggerFactory.getLogger(TestContainersConfig.class);

    // ========================================
    // MySQL Container (Singleton)
    // ========================================
    @SuppressWarnings("resource")
    public static final MySQLContainer<?> MYSQL =
            new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                    .withDatabaseName("marketplace_test")
                    .withUsername("test")
                    .withPassword("test")
                    .withCommand(
                            "--character-set-server=utf8mb4",
                            "--collation-server=utf8mb4_unicode_ci",
                            "--lower-case-table-names=1",
                            "--default-time-zone=+00:00")
                    .withReuse(true);

    // ========================================
    // Redis Container (Singleton)
    // ========================================
    public static final RedisContainer REDIS =
            new RedisContainer(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379)
                    .withReuse(true);

    static {
        log.info("[TestContainers] MySQL + Redis 컨테이너 시작...");
        MYSQL.start();
        REDIS.start();
        log.info(
                "[TestContainers] MySQL: {}:{}, Redis: {}:{}",
                MYSQL.getHost(),
                MYSQL.getFirstMappedPort(),
                REDIS.getHost(),
                REDIS.getFirstMappedPort());
    }

    private TestContainersConfig() {}

    @DynamicPropertySource
    public static void registerProperties(DynamicPropertyRegistry registry) {
        overrideProperties(registry);
    }

    /**
     * 컨테이너 속성을 DynamicPropertyRegistry에 등록합니다.
     *
     * @param registry DynamicPropertyRegistry
     */
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        // MySQL
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

        // JPA
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.jpa.open-in-view", () -> "false");
        registry.add("spring.flyway.enabled", () -> "false");

        // Redis
        registry.add("redis.enabled", () -> "true");
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getFirstMappedPort());
    }
}
