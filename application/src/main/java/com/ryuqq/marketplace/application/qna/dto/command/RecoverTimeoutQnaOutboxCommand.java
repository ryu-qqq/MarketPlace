package com.ryuqq.marketplace.application.qna.dto.command;

/** PROCESSING 타임아웃 QnA 아웃박스 복구 명령. */
public record RecoverTimeoutQnaOutboxCommand(int batchSize, int timeoutSeconds) {}
