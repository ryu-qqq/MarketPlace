package com.ryuqq.marketplace.application.category.dto.query;

public record CategoryTreeQuery(
    boolean includeInactive,
    String department,
    String productGroup
) {
    public static CategoryTreeQuery activeOnly() {
        return new CategoryTreeQuery(false, null, null);
    }

    public static CategoryTreeQuery all() {
        return new CategoryTreeQuery(true, null, null);
    }

    public static CategoryTreeQuery forDepartment(String department) {
        return new CategoryTreeQuery(false, department, null);
    }
}
