package com.ryuqq.marketplace.adapter.in.rest.legacy.shipment;

import com.ryuqq.marketplace.adapter.in.rest.legacy.shipment.dto.response.LegacyShipmentCompanyCodeResponse;
import com.ryuqq.marketplace.application.legacyshipment.dto.response.LegacyShipmentCompanyCodeResult;
import java.util.List;

/**
 * Legacy Shipment API 테스트 Fixtures.
 *
 * <p>Legacy 배송(택배사) REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class LegacyShipmentApiFixtures {

    private LegacyShipmentApiFixtures() {}

    // ===== 상수 =====
    public static final String DEFAULT_COMPANY_NAME_CJ = "CJ대한통운";
    public static final String DEFAULT_COMPANY_CODE_CJ = "04";
    public static final String DEFAULT_COMPANY_NAME_LOTTE = "롯데택배";
    public static final String DEFAULT_COMPANY_CODE_LOTTE = "08";
    public static final String DEFAULT_COMPANY_NAME_HANJIN = "한진택배";
    public static final String DEFAULT_COMPANY_CODE_HANJIN = "05";

    // ===== Application Result Fixtures =====

    public static LegacyShipmentCompanyCodeResult cjResult() {
        return new LegacyShipmentCompanyCodeResult(
                DEFAULT_COMPANY_NAME_CJ, DEFAULT_COMPANY_CODE_CJ);
    }

    public static LegacyShipmentCompanyCodeResult lotteResult() {
        return new LegacyShipmentCompanyCodeResult(
                DEFAULT_COMPANY_NAME_LOTTE, DEFAULT_COMPANY_CODE_LOTTE);
    }

    public static LegacyShipmentCompanyCodeResult hanjinResult() {
        return new LegacyShipmentCompanyCodeResult(
                DEFAULT_COMPANY_NAME_HANJIN, DEFAULT_COMPANY_CODE_HANJIN);
    }

    public static List<LegacyShipmentCompanyCodeResult> results() {
        return List.of(cjResult(), lotteResult(), hanjinResult());
    }

    public static List<LegacyShipmentCompanyCodeResult> singleResult() {
        return List.of(cjResult());
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
