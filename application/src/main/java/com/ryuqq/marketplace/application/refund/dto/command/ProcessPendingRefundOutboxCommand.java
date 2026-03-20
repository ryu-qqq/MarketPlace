package com.ryuqq.marketplace.application.refund.dto.command;

/** PENDING 상태 환불 아웃박스 처리 명령. */
public record ProcessPendingRefundOutboxCommand(int batchSize, int delaySeconds) {}
