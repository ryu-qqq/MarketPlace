package com.ryuqq.marketplace.adapter.in.rest.brand.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * 별칭 매칭 결과 응답 DTO
 * 브랜드 별칭 검색 시 매칭 후보 브랜드 리스트를 반환하는 record
 */
public record AliasMatchApiResponse(
    String searchedAlias,
    String normalizedAlias,
    List<MatchCandidate> candidates
) {
    /**
     * 매칭 후보 브랜드
     * 신뢰도 점수와 함께 브랜드 후보 정보를 제공
     */
    public record MatchCandidate(
        Long brandId,
        String brandCode,
        String canonicalName,
        String nameKo,
        BigDecimal confidence
    ) {}
}
