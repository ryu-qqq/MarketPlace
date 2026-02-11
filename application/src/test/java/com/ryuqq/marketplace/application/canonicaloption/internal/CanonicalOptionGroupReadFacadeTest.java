package com.ryuqq.marketplace.application.canonicaloption.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.canonicaloption.assembler.CanonicalOptionGroupAssembler;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.manager.CanonicalOptionGroupReadManager;
import com.ryuqq.marketplace.application.canonicaloption.manager.CanonicalOptionValueReadManager;
import com.ryuqq.marketplace.domain.canonicaloption.CanonicalOptionFixtures;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
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
@DisplayName("CanonicalOptionGroupReadFacade 단위 테스트")
class CanonicalOptionGroupReadFacadeTest {

    @InjectMocks private CanonicalOptionGroupReadFacade sut;

    @Mock private CanonicalOptionGroupReadManager groupReadManager;

    @Mock private CanonicalOptionValueReadManager valueReadManager;

    @Mock private CanonicalOptionGroupAssembler assembler;

    @Nested
    @DisplayName("getById() - ID로 그룹 + 값 조회")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 그룹과 값을 함께 조회하여 Result를 반환한다")
        void getById_ReturnsGroupWithValues() {
            // given
            Long groupId = 1L;
            CanonicalOptionGroup group = CanonicalOptionFixtures.activeCanonicalOptionGroup();
            List<CanonicalOptionValue> values = CanonicalOptionFixtures.canonicalOptionValues();
            CanonicalOptionGroupResult expected =
                    new CanonicalOptionGroupResult(
                            1L, "COLOR", "색상", "Color", true, List.of(), java.time.Instant.now());

            given(groupReadManager.getById(CanonicalOptionGroupId.of(groupId))).willReturn(group);
            given(valueReadManager.getByCanonicalOptionGroupId(groupId)).willReturn(values);
            given(assembler.toResult(group, values)).willReturn(expected);

            // when
            CanonicalOptionGroupResult result = sut.getById(groupId);

            // then
            assertThat(result).isEqualTo(expected);
            then(groupReadManager).should().getById(CanonicalOptionGroupId.of(groupId));
            then(valueReadManager).should().getByCanonicalOptionGroupId(groupId);
            then(assembler).should().toResult(group, values);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 그룹 + 값 목록 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 그룹 목록과 값들을 조회하여 Result 목록을 반환한다")
        void findByCriteria_ReturnsGroupsWithValues() {
            // given
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    new QueryContext<>(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20),
                            false);
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);

            CanonicalOptionGroup group1 = CanonicalOptionFixtures.activeCanonicalOptionGroup(1L);
            CanonicalOptionGroup group2 = CanonicalOptionFixtures.activeCanonicalOptionGroup(2L);
            List<CanonicalOptionGroup> groups = List.of(group1, group2);

            Map<Long, List<CanonicalOptionValue>> valuesMap =
                    Map.of(
                            1L, List.of(CanonicalOptionFixtures.canonicalOptionValue(1L)),
                            2L, List.of(CanonicalOptionFixtures.canonicalOptionValue(2L)));

            CanonicalOptionGroupResult result1 =
                    new CanonicalOptionGroupResult(
                            1L, "COLOR", "색상", "Color", true, List.of(), java.time.Instant.now());
            CanonicalOptionGroupResult result2 =
                    new CanonicalOptionGroupResult(
                            2L, "COLOR", "색상", "Color", true, List.of(), java.time.Instant.now());

            given(groupReadManager.findByCriteria(criteria)).willReturn(groups);
            given(valueReadManager.getGroupedByCanonicalOptionGroupIds(List.of(1L, 2L)))
                    .willReturn(valuesMap);
            given(assembler.toResult(group1, valuesMap.get(1L))).willReturn(result1);
            given(assembler.toResult(group2, valuesMap.get(2L))).willReturn(result2);

            // when
            List<CanonicalOptionGroupResult> results = sut.findByCriteria(criteria);

            // then
            assertThat(results).hasSize(2);
            then(groupReadManager).should().findByCriteria(criteria);
            then(valueReadManager).should().getGroupedByCanonicalOptionGroupIds(List.of(1L, 2L));
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void findByCriteria_EmptyGroups_ReturnsEmptyList() {
            // given
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    new QueryContext<>(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20),
                            false);
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);

            given(groupReadManager.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<CanonicalOptionGroupResult> results = sut.findByCriteria(criteria);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("값이 없는 그룹은 빈 값 목록으로 처리된다")
        void findByCriteria_GroupsWithoutValues_ReturnsResultsWithEmptyValues() {
            // given
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

            Map<Long, List<CanonicalOptionValue>> valuesMap = Map.of();

            CanonicalOptionGroupResult result =
                    new CanonicalOptionGroupResult(
                            1L, "COLOR", "색상", "Color", true, List.of(), java.time.Instant.now());

            given(groupReadManager.findByCriteria(criteria)).willReturn(groups);
            given(valueReadManager.getGroupedByCanonicalOptionGroupIds(List.of(1L)))
                    .willReturn(valuesMap);
            given(assembler.toResult(group, List.of())).willReturn(result);

            // when
            List<CanonicalOptionGroupResult> results = sut.findByCriteria(criteria);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).values()).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 그룹 수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    new QueryContext<>(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20),
                            false);
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);
            long expected = 10L;

            given(groupReadManager.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(groupReadManager).should().countByCriteria(criteria);
        }
    }
}
