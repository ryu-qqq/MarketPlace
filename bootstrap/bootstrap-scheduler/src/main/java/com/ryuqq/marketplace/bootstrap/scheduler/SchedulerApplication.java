package com.ryuqq.marketplace.bootstrap.scheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Scheduler Application 진입점.
 *
 * <p>Outbox 패턴 기반 스케줄러 애플리케이션입니다.
 *
 * <ul>
 *   <li>SellerAuthOutbox 처리 (Identity 서비스 연동)
 *   <li>타임아웃 Outbox 복구
 * </ul>
 */
@SpringBootApplication(
        scanBasePackages = {
            "com.ryuqq.marketplace.bootstrap.scheduler",
            "com.ryuqq.marketplace.adapter.in.scheduler",
            "com.ryuqq.marketplace.application",
            "com.ryuqq.marketplace.adapter.out"
        },
        excludeName = {"com.ryuqq.marketplace.application.legacyauth"})
@ComponentScan(
        basePackages = {
            "com.ryuqq.marketplace.bootstrap.scheduler",
            "com.ryuqq.marketplace.adapter.in.scheduler",
            "com.ryuqq.marketplace.application",
            "com.ryuqq.marketplace.adapter.out"
        },
        excludeFilters =
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "com\\.ryuqq\\.marketplace\\.application\\.legacyauth\\..*"))
@EnableJpaRepositories(
        basePackages = "com.ryuqq.marketplace.adapter.out.persistence",
        excludeFilters =
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern =
                                "com\\.ryuqq\\.marketplace\\.adapter\\.out\\.persistence\\.legacy\\..*"))
@ConfigurationPropertiesScan(
        basePackages = {
            "com.ryuqq.marketplace.bootstrap.scheduler",
            "com.ryuqq.marketplace.adapter.in.scheduler",
            "com.ryuqq.marketplace.adapter.out.client"
        })
@EnableScheduling
public class SchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SchedulerApplication.class, args);
    }
}
