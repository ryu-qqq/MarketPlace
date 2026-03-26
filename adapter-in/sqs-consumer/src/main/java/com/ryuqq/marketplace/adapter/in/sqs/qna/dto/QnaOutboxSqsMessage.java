package com.ryuqq.marketplace.adapter.in.sqs.qna.dto;

import java.util.Objects;

/**
 * QnA Outbox SQS 메시지 DTO.
 *
 * @param outboxId Outbox ID
 * @param qnaId QnA ID
 * @param salesChannelId 판매채널 ID
 * @param externalQnaId 외부 QnA ID
 * @param outboxType Outbox 유형 (ANSWER)
 */
public record QnaOutboxSqsMessage(
        Long outboxId, Long qnaId, Long salesChannelId,
        String externalQnaId, String outboxType) {

    public QnaOutboxSqsMessage {
        Objects.requireNonNull(outboxId, "outboxId must not be null");
        Objects.requireNonNull(qnaId, "qnaId must not be null");
        Objects.requireNonNull(outboxType, "outboxType must not be null");
    }
}
