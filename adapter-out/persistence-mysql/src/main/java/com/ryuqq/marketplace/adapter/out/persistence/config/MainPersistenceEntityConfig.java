package com.ryuqq.marketplace.adapter.out.persistence.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypesScanner;

/**
 * 메인 Persistence Unit의 엔티티 스캔 설정.
 *
 * <p>{@code @EntityScan}은 excludeFilters를 지원하지 않으므로, {@link PersistenceManagedTypes} 빈을 직접 정의하여
 * Legacy 모듈의 엔티티가 메인 EntityManagerFactory에 포함되지 않도록 합니다.
 *
 * <p>Legacy 엔티티는 {@code LegacyJpaConfig}의 별도 EntityManagerFactory에서 관리됩니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Configuration
public class MainPersistenceEntityConfig {

    private static final String BASE_PACKAGE = "com.ryuqq.marketplace.adapter.out.persistence";
    private static final String LEGACY_PACKAGE_SEGMENT = ".persistence.legacy.";

    @Bean
    PersistenceManagedTypes persistenceManagedTypes(ResourceLoader resourceLoader) {
        PersistenceManagedTypes scanned =
                new PersistenceManagedTypesScanner(resourceLoader).scan(BASE_PACKAGE);

        List<String> filtered =
                scanned.getManagedClassNames().stream()
                        .filter(name -> !name.contains(LEGACY_PACKAGE_SEGMENT))
                        .toList();

        return PersistenceManagedTypes.of(filtered.toArray(String[]::new));
    }
}
