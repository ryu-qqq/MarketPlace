package com.ryuqq.marketplace.domain.order.vo;

import com.ryuqq.marketplace.domain.common.vo.Money;
import java.time.Instant;

/** 정산 정보. */
public record SettlementInfo(
        int commissionRate,
        Money fee,
        Money expectationSettlementAmount,
        Money settlementAmount,
        int shareRatio,
        Instant expectedSettlementDay,
        Instant settlementDay) {}
