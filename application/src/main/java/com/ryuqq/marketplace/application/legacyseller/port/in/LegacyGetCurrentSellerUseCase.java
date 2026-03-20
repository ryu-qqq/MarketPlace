package com.ryuqq.marketplace.application.legacyseller.port.in;

import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;

/** 레거시 현재 인증된 셀러 정보 조회 UseCase. */
public interface LegacyGetCurrentSellerUseCase {

    /**
     * 셀러 ID로 셀러 정보를 조회합니다.
     *
     * @param sellerId 셀러 ID (JWT claims에서 추출)
     * @return 셀러 Admin Composite 정보
     */
    SellerAdminCompositeResult execute(long sellerId);
}
