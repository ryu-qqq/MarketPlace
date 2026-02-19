package com.ryuqq.marketplace.bootstrap.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Worker Application 진입점.
 *
 * <p>SQS 기반 검수 파이프라인 워커 애플리케이션입니다.
 *
 * <ul>
 *   <li>Inspection Scoring Consumer (AI 채점)
 *   <li>Inspection Enhancement Consumer (LLM 보강)
 *   <li>Inspection Verification Consumer (최종 검증)
 * </ul>
 */
@SpringBootApplication(
        scanBasePackages = {
            "com.ryuqq.marketplace.bootstrap.worker",
            "com.ryuqq.marketplace.adapter.in.sqs",
            "com.ryuqq.marketplace.application",
            "com.ryuqq.marketplace.adapter.out"
        })
@EntityScan(basePackages = "com.ryuqq.marketplace.adapter.out.persistence")
@EnableJpaRepositories(basePackages = "com.ryuqq.marketplace.adapter.out.persistence")
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
