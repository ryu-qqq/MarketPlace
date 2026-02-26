package com.ryuqq.marketplace.application.auth.port.out.client;

import com.ryuqq.marketplace.application.auth.dto.response.LoginResult;
import com.ryuqq.marketplace.application.auth.dto.response.MyInfoResult;
import com.ryuqq.marketplace.application.auth.dto.response.RefreshResult;

/**
 * 인증 클라이언트.
 *
 * <p>외부 인증 서비스(AuthHub 등)와의 통신을 추상화합니다.
 *
 * <p>Hexagonal Architecture의 Port-Out으로, Application Layer에서 외부 인증 서비스에 대한 의존성을 역전시킵니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface AuthClient {

    /**
     * 로그인을 수행합니다.
     *
     * @param identifier 사용자 식별자 (이메일 또는 사용자명)
     * @param password 비밀번호
     * @return 로그인 결과
     */
    LoginResult login(String identifier, String password);

    /**
     * 로그아웃을 수행합니다.
     *
     * @param userId 사용자 ID
     */
    void logout(String userId);

    /**
     * 토큰을 갱신합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 토큰 갱신 결과
     */
    RefreshResult refresh(String refreshToken);

    /**
     * 사용자 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    MyInfoResult getMyInfo(String userId);
}
