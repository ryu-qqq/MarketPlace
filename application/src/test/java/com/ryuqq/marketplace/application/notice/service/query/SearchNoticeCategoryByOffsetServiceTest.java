package com.ryuqq.marketplace.application.notice.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.notice.NoticeQueryFixtures;
import com.ryuqq.marketplace.application.notice.assembler.NoticeCategoryAssembler;
import com.ryuqq.marketplace.application.notice.dto.query.NoticeCategorySearchParams;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryPageResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.factory.NoticeCategoryQueryFactory;
import com.ryuqq.marketplace.application.notice.manager.NoticeCategoryReadManager;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.notice.NoticeFixtures;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySortKey;
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
@DisplayName("SearchNoticeCategoryByOffsetService 단위 테스트")
class SearchNoticeCategoryByOffsetServiceTest {

    @InjectMocks private SearchNoticeCategoryByOffsetService sut;

    @Mock private NoticeCategoryReadManager readManager;
    @Mock private NoticeCategoryQueryFactory queryFactory;
    @Mock private NoticeCategoryAssembler assembler;

    private static NoticeCategorySearchCriteria defaultCriteria() {
        return new NoticeCategorySearchCriteria(
                null, null, null, QueryContext.defaultOf(NoticeCategorySortKey.defaultKey()));
    }

    private static NoticeCategorySearchCriteria criteriaWithPage(int page, int size) {
        return new NoticeCategorySearchCriteria(
                null,
                null,
                null,
                QueryContext.of(
                        NoticeCategorySortKey.defaultKey(),
                        SortDirection.DESC,
                        PageRequest.of(page, size),
                        false));
    }

    @Nested
    @DisplayName("execute() - 고시정보 카테고리 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 파라미터로 고시정보 카테고리를 페이징 조회한다")
        void execute_ValidParams_ReturnsPageResult() {
            // given
            NoticeCategorySearchParams params = NoticeQueryFixtures.searchParams();
            NoticeCategorySearchCriteria criteria = defaultCriteria();

            NoticeCategory category1 = NoticeFixtures.activeNoticeCategory(1L);
            NoticeCategory category2 = NoticeFixtures.activeNoticeCategory(2L);
            List<NoticeCategory> categories = List.of(category1, category2);
            long totalElements = 2L;

            NoticeCategoryResult result1 = NoticeQueryFixtures.noticeCategoryResult(1L);
            NoticeCategoryResult result2 = NoticeQueryFixtures.noticeCategoryResult(2L);
            List<NoticeCategoryResult> results = List.of(result1, result2);
            NoticeCategoryPageResult expectedResult =
                    NoticeQueryFixtures.noticeCategoryPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(categories);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toResult(category1)).willReturn(result1);
            given(assembler.toResult(category2)).willReturn(result2);
            given(assembler.toPageResult(results, criteria.page(), criteria.size(), totalElements))
                    .willReturn(expectedResult);

            // when
            NoticeCategoryPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            NoticeCategorySearchParams params = NoticeQueryFixtures.searchParams();
            NoticeCategorySearchCriteria criteria = defaultCriteria();

            List<NoticeCategory> categories = List.of();
            long totalElements = 0L;

            List<NoticeCategoryResult> emptyResults = List.of();
            NoticeCategoryPageResult emptyResult = NoticeQueryFixtures.emptyPageResult();

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(categories);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(
                            assembler.toPageResult(
                                    emptyResults, criteria.page(), criteria.size(), totalElements))
                    .willReturn(emptyResult);

            // when
            NoticeCategoryPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(emptyResult);
        }

        @Test
        @DisplayName("활성 상태로 필터링하여 고시정보 카테고리를 조회한다")
        void execute_WithActiveFilter_ReturnsFilteredResults() {
            // given
            NoticeCategorySearchParams params = NoticeQueryFixtures.searchParams(true);
            NoticeCategorySearchCriteria criteria =
                    new NoticeCategorySearchCriteria(
                            true,
                            null,
                            null,
                            QueryContext.defaultOf(NoticeCategorySortKey.defaultKey()));

            NoticeCategory category = NoticeFixtures.activeNoticeCategory(1L);
            List<NoticeCategory> categories = List.of(category);
            long totalElements = 1L;

            NoticeCategoryResult categoryResult =
                    NoticeQueryFixtures.noticeCategoryResult(1L, true);
            List<NoticeCategoryResult> activeResults = List.of(categoryResult);
            NoticeCategoryPageResult expectedResult =
                    NoticeQueryFixtures.noticeCategoryPageResult(0, 20, 1L);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(categories);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toResult(category)).willReturn(categoryResult);
            given(
                            assembler.toPageResult(
                                    activeResults, criteria.page(), criteria.size(), totalElements))
                    .willReturn(expectedResult);

            // when
            NoticeCategoryPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("검색어로 고시정보 카테고리를 필터링 조회한다")
        void execute_WithSearchWord_ReturnsFilteredResults() {
            // given
            NoticeCategorySearchParams params =
                    NoticeQueryFixtures.searchParams("code", "CLOTHING");
            NoticeCategorySearchCriteria criteria =
                    new NoticeCategorySearchCriteria(
                            null,
                            "code",
                            "CLOTHING",
                            QueryContext.defaultOf(NoticeCategorySortKey.defaultKey()));

            NoticeCategory category = NoticeFixtures.activeNoticeCategory(1L);
            List<NoticeCategory> categories = List.of(category);
            long totalElements = 1L;

            NoticeCategoryResult categoryResult =
                    NoticeQueryFixtures.noticeCategoryResult(1L, "CLOTHING");
            List<NoticeCategoryResult> searchResults = List.of(categoryResult);
            NoticeCategoryPageResult expectedResult =
                    NoticeQueryFixtures.noticeCategoryPageResult(0, 20, 1L);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(categories);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toResult(category)).willReturn(categoryResult);
            given(
                            assembler.toPageResult(
                                    searchResults, criteria.page(), criteria.size(), totalElements))
                    .willReturn(expectedResult);

            // when
            NoticeCategoryPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("페이징 파라미터로 고시정보 카테고리를 조회한다")
        void execute_WithPagingParams_ReturnsPagedResults() {
            // given
            int page = 1;
            int size = 10;
            NoticeCategorySearchParams params = NoticeQueryFixtures.searchParams(page, size);
            NoticeCategorySearchCriteria criteria = criteriaWithPage(page, size);

            NoticeCategory category = NoticeFixtures.activeNoticeCategory(1L);
            List<NoticeCategory> categories = List.of(category);
            long totalElements = 15L;

            NoticeCategoryResult categoryResult = NoticeQueryFixtures.noticeCategoryResult(1L);
            List<NoticeCategoryResult> results = List.of(categoryResult);
            NoticeCategoryPageResult expectedResult =
                    NoticeQueryFixtures.noticeCategoryPageResult(page, size, totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(categories);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toResult(category)).willReturn(categoryResult);
            given(assembler.toPageResult(results, criteria.page(), criteria.size(), totalElements))
                    .willReturn(expectedResult);

            // when
            NoticeCategoryPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expectedResult);
        }
    }
}
