package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response;

import java.util.List;

/**
 * 세토프 호환 QnA 답변 등록/수정 응답 DTO.
 *
 * <p>POST/PUT /api/v1/legacy/qna/reply
 */
public record LegacyCreateQnaAnswerResponse(
        long qnaId,
        long qnaAnswerId,
        String qnaType,
        String qnaStatus,
        List<LegacyQnaImageResponse> qnaImages) {}
