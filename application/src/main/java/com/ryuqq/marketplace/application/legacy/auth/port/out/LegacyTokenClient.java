package com.ryuqq.marketplace.application.legacy.auth.port.out;

import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacyTokenResult;

/**
 * 레거시 JWT 토큰 클라이언트 포트.
 *
 * <p>레거시 호환 HS256 JWT 토큰의 발급과 검증을 담당합니다.
 */
public interface LegacyTokenClient {

    /**
     * 레거시 JWT 토큰을 발급합니다.
     *
     * @param email subject (이메일)
     * @param sellerId 셀러 ID
     * @param roleType 역할 (MASTER, SELLER)
     * @return 토큰 발급 결과 (accessToken + refreshToken)
     */
    LegacyTokenResult generateToken(String email, long sellerId, String roleType);

    /**
     * 토큰에서 subject(이메일)를 추출합니다. 만료된 토큰도 subject 추출 가능.
     *
     * @param token JWT 토큰
     * @return subject (이메일)
     */
    String extractSubject(String token);

    /**
     * 토큰이 유효한지 검증합니다.
     *
     * @param token JWT 토큰
     * @return 유효하면 true
     */
    boolean isValid(String token);

    /**
     * 토큰이 만료되었는지 확인합니다.
     *
     * @param token JWT 토큰
     * @return 만료되었으면 true
     */
    boolean isExpired(String token);

    /**
     * 토큰에서 셀러 ID를 추출합니다. 만료된 토큰도 추출 가능.
     *
     * @param token JWT 토큰
     * @return 셀러 ID
     */
    long extractSellerId(String token);

    /**
     * 토큰에서 역할을 추출합니다. 만료된 토큰도 추출 가능.
     *
     * @param token JWT 토큰
     * @return 역할 (MASTER, SELLER)
     */
    String extractRole(String token);
}
