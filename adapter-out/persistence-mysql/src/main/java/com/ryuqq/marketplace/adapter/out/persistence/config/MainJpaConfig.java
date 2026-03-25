package com.ryuqq.marketplace.adapter.out.persistence.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.Nullable;
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
 * <p>Flyway 마이그레이션도 명시적으로 설정합니다. Spring Boot 자동 설정의 Flyway는 커스텀 DataSource 사용 시 동작하지 않으므로, {@code
 * spring.flyway.enabled=true}일 때 직접 Flyway 빈을 생성합니다.
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

    @Bean(initMethod = "migrate")
    @Primary
    @ConditionalOnProperty(
            name = "spring.flyway.enabled",
            havingValue = "true",
            matchIfMissing = true)
    public Flyway mainFlyway(@Qualifier("dataSource") DataSource dataSource) {
        Flyway flyway =
                Flyway.configure()
                        .dataSource(dataSource)
                        .locations("classpath:db/migration")
                        .table("flyway_schema_history")
                        .baselineOnMigrate(true)
                        .baselineVersion("87")
                        .validateOnMigrate(true)
                        .outOfOrder(false)
                        .cleanDisabled(true)
                        .load();
        flyway.repair();
        return flyway;
    }

    @Bean
    @Primary
    @SuppressWarnings("unused")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier("dataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder,
            PersistenceManagedTypes persistenceManagedTypes,
            @Nullable Flyway mainFlyway) {
        List<String> filtered =
                persistenceManagedTypes.getManagedClassNames().stream()
                        .filter(
                                name ->
                                        !name.startsWith(
                                                "com.ryuqq.marketplace.adapter.out.persistence.legacy."))
                        .toList();
        PersistenceManagedTypes mainManagedTypes =
                PersistenceManagedTypes.of(filtered, List.of());
        return builder.dataSource(dataSource)
                .managedTypes(mainManagedTypes)
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
