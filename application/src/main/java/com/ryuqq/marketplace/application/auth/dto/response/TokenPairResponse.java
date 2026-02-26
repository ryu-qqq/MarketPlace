package com.ryuqq.marketplace.application.auth.dto.response;

/**
 * Token Pair Response
 *
 * <p>Access Token과 Refresh Token 쌍을 담는 응답 DTO입니다.
 *
 * @param accessToken Access Token 문자열
 * @param accessTokenExpiresIn Access Token 만료 시간 (초)
 * @param refreshToken Refresh Token 문자열
 * @param refreshTokenExpiresIn Refresh Token 만료 시간 (초)
 * @author development-team
 * @since 1.0.0
 */
public record TokenPairResponse(
        String accessToken,
        long accessTokenExpiresIn,
        String refreshToken,
        long refreshTokenExpiresIn) {}
