package com.ryuqq.marketplace.application.saleschannel.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.saleschannel.SalesChannelQueryFixtures;
import com.ryuqq.marketplace.application.saleschannel.assembler.SalesChannelAssembler;
import com.ryuqq.marketplace.application.saleschannel.dto.query.SalesChannelSearchParams;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelPageResult;
import com.ryuqq.marketplace.application.saleschannel.factory.SalesChannelQueryFactory;
import com.ryuqq.marketplace.application.saleschannel.manager.SalesChannelReadManager;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
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
@DisplayName("SearchSalesChannelByOffsetService 단위 테스트")
class SearchSalesChannelByOffsetServiceTest {

    @InjectMocks private SearchSalesChannelByOffsetService sut;

    @Mock private SalesChannelReadManager readManager;
    @Mock private SalesChannelQueryFactory queryFactory;
    @Mock private SalesChannelAssembler assembler;

    @Nested
    @DisplayName("execute() - 판매채널 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 판매채널을 페이징 조회한다")
        void execute_ValidParams_ReturnsPageResult() {
            // given
            SalesChannelSearchParams params = SalesChannelQueryFixtures.searchParams();
            SalesChannelSearchCriteria criteria = null; // mock 대상이므로 null 허용
            List<SalesChannel> salesChannels =
                    List.of(
                            SalesChannelFixtures.activeSalesChannel(1L),
                            SalesChannelFixtures.activeSalesChannel(2L));
            long totalElements = 2L;
            SalesChannelPageResult expectedResult =
                    SalesChannelQueryFixtures.salesChannelPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(salesChannels);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(
                            assembler.toPageResult(
                                    salesChannels, params.page(), params.size(), totalElements))
                    .willReturn(expectedResult);

            // when
            SalesChannelPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler)
                    .should()
                    .toPageResult(salesChannels, params.page(), params.size(), totalElements);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            SalesChannelSearchParams params = SalesChannelQueryFixtures.searchParams();
            SalesChannelSearchCriteria criteria = null;
            List<SalesChannel> emptySalesChannels = List.of();
            long totalElements = 0L;
            SalesChannelPageResult emptyResult = SalesChannelQueryFixtures.emptyPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptySalesChannels);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(
                            assembler.toPageResult(
                                    emptySalesChannels,
                                    params.page(),
                                    params.size(),
                                    totalElements))
                    .willReturn(emptyResult);

            // when
            SalesChannelPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(emptyResult);
            assertThat(result.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("상태별로 판매채널을 필터링 조회한다")
        void execute_WithStatusFilter_ReturnsFilteredResults() {
            // given
            List<String> statuses = List.of("ACTIVE");
            SalesChannelSearchParams params = SalesChannelQueryFixtures.searchParams(statuses);
            SalesChannelSearchCriteria criteria = null;
            List<SalesChannel> activeSalesChannels =
                    List.of(SalesChannelFixtures.activeSalesChannel(1L));
            long totalElements = 1L;
            SalesChannelPageResult expectedResult =
                    SalesChannelQueryFixtures.salesChannelPageResult(0, 20, 1L);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(activeSalesChannels);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(
                            assembler.toPageResult(
                                    activeSalesChannels,
                                    params.page(),
                                    params.size(),
                                    totalElements))
                    .willReturn(expectedResult);

            // when
            SalesChannelPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
        }
    }
}
