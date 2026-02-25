package com.ryuqq.marketplace.application.legacyshipment.port.in;

import com.ryuqq.marketplace.application.legacyshipment.dto.response.LegacyShipmentCompanyCodeResult;
import java.util.List;

/** 레거시 택배사 코드 목록 조회 UseCase. */
public interface LegacyGetShipmentCompanyCodesUseCase {

    /**
     * 택배사 코드 목록을 조회합니다.
     *
     * @return 택배사 코드 목록
     */
    List<LegacyShipmentCompanyCodeResult> execute();
}
