package com.ryuqq.marketplace.adapter.out.client.authhub.config;

import com.ryuqq.authhub.sdk.api.AuthApi;
import com.ryuqq.authhub.sdk.api.InternalApi;
import com.ryuqq.authhub.sdk.api.OnboardingApi;
import com.ryuqq.authhub.sdk.api.UserApi;
import com.ryuqq.authhub.sdk.auth.TokenResolver;
import com.ryuqq.authhub.sdk.client.AuthHubClient;
import com.ryuqq.authhub.sdk.client.GatewayClient;
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
     * <p>TokenResolver는 SDK AutoConfiguration이 생성한 ChainTokenResolver를 주입받습니다. ThreadLocal(요청별 사용자
     * 토큰)을 우선 시도하고, 없으면 서비스 토큰으로 폴백합니다.
     *
     * @param properties AuthHub 설정
     * @param tokenResolver SDK AutoConfiguration이 생성한 TokenResolver
     * @return AuthHubClient 인스턴스
     */
    @Bean
    public AuthHubClient authHubClient(AuthHubProperties properties, TokenResolver tokenResolver) {
        return AuthHubClient.builder()
                .baseUrl(properties.getBaseUrl())
                .tokenResolver(tokenResolver)
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

    /**
     * UserApi 빈 생성.
     *
     * <p>사용자 생성 및 역할 할당 API를 제공합니다.
     *
     * @param authHubClient AuthHub 클라이언트
     * @return UserApi 인스턴스
     */
    @Bean
    public UserApi userApi(AuthHubClient authHubClient) {
        return authHubClient.user();
    }

    /**
     * GatewayClient 빈 생성.
     *
     * <p>서비스 토큰 인증을 사용하여 AuthHub Internal API에 직접 연결합니다.
     *
     * @param properties AuthHub 설정
     * @return GatewayClient 인스턴스
     */
    @Bean
    public GatewayClient gatewayClient(AuthHubProperties properties) {
        return GatewayClient.builder()
                .baseUrl(properties.getBaseUrl())
                .serviceName(properties.getServiceCode())
                .serviceToken(properties.getServiceToken())
                .connectTimeout(properties.getTimeout().getConnect())
                .readTimeout(properties.getTimeout().getRead())
                .build();
    }

    /**
     * InternalApi 빈 생성.
     *
     * <p>GatewayClient를 통해 서비스 토큰 인증 기반 Internal API를 제공합니다.
     *
     * @param gatewayClient GatewayClient 인스턴스
     * @return InternalApi 인스턴스
     */
    @Bean
    public InternalApi internalApi(GatewayClient gatewayClient) {
        return gatewayClient.internal();
    }
}
