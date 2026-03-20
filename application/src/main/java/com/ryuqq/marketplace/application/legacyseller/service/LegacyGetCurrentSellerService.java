package com.ryuqq.marketplace.application.legacyseller.service;

import com.ryuqq.marketplace.application.legacyseller.manager.LegacySellerCompositionReadManager;
import com.ryuqq.marketplace.application.legacyseller.port.in.LegacyGetCurrentSellerUseCase;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import org.springframework.stereotype.Service;

/** 레거시 현재 인증된 셀러 정보 조회 서비스. */
@Service
public class LegacyGetCurrentSellerService implements LegacyGetCurrentSellerUseCase {

    private final LegacySellerCompositionReadManager compositionReadManager;

    public LegacyGetCurrentSellerService(LegacySellerCompositionReadManager compositionReadManager) {
        this.compositionReadManager = compositionReadManager;
    }

    @Override
    public SellerAdminCompositeResult execute(long sellerId) {
        return compositionReadManager.getAdminComposite(sellerId);
    }
}
