package com.ryuqq.marketplace.application.qna.dto.command;

/** PENDING 상태 QnA 아웃박스 일괄 처리 명령. */
public record ProcessPendingQnaOutboxCommand(int batchSize, int delaySeconds) {}
