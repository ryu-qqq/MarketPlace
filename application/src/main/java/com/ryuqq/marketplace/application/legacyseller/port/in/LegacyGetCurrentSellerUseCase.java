package com.ryuqq.marketplace.application.legacyseller.port.in;

import com.ryuqq.marketplace.application.legacyseller.dto.response.LegacySellerResult;

/** 레거시 현재 인증된 셀러 정보 조회 UseCase. */
public interface LegacyGetCurrentSellerUseCase {

    /**
     * 인증 테넌트 ID로 셀러 정보를 조회합니다.
     *
     * @param authTenantId 인증 테넌트 ID
     * @return 셀러 정보
     */
    LegacySellerResult execute(String authTenantId);
}
