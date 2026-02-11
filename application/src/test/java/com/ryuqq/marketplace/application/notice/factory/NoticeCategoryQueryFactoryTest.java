package com.ryuqq.marketplace.application.notice.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.notice.NoticeQueryFixtures;
import com.ryuqq.marketplace.application.notice.dto.query.NoticeCategorySearchParams;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySortKey;
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
@DisplayName("NoticeCategoryQueryFactory 단위 테스트")
class NoticeCategoryQueryFactoryTest {

    @InjectMocks private NoticeCategoryQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @Nested
    @DisplayName("createCriteria() - 검색 조건 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("검색 파라미터로부터 SearchCriteria를 생성한다")
        void createCriteria_ValidParams_ReturnsCriteria() {
            // given
            NoticeCategorySearchParams params = NoticeQueryFixtures.searchParams();
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<NoticeCategorySortKey> queryContext =
                    QueryContext.of(
                            NoticeCategorySortKey.CREATED_AT, sortDirection, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection()))
                    .willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(
                            params.commonSearchParams().page(), params.commonSearchParams().size()))
                    .willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            NoticeCategorySortKey.CREATED_AT, sortDirection, pageRequest, false))
                    .willReturn(queryContext);

            // when
            NoticeCategorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("활성 상태 필터가 있는 경우 SearchCriteria를 생성한다")
        void createCriteria_WithActiveFilter_ReturnsCriteria() {
            // given
            NoticeCategorySearchParams params = NoticeQueryFixtures.searchParams(true);
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<NoticeCategorySortKey> queryContext =
                    QueryContext.of(
                            NoticeCategorySortKey.CREATED_AT, sortDirection, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection()))
                    .willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(
                            params.commonSearchParams().page(), params.commonSearchParams().size()))
                    .willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            NoticeCategorySortKey.CREATED_AT, sortDirection, pageRequest, false))
                    .willReturn(queryContext);

            // when
            NoticeCategorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.active()).isTrue();
        }

        @Test
        @DisplayName("검색어가 있는 경우 SearchCriteria를 생성한다")
        void createCriteria_WithSearchWord_ReturnsCriteria() {
            // given
            NoticeCategorySearchParams params = NoticeQueryFixtures.searchParams("code", "CLOTHING");
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(0, 20);
            QueryContext<NoticeCategorySortKey> queryContext =
                    QueryContext.of(
                            NoticeCategorySortKey.CREATED_AT, sortDirection, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection()))
                    .willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(
                            params.commonSearchParams().page(), params.commonSearchParams().size()))
                    .willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            NoticeCategorySortKey.CREATED_AT, sortDirection, pageRequest, false))
                    .willReturn(queryContext);

            // when
            NoticeCategorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchField()).isEqualTo("code");
            assertThat(result.searchWord()).isEqualTo("CLOTHING");
        }

        @Test
        @DisplayName("페이징 정보가 포함된 SearchCriteria를 생성한다")
        void createCriteria_WithPaging_ReturnsCriteria() {
            // given
            NoticeCategorySearchParams params = NoticeQueryFixtures.searchParams(1, 10);
            SortDirection sortDirection = SortDirection.DESC;
            PageRequest pageRequest = PageRequest.of(1, 10);
            QueryContext<NoticeCategorySortKey> queryContext =
                    QueryContext.of(
                            NoticeCategorySortKey.CREATED_AT, sortDirection, pageRequest, false);

            given(commonVoFactory.parseSortDirection(params.commonSearchParams().sortDirection()))
                    .willReturn(sortDirection);
            given(commonVoFactory.createPageRequest(
                            params.commonSearchParams().page(), params.commonSearchParams().size()))
                    .willReturn(pageRequest);
            given(commonVoFactory.createQueryContext(
                            NoticeCategorySortKey.CREATED_AT, sortDirection, pageRequest, false))
                    .willReturn(queryContext);

            // when
            NoticeCategorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.page()).isEqualTo(1);
            assertThat(result.size()).isEqualTo(10);
        }
    }
}
