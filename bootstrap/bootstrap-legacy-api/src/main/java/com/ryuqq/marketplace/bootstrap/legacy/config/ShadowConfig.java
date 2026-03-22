package com.ryuqq.marketplace.bootstrap.legacy.config;

import com.ryuqq.marketplace.adapter.in.rest.legacy.shadow.filter.ShadowTransactionFilter;
import com.ryuqq.marketplace.application.shadow.port.out.ShadowSnapshotStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Shadow Traffic 인프라 설정.
 *
 * <p>ShadowTransactionFilter를 등록하여 X-Shadow-Mode: verify 요청의 POST/PUT/PATCH를 실행 후 롤백합니다. 응답 스냅샷은
 * Redis에 저장되고, Python Shadow Lambda가 DMS 복제 후 GET API로 검증합니다.
 */
@Configuration
public class ShadowConfig {

    @Bean
    public ShadowTransactionFilter shadowTransactionFilter(
            @Qualifier("legacyTransactionManager")
                    PlatformTransactionManager legacyTransactionManager,
            ShadowSnapshotStore snapshotStore) {
        return new ShadowTransactionFilter(legacyTransactionManager, snapshotStore);
    }

    @Bean
    public FilterRegistrationBean<ShadowTransactionFilter> shadowTransactionFilterRegistration(
            ShadowTransactionFilter shadowTransactionFilter) {
        FilterRegistrationBean<ShadowTransactionFilter> registration =
                new FilterRegistrationBean<>(shadowTransactionFilter);
        registration.addUrlPatterns("/api/v1/legacy/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        registration.setName("shadowTransactionFilter");
        return registration;
    }
}
