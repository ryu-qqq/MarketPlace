package com.ryuqq.marketplace.application.brand.dto.command;

public record UpdateBrandCommand(
    Long brandId,
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
    public UpdateBrandCommand {
        if (brandId == null) {
            throw new IllegalArgumentException("brandId is required");
        }
    }
}
