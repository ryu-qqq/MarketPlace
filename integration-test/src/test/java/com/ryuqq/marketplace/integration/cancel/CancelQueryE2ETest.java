package com.ryuqq.marketplace.integration.cancel;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.CancelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.repository.CancelJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.canceloutbox.repository.CancelOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.order.repository.OrderItemHistoryJpaRepository;
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
 * Cancel Query E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>Q12: GET /cancels/summary - 취소 상태별 요약 조회
 *   <li>Q13: GET /cancels - 취소 목록 페이징 조회
 *   <li>Q14: GET /cancels/{cancelId} - 취소 상세 조회
 * </ul>
 */
@Tag("e2e")
@Tag("cancel")
@Tag("query")
@DisplayName("Cancel Query E2E 테스트")
class CancelQueryE2ETest extends E2ETestBase {

    private static final String CANCELS = "/cancels";
    private static final String CANCEL_SUMMARY = "/cancels/summary";
    private static final String CANCEL_DETAIL = "/cancels/{cancelId}";

    @Autowired private CancelJpaRepository cancelRepository;
    @Autowired private CancelOutboxJpaRepository cancelOutboxRepository;
    @Autowired private ClaimHistoryJpaRepository claimHistoryRepository;
    @Autowired private OrderItemHistoryJpaRepository orderItemHistoryRepository;
    @Autowired private OrderItemJpaRepository orderItemRepository;
    @Autowired private OrderJpaRepository orderRepository;

    @BeforeEach
    void setUp() {
        claimHistoryRepository.deleteAll();
        cancelOutboxRepository.deleteAll();
        cancelRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        claimHistoryRepository.deleteAll();
        cancelOutboxRepository.deleteAll();
        cancelRepository.deleteAll();
        orderItemHistoryRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Nested
    @DisplayName("Q12: GET /cancels/summary - 취소 요약 조회")
    class CancelSummaryTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q12-1] 취소 데이터 없을 때 요약 조회 - 빈 카운트 반환")
        void getSummary_NoData_ReturnsEmptyCounts() {
            given().spec(givenSuperAdmin())
                    .when()
                    .get(CANCEL_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q12-2] 다양한 상태의 취소 데이터 존재 시 요약 조회")
        void getSummary_VariousStatuses_ReturnsCorrectCounts() {
            // REQUESTED 2건, APPROVED 1건, REJECTED 1건 직접 시딩
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-sum-req-001", "CAN-SUM-REQ-001", "order-item-sum-001", 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-sum-req-002", "CAN-SUM-REQ-002", "order-item-sum-002", 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.approvedEntity(
                            "cancel-sum-app-001", "order-item-sum-003", 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.rejectedEntity(
                            "cancel-sum-rej-001", "order-item-sum-004", 10L));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(CANCEL_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q12-3] 권한 없는 사용자 요약 조회 - 403")
        void getSummary_NoPermission_Returns403() {
            given().spec(givenWithPermission("order:read"))
                    .when()
                    .get(CANCEL_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q12-4] 비인증 요청 요약 조회 - 401")
        void getSummary_Unauthenticated_Returns401() {
            given().spec(givenUnauthenticated())
                    .when()
                    .get(CANCEL_SUMMARY)
                    .then()
                    .statusCode(HttpStatus.UNAUTHORIZED.value());
        }
    }

    @Nested
    @DisplayName("Q13: GET /cancels - 취소 목록 조회")
    class CancelListTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q13-1] 데이터 없을 때 취소 목록 조회 - 빈 페이지 반환")
        void getList_NoData_ReturnsEmptyPage() {
            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(CANCELS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    .body("data.content", hasSize(0))
                    .body("data.totalElements", equalTo(0));
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q13-2] 취소 목록 존재 시 정상 페이징 조회")
        void getList_ThreeRecords_ReturnsPaged() {
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-list-001", "CAN-LIST-001", "order-item-list-001", 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.approvedEntity(
                            "cancel-list-002", "order-item-list-002", 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.rejectedEntity(
                            "cancel-list-003", "order-item-list-003", 10L));

            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(CANCELS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3))
                    .body("data.totalElements", equalTo(3));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q13-3] page, size 파라미터 페이징 동작 확인")
        void getList_PageSize2_ReturnsCorrectPage() {
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-page-001", "CAN-PAGE-001", "order-item-page-001", 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-page-002", "CAN-PAGE-002", "order-item-page-002", 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-page-003", "CAN-PAGE-003", "order-item-page-003", 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-page-004", "CAN-PAGE-004", "order-item-page-004", 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-page-005", "CAN-PAGE-005", "order-item-page-005", 10L));

            given().spec(givenSuperAdmin())
                    .queryParam("page", 0)
                    .queryParam("size", 2)
                    .when()
                    .get(CANCELS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2))
                    .body("data.totalElements", equalTo(5));
        }

        @Test
        @Tag("P1")
        @DisplayName("[Q13-4] statuses 필터 동작 확인")
        void getList_StatusFilter_FiltersCorrectly() {
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-filter-req-001",
                            "CAN-FILT-REQ-001",
                            "order-item-filt-001",
                            10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntityWithNumber(
                            "cancel-filter-req-002",
                            "CAN-FILT-REQ-002",
                            "order-item-filt-002",
                            10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.approvedEntity(
                            "cancel-filter-app-001", "order-item-filt-003", 10L));
            cancelRepository.save(
                    CancelJpaEntityFixtures.rejectedEntity(
                            "cancel-filter-rej-001", "order-item-filt-004", 10L));

            given().spec(givenSuperAdmin())
                    .queryParam("statuses", "REQUESTED")
                    .queryParam("page", 0)
                    .queryParam("size", 10)
                    .when()
                    .get(CANCELS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(2));
        }
    }

    @Nested
    @DisplayName("Q14: GET /cancels/{cancelId} - 취소 상세 조회")
    class CancelDetailTest {

        @Test
        @Tag("P0")
        @DisplayName("[Q14-1] 존재하는 cancelId로 상세 조회")
        void getDetail_ExistingId_ReturnsDetail() {
            cancelRepository.save(
                    CancelJpaEntityFixtures.requestedEntity(
                            "cancel-detail-001", "order-item-detail-001", 10L));

            given().spec(givenSuperAdmin())
                    .when()
                    .get(CANCEL_DETAIL, "cancel-detail-001")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", notNullValue())
                    .body("data.cancelId", equalTo("cancel-detail-001"))
                    .body("data.cancelStatus", notNullValue())
                    .body("data.cancelType", notNullValue());
        }

        @Test
        @Tag("P0")
        @DisplayName("[Q14-2] 존재하지 않는 cancelId로 상세 조회 - 404")
        void getDetail_NonExistentId_Returns404() {
            given().spec(givenSuperAdmin())
                    .when()
                    .get(CANCEL_DETAIL, "non-existent-cancel-id")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }
    }
}
