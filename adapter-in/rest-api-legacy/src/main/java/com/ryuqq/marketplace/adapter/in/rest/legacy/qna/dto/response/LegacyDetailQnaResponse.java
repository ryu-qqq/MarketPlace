package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.response;

import java.util.Set;

/**
 * 세토프 호환 QnA 상세 조회 응답 DTO.
 *
 * <p>GET /api/v1/legacy/qna/{qnaId}
 */
public record LegacyDetailQnaResponse(
        LegacyFetchQnaResponse qna, Set<LegacyAnswerQnaResponse> answerQnas) {}
