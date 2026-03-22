package com.ryuqq.marketplace.integration.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Testcontainers E2E 테스트용 Spring Boot Application.
 *
 * <p>MarketPlaceApplication과 동일한 ComponentScan 범위를 유지하되,
 * Redis Adapter를 포함(excludeFilter에서 redis 제외)합니다.
 * Testcontainers로 실제 Redis를 기동하므로 Redis 관련 빈을 활성화합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(
        basePackages = {
            "com.ryuqq.marketplace.bootstrap.config",
            "com.ryuqq.marketplace.domain",
            "com.ryuqq.marketplace.application",
            "com.ryuqq.marketplace.adapter.in.rest",
            "com.ryuqq.marketplace.adapter.out.persistence",
            "com.ryuqq.marketplace.adapter.out.security",
            "com.ryuqq.marketplace.adapter.out.client"
        },
        excludeFilters =
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = {
                            "com\\.ryuqq\\.marketplace\\.adapter\\.out\\.persistence\\.legacy\\..*",
                            "com\\.ryuqq\\.marketplace\\.application\\.legacy\\..*",
                            "com\\.ryuqq\\.marketplace\\.application\\.legacyconversion\\..*",
                            "com\\.ryuqq\\.marketplace\\.application\\.legacyseller\\..*",
                            "com\\.ryuqq\\.marketplace\\.application\\.legacyshipment\\..*",
                            "com\\.ryuqq\\.marketplace\\.application\\.legacycommoncode\\..*",
                            "com\\.ryuqq\\.marketplace\\.application\\.legacyauth\\..*"
                        }))
@EnableJpaRepositories(
        basePackages = "com.ryuqq.marketplace.adapter.out.persistence",
        excludeFilters =
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern =
                                "com\\.ryuqq\\.marketplace\\.adapter\\.out\\.persistence\\.legacy\\..*"))
public class TestContainersWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestContainersWebApplication.class, args);
    }
}
