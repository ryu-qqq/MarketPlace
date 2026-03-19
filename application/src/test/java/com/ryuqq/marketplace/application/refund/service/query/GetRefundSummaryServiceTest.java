package com.ryuqq.marketplace.application.refund.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.refund.RefundQueryFixtures;
import com.ryuqq.marketplace.application.refund.assembler.RefundAssembler;
import com.ryuqq.marketplace.application.refund.dto.response.RefundSummaryResult;
import com.ryuqq.marketplace.application.refund.port.out.query.RefundQueryPort;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetRefundSummaryService 단위 테스트")
class GetRefundSummaryServiceTest {

    @InjectMocks private GetRefundSummaryService sut;

    @Mock private RefundQueryPort refundQueryPort;
    @Mock private RefundAssembler assembler;

    @Nested
    @DisplayName("execute() - 환불 상태별 요약 조회")
    class ExecuteTest {

        @Test
        @DisplayName("상태별 환불 건수를 집계하여 요약 결과를 반환한다")
        void execute_ReturnsRefundSummaryResult() {
            // given
            Map<RefundStatus, Long> statusCounts =
                    Map.of(
                            RefundStatus.REQUESTED, 5L,
                            RefundStatus.COLLECTING, 3L,
                            RefundStatus.COLLECTED, 2L,
                            RefundStatus.COMPLETED, 10L,
                            RefundStatus.REJECTED, 1L,
                            RefundStatus.CANCELLED, 0L);
            RefundSummaryResult expectedResult = RefundQueryFixtures.refundSummaryResult();

            given(refundQueryPort.countByStatus()).willReturn(statusCounts);
            given(assembler.toSummaryResult(statusCounts)).willReturn(expectedResult);

            // when
            RefundSummaryResult result = sut.execute();

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(refundQueryPort).should().countByStatus();
            then(assembler).should().toSummaryResult(statusCounts);
        }

        @Test
        @DisplayName("상태 카운트가 비어있어도 요약 결과를 반환한다")
        void execute_EmptyStatusCounts_ReturnsEmptySummaryResult() {
            // given
            Map<RefundStatus, Long> emptyStatusCounts = Map.of();
            RefundSummaryResult emptyResult = RefundQueryFixtures.emptyRefundSummaryResult();

            given(refundQueryPort.countByStatus()).willReturn(emptyStatusCounts);
            given(assembler.toSummaryResult(emptyStatusCounts)).willReturn(emptyResult);

            // when
            RefundSummaryResult result = sut.execute();

            // then
            assertThat(result).isEqualTo(emptyResult);
            assertThat(result.requested()).isZero();
            assertThat(result.collecting()).isZero();
        }
    }
}
