package com.ryuqq.marketplace.application.legacy.shipment.service;

import com.ryuqq.marketplace.application.legacy.commoncode.manager.LegacyCommonCodeReadManager;
import com.ryuqq.marketplace.application.legacy.shipment.port.in.LegacyGetShipmentCompanyCodesUseCase;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

/** 레거시 택배사 코드 목록 조회 서비스. */
@Service
public class LegacyGetShipmentCompanyCodesService implements LegacyGetShipmentCompanyCodesUseCase {

    private static final Long SHIPMENT_COMPANY_CODE_GROUP_ID = 2L;

    private final LegacyCommonCodeReadManager legacyCommonCodeReadManager;

    public LegacyGetShipmentCompanyCodesService(
            LegacyCommonCodeReadManager legacyCommonCodeReadManager) {
        this.legacyCommonCodeReadManager = legacyCommonCodeReadManager;
    }

    @Override
    public List<CommonCode> execute() {
        return legacyCommonCodeReadManager.getByCodeGroupId(SHIPMENT_COMPANY_CODE_GROUP_ID).stream()
                .map(this::toCommonCode)
                .toList();
    }

    private CommonCode toCommonCode(LegacyCommonCode legacy) {
        return CommonCode.reconstitute(
                CommonCodeId.of(legacy.id()),
                CommonCodeTypeId.of(legacy.codeGroupId()),
                legacy.codeDetail(),
                legacy.codeDetailDisplayName(),
                legacy.displayOrder() != null ? legacy.displayOrder() : 0,
                true,
                null,
                Instant.now(),
                Instant.now());
    }
}
