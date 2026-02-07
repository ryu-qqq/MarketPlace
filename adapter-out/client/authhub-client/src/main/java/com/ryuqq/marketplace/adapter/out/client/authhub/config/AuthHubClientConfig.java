package com.ryuqq.marketplace.adapter.out.client.authhub.config;

import com.ryuqq.authhub.sdk.api.AuthApi;
import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.client.AuthHubClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AuthHub Client Configuration.
 *
 * <p>AuthHub SDK의 AuthApi, OnboardingApi 빈을 생성합니다.
 *
 * <p>authhub.base-url 설정이 있을 때만 활성화됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(AuthHubProperties.class)
@ConditionalOnProperty(prefix = "authhub", name = "base-url")
public class AuthHubClientConfig {

    /**
     * AuthHubClient 빈 생성.
     *
     * @param properties AuthHub 설정
     * @return AuthHubClient 인스턴스
     */
    @Bean
    public AuthHubClient authHubClient(AuthHubProperties properties) {
        return AuthHubClient.builder()
                .baseUrl(properties.getBaseUrl())
                .serviceToken(properties.getServiceToken())
                .connectTimeout(properties.getTimeout().getConnect())
                .readTimeout(properties.getTimeout().getRead())
                .build();
    }

    /**
     * AuthApi 빈 생성.
     *
     * @param authHubClient AuthHub 클라이언트
     * @return AuthApi 인스턴스
     */
    @Bean
    public AuthApi authApi(AuthHubClient authHubClient) {
        return authHubClient.auth();
    }

    /**
     * OnboardingApi 빈 생성.
     *
     * @param authHubClient AuthHub 클라이언트
     * @return OnboardingApi 인스턴스
     */
    @Bean
    public OnboardingApi onboardingApi(AuthHubClient authHubClient) {
        return authHubClient.onboarding();
    }
}
