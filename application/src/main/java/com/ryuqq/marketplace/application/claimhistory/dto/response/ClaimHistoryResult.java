package com.ryuqq.marketplace.application.claimhistory.dto.response;

import java.time.Instant;

/** 클레임 이력 응답 DTO. */
public record ClaimHistoryResult(
        String historyId,
        String type,
        String title,
        String message,
        String actorType,
        String actorId,
        String actorName,
        Instant createdAt) {}
