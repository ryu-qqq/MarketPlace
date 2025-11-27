package com.ryuqq.marketplace.application.brand.dto.command;

public record CreateBrandCommand(
    String code,
    String canonicalName,
    String nameKo,
    String nameEn,
    String shortName,
    String country,
    String department,
    boolean isLuxury,
    String officialWebsite,
    String logoUrl,
    String description
) {
    public CreateBrandCommand {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code is required");
        }
        if (canonicalName == null || canonicalName.isBlank()) {
            throw new IllegalArgumentException("canonicalName is required");
        }
    }
}
