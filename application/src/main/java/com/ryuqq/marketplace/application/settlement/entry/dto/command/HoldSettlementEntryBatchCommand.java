package com.ryuqq.marketplace.application.settlement.entry.dto.command;

import java.util.List;

/**
 * 정산 원장 일괄 보류(HOLD) 배치 커맨드.
 *
 * @param entryIds 보류 처리할 정산 원장 ID 목록
 * @param holdReason 보류 사유
 */
public record HoldSettlementEntryBatchCommand(List<String> entryIds, String holdReason) {}
