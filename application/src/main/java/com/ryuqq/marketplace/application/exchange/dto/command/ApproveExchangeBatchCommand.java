package com.ryuqq.marketplace.application.exchange.dto.command;

import java.util.List;

/** 교환 승인 일괄 처리 명령 (수거 시작). sellerId가 null이면 슈퍼어드민. */
public record ApproveExchangeBatchCommand(
        List<String> exchangeClaimIds, String processedBy, Long sellerId) {}
