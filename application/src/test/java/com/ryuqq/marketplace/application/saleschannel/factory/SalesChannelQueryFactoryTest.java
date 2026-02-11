package com.ryuqq.marketplace.application.saleschannel.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.saleschannel.SalesChannelQueryFixtures;
import com.ryuqq.marketplace.application.saleschannel.dto.query.SalesChannelSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSortKey;
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
@DisplayName("SalesChannelQueryFactory 단위 테스트")
class SalesChannelQueryFactoryTest {

    @InjectMocks private SalesChannelQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @Nested
    @DisplayName("createCriteria() - 검색 조건 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("검색 파라미터로부터 SearchCriteria를 생성한다")
        void createCriteria_ValidParams_ReturnsCriteria() {
            // given
            SalesChannelSearchParams params = SalesChannelQueryFixtures.searchParams();
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<SalesChannelSortKey> queryContext =
                    QueryContext.of(
                            SalesChannelSortKey.CREATED_AT, sortDirection, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.sortDirection()))
                    .willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(
                            commonVoFactory.createQueryContext(
                                    SalesChannelSortKey.CREATED_AT,
                                    sortDirection,
                                    pageRequest,
                                    false))
                    .willReturn(queryContext);

            // when
            SalesChannelSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("상태 필터가 있는 경우 SearchCriteria를 생성한다")
        void createCriteria_WithStatusFilter_ReturnsCriteria() {
            // given
            List<String> statuses = List.of("ACTIVE");
            SalesChannelSearchParams params = SalesChannelQueryFixtures.searchParams(statuses);
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<SalesChannelSortKey> queryContext =
                    QueryContext.of(
                            SalesChannelSortKey.CREATED_AT, sortDirection, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.sortDirection()))
                    .willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(
                            commonVoFactory.createQueryContext(
                                    SalesChannelSortKey.CREATED_AT,
                                    sortDirection,
                                    pageRequest,
                                    false))
                    .willReturn(queryContext);

            // when
            SalesChannelSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).hasSize(1);
        }

        @Test
        @DisplayName("검색어가 있는 경우 SearchCriteria를 생성한다")
        void createCriteria_WithSearchWord_ReturnsCriteria() {
            // given
            SalesChannelSearchParams params =
                    SalesChannelQueryFixtures.searchParams("channelName", "테스트");
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<SalesChannelSortKey> queryContext =
                    QueryContext.of(
                            SalesChannelSortKey.CREATED_AT, sortDirection, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.sortDirection()))
                    .willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(
                            commonVoFactory.createQueryContext(
                                    SalesChannelSortKey.CREATED_AT,
                                    sortDirection,
                                    pageRequest,
                                    false))
                    .willReturn(queryContext);

            // when
            SalesChannelSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isEqualTo("테스트");
        }
    }
}
