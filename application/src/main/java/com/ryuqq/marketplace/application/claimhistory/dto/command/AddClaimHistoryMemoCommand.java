package com.ryuqq.marketplace.application.claimhistory.dto.command;

import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;

/** 수기 메모 등록 커맨드. */
public record AddClaimHistoryMemoCommand(
        ClaimType claimType,
        String claimId,
        String message,
        String actorId,
        String actorName) {}
