package com.ryuqq.marketplace.application.externalsource.dto.response;

import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import java.time.Instant;

/** 외부 소스 조회 결과 DTO. */
public record ExternalSourceResult(
        Long id,
        String code,
        String name,
        String type,
        String status,
        String description,
        Instant createdAt,
        Instant updatedAt) {

    public static ExternalSourceResult from(ExternalSource source) {
        return new ExternalSourceResult(
                source.idValue(),
                source.codeValue(),
                source.name(),
                source.type().name(),
                source.status().name(),
                source.description(),
                source.createdAt(),
                source.updatedAt());
    }
}
