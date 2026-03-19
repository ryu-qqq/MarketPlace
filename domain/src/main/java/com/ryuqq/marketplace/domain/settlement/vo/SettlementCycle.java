package com.ryuqq.marketplace.domain.settlement.vo;

/** 정산 주기. */
public enum SettlementCycle {

    /** 일간 정산. */
    DAILY,

    /** 주간 정산. */
    WEEKLY,

    /** 격주 정산. */
    BIWEEKLY,

    /** 월간 정산. */
    MONTHLY
}
