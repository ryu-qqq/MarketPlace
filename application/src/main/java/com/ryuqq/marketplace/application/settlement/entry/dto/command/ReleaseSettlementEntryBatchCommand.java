package com.ryuqq.marketplace.application.settlement.entry.dto.command;

import java.util.List;

/**
 * 정산 원장 일괄 보류 해제(PENDING) 배치 커맨드.
 *
 * @param entryIds 보류 해제할 정산 원장 ID 목록
 */
public record ReleaseSettlementEntryBatchCommand(List<String> entryIds) {}
