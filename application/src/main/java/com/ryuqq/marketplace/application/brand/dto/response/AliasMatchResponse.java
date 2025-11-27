package com.ryuqq.marketplace.application.brand.dto.response;

import java.util.List;

public record AliasMatchResponse(
    List<AliasMatch> matches
) {
    public record AliasMatch(
        Long brandId,
        String brandCode,
        String canonicalName,
        String nameKo,
        double confidence
    ) {}

    public static AliasMatchResponse empty() {
        return new AliasMatchResponse(List.of());
    }
}
