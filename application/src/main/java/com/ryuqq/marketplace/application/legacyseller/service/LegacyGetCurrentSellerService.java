package com.ryuqq.marketplace.application.legacyseller.service;

import com.ryuqq.marketplace.application.legacyseller.dto.response.LegacySellerResult;
import com.ryuqq.marketplace.application.legacyseller.port.in.LegacyGetCurrentSellerUseCase;
import com.ryuqq.marketplace.application.seller.dto.composite.SellerAdminCompositeResult;
import com.ryuqq.marketplace.application.seller.manager.SellerCompositionReadManager;
import org.springframework.stereotype.Service;

/** 레거시 현재 인증된 셀러 정보 조회 서비스. */
@Service
public class LegacyGetCurrentSellerService implements LegacyGetCurrentSellerUseCase {

    private final SellerCompositionReadManager compositionReadManager;

    public LegacyGetCurrentSellerService(SellerCompositionReadManager compositionReadManager) {
        this.compositionReadManager = compositionReadManager;
    }

    @Override
    public LegacySellerResult execute(String authTenantId) {
        SellerAdminCompositeResult composite =
                compositionReadManager.getAdminCompositeByAuthTenantId(authTenantId);

        return new LegacySellerResult(
                composite.seller().id(),
                composite.seller().sellerName(),
                composite.businessInfo().registrationNumber());
    }
}
