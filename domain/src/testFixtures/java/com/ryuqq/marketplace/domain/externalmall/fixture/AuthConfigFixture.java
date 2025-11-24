package com.ryuqq.marketplace.domain.externalmall.fixture;

import com.ryuqq.marketplace.domain.externalmall.AuthConfig;
import com.ryuqq.marketplace.domain.externalmall.BuymaAuthConfig;
import com.ryuqq.marketplace.domain.externalmall.LfAuthConfig;
import com.ryuqq.marketplace.domain.externalmall.OcoAuthConfig;
import com.ryuqq.marketplace.domain.externalmall.SellicAuthConfig;

/**
 * AuthConfig TestFixture (Object Mother Pattern)
 * 테스트에서 AuthConfig 생성을 단순화하고 일관성을 보장
 */
public class AuthConfigFixture {

    /**
     * OCO 외부몰 인증 설정 생성
     */
    public static AuthConfig ocoAuthConfig() {
        return new OcoAuthConfig(
                "oco-client-id",
                "oco-client-secret",
                "oco-api-key"
        );
    }

    /**
     * Sellic 외부몰 인증 설정 생성
     */
    public static AuthConfig sellicAuthConfig() {
        return new SellicAuthConfig(
                "sellic-api-key",
                "sellic-api-secret"
        );
    }

    /**
     * LF 외부몰 인증 설정 생성
     */
    public static AuthConfig lfAuthConfig() {
        return new LfAuthConfig(
                "lf-access-token"
        );
    }

    /**
     * BUYMA 외부몰 인증 설정 생성
     */
    public static AuthConfig buymaAuthConfig() {
        return new BuymaAuthConfig(
                "buyma-username",
                "buyma-password"
        );
    }

    /**
     * 커스텀 OCO 인증 설정 생성
     */
    public static AuthConfig ocoAuthConfig(String clientId, String clientSecret, String apiKey) {
        return new OcoAuthConfig(clientId, clientSecret, apiKey);
    }

    /**
     * 커스텀 Sellic 인증 설정 생성
     */
    public static AuthConfig sellicAuthConfig(String apiKey, String apiSecret) {
        return new SellicAuthConfig(apiKey, apiSecret);
    }

    /**
     * 커스텀 LF 인증 설정 생성
     */
    public static AuthConfig lfAuthConfig(String accessToken) {
        return new LfAuthConfig(accessToken);
    }

    /**
     * 커스텀 BUYMA 인증 설정 생성
     */
    public static AuthConfig buymaAuthConfig(String username, String password) {
        return new BuymaAuthConfig(username, password);
    }
}
