package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response;

/**
 * 세토프 QnA(문의) 목록 호환 응답 DTO.
 *
 * <p>GET /qnas - 문의 목록 조회 (페이징)
 */
public record LegacyQnAListResponse(
        long qnaId, String questionType, String status, String createdAt) {}
