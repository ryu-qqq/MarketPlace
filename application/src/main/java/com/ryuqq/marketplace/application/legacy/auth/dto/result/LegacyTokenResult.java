package com.ryuqq.marketplace.application.legacy.auth.dto.result;

/**
 * 레거시 토큰 발급 결과.
 *
 * @param accessToken 액세스 토큰
 * @param refreshToken 리프레시 토큰
 * @param email 토큰 subject (이메일)
 * @param expiresInSeconds 리프레시 토큰 만료 시간 (초)
 */
public record LegacyTokenResult(
        String accessToken, String refreshToken, String email, long expiresInSeconds) {}
