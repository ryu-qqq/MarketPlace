package com.ryuqq.marketplace.adapter.in.rest.legacy.qna.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 세토프 호환 QnA 답변 등록 요청 DTO.
 *
 * <p>POST /api/v1/legacy/qna/reply
 */
public record LegacyCreateQnaAnswerRequest(
        @NotNull(message = "qnaId는 필수입니다") long qnaId,
        @Valid LegacyQnaContentsRequest qnaContents,
        @Size(max = 3, message = "질문 답변에 등록 할 수 있는 사진은 최대 3장입니다.")
                List<LegacyQnaImageRequest> qnaImages) {

    public LegacyCreateQnaAnswerRequest {
        qnaImages = qnaImages != null ? List.copyOf(qnaImages) : List.of();
    }
}
