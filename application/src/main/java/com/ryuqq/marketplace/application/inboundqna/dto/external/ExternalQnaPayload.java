package com.ryuqq.marketplace.application.inboundqna.dto.external;

/** 외부 판매채널에서 수신한 QnA 원본 데이터. */
public record ExternalQnaPayload(
        String externalQnaId,
        String qnaType,
        String questionContent,
        String questionAuthor,
        String externalProductId,
        String externalOrderId,
        String rawPayload) {}
