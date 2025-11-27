package com.ryuqq.marketplace.application.brand.dto.query;

public record BrandSearchQuery(
    String keyword,
    String status,
    Boolean isLuxury,
    String department,
    String country
) {
    public static BrandSearchQuery empty() {
        return new BrandSearchQuery(null, null, null, null, null);
    }

    public static BrandSearchQuery byKeyword(String keyword) {
        return new BrandSearchQuery(keyword, null, null, null, null);
    }
}
