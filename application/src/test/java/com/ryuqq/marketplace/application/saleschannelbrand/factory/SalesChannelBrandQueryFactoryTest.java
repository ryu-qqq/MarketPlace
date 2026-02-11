package com.ryuqq.marketplace.application.saleschannelbrand.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.saleschannelbrand.SalesChannelBrandQueryFixtures;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.query.SalesChannelBrandSearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelbrand.query.SalesChannelBrandSortKey;
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
@DisplayName("SalesChannelBrandQueryFactory 단위 테스트")
class SalesChannelBrandQueryFactoryTest {

    @InjectMocks private SalesChannelBrandQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @Nested
    @DisplayName("createCriteria() - SalesChannelBrandSearchCriteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("SalesChannelBrandSearchParams로 SearchCriteria를 생성한다")
        void createCriteria_ReturnsCriteria() {
            // given
            SalesChannelBrandSearchParams params = SalesChannelBrandQueryFixtures.searchParams();
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<SalesChannelBrandSortKey> queryContext =
                    QueryContext.of(
                            SalesChannelBrandSortKey.defaultKey(),
                            sortDirection,
                            pageRequest,
                            false);

            given(commonVoFactory.parseSortDirection(anyString())).willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(anyInt(), anyInt())).willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            SalesChannelBrandSortKey.defaultKey(),
                            sortDirection,
                            pageRequest,
                            false))
                    .willReturn(queryContext);

            // when
            SalesChannelBrandSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("판매채널 ID 필터가 있는 SearchCriteria를 생성한다")
        void createCriteria_WithSalesChannelIdFilter_ReturnsCriteria() {
            // given
            List<Long> salesChannelIds = List.of(1L, 2L);
            SalesChannelBrandSearchParams params =
                    SalesChannelBrandQueryFixtures.searchParams(salesChannelIds);
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<SalesChannelBrandSortKey> queryContext =
                    QueryContext.of(
                            SalesChannelBrandSortKey.defaultKey(),
                            sortDirection,
                            pageRequest,
                            false);

            given(commonVoFactory.parseSortDirection(anyString())).willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(anyInt(), anyInt())).willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            SalesChannelBrandSortKey.defaultKey(),
                            sortDirection,
                            pageRequest,
                            false))
                    .willReturn(queryContext);

            // when
            SalesChannelBrandSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.salesChannelIds()).containsExactlyElementsOf(salesChannelIds);
        }

        @Test
        @DisplayName("검색 필드와 검색어가 있는 SearchCriteria를 생성한다")
        void createCriteria_WithSearchFieldAndWord_ReturnsCriteria() {
            // given
            SalesChannelBrandSearchParams params =
                    SalesChannelBrandQueryFixtures.searchParams("externalBrandName", "테스트");
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<SalesChannelBrandSortKey> queryContext =
                    QueryContext.of(
                            SalesChannelBrandSortKey.defaultKey(),
                            sortDirection,
                            pageRequest,
                            false);

            given(commonVoFactory.parseSortDirection(anyString())).willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(anyInt(), anyInt())).willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            SalesChannelBrandSortKey.defaultKey(),
                            sortDirection,
                            pageRequest,
                            false))
                    .willReturn(queryContext);

            // when
            SalesChannelBrandSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isEqualTo("테스트");
        }
    }
}
