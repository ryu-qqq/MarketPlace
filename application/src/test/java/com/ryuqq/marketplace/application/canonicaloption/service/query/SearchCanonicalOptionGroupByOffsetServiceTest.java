package com.ryuqq.marketplace.application.canonicaloption.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.canonicaloption.CanonicalOptionQueryFixtures;
import com.ryuqq.marketplace.application.canonicaloption.assembler.CanonicalOptionGroupAssembler;
import com.ryuqq.marketplace.application.canonicaloption.dto.query.CanonicalOptionGroupSearchParams;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.factory.CanonicalOptionGroupQueryFactory;
import com.ryuqq.marketplace.application.canonicaloption.internal.CanonicalOptionGroupReadFacade;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
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
@DisplayName("SearchCanonicalOptionGroupByOffsetService 단위 테스트")
class SearchCanonicalOptionGroupByOffsetServiceTest {

    @InjectMocks private SearchCanonicalOptionGroupByOffsetService sut;

    @Mock private CanonicalOptionGroupReadFacade readFacade;

    @Mock private CanonicalOptionGroupQueryFactory queryFactory;

    @Mock private CanonicalOptionGroupAssembler assembler;

    @Nested
    @DisplayName("execute() - 캐노니컬 옵션 그룹 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 캐노니컬 옵션 그룹 목록을 조회한다")
        void searchCanonicalOptionGroups_Success() {
            // given
            CanonicalOptionGroupSearchParams params = CanonicalOptionQueryFixtures.searchParams();
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    new QueryContext<>(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20),
                            false);
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);
            List<CanonicalOptionGroupResult> results =
                    List.of(CanonicalOptionQueryFixtures.canonicalOptionGroupResult(1L));
            long totalElements = 1L;
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionGroupPageResult.of(results, 0, 20, totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.findByCriteria(criteria)).willReturn(results);
            given(readFacade.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(results, 0, 20, totalElements)).willReturn(pageResult);

            // when
            CanonicalOptionGroupPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
            then(queryFactory).should().createCriteria(params);
            then(readFacade).should().findByCriteria(criteria);
            then(readFacade).should().countByCriteria(criteria);
            then(assembler).should().toPageResult(results, 0, 20, totalElements);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 페이지를 반환한다")
        void searchCanonicalOptionGroups_Empty_ReturnsEmptyPage() {
            // given
            CanonicalOptionGroupSearchParams params =
                    CanonicalOptionQueryFixtures.searchParams("code", "NONEXISTENT");
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    new QueryContext<>(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20),
                            false);
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null, "code", "NONEXISTENT", queryContext);
            List<CanonicalOptionGroupResult> results = List.of();
            long totalElements = 0L;
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionGroupPageResult.of(results, 0, 20, totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.findByCriteria(criteria)).willReturn(results);
            given(readFacade.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(results, 0, 20, totalElements)).willReturn(pageResult);

            // when
            CanonicalOptionGroupPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("활성화 필터로 캐노니컬 옵션 그룹을 조회한다")
        void searchCanonicalOptionGroups_WithActiveFilter() {
            // given
            CanonicalOptionGroupSearchParams params =
                    CanonicalOptionQueryFixtures.searchParams(true);
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    new QueryContext<>(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20),
                            false);
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(true, null, null, queryContext);
            List<CanonicalOptionGroupResult> results =
                    List.of(CanonicalOptionQueryFixtures.canonicalOptionGroupResult(1L));
            long totalElements = 1L;
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionGroupPageResult.of(results, 0, 20, totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.findByCriteria(criteria)).willReturn(results);
            given(readFacade.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(results, 0, 20, totalElements)).willReturn(pageResult);

            // when
            CanonicalOptionGroupPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
        }

        @Test
        @DisplayName("여러 페이지가 있는 경우 올바른 페이지를 반환한다")
        void searchCanonicalOptionGroups_MultiplePages_ReturnsCorrectPage() {
            // given
            CanonicalOptionGroupSearchParams params =
                    CanonicalOptionQueryFixtures.searchParams(1, 10);
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    new QueryContext<>(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(1, 10),
                            false);
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);
            List<CanonicalOptionGroupResult> results =
                    List.of(
                            CanonicalOptionQueryFixtures.canonicalOptionGroupResult(11L),
                            CanonicalOptionQueryFixtures.canonicalOptionGroupResult(12L));
            long totalElements = 25L;
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionGroupPageResult.of(results, 1, 10, totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readFacade.findByCriteria(criteria)).willReturn(results);
            given(readFacade.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(results, 1, 10, totalElements)).willReturn(pageResult);

            // when
            CanonicalOptionGroupPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }
    }
}
