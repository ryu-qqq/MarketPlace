package com.ryuqq.marketplace.adapter.in.rest.legacy.shipment;

import com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.dto.response.LegacyShipmentCompanyCodeResponse;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import java.time.Instant;
import java.util.List;

/**
 * Legacy Shipment API 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyShipmentApiFixtures {

    private LegacyShipmentApiFixtures() {}

    public static final long SHIPMENT_CODE_TYPE_ID = 2L;
    public static final String DEFAULT_COMPANY_NAME_CJ = "CJ대한통운";
    public static final String DEFAULT_COMPANY_CODE_CJ = "CJ_LOGISTICS";
    public static final String DEFAULT_COMPANY_NAME_LOTTE = "롯데택배";
    public static final String DEFAULT_COMPANY_CODE_LOTTE = "LOTTE_LOGISTICS";
    public static final String DEFAULT_COMPANY_NAME_HANJIN = "한진택배";
    public static final String DEFAULT_COMPANY_CODE_HANJIN = "HANJIN_EXPRESS";

    // ===== CommonCode Fixtures =====

    public static CommonCode cjCommonCode() {
        return CommonCode.reconstitute(
                CommonCodeId.of(1L),
                CommonCodeTypeId.of(SHIPMENT_CODE_TYPE_ID),
                DEFAULT_COMPANY_CODE_CJ,
                DEFAULT_COMPANY_NAME_CJ,
                1,
                true,
                null,
                Instant.now(),
                Instant.now());
    }

    public static CommonCode lotteCommonCode() {
        return CommonCode.reconstitute(
                CommonCodeId.of(2L),
                CommonCodeTypeId.of(SHIPMENT_CODE_TYPE_ID),
                DEFAULT_COMPANY_CODE_LOTTE,
                DEFAULT_COMPANY_NAME_LOTTE,
                2,
                true,
                null,
                Instant.now(),
                Instant.now());
    }

    public static CommonCode hanjinCommonCode() {
        return CommonCode.reconstitute(
                CommonCodeId.of(3L),
                CommonCodeTypeId.of(SHIPMENT_CODE_TYPE_ID),
                DEFAULT_COMPANY_CODE_HANJIN,
                DEFAULT_COMPANY_NAME_HANJIN,
                3,
                true,
                null,
                Instant.now(),
                Instant.now());
    }

    public static List<CommonCode> commonCodes() {
        return List.of(cjCommonCode(), lotteCommonCode(), hanjinCommonCode());
    }

    public static List<CommonCode> singleCommonCode() {
        return List.of(cjCommonCode());
    }

    // ===== API Response Fixtures =====

    public static LegacyShipmentCompanyCodeResponse cjResponse() {
        return new LegacyShipmentCompanyCodeResponse(
                DEFAULT_COMPANY_NAME_CJ, DEFAULT_COMPANY_CODE_CJ);
    }

    public static LegacyShipmentCompanyCodeResponse lotteResponse() {
        return new LegacyShipmentCompanyCodeResponse(
                DEFAULT_COMPANY_NAME_LOTTE, DEFAULT_COMPANY_CODE_LOTTE);
    }

    public static List<LegacyShipmentCompanyCodeResponse> responses() {
        return List.of(cjResponse(), lotteResponse());
    }

    public static List<LegacyShipmentCompanyCodeResponse> singleResponse() {
        return List.of(cjResponse());
    }
}
