package com.ryuqq.marketplace.adapter.out.client.naver.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Naver Commerce OAuth2 토큰 응답.
 *
 * @param accessToken 액세스 토큰
 * @param expiresIn 만료 시간(초)
 * @param tokenType 토큰 타입
 */
public record NaverCommerceTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("token_type") String tokenType) {}
