package com.ryuqq.marketplace.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Setof Commerce Application Entry Point.
 *
 * <p>Spring Boot 애플리케이션의 메인 클래스입니다. 헥사고날 아키텍처 기반으로 구성된 커머스 플랫폼입니다.
 *
 * <p>Bootstrap 모듈별로 필요한 Adapter만 스캔하도록 명시적으로 패키지를 지정합니다. 이를 통해 불필요한 빈 생성을 방지하고 애플리케이션 시작 시간을
 * 최적화합니다.
 *
 * <p>현재 web-api 부트스트랩에서 스캔하는 모듈:
 *
 * <ul>
 *   <li>config - 공통 설정 (Clock)
 *   <li>domain - 도메인 레이어 (Aggregate, VO, Event)
 *   <li>application - 애플리케이션 레이어 (UseCase, Service)
 *   <li>adapter-in:rest-api - REST API 어댑터 (Controller, DTO)
 *   <li>adapter-out:persistence-mysql - MySQL 영속성 어댑터 (Entity, Repository)
 *   <li>adapter-out:persistence-redis - Redis 영속성 어댑터 (Cache)
 *   <li>adapter-out:security - Security 어댑터 (JWT, Password Encoder)
 *   <li>adapter-out:portone-client - PortOne 외부 API 어댑터 (계좌 검증)
 * </ul>
 *
 * @author ryuqq
 * @since 1.0.0
 */
@SpringBootApplication(
        scanBasePackages = {
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
