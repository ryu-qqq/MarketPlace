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
import com.ryuqq.marketplace.application.canonicaloption.manager.CanonicalOptionGroupReadManager;
import com.ryuqq.marketplace.domain.canonicaloption.CanonicalOptionFixtures;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
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

    @Mock private CanonicalOptionGroupReadManager readManager;
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

            CanonicalOptionGroup group = CanonicalOptionFixtures.activeCanonicalOptionGroup(1L);
            List<CanonicalOptionGroup> groups = List.of(group);
            long totalElements = 1L;

            CanonicalOptionGroupResult groupResult =
                    CanonicalOptionQueryFixtures.canonicalOptionGroupResult(1L);
            List<CanonicalOptionGroupResult> results = List.of(groupResult);
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionGroupPageResult.of(results, 0, 20, totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(groups);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toResult(group)).willReturn(groupResult);
            given(assembler.toPageResult(results, 0, 20, totalElements)).willReturn(pageResult);

            // when
            CanonicalOptionGroupPageResult result = sut.execute(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
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

            List<CanonicalOptionGroup> groups = List.of();
            long totalElements = 0L;

            List<CanonicalOptionGroupResult> results = List.of();
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionGroupPageResult.of(results, 0, 20, totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(groups);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
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

            CanonicalOptionGroup group = CanonicalOptionFixtures.activeCanonicalOptionGroup(1L);
            List<CanonicalOptionGroup> groups = List.of(group);
            long totalElements = 1L;

            CanonicalOptionGroupResult groupResult =
                    CanonicalOptionQueryFixtures.canonicalOptionGroupResult(1L);
            List<CanonicalOptionGroupResult> results = List.of(groupResult);
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionGroupPageResult.of(results, 0, 20, totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(groups);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toResult(group)).willReturn(groupResult);
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

            CanonicalOptionGroup group1 = CanonicalOptionFixtures.activeCanonicalOptionGroup(11L);
            CanonicalOptionGroup group2 = CanonicalOptionFixtures.activeCanonicalOptionGroup(12L);
            List<CanonicalOptionGroup> groups = List.of(group1, group2);
            long totalElements = 25L;

            CanonicalOptionGroupResult result1 =
                    CanonicalOptionQueryFixtures.canonicalOptionGroupResult(11L);
            CanonicalOptionGroupResult result2 =
                    CanonicalOptionQueryFixtures.canonicalOptionGroupResult(12L);
            List<CanonicalOptionGroupResult> results = List.of(result1, result2);
            CanonicalOptionGroupPageResult pageResult =
                    CanonicalOptionGroupPageResult.of(results, 1, 10, totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(groups);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toResult(group1)).willReturn(result1);
            given(assembler.toResult(group2)).willReturn(result2);
            given(assembler.toPageResult(results, 1, 10, totalElements)).willReturn(pageResult);

            // when
            CanonicalOptionGroupPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }
    }
}
