package com.ryuqq.marketplace.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * MarketPlace Web API Application Entry Point.
 *
 * <p>헥사고날 아키텍처 기반 커머스 플랫폼.
 *
 * <p>Legacy API는 별도 모듈(bootstrap-legacy-api)로 분리되었습니다. 이 서버는 새 API만 서빙합니다.
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
public class MarketPlaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketPlaceApplication.class, args);
    }
}
