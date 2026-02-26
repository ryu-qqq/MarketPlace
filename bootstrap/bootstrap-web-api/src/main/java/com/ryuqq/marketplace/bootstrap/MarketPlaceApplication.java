package com.ryuqq.marketplace.bootstrap;

import com.ryuqq.marketplace.bootstrap.config.LegacyModuleConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * MarketPlace Web API Application Entry Point.
 *
 * <p>헥사고날 아키텍처 기반 커머스 플랫폼.
 *
 * <p>Legacy 모듈(세토프 DB)은 persistence.legacy.enabled=true 조건으로만 활성화됩니다. 기본 ComponentScan에서 모든 legacy
 * 패키지를 제외하고, {@link LegacyModuleConfig}에서 조건부로 스캔합니다.
 *
 * <p>{@code @EnableJpaRepositories}와 {@code @EntityScan}은 메인 persistence 모듈 범위를 설정합니다. Legacy 모듈은
 * {@code LegacyJpaConfig}에서 별도의 EntityManagerFactory로 관리됩니다.
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
                            "com\\.ryuqq\\.marketplace\\.adapter\\.in\\.rest\\.legacy\\..*",
                            "com\\.ryuqq\\.marketplace\\.application\\.legacy.*"
                        }))
@Import(LegacyModuleConfig.class)
@EnableJpaRepositories(
        basePackages = "com.ryuqq.marketplace.adapter.out.persistence",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager",
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
