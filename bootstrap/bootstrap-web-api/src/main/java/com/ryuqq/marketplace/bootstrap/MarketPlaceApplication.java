package com.ryuqq.marketplace.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * MarketPlace Web API Application Entry Point.
 *
 * <p>헥사고날 아키텍처 기반 커머스 플랫폼.
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
        })
@EnableJpaRepositories(basePackages = "com.ryuqq.marketplace.adapter.out.persistence")
@EntityScan(basePackages = "com.ryuqq.marketplace.adapter.out.persistence")
public class MarketPlaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketPlaceApplication.class, args);
    }
}
