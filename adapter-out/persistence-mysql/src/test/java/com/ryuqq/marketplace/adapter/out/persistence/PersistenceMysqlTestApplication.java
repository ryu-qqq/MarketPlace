package com.ryuqq.marketplace.adapter.out.persistence;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * PersistenceMysqlTestApplication - persistence-mysql 모듈 테스트용 부트스트랩.
 *
 * <p>{@code @DataJpaTest} 등이 사용할 {@code @SpringBootConfiguration}을 제공합니다.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackages = "com.ryuqq.marketplace.adapter.out.persistence")
public class PersistenceMysqlTestApplication {}
