package com.ryuqq.marketplace.adapter.out.persistence.legacy.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * LegacyJpaConfig - 레거시 DB(luxurydb)용 JPA 설정.
 *
 * <p>Strangler Fig 패턴의 전환기에서 레거시 스키마 접근을 위해 사용됩니다. 메인 DataSource와 분리된 별도의 DataSource를 사용하며, 레거시
 * 엔티티와 JPAQueryFactory를 독립적으로 관리합니다.
 *
 * <p>활성화 조건: persistence.legacy.enabled=true
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "persistence.legacy.enabled", havingValue = "true")
@EnableJpaRepositories(
        basePackages = "com.ryuqq.marketplace.adapter.out.persistence.legacy",
        entityManagerFactoryRef = "legacyEntityManagerFactory",
        transactionManagerRef = "legacyTransactionManager")
public class LegacyJpaConfig {

    @Bean
    @ConfigurationProperties("persistence.legacy.datasource")
    public DataSource legacyDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean legacyEntityManagerFactory(
            @Qualifier("legacyDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder) {
        return builder.dataSource(dataSource)
                .packages("com.ryuqq.marketplace.adapter.out.persistence.legacy")
                .persistenceUnit("legacy")
                .properties(
                        Map.of(
                                "hibernate.hbm2ddl.auto", "none",
                                "hibernate.show_sql", "false",
                                "hibernate.physical_naming_strategy",
                                        "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl"))
                .build();
    }

    @Bean
    public PlatformTransactionManager legacyTransactionManager(
            @Qualifier("legacyEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean("legacyJpaQueryFactory")
    public JPAQueryFactory legacyJpaQueryFactory(
            @Qualifier("legacyEntityManagerFactory") EntityManagerFactory emf) {
        return new JPAQueryFactory(SharedEntityManagerCreator.createSharedEntityManager(emf));
    }
}
