package com.ryuqq.marketplace.integration.legacy;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * 레거시 배송(택배사) API E2E 테스트.
 *
 * <p>테스트 대상: GET /api/v1/legacy/shipment/company-codes - 택배사 코드 목록 조회
 *
 * <p>인증 필요 엔드포인트. 레거시 DB(luxurydb)에서 code_group_id=2 로 조회합니다. H2 테스트 환경에서는 레거시 테이블이 없으므로 빈 목록을
 * 반환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("legacy")
@Tag("shipment")
@DisplayName("레거시 배송(택배사) API E2E 테스트")
class LegacyShipmentE2ETest extends LegacyE2ETestBase {

    private static final String COMPANY_CODES_URL = "/api/v1/legacy/shipment/company-codes";

    @Nested
    @DisplayName("GET /api/v1/legacy/shipment/company-codes - 택배사 코드 목록 조회")
    class GetCompanyCodesTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-LSH-S01] 인증된 사용자의 택배사 코드 목록 조회 성공")
        void getCompanyCodes_Authenticated_Returns200() {
            // when & then
            // H2 환경에서 레거시 common_code 테이블 미존재 시 빈 목록 반환 (정상 동작)
            givenLegacyAuth()
                    .when()
                    .get(COMPANY_CODES_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    .body("response.status", equalTo(200))
                    .body("response.message", equalTo("success"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/legacy/shipment/company-codes - 인증 실패 시나리오")
    class GetCompanyCodesAuthTest {

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-LSH-A01] 토큰 없이 요청 시 401 반환")
        void getCompanyCodes_Unauthenticated_Returns401() {
            // when & then
            givenUnauthenticated()
                    .when()
                    .get(COMPANY_CODES_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
