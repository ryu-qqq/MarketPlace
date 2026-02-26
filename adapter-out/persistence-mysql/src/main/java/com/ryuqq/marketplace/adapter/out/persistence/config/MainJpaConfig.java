package com.ryuqq.marketplace.adapter.out.persistence.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 메인 JPA 인프라 설정.
 *
 * <p>Legacy 모듈({@code LegacyJpaConfig})이 별도의 {@code DataSource}와 {@code
 * LocalContainerEntityManagerFactoryBean}을 정의하면 Spring Boot 자동 설정의
 * {@code @ConditionalOnMissingBean} 조건으로 인해 메인 빈이 생성되지 않습니다.
 *
 * <p>이 설정 클래스는 메인 DataSource, EntityManagerFactory, TransactionManager를 {@code @Primary}로 명시적으로
 * 정의하여 Legacy 모듈과 공존할 수 있도록 합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Configuration
public class MainJpaConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder,
            PersistenceManagedTypes persistenceManagedTypes) {
        return builder.dataSource(dataSource)
                .managedTypes(persistenceManagedTypes)
                .persistenceUnit("main")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
