package com.ryuqq.marketplace.application.brand.dto.command;

public record UpdateAliasConfidenceCommand(
    Long brandId,
    Long aliasId,
    Double confidence
) {
    public UpdateAliasConfidenceCommand {
        if (brandId == null) {
            throw new IllegalArgumentException("brandId is required");
        }
        if (aliasId == null) {
            throw new IllegalArgumentException("aliasId is required");
        }
        if (confidence == null || confidence < 0.0 || confidence > 1.0) {
            throw new IllegalArgumentException("confidence must be between 0.0 and 1.0");
        }
    }
}
