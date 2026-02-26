package com.ryuqq.marketplace.adapter.in.rest.legacy.seller.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response.LegacySellerResponse;
import com.ryuqq.marketplace.application.legacyseller.dto.response.LegacySellerResult;
import org.springframework.stereotype.Component;

/** 레거시 셀러 조회 결과 → 응답 DTO 변환 매퍼. */
@Component
public class LegacySellerQueryApiMapper {

    /** LegacySellerResult → LegacySellerResponse. */
    public LegacySellerResponse toSellerResponse(LegacySellerResult result) {
        return new LegacySellerResponse(result.sellerId(), result.sellerName(), result.bizNo());
    }
}
