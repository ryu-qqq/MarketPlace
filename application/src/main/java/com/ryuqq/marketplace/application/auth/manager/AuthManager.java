package com.ryuqq.marketplace.application.auth.manager;

import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.dto.response.RefreshResult;
import com.ryuqq.marketplace.application.auth.port.out.client.AuthClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 인증 Manager.
 *
 * <p>AuthClient를 통해 인증 관련 작업을 수행합니다.
 *
 * <p>Service와 Port-Out 사이의 중간 계층으로, 인증 로직의 재사용성과 확장성을 제공합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnBean(AuthClient.class)
public class AuthManager {

    private final AuthClient authClient;

    public AuthManager(AuthClient authClient) {
        this.authClient = authClient;
    }

    /**
     * 로그인을 수행합니다.
     *
     * @param identifier 사용자 식별자 (이메일 또는 사용자명)
     * @param password 비밀번호
     * @return 로그인 결과
     */
    public LoginResult login(String identifier, String password) {
        return authClient.login(identifier, password);
    }

    /**
     * 로그아웃을 수행합니다.
     *
     * @param userId 사용자 ID
     */
    public void logout(String userId) {
        authClient.logout(userId);
    }

    /**
     * 토큰을 갱신합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 토큰 갱신 결과
     */
    public RefreshResult refresh(String refreshToken) {
        return authClient.refresh(refreshToken);
    }

    /**
     * 현재 사용자 정보를 조회합니다.
     *
     * @param accessToken 액세스 토큰
     * @return 사용자 정보
     */
    public MyInfoResult getMyInfo(String accessToken) {
        return authClient.getMyInfo(accessToken);
    }
}
