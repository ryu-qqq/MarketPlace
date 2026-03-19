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
 * Settlement Flow E2E 테스트.
 *
 * <p>테스트 대상:
 *
 * <ul>
 *   <li>F1: 정산 보류 → 조회 → 해제 전체 플로우
 *   <li>F2: 정산 원장 목록 조회 → 일괄 보류 → 일괄 해제 → 일괄 완료 전체 플로우
 * </ul>
 */
@Tag("e2e")
@Tag("settlement")
@Tag("flow")
@DisplayName("Settlement 전체 플로우 E2E 테스트")
class SettlementFlowE2ETest extends E2ETestBase {

    private static final String SETTLEMENTS = "/settlements";
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
    @DisplayName("F1: 정산 보류 → 조회 → 해제 전체 플로우")
    class HoldAndReleaseFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F1-1] CALCULATING 정산을 보류 처리 후 해제하면 CALCULATING으로 복원")
        void holdThenRelease_RestoredToCalculating() {
            // 1. CALCULATING 상태 정산 시딩
            settlementRepository.save(
                    SettlementJpaEntityFixtures.calculatingEntity("settlement-flow-001"));

            // 2. 정산 보류 처리
            given().spec(givenSuperAdmin())
                    .body(Map.of("reason", "이상 거래 의심으로 인한 보류"))
                    .when()
                    .post(SETTLEMENT_HOLD, "settlement-flow-001")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // 3. HOLD 상태 확인
            var held = settlementRepository.findById("settlement-flow-001").orElseThrow();
            assertThat(held.getSettlementStatus()).isEqualTo("HOLD");
            assertThat(held.getHoldReason()).isNotBlank();

            // 4. 정산 보류 해제
            given().spec(givenSuperAdmin())
                    .when()
                    .post(SETTLEMENT_RELEASE, "settlement-flow-001")
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // 5. CALCULATING 상태 복원 확인
            var released = settlementRepository.findById("settlement-flow-001").orElseThrow();
            assertThat(released.getSettlementStatus()).isEqualTo("CALCULATING");
            assertThat(released.getHoldReason()).isNull();
        }
    }

    @Nested
    @DisplayName("F2: 정산 원장 목록 조회 → 일괄 보류 → 일괄 해제 → 일괄 완료 전체 플로우")
    class EntryBatchFlowTest {

        @Test
        @Tag("P0")
        @DisplayName("[F2-1] PENDING 원장 목록 조회 후 일괄 보류, 해제, 확정(CONFIRMED) 순차 플로우")
        void entryBatchFlow_PendingToHoldToReleaseToCOnfirmed() {
            // 1. PENDING 상태 정산 원장 3건 시딩
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("flow-entry-001"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("flow-entry-002"));
            settlementEntryRepository.save(
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("flow-entry-003"));

            // 2. 목록 조회 - PENDING 3건 확인
            given().spec(givenSuperAdmin())
                    .queryParam("status", "PENDING")
                    .queryParam("page", 0)
                    .queryParam("size", 20)
                    .when()
                    .get(SETTLEMENTS)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("data.content", hasSize(3));

            // 3. 일괄 보류 처리 (PENDING → HOLD)
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "settlementIds",
                                    List.of("flow-entry-001", "flow-entry-002", "flow-entry-003"),
                                    "holdReason",
                                    "월말 감사로 인한 일괄 보류"))
                    .when()
                    .post(HOLD_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // 4. HOLD 상태 확인
            assertThat(
                            settlementEntryRepository
                                    .findById("flow-entry-001")
                                    .orElseThrow()
                                    .getEntryStatus())
                    .isEqualTo("HOLD");
            assertThat(
                            settlementEntryRepository
                                    .findById("flow-entry-002")
                                    .orElseThrow()
                                    .getEntryStatus())
                    .isEqualTo("HOLD");
            assertThat(
                            settlementEntryRepository
                                    .findById("flow-entry-003")
                                    .orElseThrow()
                                    .getEntryStatus())
                    .isEqualTo("HOLD");

            // 5. 일괄 보류 해제 (HOLD → PENDING)
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "settlementIds",
                                    List.of("flow-entry-001", "flow-entry-002", "flow-entry-003")))
                    .when()
                    .post(RELEASE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // 6. PENDING 상태 복원 확인
            assertThat(
                            settlementEntryRepository
                                    .findById("flow-entry-001")
                                    .orElseThrow()
                                    .getEntryStatus())
                    .isEqualTo("PENDING");

            // 7. 일괄 확정 처리 (PENDING → CONFIRMED) - completeBatch API가 confirm() 수행
            given().spec(givenSuperAdmin())
                    .body(
                            Map.of(
                                    "settlementIds",
                                    List.of("flow-entry-001", "flow-entry-002", "flow-entry-003")))
                    .when()
                    .post(COMPLETE_BATCH)
                    .then()
                    .statusCode(HttpStatus.OK.value());

            // 8. CONFIRMED 상태 확인
            assertThat(
                            settlementEntryRepository
                                    .findById("flow-entry-001")
                                    .orElseThrow()
                                    .getEntryStatus())
                    .isEqualTo("CONFIRMED");
            assertThat(
                            settlementEntryRepository
                                    .findById("flow-entry-002")
                                    .orElseThrow()
                                    .getEntryStatus())
                    .isEqualTo("CONFIRMED");
            assertThat(
                            settlementEntryRepository
                                    .findById("flow-entry-003")
                                    .orElseThrow()
                                    .getEntryStatus())
                    .isEqualTo("CONFIRMED");
        }
    }
}
