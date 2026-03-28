package com.ryuqq.marketplace.application.legacy.shipment.service;

import com.ryuqq.marketplace.application.commoncode.manager.CommonCodeReadManager;
import com.ryuqq.marketplace.application.legacy.shipment.port.in.LegacyGetShipmentCompanyCodesUseCase;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSearchCriteria;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 레거시 택배사 코드 목록 조회 서비스.
 *
 * <p>market 스키마의 common_codes (COURIER_CODE_LEGACY 타입)에서 조회합니다.
 */
@Service
public class LegacyGetShipmentCompanyCodesService implements LegacyGetShipmentCompanyCodesUseCase {

    private static final String COURIER_CODE_LEGACY = "COURIER_CODE_LEGACY";

    private final CommonCodeReadManager commonCodeReadManager;

    public LegacyGetShipmentCompanyCodesService(CommonCodeReadManager commonCodeReadManager) {
        this.commonCodeReadManager = commonCodeReadManager;
    }

    @Override
    public List<CommonCode> execute() {
        return commonCodeReadManager.findByCriteria(
                CommonCodeSearchCriteria.defaultOf(COURIER_CODE_LEGACY));
    }
}
