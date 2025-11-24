package com.ryuqq.marketplace.domain.externalmall;

/**
 * 외부몰 인증 설정을 위한 Sealed Interface
 * 각 외부몰은 자신만의 인증 설정 구현체를 가진다
 */
public sealed interface AuthConfig
        permits OcoAuthConfig, SellicAuthConfig, LfAuthConfig, BuymaAuthConfig {

    /**
     * 인증 설정의 유효성을 검증한다
     *
     * @throws IllegalArgumentException 인증 설정이 유효하지 않을 때
     */
    void validate();
}
