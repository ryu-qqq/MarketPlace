package com.ryuqq.marketplace.domain.outboundproduct.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OmsProductSearchCriteria 단위 테스트")
class OmsProductSearchCriteriaTest {

    @Nested
    @DisplayName("defaultCriteria() 팩토리 테스트")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("defaultCriteria()는 모든 필터가 비어있는 기본 Criteria를 반환한다")
        void defaultCriteriaHasNoFilters() {
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.syncStatuses()).isEmpty();
            assertThat(criteria.shopIds()).isEmpty();
            assertThat(criteria.partnerIds()).isEmpty();
            assertThat(criteria.productCodes()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.dateRange()).isNull();
        }

        @Test
        @DisplayName("defaultCriteria()의 queryContext는 기본 정렬 키를 사용한다")
        void defaultCriteriaUsesDefaultSortKey() {
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            assertThat(criteria.queryContext().sortKey()).isEqualTo(OmsProductSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("필터 조건 확인 메서드 테스트")
    class FilterCheckTest {

        @Test
        @DisplayName("상태 필터가 있으면 hasStatusFilter()가 true이다")
        void hasStatusFilterWhenStatusesNotEmpty() {
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(ProductGroupStatus.ACTIVE),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(OmsProductSortKey.defaultKey()));

            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 없으면 hasStatusFilter()가 false이다")
        void hasStatusFilterFalseWhenEmpty() {
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            assertThat(criteria.hasStatusFilter()).isFalse();
        }

        @Test
        @DisplayName("syncStatus 필터가 있으면 hasSyncStatusFilter()가 true이다")
        void hasSyncStatusFilterWhenSyncStatusesNotEmpty() {
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(SyncStatus.PENDING),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(OmsProductSortKey.defaultKey()));

            assertThat(criteria.hasSyncStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("shopId 필터가 있으면 hasShopFilter()가 true이다")
        void hasShopFilterWhenShopIdsNotEmpty() {
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(1L),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(OmsProductSortKey.defaultKey()));

            assertThat(criteria.hasShopFilter()).isTrue();
        }

        @Test
        @DisplayName("partnerIds 필터가 있으면 hasPartnerFilter()가 true이다")
        void hasPartnerFilterWhenPartnerIdsNotEmpty() {
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(100L),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(OmsProductSortKey.defaultKey()));

            assertThat(criteria.hasPartnerFilter()).isTrue();
        }

        @Test
        @DisplayName("productCodes 필터가 있으면 hasProductCodeFilter()가 true이다")
        void hasProductCodeFilterWhenProductCodesNotEmpty() {
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of("PG-001"),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(OmsProductSortKey.defaultKey()));

            assertThat(criteria.hasProductCodeFilter()).isTrue();
        }
    }

    @Nested
    @DisplayName("검색 조건 확인 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("searchWord가 있으면 hasSearchCondition()이 true이다")
        void hasSearchConditionWhenSearchWordPresent() {
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            OmsProductSearchField.PRODUCT_NAME,
                            "나이키",
                            null,
                            null,
                            QueryContext.defaultOf(OmsProductSortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("searchWord가 null이면 hasSearchCondition()이 false이다")
        void hasSearchConditionFalseWhenNullSearchWord() {
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("searchWord가 공백이면 hasSearchCondition()이 false이다")
        void hasSearchConditionFalseWhenBlankSearchWord() {
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            "   ",
                            null,
                            null,
                            QueryContext.defaultOf(OmsProductSortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("searchField가 있으면 hasSearchField()가 true이다")
        void hasSearchFieldWhenFieldPresent() {
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            OmsProductSearchField.PRODUCT_NAME,
                            "검색어",
                            null,
                            null,
                            QueryContext.defaultOf(OmsProductSortKey.defaultKey()));

            assertThat(criteria.hasSearchField()).isTrue();
        }
    }

    @Nested
    @DisplayName("null 리스트 입력 시 빈 리스트로 정규화 테스트")
    class NullNormalizationTest {

        @Test
        @DisplayName("null 리스트 입력 시 빈 리스트로 정규화된다")
        void nullListsAreNormalizedToEmptyLists() {
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(OmsProductSortKey.defaultKey()));

            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.syncStatuses()).isEmpty();
            assertThat(criteria.shopIds()).isEmpty();
            assertThat(criteria.partnerIds()).isEmpty();
            assertThat(criteria.productCodes()).isEmpty();
        }
    }

    @Nested
    @DisplayName("페이징 정보 위임 테스트")
    class PagingDelegationTest {

        @Test
        @DisplayName("size()는 queryContext.size()를 반환한다")
        void sizeIsDelegatedToQueryContext() {
            OmsProductSearchCriteria criteria =
                    new OmsProductSearchCriteria(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.firstPage(
                                    OmsProductSortKey.CREATED_AT, SortDirection.DESC, 30));

            assertThat(criteria.size()).isEqualTo(30);
        }

        @Test
        @DisplayName("offset()은 queryContext.offset()을 반환한다")
        void offsetIsDelegatedToQueryContext() {
            OmsProductSearchCriteria criteria = OmsProductSearchCriteria.defaultCriteria();

            assertThat(criteria.offset()).isEqualTo(0L);
        }
    }
}
