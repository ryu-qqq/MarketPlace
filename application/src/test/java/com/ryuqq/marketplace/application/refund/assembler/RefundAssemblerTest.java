package com.ryuqq.marketplace.application.refund.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.claimhistory.assembler.ClaimHistoryAssembler;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundDetailResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundListResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundPageResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundSummaryResult;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("RefundAssembler 단위 테스트")
class RefundAssemblerTest {

    @Mock private ClaimHistoryAssembler historyAssembler;

    private RefundAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new RefundAssembler(historyAssembler);
    }

    @Nested
    @DisplayName("toListResult() - RefundClaim → RefundListResult 변환")
    class ToListResultTest {

        @Test
        @DisplayName("REQUESTED 상태 RefundClaim을 RefundListResult로 변환한다")
        void toListResult_RequestedClaim_ReturnsRefundListResult() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // when
            RefundListResult result = sut.toListResult(claim);

            // then
            assertThat(result).isNotNull();
            assertThat(result.refundClaimId()).isEqualTo(claim.idValue());
            assertThat(result.claimNumber()).isEqualTo(claim.claimNumberValue());
            assertThat(result.orderItemId()).isEqualTo(claim.orderItemIdValue());
            assertThat(result.refundQty()).isEqualTo(claim.refundQty());
            assertThat(result.refundStatus()).isEqualTo(RefundStatus.REQUESTED.name());
            assertThat(result.reasonType()).isEqualTo(claim.reason().reasonType().name());
            assertThat(result.originalAmount()).isNull();
            assertThat(result.finalAmount()).isNull();
            assertThat(result.refundMethod()).isNull();
        }

        @Test
        @DisplayName("환불 정보가 있는 COMPLETED RefundClaim을 변환하면 환불 정보가 포함된다")
        void toListResult_CompletedClaimWithRefundInfo_IncludesRefundInfo() {
            // given
            RefundClaim claim = RefundFixtures.completedRefundClaim();

            // when
            RefundListResult result = sut.toListResult(claim);

            // then
            assertThat(result.refundStatus()).isEqualTo(RefundStatus.COMPLETED.name());
            assertThat(result.originalAmount()).isNotNull();
            assertThat(result.finalAmount()).isNotNull();
            assertThat(result.refundMethod()).isNotNull();
        }
    }

    @Nested
    @DisplayName("toDetailResult() - RefundClaim + Histories → RefundDetailResult 변환")
    class ToDetailResultTest {

        @Test
        @DisplayName("RefundClaim과 이력 목록으로 상세 결과를 생성한다")
        void toDetailResult_ClaimWithHistories_ReturnsRefundDetailResult() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.refundStatusChangeHistory());
            ClaimHistoryResult historyResult = org.mockito.Mockito.mock(ClaimHistoryResult.class);

            given(historyAssembler.toResults(histories)).willReturn(List.of(historyResult));

            // when
            RefundDetailResult result = sut.toDetailResult(claim, histories);

            // then
            assertThat(result).isNotNull();
            assertThat(result.refundClaimId()).isEqualTo(claim.idValue());
            assertThat(result.refundStatus()).isEqualTo(RefundStatus.REQUESTED.name());
            assertThat(result.histories()).hasSize(1);
            assertThat(result.refundInfo()).isNull();
            assertThat(result.holdInfo()).isNull();
        }

        @Test
        @DisplayName("환불 정보가 있는 COMPLETED RefundClaim은 상세 결과에 RefundInfo가 포함된다")
        void toDetailResult_CompletedClaimWithRefundInfo_IncludesRefundInfo() {
            // given
            RefundClaim claim = RefundFixtures.completedRefundClaim();
            List<ClaimHistory> histories = List.of();

            given(historyAssembler.toResults(histories)).willReturn(List.of());

            // when
            RefundDetailResult result = sut.toDetailResult(claim, histories);

            // then
            assertThat(result.refundInfo()).isNotNull();
            assertThat(result.refundInfo().originalAmount()).isPositive();
        }

        @Test
        @DisplayName("보류 정보가 있는 RefundClaim은 상세 결과에 HoldInfo가 포함된다")
        void toDetailResult_HeldClaimWithHoldInfo_IncludesHoldInfo() {
            // given
            RefundClaim claim = RefundFixtures.holdRefundClaim();
            List<ClaimHistory> histories = List.of();

            given(historyAssembler.toResults(histories)).willReturn(List.of());

            // when
            RefundDetailResult result = sut.toDetailResult(claim, histories);

            // then
            assertThat(result.holdInfo()).isNotNull();
            assertThat(result.holdInfo().holdReason()).isNotBlank();
        }

        @Test
        @DisplayName("이력이 없어도 상세 결과를 반환한다")
        void toDetailResult_EmptyHistories_ReturnsDetailWithEmptyHistories() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            given(historyAssembler.toResults(List.of())).willReturn(List.of());

            // when
            RefundDetailResult result = sut.toDetailResult(claim, List.of());

            // then
            assertThat(result.histories()).isEmpty();
        }

        @Test
        @DisplayName("수거 배송 정보가 없는 경우 collectShipment는 null이다")
        void toDetailResult_ClaimWithoutCollectShipment_CollectShipmentIsNull() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            List<ClaimHistory> histories = List.of();

            given(historyAssembler.toResults(histories)).willReturn(List.of());

            // when
            RefundDetailResult result = sut.toDetailResult(claim, histories);

            // then
            assertThat(result.collectShipment()).isNull();
        }
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 결과 생성")
    class ToPageResultTest {

        @Test
        @DisplayName("환불 목록과 페이지 정보로 페이지 결과를 생성한다")
        void toPageResult_ReturnsPageResult() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            RefundListResult listResult = sut.toListResult(claim);
            int page = 0;
            int size = 20;
            long totalCount = 1L;

            // when
            RefundPageResult result = sut.toPageResult(List.of(listResult), page, size, totalCount);

            // then
            assertThat(result).isNotNull();
            assertThat(result.refunds()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalCount);
        }

        @Test
        @DisplayName("빈 목록으로 빈 페이지 결과를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // given
            int page = 0;
            int size = 20;
            long totalCount = 0L;

            // when
            RefundPageResult result = sut.toPageResult(List.of(), page, size, totalCount);

            // then
            assertThat(result.refunds()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("toSummaryResult() - 상태별 요약 결과 생성")
    class ToSummaryResultTest {

        @Test
        @DisplayName("상태별 카운트로 요약 결과를 생성한다")
        void toSummaryResult_StatusCountMap_ReturnsSummaryResult() {
            // given
            Map<RefundStatus, Long> statusCounts =
                    Map.of(
                            RefundStatus.REQUESTED, 5L,
                            RefundStatus.COLLECTING, 3L,
                            RefundStatus.COLLECTED, 2L,
                            RefundStatus.COMPLETED, 10L,
                            RefundStatus.REJECTED, 1L,
                            RefundStatus.CANCELLED, 0L);

            // when
            RefundSummaryResult result = sut.toSummaryResult(statusCounts);

            // then
            assertThat(result).isNotNull();
            assertThat(result.requested()).isEqualTo(5L);
            assertThat(result.collecting()).isEqualTo(3L);
            assertThat(result.collected()).isEqualTo(2L);
            assertThat(result.completed()).isEqualTo(10L);
            assertThat(result.rejected()).isEqualTo(1L);
            assertThat(result.cancelled()).isEqualTo(0L);
        }

        @Test
        @DisplayName("빈 맵이면 모든 카운트가 0이다")
        void toSummaryResult_EmptyMap_ReturnsZeroCounts() {
            // given
            Map<RefundStatus, Long> emptyStatusCounts = Map.of();

            // when
            RefundSummaryResult result = sut.toSummaryResult(emptyStatusCounts);

            // then
            assertThat(result.requested()).isZero();
            assertThat(result.collecting()).isZero();
            assertThat(result.collected()).isZero();
            assertThat(result.completed()).isZero();
            assertThat(result.rejected()).isZero();
            assertThat(result.cancelled()).isZero();
        }

        @Test
        @DisplayName("일부 상태만 존재하는 경우 없는 상태는 0으로 반환된다")
        void toSummaryResult_PartialStatusCounts_ReturnsZeroForMissingStatuses() {
            // given
            Map<RefundStatus, Long> partialCounts = Map.of(RefundStatus.REQUESTED, 7L);

            // when
            RefundSummaryResult result = sut.toSummaryResult(partialCounts);

            // then
            assertThat(result.requested()).isEqualTo(7L);
            assertThat(result.collecting()).isZero();
            assertThat(result.completed()).isZero();
        }
    }
}
