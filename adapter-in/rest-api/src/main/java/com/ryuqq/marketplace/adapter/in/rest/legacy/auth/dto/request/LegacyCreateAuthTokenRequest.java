package com.ryuqq.marketplace.adapter.in.rest.legacy.auth.dto.request;

/** 세토프 CreateAuthToken 호환 요청 DTO. */
public record LegacyCreateAuthTokenRequest(String userId, String password, String roleType) {}
