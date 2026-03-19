package com.ryuqq.marketplace.integration.settlement;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.SettlementEntryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.SettlementJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entry.repository.SettlementEntryJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.repository.SettlementJpaRepository;
import com.ryuqq.marketplace.integration.E2ETestBase;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Settlement Command E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>C1: POST /settlements/{settlementId}/hold - 개별 정산 보류
 *   <li>C2: POST /settlements/{settlementId}/release - 개별 정산 보류 해제
 *   <li>C3: POST /settlements/complete/batch - 정산 원장 일괄 완료
 *   <li>C4: POST /settlements/hold/batch - 정산 원장 일괄 보류
 *   <li>C5: POST /settlements/release/batch - 정산 원장 일괄 보류 해제
 * </ul>
 */
@Tag("e2e")
@Tag("settlement")
@Tag("command")
@DisplayName("Settlement Command E2E 테스트")
class SettlementCommandE2ETest extends E2ETestBase {

    private static final String SETTLEMENT_HOLD = "/settlements/{settlementId}/hold";
    private static final String SETTLEMENT_RELEASE = "/settlements/{settlementId}/release";
    private static final String COMPLETE_BATCH = "/settlements/complete/batch";
    private static final String HOLD_BATCH = "/settlements/hold/batch";
    private static final String RELEASE_BATCH = "/settlements/release/batch";

    @Autowired private SettlementJpaRepository settlementRepository;
    @Autowired private SettlementEntryJpaRepository settlementEntryRepository;

    @BeforeEach
    void setUp() {
        settlementEntryRepository.deleteAll();
        settlementRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        settlementEntryRepository.deleteAll();
        settlementRepository.deleteAll();
    }

    @Nested
    @DisplayName("C1: POST /settlements/{settlementId}/hold - 개별 정산 보류")
    class HoldSettlementTest {

        @Test
        @Tag("P0")
        @DisplayName("[C1-1] CALCULATING 상태 정산 보류 성공")
        void holdSettlement_CalculatingStatus_Success() {
            settlementRepository.save(
                    SettlementJpaEntityFixtures.calculatingEntity("settlement-hold-001"));

            given().spec(givenSuperAdmin())
                    .body(Map.of("reason", "이상 거래 의심으로 인한 보류 처리"))
                    .when()
                    .post(SETTLEMENT_HOLD, "settlement-hold-001")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", nullValue());

            var updated = settlementRepository.findById("settlement-hold-001").orElseThrow();
            assertThat(updated.getSettlementStatus()).isEqualTo("HOLD");
            assertThat(updated.getHoldReason()).isEqualTo("이상 거래 의심으로 인한 보류 처리");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-2] CONFIRMED 상태 정산 보류 성공")
        void holdSettlement_ConfirmedStatus_Success() {
            settlementRepository.save(
                    SettlementJpaEntityFixtures.entityWithStatus(
                            "settlement-hold-002", "CONFIRMED"));

            given().spec(givenSuperAdmin())
                    .body(Map.of("reason", "정산 검토 필요"))
                    .when()
                    .post(SETTLEMENT_HOLD, "settlement-hold-002")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            var updated = settlementRepository.findById("settlement-hold-002").orElseThrow();
            assertThat(updated.getSettlementStatus()).isEqualTo("HOLD");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-3] reason 누락 - 400")
        void holdSettlement_MissingReason_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of())
                    .when()
                    .post(SETTLEMENT_HOLD, "any-settlement-id")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-4] reason 빈 문자열 - 400")
        void holdSettlement_EmptyReason_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("reason", ""))
                    .when()
                    .post(SETTLEMENT_HOLD, "any-settlement-id")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C1-5] 존재하지 않는 settlementId 보류 - 404")
        void holdSettlement_NonExistentId_Returns404() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("reason", "테스트 보류"))
                    .when()
                    .post(SETTLEMENT_HOLD, "non-existent-settlement-id")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-6] 이미 HOLD 상태 정산 보류 시도 - 상태 전이 불가")
        void holdSettlement_AlreadyHoldStatus_Fails() {
            settlementRepository.save(SettlementJpaEntityFixtures.holdEntity());

            given().spec(givenSuperAdmin())
                    .body(Map.of("reason", "중복 보류 시도"))
                    .when()
                    .post(SETTLEMENT_HOLD, SettlementJpaEntityFixtures.DEFAULT_ID)
                    .then()
                    .statusCode(not(equalTo(HttpStatus.OK.value())));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C1-7] 권한 없는 사용자 보류 요청 - 403")
        void holdSettlement_NoPermission_Returns403() {
            given().spec(givenWithPermission("settlement:read"))
                    .body(Map.of("reason", "테스트"))
                    .when()
                    .post(SETTLEMENT_HOLD, "any-settlement-id")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    @DisplayName("C2: POST /settlements/{settlementId}/release - 개별 정산 보류 해제")
    class ReleaseSettlementTest {

        @Test
        @Tag("P0")
        @DisplayName("[C2-1] HOLD 상태 정산 보류 해제 성공")
        void releaseSettlement_HoldStatus_Success() {
            settlementRepository.save(SettlementJpaEntityFixtures.holdEntity());

            given().spec(givenSuperAdmin())
                    .when()
                    .post(SETTLEMENT_RELEASE, SettlementJpaEntityFixtures.DEFAULT_ID)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", nullValue());

            var updated =
                    settlementRepository
                            .findById(SettlementJpaEntityFixtures.DEFAULT_ID)
                            .orElseThrow();
            assertThat(updated.getSettlementStatus()).isEqualTo("CALCULATING");
            assertThat(updated.getHoldReason()).isNull();
        }

        @Test
        @Tag("P0")
        @DisplayName("[C2-2] 존재하지 않는 settlementId 해제 - 404")
        void releaseSettlement_NonExistentId_Returns404() {
            given().spec(givenSuperAdmin())
                    .when()
                    .post(SETTLEMENT_RELEASE, "non-existent-settlement-id")
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C2-3] CALCULATING 상태 정산 해제 시도 - 상태 전이 불가")
        void releaseSettlement_CalculatingStatus_Fails() {
            settlementRepository.save(
                    SettlementJpaEntityFixtures.calculatingEntity("settlement-release-calc-001"));

            given().spec(givenSuperAdmin())
                    .when()
                    .post(SETTLEMENT_RELEASE, "settlement-release-calc-001")
                    .then()
                    .statusCode(not(equalTo(HttpStatus.OK.value())));
        }

        @Test
        @Tag("P1")
        @DisplayName("[C2-4] 권한 없는 사용자 해제 요청 - 403")
        void releaseSettlement_NoPermission_Returns403() {
            given().spec(givenWithPermission("settlement:read"))
                    .when()
                    .post(SETTLEMENT_RELEASE, "any-settlement-id")
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    @DisplayName("C3: POST /settlements/complete/batch - 정산 원장 일괄 확정(CONFIRMED)")
    class CompleteSettlementBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C3-1] PENDING 상태 정산 원장 1건 CONFIRMED 처리 성공")
        void completeBatch_SinglePendingEntry_Success() {
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-complete-001"));

            given().spec(givenSuperAdmin())
                    .body(createCompleteBatchRequest(List.of("entry-complete-001")))
                    .when()
                    .post(COMPLETE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", nullValue());

            var updated = settlementEntryRepository.findById("entry-complete-001").orElseThrow();
            assertThat(updated.getEntryStatus()).isEqualTo("CONFIRMED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-2] 복수 PENDING 정산 원장 일괄 CONFIRMED 처리 성공")
        void completeBatch_MultiplePendingEntries_Success() {
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-batch-c-001"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-batch-c-002"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-batch-c-003"));

            given().spec(givenSuperAdmin())
                    .body(
                            createCompleteBatchRequest(
                                    List.of(
                                            "entry-batch-c-001",
                                            "entry-batch-c-002",
                                            "entry-batch-c-003")))
                    .when()
                    .post(COMPLETE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            assertThat(
                            settlementEntryRepository
                                    .findById("entry-batch-c-001")
                                    .orElseThrow()
                                    .getEntryStatus())
                    .isEqualTo("CONFIRMED");
            assertThat(
                            settlementEntryRepository
                                    .findById("entry-batch-c-002")
                                    .orElseThrow()
                                    .getEntryStatus())
                    .isEqualTo("CONFIRMED");
            assertThat(
                            settlementEntryRepository
                                    .findById("entry-batch-c-003")
                                    .orElseThrow()
                                    .getEntryStatus())
                    .isEqualTo("CONFIRMED");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C3-3] settlementIds 빈 목록 - 400")
        void completeBatch_EmptyIds_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(createCompleteBatchRequest(List.of()))
                    .when()
                    .post(COMPLETE_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C3-4] 권한 없는 사용자 완료 처리 - 403")
        void completeBatch_NoPermission_Returns403() {
            given().spec(givenWithPermission("settlement:read"))
                    .body(createCompleteBatchRequest(List.of("entry-001")))
                    .when()
                    .post(COMPLETE_BATCH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    @DisplayName("C4: POST /settlements/hold/batch - 정산 원장 일괄 보류")
    class HoldSettlementBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C4-1] PENDING 상태 정산 원장 1건 보류 처리 성공")
        void holdBatch_SinglePendingEntry_Success() {
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-hold-001"));

            given().spec(givenSuperAdmin())
                    .body(createHoldBatchRequest(List.of("entry-hold-001"), "이상 거래 감지"))
                    .when()
                    .post(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", nullValue());

            var updated = settlementEntryRepository.findById("entry-hold-001").orElseThrow();
            assertThat(updated.getEntryStatus()).isEqualTo("HOLD");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C4-2] 복수 정산 원장 일괄 보류 처리 성공")
        void holdBatch_MultipleEntries_Success() {
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-hold-b-001"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entry-hold-b-002"));

            given().spec(givenSuperAdmin())
                    .body(
                            createHoldBatchRequest(
                                    List.of("entry-hold-b-001", "entry-hold-b-002"), "일괄 보류 테스트"))
                    .when()
                    .post(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C4-3] settlementIds 빈 목록 - 400")
        void holdBatch_EmptyIds_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(createHoldBatchRequest(List.of(), "사유"))
                    .when()
                    .post(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C4-4] holdReason 누락 - 400")
        void holdBatch_MissingReason_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("settlementIds", List.of("entry-001")))
                    .when()
                    .post(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C4-5] holdReason 빈 문자열 - 400")
        void holdBatch_EmptyReason_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(Map.of("settlementIds", List.of("entry-001"), "holdReason", ""))
                    .when()
                    .post(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C4-6] 권한 없는 사용자 보류 처리 - 403")
        void holdBatch_NoPermission_Returns403() {
            given().spec(givenWithPermission("settlement:read"))
                    .body(createHoldBatchRequest(List.of("entry-001"), "테스트"))
                    .when()
                    .post(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    @Nested
    @DisplayName("C5: POST /settlements/release/batch - 정산 원장 일괄 보류 해제")
    class ReleaseSettlementBatchTest {

        @Test
        @Tag("P0")
        @DisplayName("[C5-1] HOLD 상태 정산 원장 1건 보류 해제 성공")
        void releaseBatch_SingleHoldEntry_Success() {
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.entityWithStatus("entry-release-001", "HOLD"));

            given().spec(givenSuperAdmin())
                    .body(createReleaseBatchRequest(List.of("entry-release-001")))
                    .when()
                    .post(RELEASE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data", nullValue());

            var updated = settlementEntryRepository.findById("entry-release-001").orElseThrow();
            assertThat(updated.getEntryStatus()).isEqualTo("PENDING");
        }

        @Test
        @Tag("P0")
        @DisplayName("[C5-2] 복수 정산 원장 일괄 보류 해제 성공")
        void releaseBatch_MultipleHoldEntries_Success() {
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.entityWithStatus("entry-rel-b-001", "HOLD"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.entityWithStatus("entry-rel-b-002", "HOLD"));

            given().spec(givenSuperAdmin())
                    .body(createReleaseBatchRequest(List.of("entry-rel-b-001", "entry-rel-b-002")))
                    .when()
                    .post(RELEASE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());
        }

        @Test
        @Tag("P0")
        @DisplayName("[C5-3] settlementIds 빈 목록 - 400")
        void releaseBatch_EmptyIds_Returns400() {
            given().spec(givenSuperAdmin())
                    .body(createReleaseBatchRequest(List.of()))
                    .when()
                    .post(RELEASE_BATCH)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        @Tag("P1")
        @DisplayName("[C5-4] 권한 없는 사용자 해제 처리 - 403")
        void releaseBatch_NoPermission_Returns403() {
            given().spec(givenWithPermission("settlement:read"))
                    .body(createReleaseBatchRequest(List.of("entry-001")))
                    .when()
                    .post(RELEASE_BATCH)
                    .then()
                    .statusCode(HttpStatus.FORBIDDEN.value());
        }
    }

    // ===== Helper 메서드 =====

    private Map<String, Object> createCompleteBatchRequest(List<String> settlementIds) {
        return Map.of("settlementIds", settlementIds);
    }

    private Map<String, Object> createHoldBatchRequest(
            List<String> settlementIds, String holdReason) {
        return Map.of(
                "settlementIds", settlementIds,
                "holdReason", holdReason);
    }

    private Map<String, Object> createReleaseBatchRequest(List<String> settlementIds) {
        return Map.of("settlementIds", settlementIds);
    }
}
