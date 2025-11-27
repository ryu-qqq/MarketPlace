package com.ryuqq.marketplace.application.brand.dto.command;

public record AddBrandAliasCommand(
    Long brandId,
    String aliasName,
    String sourceType,
    Long sellerId,
    String mallCode,
    double confidence
) {
    public AddBrandAliasCommand {
        if (brandId == null) {
            throw new IllegalArgumentException("brandId is required");
        }
        if (aliasName == null || aliasName.isBlank()) {
            throw new IllegalArgumentException("aliasName is required");
        }
        if (sourceType == null || sourceType.isBlank()) {
            throw new IllegalArgumentException("sourceType is required");
        }
        if (confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("confidence must be between 0.0 and 1.0");
        }
    }
}
