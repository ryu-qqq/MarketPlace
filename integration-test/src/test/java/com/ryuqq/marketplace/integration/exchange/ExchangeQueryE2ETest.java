package com.ryuqq.marketplace.integration.exchange;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.ExchangeClaimJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.exchange.repository.ExchangeClaimJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderJpaRepository;
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
 * 교환(Exchange) Query 엔드포인트 E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>GET /exchanges/summary - 교환 요약 조회
 *   <li>GET /exchanges - 교환 목록 조회 (페이징)
 *   <li>GET /exchanges/{exchangeClaimId} - 교환 상세 조회
 * </ul>
 *
 * <p>P0 시나리오 우선 구현 (Q01~Q03 중 필수 케이스)
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("e2e")
@Tag("exchange")
@Tag("query")
@DisplayName("교환 Query API E2E 테스트")
class ExchangeQueryE2ETest extends E2ETestBase {

    private static final String BASE_URL = "/exchanges";
    private static final String SUMMARY_URL = "/exchanges/summary";

    @Autowired private ExchangeClaimJpaRepository exchangeClaimRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        claimHistoryRepository.deleteAll();
        exchangeClaimRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    // ===== Q01. GET /exchanges/summary =====

    @Nested
    @DisplayName("GET /exchanges/summary - 교환 요약 조회")
    class GetSummaryTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q01-1] 교환 건 없을 때 요약 조회 - 빈 카운트 반환")
        void getSummary_noData_returnsZeroCounts() {
            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SUMMARY_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q01-2] 다양한 상태 교환 건 존재 시 요약 조회 - REQUESTED 카운트 포함")
        void getSummary_withVariousStatuses_returnsCountsPerStatus() {
            // given: 다양한 상태의 교환 건 저장
            exchangeClaimRepository.save(ExchangeClaimJpaEntityFixtures.requestedEntity("sum-001"));
            exchangeClaimRepository.save(ExchangeClaimJpaEntityFixtures.requestedEntity("sum-002"));
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus("sum-003", "COLLECTING"));
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus("sum-004", "COMPLETED"));
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.entityWithStatus("sum-005", "REJECTED"));

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(SUMMARY_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q01-AUTH-01] 비인증 요청 시 401을 반환한다")
        void getSummary_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(SUMMARY_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q01-AUTH-02] 권한 없는 요청 시 403을 반환한다")
        void getSummary_withoutPermission_returns403() {
            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(SUMMARY_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== Q02. GET /exchanges - 교환 목록 조회 =====

    @Nested
    @DisplayName("GET /exchanges - 교환 목록 조회")
    class GetListTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q02-1] 데이터 없을 때 빈 목록 반환")
        void getList_noData_returnsEmptyContent() {
            // when & then
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    .body("data.content", hasSize(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q02-2] 데이터 존재 시 목록 페이징 조회 - page=0, size=3")
        void getList_withData_returnsPaginatedContent() {
            // given: 5건 저장
            for (int i = 1; i <= 5; i++) {
                exchangeClaimRepository.save(
                        ExchangeClaimJpaEntityFixtures.requestedEntity(
                                "list-" + String.format("%03d", i)));
            }

            // when & then
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 3)
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.totalElements", greaterThanOrEqualTo(5));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q02-AUTH-01] 비인증 요청 시 401을 반환한다")
        void getList_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q02-AUTH-02] 권한 없는 요청 시 403을 반환한다")
        void getList_withoutPermission_returns403() {
            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(BASE_URL)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== Q03. GET /exchanges/{exchangeClaimId} - 교환 상세 조회 =====

    @Nested
    @DisplayName("GET /exchanges/{exchangeClaimId} - 교환 상세 조회")
    class GetDetailTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q03-1] 존재하는 교환 건 상세 조회 - 200 응답 및 상태 검증")
        void getDetail_existingEntity_returns200WithDetails() {
            // given
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntity("detail-001"));

            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{exchangeClaimId}", "detail-001")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    .body("data.claimInfo.status", equalTo("REQUESTED"));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q03-2] 존재하지 않는 ID로 상세 조회 - 404 반환")
        void getDetail_nonExistentId_returns404() {
            // when & then
            given().spec(givenSuperAdmin())
                    .when()
                    .get(BASE_URL + "/{exchangeClaimId}", "non-existent-id-999")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q03-AUTH-01] 비인증 요청 시 401을 반환한다")
        void getDetail_unauthenticated_returns401() {
            // when & then
            given().spec(givenUnauthenticated())
                    .when()
                    .get(BASE_URL + "/{exchangeClaimId}", "some-id")
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q03-AUTH-02] 권한 없는 요청 시 403을 반환한다")
        void getDetail_withoutPermission_returns403() {
            // given
            exchangeClaimRepository.save(
                    ExchangeClaimJpaEntityFixtures.requestedEntity("detail-auth-001"));

            // when & then
            given().spec(givenAuthenticatedUser())
                    .when()
                    .get(BASE_URL + "/{exchangeClaimId}", "detail-auth-001")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }
}
