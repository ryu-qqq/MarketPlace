package com.ryuqq.marketplace.domain.brand.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandSearchCriteria 단위 테스트")
class BrandSearchCriteriaTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {
        @Test
        @DisplayName("모든 필드로 생성한다")
        void createWithAllFields() {
            // given
            List<BrandStatus> statuses = List.of(BrandStatus.ACTIVE);
            BrandSearchField searchField = BrandSearchField.NAME_KO;
            String searchWord = "나이키";
            QueryContext<BrandSortKey> queryContext =
                    QueryContext.defaultOf(BrandSortKey.defaultKey());

            // when
            BrandSearchCriteria criteria =
                    BrandSearchCriteria.of(statuses, searchField, searchWord, queryContext);

            // then
            assertThat(criteria).isNotNull();
            assertThat(criteria.statuses()).containsExactly(BrandStatus.ACTIVE);
            assertThat(criteria.searchField()).isEqualTo(searchField);
            assertThat(criteria.searchWord()).isEqualTo(searchWord);
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("defaultCriteria()로 기본 검색 조건을 생성한다")
        void createDefaultCriteria() {
            // when
            BrandSearchCriteria criteria = BrandSearchCriteria.defaultCriteria();

            // then
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.queryContext()).isNotNull();
        }

        @Test
        @DisplayName("activeOnly()로 활성 브랜드만 조회하는 조건을 생성한다")
        void createActiveOnlyCriteria() {
            // when
            BrandSearchCriteria criteria = BrandSearchCriteria.activeOnly();

            // then
            assertThat(criteria.statuses()).containsExactly(BrandStatus.ACTIVE);
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
        }

        @Test
        @DisplayName("statuses가 null이면 빈 리스트로 초기화된다")
        void createWithNullStatuses_InitializesEmptyList() {
            // when
            BrandSearchCriteria criteria =
                    BrandSearchCriteria.of(
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(BrandSortKey.defaultKey()));

            // then
            assertThat(criteria.statuses()).isEmpty();
        }

        @Test
        @DisplayName("statuses는 불변 리스트로 복사된다")
        void statusesAreCopiedAsUnmodifiableList() {
            // given
            List<BrandStatus> statuses = List.of(BrandStatus.ACTIVE, BrandStatus.INACTIVE);

            // when
            BrandSearchCriteria criteria =
                    BrandSearchCriteria.of(
                            statuses, null, null, QueryContext.defaultOf(BrandSortKey.defaultKey()));

            // then
            assertThat(criteria.statuses()).isUnmodifiable();
            assertThat(criteria.statuses()).containsExactly(BrandStatus.ACTIVE, BrandStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("hasStatusFilter() 메서드 테스트")
    class HasStatusFilterTest {
        @Test
        @DisplayName("상태 필터가 있으면 true를 반환한다")
        void returnsTrueWhenStatusFilterExists() {
            // given
            BrandSearchCriteria criteria = BrandSearchCriteria.activeOnly();

            // when
            boolean hasFilter = criteria.hasStatusFilter();

            // then
            assertThat(hasFilter).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 없으면 false를 반환한다")
        void returnsFalseWhenNoStatusFilter() {
            // given
            BrandSearchCriteria criteria = BrandSearchCriteria.defaultCriteria();

            // when
            boolean hasFilter = criteria.hasStatusFilter();

            // then
            assertThat(hasFilter).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() 메서드 테스트")
    class HasSearchConditionTest {
        @Test
        @DisplayName("검색어가 있으면 true를 반환한다")
        void returnsTrueWhenSearchWordExists() {
            // given
            BrandSearchCriteria criteria =
                    BrandSearchCriteria.of(
                            List.of(),
                            BrandSearchField.NAME_KO,
                            "나이키",
                            QueryContext.defaultOf(BrandSortKey.defaultKey()));

            // when
            boolean hasCondition = criteria.hasSearchCondition();

            // then
            assertThat(hasCondition).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsNull() {
            // given
            BrandSearchCriteria criteria = BrandSearchCriteria.defaultCriteria();

            // when
            boolean hasCondition = criteria.hasSearchCondition();

            // then
            assertThat(hasCondition).isFalse();
        }

        @Test
        @DisplayName("검색어가 빈 문자열이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsBlank() {
            // given
            BrandSearchCriteria criteria =
                    BrandSearchCriteria.of(
                            List.of(),
                            BrandSearchField.NAME_KO,
                            "   ",
                            QueryContext.defaultOf(BrandSortKey.defaultKey()));

            // when
            boolean hasCondition = criteria.hasSearchCondition();

            // then
            assertThat(hasCondition).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchField() 메서드 테스트")
    class HasSearchFieldTest {
        @Test
        @DisplayName("검색 필드가 있으면 true를 반환한다")
        void returnsTrueWhenSearchFieldExists() {
            // given
            BrandSearchCriteria criteria =
                    BrandSearchCriteria.of(
                            List.of(),
                            BrandSearchField.CODE,
                            "NIKE",
                            QueryContext.defaultOf(BrandSortKey.defaultKey()));

            // when
            boolean hasField = criteria.hasSearchField();

            // then
            assertThat(hasField).isTrue();
        }

        @Test
        @DisplayName("검색 필드가 null이면 false를 반환한다")
        void returnsFalseWhenSearchFieldIsNull() {
            // given
            BrandSearchCriteria criteria = BrandSearchCriteria.defaultCriteria();

            // when
            boolean hasField = criteria.hasSearchField();

            // then
            assertThat(hasField).isFalse();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodTest {
        @Test
        @DisplayName("size()는 페이지 크기를 반환한다")
        void sizeReturnsPageSize() {
            // given
            BrandSearchCriteria criteria = BrandSearchCriteria.defaultCriteria();

            // when
            int size = criteria.size();

            // then
            assertThat(size).isEqualTo(criteria.queryContext().size());
        }

        @Test
        @DisplayName("offset()은 오프셋을 반환한다")
        void offsetReturnsOffset() {
            // given
            BrandSearchCriteria criteria = BrandSearchCriteria.defaultCriteria();

            // when
            long offset = criteria.offset();

            // then
            assertThat(offset).isEqualTo(criteria.queryContext().offset());
        }

        @Test
        @DisplayName("page()는 페이지 번호를 반환한다")
        void pageReturnsPageNumber() {
            // given
            BrandSearchCriteria criteria = BrandSearchCriteria.defaultCriteria();

            // when
            int page = criteria.page();

            // then
            assertThat(page).isEqualTo(criteria.queryContext().page());
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {
        @Test
        @DisplayName("record로 구현되어 불변성이 보장된다")
        void recordGuaranteesImmutability() {
            // given
            BrandSearchCriteria criteria = BrandSearchCriteria.activeOnly();

            // when
            List<BrandStatus> statuses = criteria.statuses();

            // then
            assertThat(statuses).isUnmodifiable();
        }
    }
}
