package com.ryuqq.marketplace.application.cancel.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.cancel.CancelQueryFixtures;
import com.ryuqq.marketplace.application.cancel.assembler.CancelAssembler;
import com.ryuqq.marketplace.application.cancel.dto.query.CancelSearchParams;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelListResult;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelPageResult;
import com.ryuqq.marketplace.application.cancel.factory.CancelQueryFactory;
import com.ryuqq.marketplace.application.cancel.manager.CancelReadManager;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
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
@DisplayName("GetCancelListService 단위 테스트")
class GetCancelListServiceTest {

    @InjectMocks private GetCancelListService sut;

    @Mock private CancelReadManager cancelReadManager;
    @Mock private CancelQueryFactory queryFactory;
    @Mock private CancelAssembler assembler;
    @Mock private CancelSearchCriteria criteria;

    @Nested
    @DisplayName("execute() - 취소 목록 조회")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 취소 목록을 조회하고 페이지 결과를 반환한다")
        void execute_ValidParams_ReturnsCancelPageResult() {
            // given
            CancelSearchParams params = CancelQueryFixtures.searchParams();
            Cancel cancel = CancelFixtures.requestedCancel();
            CancelListResult listResult = CancelQueryFixtures.cancelListResult();
            CancelPageResult expectedResult = CancelQueryFixtures.cancelPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(cancelReadManager.findByCriteria(criteria)).willReturn(List.of(cancel));
            given(cancelReadManager.countByCriteria(criteria)).willReturn(1L);
            given(assembler.toListResult(cancel)).willReturn(listResult);
            given(assembler.toPageResult(List.of(listResult), params.page(), params.size(), 1L))
                    .willReturn(expectedResult);

            // when
            CancelPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(queryFactory).should().createCriteria(params);
            then(cancelReadManager).should().findByCriteria(criteria);
            then(cancelReadManager).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 페이지 결과를 반환한다")
        void execute_NoResults_ReturnsEmptyPageResult() {
            // given
            CancelSearchParams params = CancelQueryFixtures.searchParams();
            CancelPageResult emptyResult = CancelQueryFixtures.emptyCancelPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(cancelReadManager.findByCriteria(criteria)).willReturn(List.of());
            given(cancelReadManager.countByCriteria(criteria)).willReturn(0L);
            given(assembler.toPageResult(List.of(), params.page(), params.size(), 0L))
                    .willReturn(emptyResult);

            // when
            CancelPageResult result = sut.execute(params);

            // then
            assertThat(result.cancels()).isEmpty();
        }
    }
}
