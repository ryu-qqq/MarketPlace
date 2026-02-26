package com.ryuqq.marketplace.bootstrap.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Worker Application 진입점.
 *
 * <p>SQS 기반 인텔리전스 파이프라인 워커 애플리케이션입니다.
 *
 * <ul>
 *   <li>Intelligence Orchestration Consumer (분석 오케스트레이션)
 *   <li>Intelligence Analysis Consumers (Description, Option, Notice 분석)
 *   <li>Intelligence Aggregation Consumer (분석 결과 집계)
 * </ul>
 */
@SpringBootApplication(
        scanBasePackages = {
            "com.ryuqq.marketplace.bootstrap.worker",
            "com.ryuqq.marketplace.adapter.in.sqs",
            "com.ryuqq.marketplace.application",
            "com.ryuqq.marketplace.adapter.out"
        })
@EnableJpaRepositories(
        basePackages = "com.ryuqq.marketplace.adapter.out.persistence",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager",
        excludeFilters =
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern =
                                "com\\.ryuqq\\.marketplace\\.adapter\\.out\\.persistence\\.legacy\\..*"))
@ConfigurationPropertiesScan(
        basePackages = {
            "com.ryuqq.marketplace.bootstrap.worker",
            "com.ryuqq.marketplace.adapter.in.sqs",
            "com.ryuqq.marketplace.adapter.out.client"
        })
public class WorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }
}
