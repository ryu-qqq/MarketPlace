package com.ryuqq.marketplace.application.outboundproduct.dto.command;

/**
 * 연동 재처리 커맨드.
 *
 * @param outboxId 재처리 대상 Outbox ID
 */
public record RetryOutboundSyncCommand(long outboxId) {}
