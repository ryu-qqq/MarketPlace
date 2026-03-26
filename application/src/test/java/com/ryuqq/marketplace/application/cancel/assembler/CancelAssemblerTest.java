package com.ryuqq.marketplace.application.cancel.assembler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelPageResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;
import com.ryuqq.marketplace.application.claimhistory.assembler.ClaimHistoryAssembler;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
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
@DisplayName("CancelAssembler 단위 테스트")
class CancelAssemblerTest {

    @Mock private ClaimHistoryAssembler historyAssembler;

    private CancelAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new CancelAssembler(historyAssembler);
    }

    @Nested
    @DisplayName("toListResult() - Cancel → CancelListResult 변환")
    class ToListResultTest {

        @Test
        @DisplayName("REQUESTED 상태 Cancel을 CancelListResult로 변환한다")
        void toListResult_RequestedCancel_ReturnsCancelListResult() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();

            // when
            CancelListResult result = sut.toListResult(cancel);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cancelId()).isEqualTo(cancel.idValue());
            assertThat(result.cancelNumber()).isEqualTo(cancel.cancelNumberValue());
            assertThat(result.orderItemId()).isEqualTo(cancel.orderItemIdValue());
            assertThat(result.cancelQty()).isEqualTo(cancel.cancelQty());
            assertThat(result.cancelStatus()).isEqualTo(CancelStatus.REQUESTED.name());
            assertThat(result.cancelType()).isEqualTo(cancel.type().name());
            assertThat(result.reasonType()).isEqualTo(cancel.reason().reasonType().name());
            assertThat(result.refundAmount()).isNull();
            assertThat(result.refundMethod()).isNull();
        }

        @Test
        @DisplayName("환불 정보가 있는 COMPLETED Cancel을 변환하면 환불 정보가 포함된다")
        void toListResult_CompletedCancelWithRefundInfo_IncludesRefundInfo() {
            // given
            Cancel cancel = CancelFixtures.completedCancel();

            // when
            CancelListResult result = sut.toListResult(cancel);

            // then
            assertThat(result.cancelStatus()).isEqualTo(CancelStatus.COMPLETED.name());
            assertThat(result.refundAmount()).isNotNull();
            assertThat(result.refundMethod()).isNotNull();
        }
    }

    @Nested
    @DisplayName("toDetailResult() - Cancel + Histories → CancelDetailResult 변환")
    class ToDetailResultTest {

        @Test
        @DisplayName("Cancel과 이력 목록으로 상세 결과를 생성한다")
        void toDetailResult_CancelWithHistories_ReturnsCancelDetailResult() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            List<ClaimHistory> histories =
                    List.of(ClaimHistoryFixtures.cancelStatusChangeHistory());
            ClaimHistoryResult historyResult = org.mockito.Mockito.mock(ClaimHistoryResult.class);

            given(historyAssembler.toResults(histories)).willReturn(List.of(historyResult));

            // when
            CancelDetailResult result = sut.toDetailResult(cancel, histories);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cancelId()).isEqualTo(cancel.idValue());
            assertThat(result.cancelStatus()).isEqualTo(CancelStatus.REQUESTED.name());
            assertThat(result.histories()).hasSize(1);
            assertThat(result.refundInfo()).isNull();
        }

        @Test
        @DisplayName("환불 정보가 있는 Cancel은 상세 결과에 RefundInfo가 포함된다")
        void toDetailResult_CancelWithRefundInfo_IncludesRefundInfo() {
            // given
            Cancel cancel = CancelFixtures.completedCancel();
            List<ClaimHistory> histories = List.of();

            given(historyAssembler.toResults(histories)).willReturn(List.of());

            // when
            CancelDetailResult result = sut.toDetailResult(cancel, histories);

            // then
            assertThat(result.refundInfo()).isNotNull();
            assertThat(result.refundInfo().refundAmount()).isPositive();
        }

        @Test
        @DisplayName("이력이 없어도 상세 결과를 반환한다")
        void toDetailResult_EmptyHistories_ReturnsCancelDetailWithEmptyHistories() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();

            given(historyAssembler.toResults(List.of())).willReturn(List.of());

            // when
            CancelDetailResult result = sut.toDetailResult(cancel, List.of());

            // then
            assertThat(result.histories()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 결과 생성")
    class ToPageResultTest {

        @Test
        @DisplayName("취소 목록과 페이지 정보로 페이지 결과를 생성한다")
        void toPageResult_ReturnsPageResult() {
            // given
            Cancel cancel = CancelFixtures.requestedCancel();
            CancelListResult listResult = sut.toListResult(cancel);
            int page = 0;
            int size = 20;
            long totalCount = 1L;

            // when
            CancelPageResult result = sut.toPageResult(List.of(listResult), page, size, totalCount);

            // then
            assertThat(result).isNotNull();
            assertThat(result.cancels()).hasSize(1);
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
            CancelPageResult result = sut.toPageResult(List.of(), page, size, totalCount);

            // then
            assertThat(result.cancels()).isEmpty();
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
            Map<CancelStatus, Long> statusCounts =
                    Map.of(
                            CancelStatus.REQUESTED, 5L,
                            CancelStatus.APPROVED, 3L,
                            CancelStatus.REJECTED, 1L,
                            CancelStatus.COMPLETED, 10L,
                            CancelStatus.CANCELLED, 2L);

            // when
            CancelSummaryResult result = sut.toSummaryResult(statusCounts);

            // then
            assertThat(result).isNotNull();
            assertThat(result.requested()).isEqualTo(5L);
            assertThat(result.approved()).isEqualTo(3L);
            assertThat(result.rejected()).isEqualTo(1L);
            assertThat(result.completed()).isEqualTo(10L);
            assertThat(result.cancelled()).isEqualTo(2L);
        }

        @Test
        @DisplayName("빈 맵이면 모든 카운트가 0이다")
        void toSummaryResult_EmptyMap_ReturnsZeroCounts() {
            // given
            Map<CancelStatus, Long> emptyStatusCounts = Map.of();

            // when
            CancelSummaryResult result = sut.toSummaryResult(emptyStatusCounts);

            // then
            assertThat(result.requested()).isZero();
            assertThat(result.approved()).isZero();
            assertThat(result.rejected()).isZero();
            assertThat(result.completed()).isZero();
            assertThat(result.cancelled()).isZero();
        }

        @Test
        @DisplayName("일부 상태만 존재하는 경우 없는 상태는 0으로 반환된다")
        void toSummaryResult_PartialStatusCounts_ReturnsZeroForMissingStatuses() {
            // given
            Map<CancelStatus, Long> partialCounts = Map.of(CancelStatus.REQUESTED, 7L);

            // when
            CancelSummaryResult result = sut.toSummaryResult(partialCounts);

            // then
            assertThat(result.requested()).isEqualTo(7L);
            assertThat(result.approved()).isZero();
            assertThat(result.completed()).isZero();
        }
    }
}
