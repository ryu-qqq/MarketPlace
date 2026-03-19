package com.ryuqq.marketplace.application.settlement.entry.dto.command;

import java.util.List;

/**
 * 정산 원장 일괄 완료(CONFIRMED) 배치 커맨드.
 *
 * @param entryIds 완료 처리할 정산 원장 ID 목록
 */
public record CompleteSettlementEntryBatchCommand(List<String> entryIds) {}
