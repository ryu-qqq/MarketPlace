package com.ryuqq.marketplace.application.productgroup.dto.composite;

import java.util.List;

/**
 * 상품 그룹 목록 조회 번들 DTO.
 *
 * <p>ReadFacade에서 조회한 기본 Composite + enrichment + 전체 건수를 묶어 Service로 전달합니다. Service는 이 번들을
 * Assembler에 넘겨 최종 결과를 조립합니다.
 *
 * @param baseComposites 기본 Composition 쿼리 결과 목록
 * @param enrichments 가격 + 옵션 통합 enrichment 결과 목록
 * @param totalElements 전체 건수
 */
public record ProductGroupListBundle(
        List<ProductGroupListCompositeResult> baseComposites,
        List<ProductGroupEnrichmentResult> enrichments,
        long totalElements) {}
