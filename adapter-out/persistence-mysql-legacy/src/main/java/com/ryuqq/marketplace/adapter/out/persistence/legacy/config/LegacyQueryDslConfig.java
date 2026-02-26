package com.ryuqq.marketplace.adapter.out.persistence.legacy.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * 레거시 QueryDSL 설정.
 *
 * <p>레거시 EntityManager 기반의 JPAQueryFactory 빈을 등록합니다. LegacyJpaConfig에서 등록한
 * legacyEntityManagerFactory를 사용합니다.
 *
 * <p>참고: legacyJpaQueryFactory 빈은 LegacyJpaConfig에서 등록하므로, 이 클래스는 추가 QueryDSL 관련 설정이 필요할 때 확장 포인트로
 * 사용됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "persistence.legacy.enabled", havingValue = "true")
public class LegacyQueryDslConfig {}
