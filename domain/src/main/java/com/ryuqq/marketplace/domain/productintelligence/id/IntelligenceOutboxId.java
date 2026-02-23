package com.ryuqq.marketplace.domain.productintelligence.id;

/** Intelligence Pipeline Outbox ID Value Object. */
public record IntelligenceOutboxId(Long value) {

    public static IntelligenceOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("IntelligenceOutboxId 값은 null일 수 없습니다");
        }
        return new IntelligenceOutboxId(value);
    }

    public static IntelligenceOutboxId forNew() {
        return new IntelligenceOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
