package com.ryuqq.marketplace.domain.outboundproduct.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SyncHistorySearchCriteria 단위 테스트")
class SyncHistorySearchCriteriaTest {

    private static SyncHistorySearchCriteria defaultCriteria(long productGroupId) {
        return new SyncHistorySearchCriteria(
                productGroupId,
                null,
                null,
                QueryContext.defaultOf(SyncHistorySortKey.defaultKey()));
    }

    private static SyncHistorySearchCriteria criteriaWithShopAndStatus(
            long productGroupId, Long shopId, SyncStatus status) {
        return new SyncHistorySearchCriteria(
                productGroupId,
                shopId,
                status,
                QueryContext.defaultOf(SyncHistorySortKey.defaultKey()));
    }

    @Nested
    @DisplayName("필터 조건 확인 메서드 테스트")
    class FilterCheckTest {

        @Test
        @DisplayName("shopId가 있으면 hasShopIdFilter()가 true이다")
        void hasShopIdFilterWhenShopIdPresent() {
            SyncHistorySearchCriteria criteria = criteriaWithShopAndStatus(100L, 1L, null);

            assertThat(criteria.hasShopIdFilter()).isTrue();
        }

        @Test
        @DisplayName("shopId가 null이면 hasShopIdFilter()가 false이다")
        void hasShopIdFilterFalseWhenNull() {
            SyncHistorySearchCriteria criteria = defaultCriteria(100L);

            assertThat(criteria.hasShopIdFilter()).isFalse();
        }

        @Test
        @DisplayName("statusFilter가 있으면 hasStatusFilter()가 true이다")
        void hasStatusFilterWhenStatusPresent() {
            SyncHistorySearchCriteria criteria =
                    criteriaWithShopAndStatus(100L, null, SyncStatus.PENDING);

            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("statusFilter가 null이면 hasStatusFilter()가 false이다")
        void hasStatusFilterFalseWhenNull() {
            SyncHistorySearchCriteria criteria = defaultCriteria(100L);

            assertThat(criteria.hasStatusFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("페이징 정보 위임 테스트")
    class PagingDelegationTest {

        @Test
        @DisplayName("productGroupId가 올바르게 저장된다")
        void productGroupIdIsStoredCorrectly() {
            SyncHistorySearchCriteria criteria = defaultCriteria(999L);

            assertThat(criteria.productGroupId()).isEqualTo(999L);
        }

        @Test
        @DisplayName("size()는 queryContext.size()를 반환한다")
        void sizeIsDelegated() {
            SyncHistorySearchCriteria criteria = defaultCriteria(100L);

            assertThat(criteria.size())
                    .isEqualTo(QueryContext.defaultOf(SyncHistorySortKey.CREATED_AT).size());
        }

        @Test
        @DisplayName("offset()은 첫 페이지에서 0이다")
        void offsetIsZeroForFirstPage() {
            SyncHistorySearchCriteria criteria = defaultCriteria(100L);

            assertThat(criteria.offset()).isEqualTo(0L);
        }

        @Test
        @DisplayName("page()는 첫 페이지에서 0이다")
        void pageIsZeroForFirstPage() {
            SyncHistorySearchCriteria criteria = defaultCriteria(100L);

            assertThat(criteria.page()).isEqualTo(0);
        }
    }
}
