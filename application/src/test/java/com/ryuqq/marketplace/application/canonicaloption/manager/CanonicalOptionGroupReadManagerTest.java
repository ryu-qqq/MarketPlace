package com.ryuqq.marketplace.application.canonicaloption.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.canonicaloption.port.out.query.CanonicalOptionGroupQueryPort;
import com.ryuqq.marketplace.domain.canonicaloption.CanonicalOptionFixtures;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.exception.CanonicalOptionGroupNotFoundException;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSortKey;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import java.util.Optional;
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
@DisplayName("CanonicalOptionGroupReadManager 단위 테스트")
class CanonicalOptionGroupReadManagerTest {

    @InjectMocks private CanonicalOptionGroupReadManager sut;

    @Mock private CanonicalOptionGroupQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 캐노니컬 옵션 그룹을 조회한다")
        void getById_Exists_ReturnsCanonicalOptionGroup() {
            // given
            CanonicalOptionGroupId id = CanonicalOptionGroupId.of(1L);
            CanonicalOptionGroup expected = CanonicalOptionFixtures.activeCanonicalOptionGroup();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            CanonicalOptionGroup result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void getById_NotExists_ThrowsException() {
            // given
            CanonicalOptionGroupId id = CanonicalOptionGroupId.of(999L);

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(CanonicalOptionGroupNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 캐노니컬 옵션 그룹 목록을 조회한다")
        void findByCriteria_ReturnsCanonicalOptionGroups() {
            // given
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    new QueryContext<>(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20),
                            false);
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);
            List<CanonicalOptionGroup> expected =
                    List.of(CanonicalOptionFixtures.activeCanonicalOptionGroup());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<CanonicalOptionGroup> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void findByCriteria_Empty_ReturnsEmptyList() {
            // given
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    new QueryContext<>(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20),
                            false);
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);

            given(queryPort.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<CanonicalOptionGroup> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 캐노니컬 옵션 그룹 수를 반환한다")
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

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환한다")
        void countByCriteria_Empty_ReturnsZero() {
            // given
            QueryContext<CanonicalOptionGroupSortKey> queryContext =
                    new QueryContext<>(
                            CanonicalOptionGroupSortKey.CREATED_AT,
                            SortDirection.ASC,
                            PageRequest.of(0, 20),
                            false);
            CanonicalOptionGroupSearchCriteria criteria =
                    new CanonicalOptionGroupSearchCriteria(null, null, null, queryContext);

            given(queryPort.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }
}
