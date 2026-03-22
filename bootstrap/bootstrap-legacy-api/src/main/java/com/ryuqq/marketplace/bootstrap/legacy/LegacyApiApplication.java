package com.ryuqq.marketplace.bootstrap.legacy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Legacy API Application Entry Point.
 *
 * <p>세토프(레거시) 호환 REST API 전용 서버. 별도 포트(8081)에서 기동되며, 레거시 인증 체계(HS256 JWT)를 사용합니다.
 *
 * <p>메인 API 서버(MarketPlaceApplication)와 완전히 독립된 Spring Boot 애플리케이션입니다.
 */
@SpringBootApplication
@ComponentScan(
        basePackages = {
            "com.ryuqq.marketplace.bootstrap.legacy.config",
            "com.ryuqq.marketplace.domain",
            "com.ryuqq.marketplace.application",
            "com.ryuqq.marketplace.adapter.in.rest.legacy",
            "com.ryuqq.marketplace.adapter.in.rest.common",
            "com.ryuqq.marketplace.adapter.out.persistence",
            "com.ryuqq.marketplace.adapter.out.client",
            "com.ryuqq.marketplace.adapter.out.security"
        })
@EnableJpaRepositories(
        basePackages = "com.ryuqq.marketplace.adapter.out.persistence",
        excludeFilters =
                @org.springframework.context.annotation.ComponentScan.Filter(
                        type = org.springframework.context.annotation.FilterType.REGEX,
                        pattern = "com\\.ryuqq\\.marketplace\\.adapter\\.out\\.persistence\\.legacy\\..*"))
public class LegacyApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegacyApiApplication.class, args);
    }
}
