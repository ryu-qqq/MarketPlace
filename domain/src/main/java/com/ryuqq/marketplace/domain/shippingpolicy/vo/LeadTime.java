package com.ryuqq.marketplace.domain.shippingpolicy.vo;

import java.time.LocalTime;

/** 발송 소요일 Value Object. */
public record LeadTime(int minDays, int maxDays, LocalTime cutoffTime) {

    public LeadTime {
        if (minDays < 0) {
            throw new IllegalArgumentException("최소 발송일은 0 이상이어야 합니다");
        }
        if (maxDays < minDays) {
            throw new IllegalArgumentException("최대 발송일은 최소 발송일 이상이어야 합니다");
        }
    }

    public static LeadTime of(int minDays, int maxDays, LocalTime cutoffTime) {
        return new LeadTime(minDays, maxDays, cutoffTime);
    }

    public static LeadTime sameDay(LocalTime cutoffTime) {
        return new LeadTime(0, 0, cutoffTime);
    }

    public static LeadTime nextDay(LocalTime cutoffTime) {
        return new LeadTime(1, 1, cutoffTime);
    }
}
