package com.ryuqq.marketplace.application.refund.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.refund.RefundQueryFixtures;
import com.ryuqq.marketplace.application.refund.assembler.RefundAssembler;
import com.ryuqq.marketplace.application.refund.dto.query.RefundSearchParams;
import com.ryuqq.marketplace.application.refund.dto.response.RefundListResult;
import com.ryuqq.marketplace.application.refund.dto.response.RefundPageResult;
import com.ryuqq.marketplace.application.refund.factory.RefundQueryFactory;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchCriteria;
import java.util.List;
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
@DisplayName("GetRefundListService 단위 테스트")
class GetRefundListServiceTest {

    @InjectMocks private GetRefundListService sut;

    @Mock private RefundReadManager refundReadManager;
    @Mock private RefundQueryFactory queryFactory;
    @Mock private RefundAssembler assembler;
    @Mock private RefundSearchCriteria criteria;

    @Nested
    @DisplayName("execute() - 환불 목록 조회")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 환불 목록을 조회하고 페이지 결과를 반환한다")
        void execute_ValidParams_ReturnsRefundPageResult() {
            // given
            RefundSearchParams params = RefundQueryFixtures.searchParams();
            RefundClaim claim = RefundFixtures.requestedRefundClaim();
            RefundListResult listResult = RefundQueryFixtures.refundListResult();
            RefundPageResult expectedResult = RefundQueryFixtures.refundPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(refundReadManager.findByCriteria(criteria)).willReturn(List.of(claim));
            given(refundReadManager.countByCriteria(criteria)).willReturn(1L);
            given(assembler.toListResult(claim)).willReturn(listResult);
            given(assembler.toPageResult(List.of(listResult), params.page(), params.size(), 1L))
                    .willReturn(expectedResult);

            // when
            RefundPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(queryFactory).should().createCriteria(params);
            then(refundReadManager).should().findByCriteria(criteria);
            then(refundReadManager).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 페이지 결과를 반환한다")
        void execute_NoResults_ReturnsEmptyPageResult() {
            // given
            RefundSearchParams params = RefundQueryFixtures.searchParams();
            RefundPageResult emptyResult = RefundQueryFixtures.emptyRefundPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(refundReadManager.findByCriteria(criteria)).willReturn(List.of());
            given(refundReadManager.countByCriteria(criteria)).willReturn(0L);
            given(assembler.toPageResult(List.of(), params.page(), params.size(), 0L))
                    .willReturn(emptyResult);

            // when
            RefundPageResult result = sut.execute(params);

            // then
            assertThat(result.refunds()).isEmpty();
        }
    }
}
