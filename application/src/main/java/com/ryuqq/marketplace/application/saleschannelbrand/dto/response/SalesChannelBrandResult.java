package com.ryuqq.marketplace.application.saleschannelbrand.dto.response;

import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import java.time.Instant;

/** 외부채널 브랜드 조회 결과 DTO. */
public record SalesChannelBrandResult(
        Long id,
        Long salesChannelId,
        String externalBrandCode,
        String externalBrandName,
        String status,
        Instant createdAt,
        Instant updatedAt) {

    public static SalesChannelBrandResult from(SalesChannelBrand brand) {
        return new SalesChannelBrandResult(
                brand.idValue(),
                brand.salesChannelId(),
                brand.externalBrandCode(),
                brand.externalBrandName(),
                brand.status().name(),
                brand.createdAt(),
                brand.updatedAt());
    }
}
