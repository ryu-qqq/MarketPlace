package com.ryuqq.marketplace.integration.settlement;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.SettlementEntryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.repository.SettlementEntryJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Settlement Query E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>Q1: GET /settlements - 정산 원장 목록 조회 (페이징)
 *   <li>Q2: GET /settlements/daily - 일별 정산 내역 조회
 * </ul>
 */
@Tag("e2e")
@Tag("settlement")
@Tag("query")
@DisplayName("Settlement Query E2E 테스트")
class SettlementQueryE2ETest extends E2ETestBase {

    private static final String SETTLEMENTS = "/settlements";
    private static final String SETTLEMENTS_DAILY = "/settlements/daily";

    @Autowired private SettlementEntryJpaRepository settlementEntryRepository;

    @BeforeEach
    void setUp() {
        settlementEntryRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        settlementEntryRepository.deleteAll();
    }

    @Nested
    @DisplayName("Q1: GET /settlements - 정산 원장 목록 조회")
    class SettlementListTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q1-1] 데이터 없을 때 목록 조회 - 빈 페이지 반환")
        void getSettlements_NoData_ReturnsEmptyPage() {
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    .body("data.content", hasSize(0))
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q1-2] 정산 원장 3건 존재 시 목록 조회")
        void getSettlements_ThreeEntries_ReturnsPaged() {
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-list-001"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.entityWithStatus("entry-list-002", "HOLD"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.entityWithStatus(
                            "entry-list-003", "CONFIRMED"));

            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.totalElements", equalTo(3));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-3] page, size 파라미터 페이징 동작 확인")
        void getSettlements_PageSize2_ReturnsCorrectPage() {
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-page-001"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-page-002"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-page-003"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-page-004"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-page-005"));

            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(SETTLEMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.totalElements", equalTo(5));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-4] status 필터 동작 확인 - PENDING만 조회")
        void getSettlements_StatusFilter_FiltersCorrectly() {
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-filter-p-001"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-filter-p-002"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.entityWithStatus(
                            "entry-filter-h-001", "HOLD"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.entityWithStatus(
                            "entry-filter-c-001", "CONFIRMED"));

            given().spec(givenSuperAdmin())
                    .queryParam("status", "PENDING")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-5] sellerId 필터 동작 확인")
        void getSettlements_SellerIdFilter_FiltersCorrectly() {
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-seller-001"));

            given().spec(givenSuperAdmin())
                    .queryParam("sellerIds", SettlementEntryJpaEntityFixtures.DEFAULT_SELLER_ID)
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(greaterThanOrEqualTo(1)));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-6] page 파라미터 누락 - 400")
        void getSettlements_MissingPage_Returns400() {
            given().spec(givenSuperAdmin())
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-7] size 파라미터 누락 - 400")
        void getSettlements_MissingSize_Returns400() {
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .when()
                    .get(SETTLEMENTS)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-8] 권한 없는 사용자 목록 조회 - 403")
        void getSettlements_NoPermission_Returns403() {
            given().spec(givenWithPermission("order:read"))
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q1-9] 비인증 요청 - 401")
        void getSettlements_Unauthenticated_Returns401() {
            given().spec(givenUnauthenticated())
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Nested
    @DisplayName("Q2: GET /settlements/daily - 일별 정산 내역 조회")
    class DailySettlementTest {

        @Test
        @Tag("P0")
        @Disabled("H2는 CONVERT_TZ() 함수를 지원하지 않아 500 오류 발생. MySQL 환경에서 실행 필요.")
        @DisplayName("[Q2-1] 정상 기간으로 일별 정산 조회 - 빈 데이터 반환")
        void getDaily_NoData_ReturnsEmptyPage() {
            given().spec(givenSuperAdmin())
                    .queryParam("startDate", "2026-03-01")
                    .queryParam("endDate", "2026-03-31")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS_DAILY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @Disabled("H2는 CONVERT_TZ() 함수를 지원하지 않아 500 오류 발생. MySQL 환경에서 실행 필요.")
        @DisplayName("[Q2-2] 정산 원장 데이터 존재 시 일별 정산 조회")
        void getDaily_WithData_ReturnsAggregatedResult() {
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-daily-001"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-daily-002"));

            given().spec(givenSuperAdmin())
                    .queryParam("startDate", "2026-01-01")
                    .queryParam("endDate", "2026-12-31")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS_DAILY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q2-3] startDate 파라미터 누락 - 400")
        void getDaily_MissingStartDate_Returns400() {
            given().spec(givenSuperAdmin())
                    .queryParam("endDate", "2026-03-31")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS_DAILY)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q2-4] endDate 파라미터 누락 - 400")
        void getDaily_MissingEndDate_Returns400() {
            given().spec(givenSuperAdmin())
                    .queryParam("startDate", "2026-03-01")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS_DAILY)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q2-5] 권한 없는 사용자 일별 정산 조회 - 403")
        void getDaily_NoPermission_Returns403() {
            given().spec(givenWithPermission("order:read"))
                    .queryParam("startDate", "2026-03-01")
                    .queryParam("endDate", "2026-03-31")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS_DAILY)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q2-6] 비인증 요청 - 401")
        void getDaily_Unauthenticated_Returns401() {
            given().spec(givenUnauthenticated())
                    .queryParam("startDate", "2026-03-01")
                    .queryParam("endDate", "2026-03-31")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS_DAILY)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
