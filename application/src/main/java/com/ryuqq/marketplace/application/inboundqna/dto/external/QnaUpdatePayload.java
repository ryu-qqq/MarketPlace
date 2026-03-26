package com.ryuqq.marketplace.application.inboundqna.dto.external;

/** QnA 수정 페이로드. */
public record QnaUpdatePayload(
        String externalQnaId, String questionTitle, String questionContent) {}
