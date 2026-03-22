package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 세토프 호환 QnA 답변 수정 요청 DTO.
 *
 * <p>PUT /api/v1/legacy/qna/reply
 */
public record LegacyUpdateQnaAnswerRequest(
        @NotNull(message = "qnaAnswerId는 필수입니다") Long qnaAnswerId,
        @NotNull(message = "qnaId는 필수입니다") long qnaId,
        @Valid LegacyQnaContentsRequest qnaContents,
        @Size(max = 3, message = "질문 답변에 등록 할 수 있는 사진은 최대 3장입니다.")
                List<LegacyQnaImageRequest> qnaImages) {}
