package com.ryuqq.marketplace.integration.legacy;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * 레거시 셀러 API E2E 테스트.
 *
 * <p>테스트 대상: GET /api/v1/legacy/seller - 현재 인증된 셀러 정보 조회
 *
 * <p>인증 필요 엔드포인트. Stub 토큰 기반 인증 시 sellerId=10 이 사용됩니다.
 *
 * <p>LegacyGetCurrentSellerUseCase는 StubExternalClientConfig에서 stub으로 등록되며, sellerId=10 에 대해
 * stub 응답을 반환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("legacy")
@Tag("seller")
@DisplayName("레거시 셀러 API E2E 테스트")
class LegacySellerE2ETest extends LegacyE2ETestBase {

    private static final String SELLER_URL = "/api/v1/legacy/seller";

    /** Stub 토큰이 반환하는 sellerId. StubLegacyTokenClient.extractSellerId() 반환값과 동일해야 합니다. */
    static final long STUB_SELLER_ID = 10L;

    @Nested
    @DisplayName("GET /api/v1/legacy/seller - 현재 셀러 정보 조회")
    class GetCurrentSellerTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-LS-S01] 인증된 셀러 정보 조회 성공")
        void getCurrentSeller_Authenticated_Returns200() {
            // when & then
            // StubExternalClientConfig의 LegacyGetCurrentSellerUseCase가 sellerId=10 에 대해 stub 응답 반환
            givenLegacyAuth()
                    .when()
                    .get(SELLER_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.sellerId", equalTo((int) STUB_SELLER_ID))
                    .body("data.sellerName", notNullValue())
                    .body("response.status", equalTo(200))
                    .body("response.message", equalTo("success"));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/legacy/seller - 인증 실패 시나리오")
    class GetCurrentSellerAuthTest {

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-LS-A01] 토큰 없이 요청 시 401 반환")
        void getCurrentSeller_Unauthenticated_Returns401() {
            // when & then
            givenUnauthenticated()
                    .when()
                    .get(SELLER_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
