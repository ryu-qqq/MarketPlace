package com.ryuqq.marketplace.application.brand.port.out.query;

public record AliasMatchResult(
    Long brandId,
    String brandCode,
    String canonicalName,
    String nameKo,
    double confidence
) {}
