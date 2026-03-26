package com.ryuqq.marketplace.application.qna.dto.command;

/** QnA 아웃박스 실행 명령 (SQS Consumer에서 수신). */
public record ExecuteQnaOutboxCommand(
        long outboxId, long qnaId, long salesChannelId, String externalQnaId, String outboxType) {}
