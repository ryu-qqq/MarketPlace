package com.ryuqq.marketplace.adapter.in.rest.legacy.seller.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.seller.dto.response.LegacySellerResponse;
import com.ryuqq.marketplace.application.legacy.auth.dto.result.LegacySellerAuthResult;
import org.springframework.stereotype.Component;

/** 레거시 셀러 조회 결과 → 응답 DTO 변환 매퍼. */
@Component
public class LegacySellerQueryApiMapper {

    /** LegacySellerAuthResult → LegacySellerResponse (세토프 레거시 호환). */
    public LegacySellerResponse toSellerResponse(LegacySellerAuthResult result) {
        return new LegacySellerResponse(
                result.sellerId(),
                result.email(),
                result.passwordHash(),
                result.roleType(),
                result.approvalStatus());
    }
}
