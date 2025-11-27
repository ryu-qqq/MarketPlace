package com.ryuqq.marketplace.application.brand.dto.query;

public record ResolveAliasQuery(
    String aliasName
) {
    public ResolveAliasQuery {
        if (aliasName == null || aliasName.isBlank()) {
            throw new IllegalArgumentException("aliasName is required");
        }
    }
}
