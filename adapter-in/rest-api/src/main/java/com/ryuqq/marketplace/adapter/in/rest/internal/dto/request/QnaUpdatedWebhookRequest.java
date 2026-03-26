package com.ryuqq.marketplace.adapter.in.rest.internal.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * QnA 수정 웹훅 요청.
 *
 * <p>자사몰에서 고객이 QnA 내용을 수정했을 때 호출.
 *
 * @param salesChannelId 판매채널 ID
 * @param qnas 수정된 QnA 목록
 */
public record QnaUpdatedWebhookRequest(
        @Positive long salesChannelId, @NotEmpty @Valid List<QnaUpdateItemRequest> qnas) {

    /**
     * QnA 수정 아이템.
     *
     * @param externalQnaId 외부 QnA ID (세토프 QnA PK)
     * @param questionTitle 수정된 제목 (null이면 변경 없음)
     * @param questionContent 수정된 내용 (null이면 변경 없음)
     */
    public record QnaUpdateItemRequest(
            @NotNull Long externalQnaId, String questionTitle, String questionContent) {}
}
