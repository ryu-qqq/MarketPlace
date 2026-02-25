package com.ryuqq.marketplace.application.legacyshipment.service;

import com.ryuqq.marketplace.application.legacyshipment.dto.response.LegacyShipmentCompanyCodeResult;
import com.ryuqq.marketplace.application.legacyshipment.port.in.LegacyGetShipmentCompanyCodesUseCase;
import java.util.List;
import org.springframework.stereotype.Service;

/** 레거시 택배사 코드 목록 조회 서비스. */
@Service
public class LegacyGetShipmentCompanyCodesService implements LegacyGetShipmentCompanyCodesUseCase {

    // Todo legacy persistence mysql 에 select * from common_code where CODE_GROUP_ID = 2; 로 조회
    @Override
    public List<LegacyShipmentCompanyCodeResult> execute() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
