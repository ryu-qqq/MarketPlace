package com.ryuqq.marketplace.adapter.out.persistence.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * QueryDSL 및 JPA Auditing 설정.
 *
 * <p>JPAQueryFactory 빈 등록과 JPA Auditing 활성화를 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
@EnableJpaAuditing
public class QueryDslConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
