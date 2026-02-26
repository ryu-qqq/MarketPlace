package com.ryuqq.marketplace.domain.brandmapping.query;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.brandmapping.vo.BrandMappingStatus;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandMappingSearchCriteria 테스트")
class BrandMappingSearchCriteriaTest {

    @Nested
    @DisplayName("of() - 정적 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 파라미터로 검색 조건을 생성한다")
        void createWithAllParameters() {
            QueryContext<BrandMappingSortKey> queryContext =
                    QueryContext.defaultOf(BrandMappingSortKey.defaultKey());

            BrandMappingSearchCriteria criteria =
                    BrandMappingSearchCriteria.of(
                            List.of(100L),
                            List.of(10L),
                            List.of(1L),
                            List.of(BrandMappingStatus.ACTIVE),
                            BrandMappingSearchField.EXTERNAL_BRAND_NAME,
                            "테스트",
                            queryContext);

            assertThat(criteria.salesChannelBrandIds()).containsExactly(100L);
            assertThat(criteria.internalBrandIds()).containsExactly(10L);
            assertThat(criteria.salesChannelIds()).containsExactly(1L);
            assertThat(criteria.statuses()).containsExactly(BrandMappingStatus.ACTIVE);
            assertThat(criteria.searchField())
                    .isEqualTo(BrandMappingSearchField.EXTERNAL_BRAND_NAME);
            assertThat(criteria.searchWord()).isEqualTo("테스트");
        }
    }

    @Nested
    @DisplayName("defaultCriteria() - 기본 검색 조건")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건을 생성한다")
        void createDefaultCriteria() {
            BrandMappingSearchCriteria criteria = BrandMappingSearchCriteria.defaultCriteria();

            assertThat(criteria.salesChannelBrandIds()).isEmpty();
            assertThat(criteria.internalBrandIds()).isEmpty();
            assertThat(criteria.salesChannelIds()).isEmpty();
            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.queryContext()).isNotNull();
        }
    }

    @Nested
    @DisplayName("필터 존재 여부 테스트")
    class FilterCheckTest {

        @Test
        @DisplayName("각 필터의 존재 여부를 확인한다")
        void checkFilters() {
            BrandMappingSearchCriteria withFilters =
                    BrandMappingSearchCriteria.of(
                            List.of(100L),
                            List.of(10L),
                            List.of(1L),
                            List.of(BrandMappingStatus.ACTIVE),
                            BrandMappingSearchField.EXTERNAL_BRAND_NAME,
                            "테스트",
                            QueryContext.defaultOf(BrandMappingSortKey.defaultKey()));

            assertThat(withFilters.hasSalesChannelBrandFilter()).isTrue();
            assertThat(withFilters.hasInternalBrandFilter()).isTrue();
            assertThat(withFilters.hasSalesChannelFilter()).isTrue();
            assertThat(withFilters.hasStatusFilter()).isTrue();
            assertThat(withFilters.hasSearchCondition()).isTrue();
            assertThat(withFilters.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("기본 검색 조건은 모든 필터가 false를 반환한다")
        void defaultCriteriaHasNoFilters() {
            BrandMappingSearchCriteria defaultCriteria =
                    BrandMappingSearchCriteria.defaultCriteria();

            assertThat(defaultCriteria.hasSalesChannelBrandFilter()).isFalse();
            assertThat(defaultCriteria.hasInternalBrandFilter()).isFalse();
            assertThat(defaultCriteria.hasSalesChannelFilter()).isFalse();
            assertThat(defaultCriteria.hasStatusFilter()).isFalse();
            assertThat(defaultCriteria.hasSearchCondition()).isFalse();
            assertThat(defaultCriteria.hasSearchField()).isFalse();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodsTest {

        @Test
        @DisplayName("size()는 QueryContext의 size를 반환한다")
        void sizeReturnsQueryContextSize() {
            QueryContext<BrandMappingSortKey> queryContext =
                    QueryContext.of(
                            BrandMappingSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));
            BrandMappingSearchCriteria criteria =
                    BrandMappingSearchCriteria.of(null, null, null, null, null, null, queryContext);

            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            QueryContext<BrandMappingSortKey> queryContext =
                    QueryContext.of(
                            BrandMappingSortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 10));
            BrandMappingSearchCriteria criteria =
                    BrandMappingSearchCriteria.of(null, null, null, null, null, null, queryContext);

            assertThat(criteria.offset()).isEqualTo(20L);
        }
    }

    @Nested
    @DisplayName("null 리스트 처리 테스트")
    class NullListHandlingTest {

        @Test
        @DisplayName("null 리스트는 빈 리스트로 변환된다")
        void nullListsConvertedToEmpty() {
            BrandMappingSearchCriteria criteria =
                    BrandMappingSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(BrandMappingSortKey.defaultKey()));

            assertThat(criteria.salesChannelBrandIds()).isEmpty();
            assertThat(criteria.internalBrandIds()).isEmpty();
            assertThat(criteria.salesChannelIds()).isEmpty();
            assertThat(criteria.statuses()).isEmpty();
        }

        @Test
        @DisplayName("queryContext가 null이면 예외가 발생한다")
        void nullQueryContextThrowsException() {
            assertThatThrownBy(
                            () ->
                                    BrandMappingSearchCriteria.of(
                                            null, null, null, null, null, null, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
