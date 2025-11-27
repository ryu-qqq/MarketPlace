package com.ryuqq.marketplace.application.category.dto.query;

public record CategorySearchQuery(
    String keyword,
    String department,
    String productGroup,
    String genderScope,
    Boolean onlyLeaf,
    Boolean onlyListable
) {
    public static CategorySearchQuery of(String keyword) {
        return new CategorySearchQuery(keyword, null, null, null, null, null);
    }

    public static CategorySearchQuery leafOnly(String department, String productGroup) {
        return new CategorySearchQuery(null, department, productGroup, null, true, true);
    }

    public boolean hasKeyword() {
        return keyword != null && !keyword.isBlank();
    }
}
