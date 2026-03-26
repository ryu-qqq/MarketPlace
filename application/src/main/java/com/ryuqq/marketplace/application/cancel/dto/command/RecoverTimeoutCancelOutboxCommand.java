package com.ryuqq.marketplace.application.cancel.dto.command;

/** 타임아웃 취소 아웃박스 복구 명령. */
public record RecoverTimeoutCancelOutboxCommand(int batchSize, long timeoutSeconds) {}
