package com.ryuqq.marketplace.application.legacyshipment.service;

import com.ryuqq.marketplace.application.legacycommoncode.manager.LegacyCommonCodeReadManager;
import com.ryuqq.marketplace.application.legacyshipment.assembler.LegacyShipmentAssembler;
import com.ryuqq.marketplace.application.legacyshipment.dto.response.LegacyShipmentCompanyCodeResult;
import com.ryuqq.marketplace.application.legacyshipment.port.in.LegacyGetShipmentCompanyCodesUseCase;
import java.util.List;
import org.springframework.stereotype.Service;

/** 레거시 택배사 코드 목록 조회 서비스. */
@Service
public class LegacyGetShipmentCompanyCodesService implements LegacyGetShipmentCompanyCodesUseCase {

    private static final Long SHIPMENT_COMPANY_CODE_GROUP_ID = 2L;

    private final LegacyCommonCodeReadManager legacyCommonCodeReadManager;
    private final LegacyShipmentAssembler legacyShipmentAssembler;

    public LegacyGetShipmentCompanyCodesService(
            LegacyCommonCodeReadManager legacyCommonCodeReadManager,
            LegacyShipmentAssembler legacyShipmentAssembler) {
        this.legacyCommonCodeReadManager = legacyCommonCodeReadManager;
        this.legacyShipmentAssembler = legacyShipmentAssembler;
    }

    @Override
    public List<LegacyShipmentCompanyCodeResult> execute() {
        return legacyShipmentAssembler.toCompanyCodeResults(
                legacyCommonCodeReadManager.getByCodeGroupId(SHIPMENT_COMPANY_CODE_GROUP_ID));
    }
}
