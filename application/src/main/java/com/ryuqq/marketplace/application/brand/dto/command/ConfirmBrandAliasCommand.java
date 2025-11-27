package com.ryuqq.marketplace.application.brand.dto.command;

public record ConfirmBrandAliasCommand(
    Long brandId,
    Long aliasId,
    boolean confirmed
) {
    public ConfirmBrandAliasCommand {
        if (brandId == null) {
            throw new IllegalArgumentException("brandId is required");
        }
        if (aliasId == null) {
            throw new IllegalArgumentException("aliasId is required");
        }
    }
}
