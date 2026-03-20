package com.ryuqq.marketplace.application.exchange.dto.command;

/** 타임아웃 교환 아웃박스 복구 명령. */
public record RecoverTimeoutExchangeOutboxCommand(int batchSize, long timeoutSeconds) {}
