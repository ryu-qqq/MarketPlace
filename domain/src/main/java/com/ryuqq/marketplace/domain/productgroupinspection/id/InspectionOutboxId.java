package com.ryuqq.marketplace.domain.productgroupinspection.id;

/** 상품 그룹 검수 Outbox ID Value Object. */
public record InspectionOutboxId(Long value) {

    public static InspectionOutboxId of(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("InspectionOutboxId 값은 null일 수 없습니다");
        }
        return new InspectionOutboxId(value);
    }

    public static InspectionOutboxId forNew() {
        return new InspectionOutboxId(null);
    }

    public boolean isNew() {
        return value == null;
    }
}
