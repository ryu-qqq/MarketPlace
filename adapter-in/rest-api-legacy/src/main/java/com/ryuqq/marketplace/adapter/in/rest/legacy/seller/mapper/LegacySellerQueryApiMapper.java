package com.ryuqq.marketplace.adapter.in.rest.legacy.seller.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response.LegacySellerResponse;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import org.springframework.stereotype.Component;

/** 레거시 셀러 조회 결과 → 응답 DTO 변환 매퍼. */
@Component
public class LegacySellerQueryApiMapper {

    /** SellerAdminCompositeResult → LegacySellerResponse. */
    public LegacySellerResponse toSellerResponse(SellerAdminCompositeResult result) {
        return new LegacySellerResponse(
                result.seller().id(),
                result.seller().sellerName(),
                result.businessInfo().registrationNumber());
    }
}
