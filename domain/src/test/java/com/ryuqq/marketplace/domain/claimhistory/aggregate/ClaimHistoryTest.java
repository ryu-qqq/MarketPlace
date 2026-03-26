package com.ryuqq.marketplace.domain.claimhistory.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.id.ClaimHistoryId;
import com.ryuqq.marketplace.domain.claimhistory.vo.Actor;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimHistoryType;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ClaimHistory Aggregate 단위 테스트")
class ClaimHistoryTest {

    @Nested
    @DisplayName("forStatusChange() - 상태 변경 이력 생성")
    class ForStatusChangeTest {

        @Test
        @DisplayName("상태 변경 이력을 생성한다")
        void createStatusChangeHistory() {
            // given
            ClaimHistoryId id = ClaimHistoryFixtures.defaultClaimHistoryId();
            ClaimType claimType = ClaimType.CANCEL;
            String claimId = "cancel-claim-001";
            String fromStatus = "REQUESTED";
            String toStatus = "APPROVED";
            Actor actor = ClaimHistoryFixtures.systemActor();
            Instant now = CommonVoFixtures.now();

            // when
            ClaimHistory history =
                    ClaimHistory.forStatusChange(
                            id,
                            claimType,
                            claimId,
                            "order-item-001",
                            fromStatus,
                            toStatus,
                            actor,
                            now);

            // then
            assertThat(history.id()).isEqualTo(id);
            assertThat(history.claimType()).isEqualTo(ClaimType.CANCEL);
            assertThat(history.claimId()).isEqualTo(claimId);
            assertThat(history.historyType()).isEqualTo(ClaimHistoryType.STATUS_CHANGE);
            assertThat(history.message()).isEqualTo("REQUESTED → APPROVED");
            assertThat(history.actor()).isEqualTo(actor);
            assertThat(history.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("APPROVED 상태로 전환 시 title은 '승인'이다")
        void statusChangeToApprovedHasTitleApproved() {
            // when
            ClaimHistory history =
                    ClaimHistory.forStatusChange(
                            ClaimHistoryFixtures.defaultClaimHistoryId(),
                            ClaimType.REFUND,
                            "refund-001",
                            "order-item-001",
                            "REQUESTED",
                            "APPROVED",
                            ClaimHistoryFixtures.systemActor(),
                            CommonVoFixtures.now());

            // then
            assertThat(history.title()).isEqualTo("승인");
        }

        @Test
        @DisplayName("REQUESTED 상태로 전환 시 title은 '요청'이다")
        void statusChangeToRequestedHasTitleRequested() {
            // when
            ClaimHistory history =
                    ClaimHistory.forStatusChange(
                            ClaimHistoryFixtures.defaultClaimHistoryId(),
                            ClaimType.EXCHANGE,
                            "exchange-001",
                            "order-item-001",
                            "NONE",
                            "REQUESTED",
                            ClaimHistoryFixtures.customerActor(),
                            CommonVoFixtures.now());

            // then
            assertThat(history.title()).isEqualTo("요청");
        }

        @Test
        @DisplayName("COLLECTING 상태로 전환 시 title은 '수거 시작'이다")
        void statusChangeToCollectingHasTitleCollecting() {
            // when
            ClaimHistory history =
                    ClaimHistory.forStatusChange(
                            ClaimHistoryFixtures.defaultClaimHistoryId(),
                            ClaimType.EXCHANGE,
                            "exchange-001",
                            "order-item-001",
                            "APPROVED",
                            "COLLECTING",
                            ClaimHistoryFixtures.systemActor(),
                            CommonVoFixtures.now());

            // then
            assertThat(history.title()).isEqualTo("수거 시작");
        }

        @Test
        @DisplayName("COMPLETED 상태로 전환 시 title은 '완료'이다")
        void statusChangeToCompletedHasTitleCompleted() {
            // when
            ClaimHistory history =
                    ClaimHistory.forStatusChange(
                            ClaimHistoryFixtures.defaultClaimHistoryId(),
                            ClaimType.CANCEL,
                            "cancel-001",
                            "order-item-001",
                            "APPROVED",
                            "COMPLETED",
                            ClaimHistoryFixtures.systemActor(),
                            CommonVoFixtures.now());

            // then
            assertThat(history.title()).isEqualTo("완료");
        }

        @Test
        @DisplayName("REJECTED 상태로 전환 시 title은 '거절'이다")
        void statusChangeToRejectedHasTitleRejected() {
            // when
            ClaimHistory history =
                    ClaimHistory.forStatusChange(
                            ClaimHistoryFixtures.defaultClaimHistoryId(),
                            ClaimType.REFUND,
                            "refund-001",
                            "order-item-001",
                            "REQUESTED",
                            "REJECTED",
                            ClaimHistoryFixtures.adminActor(),
                            CommonVoFixtures.now());

            // then
            assertThat(history.title()).isEqualTo("거절");
        }

        @Test
        @DisplayName("CANCELLED 상태로 전환 시 title은 '취소'이다")
        void statusChangeToCancelledHasTitleCancelled() {
            // when
            ClaimHistory history =
                    ClaimHistory.forStatusChange(
                            ClaimHistoryFixtures.defaultClaimHistoryId(),
                            ClaimType.CANCEL,
                            "cancel-001",
                            "order-item-001",
                            "REQUESTED",
                            "CANCELLED",
                            ClaimHistoryFixtures.customerActor(),
                            CommonVoFixtures.now());

            // then
            assertThat(history.title()).isEqualTo("취소");
        }

        @Test
        @DisplayName("알 수 없는 상태로 전환 시 title은 '상태 변경'이다")
        void statusChangeToUnknownHasTitleDefault() {
            // when
            ClaimHistory history =
                    ClaimHistory.forStatusChange(
                            ClaimHistoryFixtures.defaultClaimHistoryId(),
                            ClaimType.CANCEL,
                            "cancel-001",
                            "order-item-001",
                            "UNKNOWN_FROM",
                            "UNKNOWN_TO",
                            ClaimHistoryFixtures.systemActor(),
                            CommonVoFixtures.now());

            // then
            assertThat(history.title()).isEqualTo("상태 변경");
        }

        @Test
        @DisplayName("SHIPPING 상태로 전환 시 title은 '재배송 시작'이다")
        void statusChangeToShippingHasTitleShipping() {
            // when
            ClaimHistory history =
                    ClaimHistory.forStatusChange(
                            ClaimHistoryFixtures.defaultClaimHistoryId(),
                            ClaimType.EXCHANGE,
                            "exchange-001",
                            "order-item-001",
                            "PREPARING",
                            "SHIPPING",
                            ClaimHistoryFixtures.systemActor(),
                            CommonVoFixtures.now());

            // then
            assertThat(history.title()).isEqualTo("재배송 시작");
        }

        @Test
        @DisplayName("Cancel 클레임의 상태 변경 이력을 생성한다")
        void createCancelStatusChangeHistory() {
            // when
            ClaimHistory history = ClaimHistoryFixtures.cancelStatusChangeHistory();

            // then
            assertThat(history.claimType()).isEqualTo(ClaimType.CANCEL);
            assertThat(history.historyType()).isEqualTo(ClaimHistoryType.STATUS_CHANGE);
        }

        @Test
        @DisplayName("Refund 클레임의 상태 변경 이력을 생성한다")
        void createRefundStatusChangeHistory() {
            // when
            ClaimHistory history = ClaimHistoryFixtures.refundStatusChangeHistory();

            // then
            assertThat(history.claimType()).isEqualTo(ClaimType.REFUND);
            assertThat(history.historyType()).isEqualTo(ClaimHistoryType.STATUS_CHANGE);
        }

        @Test
        @DisplayName("Exchange 클레임의 상태 변경 이력을 생성한다")
        void createExchangeStatusChangeHistory() {
            // when
            ClaimHistory history = ClaimHistoryFixtures.exchangeStatusChangeHistory();

            // then
            assertThat(history.claimType()).isEqualTo(ClaimType.EXCHANGE);
            assertThat(history.historyType()).isEqualTo(ClaimHistoryType.STATUS_CHANGE);
        }
    }

    @Nested
    @DisplayName("forManual() - 수기 메모 이력 생성")
    class ForManualTest {

        @Test
        @DisplayName("수기 메모 이력을 생성한다")
        void createManualHistory() {
            // given
            ClaimHistoryId id = ClaimHistoryFixtures.defaultClaimHistoryId();
            ClaimType claimType = ClaimType.REFUND;
            String claimId = "refund-claim-001";
            String message = "고객 요청으로 환불 처리";
            Actor actor = ClaimHistoryFixtures.adminActor();
            Instant now = CommonVoFixtures.now();

            // when
            ClaimHistory history =
                    ClaimHistory.forManual(
                            id, claimType, claimId, "order-item-001", message, actor, now);

            // then
            assertThat(history.id()).isEqualTo(id);
            assertThat(history.claimType()).isEqualTo(ClaimType.REFUND);
            assertThat(history.claimId()).isEqualTo(claimId);
            assertThat(history.historyType()).isEqualTo(ClaimHistoryType.MANUAL);
            assertThat(history.title()).isEqualTo("CS 메모");
            assertThat(history.message()).isEqualTo(message);
            assertThat(history.actor()).isEqualTo(actor);
            assertThat(history.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("수기 메모의 title은 항상 'CS 메모'이다")
        void manualHistoryTitleIsAlwaysCsMemo() {
            // when
            ClaimHistory history = ClaimHistoryFixtures.manualClaimHistory();

            // then
            assertThat(history.title()).isEqualTo("CS 메모");
            assertThat(history.historyType()).isEqualTo(ClaimHistoryType.MANUAL);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("상태 변경 이력을 영속성에서 복원한다")
        void reconstituteStatusChangeHistory() {
            // given
            ClaimHistoryId id = ClaimHistoryFixtures.defaultClaimHistoryId();
            ClaimType claimType = ClaimType.CANCEL;
            String claimId = "cancel-claim-001";
            ClaimHistoryType historyType = ClaimHistoryType.STATUS_CHANGE;
            String title = "승인";
            String message = "REQUESTED → APPROVED";
            Actor actor = ClaimHistoryFixtures.systemActor();
            Instant createdAt = CommonVoFixtures.yesterday();

            // when
            ClaimHistory history =
                    ClaimHistory.reconstitute(
                            id,
                            claimType,
                            claimId,
                            "order-item-001",
                            historyType,
                            title,
                            message,
                            actor,
                            createdAt);

            // then
            assertThat(history.id()).isEqualTo(id);
            assertThat(history.claimType()).isEqualTo(ClaimType.CANCEL);
            assertThat(history.claimId()).isEqualTo(claimId);
            assertThat(history.orderItemId()).isEqualTo("order-item-001");
            assertThat(history.historyType()).isEqualTo(ClaimHistoryType.STATUS_CHANGE);
            assertThat(history.title()).isEqualTo(title);
            assertThat(history.message()).isEqualTo(message);
            assertThat(history.actor()).isEqualTo(actor);
            assertThat(history.createdAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("수기 메모 이력을 영속성에서 복원한다")
        void reconstituteManualHistory() {
            // when
            ClaimHistory history = ClaimHistoryFixtures.reconstitutedManualClaimHistory();

            // then
            assertThat(history.historyType()).isEqualTo(ClaimHistoryType.MANUAL);
            assertThat(history.title()).isEqualTo("CS 메모");
            assertThat(history.claimType()).isEqualTo(ClaimType.REFUND);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 문자열 값을 반환한다")
        void idValueReturnsStringValue() {
            // given
            ClaimHistory history = ClaimHistoryFixtures.statusChangeClaimHistory();

            // when
            String idValue = history.idValue();

            // then
            assertThat(idValue).isEqualTo(ClaimHistoryFixtures.defaultClaimHistoryId().value());
        }

        @Test
        @DisplayName("모든 getter가 올바른 값을 반환한다")
        void allGettersReturnCorrectValues() {
            // given
            ClaimHistory history = ClaimHistoryFixtures.reconstitutedClaimHistory();

            // then
            assertThat(history.id()).isNotNull();
            assertThat(history.idValue()).isNotNull();
            assertThat(history.claimType()).isEqualTo(ClaimType.CANCEL);
            assertThat(history.claimId()).isEqualTo("cancel-claim-001");
            assertThat(history.historyType()).isEqualTo(ClaimHistoryType.STATUS_CHANGE);
            assertThat(history.title()).isEqualTo("승인");
            assertThat(history.message()).isEqualTo("REQUESTED → APPROVED");
            assertThat(history.actor()).isNotNull();
            assertThat(history.createdAt()).isNotNull();
        }
    }
}
