package com.ryuqq.marketplace.integration.legacy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

/**
 * 레거시 상품그룹 조회 API E2E 테스트.
 *
 * <p>테스트 대상: GET /api/v1/product/group/{productGroupId} - 레거시 상품그룹 상세 조회
 *
 * <p>접근 제어: {@code @PreAuthorize("@legacyAccess.isProductOwnerOrMaster(#productGroupId)")}
 *
 * <ul>
 *   <li>MASTER 역할: 모든 상품그룹 접근 가능
 *   <li>SELLER 역할: 본인 소유 상품그룹만 접근 가능
 *   <li>미인증: 401 반환
 * </ul>
 */
@Tag("e2e")
@Tag("legacy")
@Tag("product-group")
@DisplayName("레거시 상품그룹 조회 API E2E 테스트")
class LegacyProductGroupQueryE2ETest extends LegacyE2ETestBase {

    private static final String PRODUCT_GROUP_URL = "/api/v1/product/group";

    @Nested
    @DisplayName("GET /api/v1/product/group/{productGroupId} - MASTER 역할 접근")
    class GetProductGroupDetailMasterTest {

        @Test
        @Tag("P0")
        @DisplayName("[TC-LPG-A01] MASTER 역할로 상품그룹 조회 시 접근 허용 (200 또는 500)")
        void getProductGroup_MasterRole_AccessAllowed() {
            // given: stub LegacyTokenClient가 role=MASTER를 반환
            long anyProductGroupId = 1L;

            // when & then
            // MASTER 역할이므로 LegacyAccessChecker.isProductOwnerOrMaster() → true
            // 실제 데이터가 없으므로 UseCase에서 예외 발생 가능하지만 403은 아님
            int statusCode =
                    givenLegacyAuth()
                            .when()
                            .get(PRODUCT_GROUP_URL + "/{productGroupId}", anyProductGroupId)
                            .then()
                            .extract()
                            .statusCode();

            // 403(접근 거부)이 아니면 권한 검사 통과
            assert statusCode != HttpStatus.FORBIDDEN.value()
                    : "MASTER 역할인데 403이 반환됨 — LegacyAccessChecker 설정 확인 필요";
        }
    }

    @Nested
    @DisplayName("GET /api/v1/product/group/{productGroupId} - 인증 실패 시나리오")
    class GetProductGroupDetailAuthTest {

        @Test
        @Tag("P0")
        @Tag("auth")
        @DisplayName("[TC-LPG-A02] 토큰 없이 요청 시 401 반환")
        void getProductGroup_Unauthenticated_Returns401() {
            // given
            long anyProductGroupId = 1L;

            // when & then
            givenUnauthenticated()
                    .when()
                    .get(PRODUCT_GROUP_URL + "/{productGroupId}", anyProductGroupId)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
