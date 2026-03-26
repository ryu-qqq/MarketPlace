package com.ryuqq.marketplace.application.inboundqna.dto.external;

/**
 * 외부 판매채널에서 수신한 QnA 원본 데이터.
 *
 * @param parentExternalQnaId 부모 QnA ID (대댓글일 때, null → 최상위 질문)
 */
public record ExternalQnaPayload(
        String externalQnaId,
        String parentExternalQnaId,
        String qnaType,
        String questionTitle,
        String questionContent,
        String questionAuthor,
        String externalProductId,
        String externalOrderId,
        String rawPayload) {}
