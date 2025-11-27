package com.ryuqq.marketplace.application.brand.dto.command;

public record ChangeBrandStatusCommand(
    Long brandId,
    String newStatus
) {
    public ChangeBrandStatusCommand {
        if (brandId == null) {
            throw new IllegalArgumentException("brandId is required");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new IllegalArgumentException("newStatus is required");
        }
    }
}
