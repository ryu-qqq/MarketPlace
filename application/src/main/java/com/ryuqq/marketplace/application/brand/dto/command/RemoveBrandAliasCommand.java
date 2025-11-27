package com.ryuqq.marketplace.application.brand.dto.command;

public record RemoveBrandAliasCommand(
    Long brandId,
    Long aliasId
) {
    public RemoveBrandAliasCommand {
        if (brandId == null) {
            throw new IllegalArgumentException("brandId is required");
        }
        if (aliasId == null) {
            throw new IllegalArgumentException("aliasId is required");
        }
    }
}
