package com.ryuqq.marketplace.application.refund.dto.command;

/** 타임아웃 환불 아웃박스 복구 명령. */
public record RecoverTimeoutRefundOutboxCommand(int batchSize, long timeoutSeconds) {}
