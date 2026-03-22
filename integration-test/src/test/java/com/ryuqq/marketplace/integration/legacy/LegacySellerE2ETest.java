package com.ryuqq.marketplace.integration.legacy;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.ryuqq.marketplace.application.legacy.seller.port.out.LegacySellerCompositionQueryPort;
import com.ryuqq.marketplace.application.seller.dto.response.SellerAdminCompositeResult;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;

/**
 * 레거시 셀러 API E2E 테스트.
 *
 * <p>테스트 대상: GET /api/v1/legacy/seller - 현재 인증된 셀러 정보 조회
 *
 * <p>인증 필요 엔드포인트. Stub 토큰 기반 인증 시 sellerId=10 이 사용됩니다.
 *
 * <p>LegacySellerCompositionQueryPort는 레거시 DB(luxurydb)에서 조회하므로, H2 테스트 환경에서는
 * StubLegacySellerCompositionQueryPort로 대체하여 sellerId=10 에 대한 stub 응답을 반환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("legacy")
@Tag("seller")
@DisplayName("레거시 셀러 API E2E 테스트")
@Import(LegacySellerE2ETest.StubSellerConfig.class)
class LegacySellerE2ETest extends LegacyE2ETestBase {

    private static final String SELLER_URL = "/api/v1/legacy/seller";

    /** Stub 토큰이 반환하는 sellerId. StubLegacyTokenClient.extractSellerId() 반환값과 동일해야 합니다. */
    static final long STUB_SELLER_ID = 10L;

    static final long UNKNOWN_SELLER_ID = 99999L;

    @Nested
    @DisplayName("GET /api/v1/legacy/seller - 현재 셀러 정보 조회")
    class GetCurrentSellerTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-LS-S01] 인증된 셀러 정보 조회 성공")
        void getCurrentSeller_Authenticated_Returns200() {
            // when & then
            // StubSellerConfig의 LegacySellerCompositionQueryPort가 sellerId=10 에 대해 stub 응답 반환
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

    // ===== Stub TestConfiguration =====

    /**
     * 레거시 셀러 조회 Port Stub.
     *
     * <p>LegacySellerCompositionQueryAdapter는 레거시 DB(luxurydb) 전용이므로 H2 테스트 환경에서 사용할 수 없습니다.
     * sellerId=10 에 대해서는 stub 응답을 반환하고, 그 외 ID는 Optional.empty()를 반환합니다.
     */
    @TestConfiguration
    static class StubSellerConfig {

        @Bean
        @Primary
        public LegacySellerCompositionQueryPort stubLegacySellerCompositionQueryPort() {
            return sellerId -> {
                if (sellerId == STUB_SELLER_ID) {
                    return Optional.of(stubSellerResult());
                }
                return Optional.empty();
            };
        }

        private SellerAdminCompositeResult stubSellerResult() {
            Instant now = Instant.now();
            return new SellerAdminCompositeResult(
                    new SellerAdminCompositeResult.SellerInfo(
                            STUB_SELLER_ID, "스텁 셀러", "스텁 스토어", null, null, true, now, now),
                    new SellerAdminCompositeResult.BusinessInfo(
                            null, "123-45-67890", "스텁 회사", "대표자", null, null, null, null),
                    new SellerAdminCompositeResult.CsInfo(
                            null, null, null, null, null, null, null, null),
                    null,
                    null);
        }
    }
}
