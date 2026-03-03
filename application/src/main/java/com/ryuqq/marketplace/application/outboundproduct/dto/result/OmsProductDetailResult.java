package com.ryuqq.marketplace.application.outboundproduct.dto.result;

import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;

/**
 * OMS 상품 상세 결과 DTO (상품 상세 + syncSummary).
 *
 * @param productGroup 상품그룹 상세 정보
 * @param syncSummary 연동 통계
 */
public record OmsProductDetailResult(
        ProductGroupDetailCompositeResult productGroup, SyncSummaryResult syncSummary) {}
