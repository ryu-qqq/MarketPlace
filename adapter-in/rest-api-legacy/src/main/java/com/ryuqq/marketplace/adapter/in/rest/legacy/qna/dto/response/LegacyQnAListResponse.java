package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response;

/**
 * 세토프 QnA(문의) 목록 호환 응답 DTO.
 *
 * @deprecated {@link LegacyFetchQnaResponse}로 대체되었습니다.
 */
@Deprecated(forRemoval = true)
public record LegacyQnAListResponse(
        long qnaId, String questionType, String status, String createdAt) {}
