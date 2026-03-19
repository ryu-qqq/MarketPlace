package com.ryuqq.marketplace.application.cancel.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.cancel.CancelQueryFixtures;
import com.ryuqq.marketplace.application.cancel.assembler.CancelAssembler;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelSummaryResult;
import com.ryuqq.marketplace.application.cancel.port.out.query.CancelQueryPort;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
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
@DisplayName("GetCancelSummaryService 단위 테스트")
class GetCancelSummaryServiceTest {

    @InjectMocks private GetCancelSummaryService sut;

    @Mock private CancelQueryPort cancelQueryPort;
    @Mock private CancelAssembler assembler;

    @Nested
    @DisplayName("execute() - 취소 상태별 요약 조회")
    class ExecuteTest {

        @Test
        @DisplayName("상태별 취소 건수를 집계하여 요약 결과를 반환한다")
        void execute_ReturnsCancelSummaryResult() {
            // given
            Map<CancelStatus, Long> statusCounts =
                    Map.of(
                            CancelStatus.REQUESTED, 5L,
                            CancelStatus.APPROVED, 3L,
                            CancelStatus.REJECTED, 1L,
                            CancelStatus.COMPLETED, 10L,
                            CancelStatus.CANCELLED, 2L);
            CancelSummaryResult expectedResult = CancelQueryFixtures.cancelSummaryResult();

            given(cancelQueryPort.countByStatus()).willReturn(statusCounts);
            given(assembler.toSummaryResult(statusCounts)).willReturn(expectedResult);

            // when
            CancelSummaryResult result = sut.execute();

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(cancelQueryPort).should().countByStatus();
            then(assembler).should().toSummaryResult(statusCounts);
        }

        @Test
        @DisplayName("상태 카운트가 비어있어도 요약 결과를 반환한다")
        void execute_EmptyStatusCounts_ReturnsEmptySummaryResult() {
            // given
            Map<CancelStatus, Long> emptyStatusCounts = Map.of();
            CancelSummaryResult emptyResult = CancelQueryFixtures.emptyCancelSummaryResult();

            given(cancelQueryPort.countByStatus()).willReturn(emptyStatusCounts);
            given(assembler.toSummaryResult(emptyStatusCounts)).willReturn(emptyResult);

            // when
            CancelSummaryResult result = sut.execute();

            // then
            assertThat(result).isEqualTo(emptyResult);
            assertThat(result.requested()).isZero();
            assertThat(result.approved()).isZero();
        }
    }
}
