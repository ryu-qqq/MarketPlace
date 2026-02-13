package com.ryuqq.marketplace.integration.saleschannel;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.SalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository.SalesChannelJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Sales Channel Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상: - GET /sales-channels - 판매채널 목록 조회
 *
 * <p>시나리오: - P0: 6개 시나리오 (필수 기능)
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("e2e")
@Tag("saleschannel")
@Tag("query")
@DisplayName("Sales Channel Query API E2E 테스트")
class SalesChannelQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/sales-channels";

    @Autowired private SalesChannelJpaRepository salesChannelRepository;

    @BeforeEach
    void setUp() {
        salesChannelRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        salesChannelRepository.deleteAll();
    }

    // ===== GET /sales-channels - 판매채널 목록 조회 =====

    @Nested
    @DisplayName("GET /sales-channels - 판매채널 목록 조회")
    class SearchSalesChannelsTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-1] 데이터 존재 시 정상 조회")
        void searchSalesChannels_withData_returnsOk() {
            // given: 3건 저장
            salesChannelRepository.save(SalesChannelJpaEntityFixtures.activeEntityWithName("쿠팡"));
            salesChannelRepository.save(SalesChannelJpaEntityFixtures.activeEntityWithName("네이버"));
            salesChannelRepository.save(SalesChannelJpaEntityFixtures.activeEntityWithName("11번가"));

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content.size()", equalTo(3))
                    .body("data.totalElements", equalTo(3));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-2] 데이터 없을 때 빈 목록")
        void searchSalesChannels_noData_returnsEmptyList() {
            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", empty())
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-5] 권한 검증 - 비인증 사용자")
        void searchSalesChannels_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-6] 권한 검증 - 일반 사용자")
        void searchSalesChannels_authenticatedUser_returns403() {
            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }
}
