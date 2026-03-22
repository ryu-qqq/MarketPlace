package com.ryuqq.marketplace.application.shadow.dto;

/**
 * Shadow 트랜잭션에서 캡처한 응답 스냅샷.
 *
 * <p>Redis에 저장되어 Python Shadow Lambda가 나중에 GET API 응답과 비교합니다.
 *
 * @param correlationId Gateway가 부여한 추적 ID (X-Shadow-Correlation-Id)
 * @param timestamp 요청 발생 시각 (ISO-8601)
 * @param httpMethod 원본 요청 HTTP 메서드 (POST, PUT, PATCH)
 * @param requestPath 원본 요청 경로
 * @param statusCode Shadow 서버의 HTTP 응답 코드
 * @param responseBody Shadow 서버의 응답 본문 (JSON)
 */
public record ShadowSnapshot(
        String correlationId,
        String timestamp,
        String httpMethod,
        String requestPath,
        int statusCode,
        String responseBody) {}
