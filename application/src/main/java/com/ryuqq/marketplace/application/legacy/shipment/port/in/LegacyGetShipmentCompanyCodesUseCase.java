package com.ryuqq.marketplace.application.legacy.shipment.port.in;

import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import java.util.List;

/** 레거시 택배사 코드 목록 조회 UseCase. */
public interface LegacyGetShipmentCompanyCodesUseCase {

    /**
     * 택배사 코드 목록을 조회합니다.
     *
     * @return 택배사 공통 코드 목록
     */
    List<CommonCode> execute();
}
