package com.ryuqq.marketplace.adapter.out.persistence.legacy;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * PersistenceMysqlLegacyTestApplication - persistence-mysql-legacy 모듈 테스트용 부트스트랩.
 *
 * <p>{@code @DataJpaTest} 등이 사용할 {@code @SpringBootConfiguration}을 제공합니다.
 *
 * <p>레거시 모듈은 단일 DataSource (H2/embedded)를 사용하는 테스트 환경에서 실행됩니다.
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackages = "com.ryuqq.marketplace.adapter.out.persistence.legacy")
public class PersistenceMysqlLegacyTestApplication {}
