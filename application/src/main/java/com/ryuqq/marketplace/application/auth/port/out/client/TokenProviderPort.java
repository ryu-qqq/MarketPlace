package com.ryuqq.marketplace.application.auth.port.out.client;

import com.ryuqq.marketplace.application.auth.dto.response.TokenPairResponse;

/**
 * Token Provider Port
 *
 * <p>토큰 생성 및 검증을 위한 아웃바운드 포트입니다.
 *
 * <p><strong>구현체:</strong>
 *
 * <ul>
 *   <li>JwtTokenProviderAdapter - jjwt 기반 JWT 토큰 제공자
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface TokenProviderPort {

    /**
     * Access Token과 Refresh Token 쌍을 생성합니다.
     *
     * @param memberId 회원 ID
     * @return 토큰 쌍 응답
     */
    TokenPairResponse generateTokenPair(String memberId);

    /**
     * Access Token에서 회원 ID를 추출합니다.
     *
     * @param accessToken Access Token
     * @return 회원 ID
     */
    String extractMemberId(String accessToken);

    /**
     * Access Token의 유효성을 검증합니다.
     *
     * @param accessToken Access Token
     * @return 유효 여부
     */
    boolean validateAccessToken(String accessToken);

    /**
     * Refresh Token의 유효성을 검증합니다.
     *
     * @param refreshToken Refresh Token
     * @return 유효 여부
     */
    boolean validateRefreshToken(String refreshToken);

    /**
     * Refresh Token에서 회원 ID를 추출합니다.
     *
     * @param refreshToken Refresh Token
     * @return 회원 ID
     */
    String extractMemberIdFromRefreshToken(String refreshToken);

    /**
     * Access Token이 만료되었는지 확인합니다.
     *
     * @param accessToken Access Token
     * @return 만료 여부
     */
    boolean isAccessTokenExpired(String accessToken);
}
