package com.ryuqq.marketplace.domain.selleradmin.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAdminSearchCriteria 단위 테스트")
class SellerAdminSearchCriteriaTest {

    @Nested
    @DisplayName("defaultCriteria() - 기본 검색 조건 생성")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건은 필터 없는 상태로 생성된다")
        void createDefaultCriteria() {
            SellerAdminSearchCriteria criteria = SellerAdminSearchCriteria.defaultCriteria();

            assertThat(criteria.sellerIds()).isNull();
            assertThat(criteria.status()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.dateRange()).isNull();
        }

        @Test
        @DisplayName("기본 검색 조건은 QueryContext를 가진다")
        void defaultCriteriaHasQueryContext() {
            SellerAdminSearchCriteria criteria = SellerAdminSearchCriteria.defaultCriteria();

            assertThat(criteria.queryContext()).isNotNull();
        }
    }

    @Nested
    @DisplayName("pendingOnly() - 승인 대기 조건 생성")
    class PendingOnlyTest {

        @Test
        @DisplayName("pendingOnly는 PENDING_APPROVAL 상태 필터를 가진다")
        void pendingOnlyCriteriaHasPendingStatus() {
            SellerAdminSearchCriteria criteria = SellerAdminSearchCriteria.pendingOnly();

            assertThat(criteria.status()).containsExactly(SellerAdminStatus.PENDING_APPROVAL);
            assertThat(criteria.hasStatusFilter()).isTrue();
        }
    }

    @Nested
    @DisplayName("of() - 직접 생성")
    class OfTest {

        @Test
        @DisplayName("상태 필터와 sellerId 필터를 포함한 검색 조건을 생성한다")
        void createWithStatusAndSellerFilter() {
            List<SellerAdminStatus> statuses =
                    List.of(SellerAdminStatus.ACTIVE, SellerAdminStatus.INACTIVE);
            List<Long> sellerIds = List.of(1L, 2L);
            QueryContext<SellerAdminSortKey> queryContext =
                    QueryContext.defaultOf(SellerAdminSortKey.defaultKey());

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            sellerIds, statuses, null, null, null, queryContext);

            assertThat(criteria.status())
                    .containsExactlyInAnyOrder(
                            SellerAdminStatus.ACTIVE, SellerAdminStatus.INACTIVE);
            assertThat(criteria.sellerIds()).containsExactlyInAnyOrder(1L, 2L);
        }

        @Test
        @DisplayName("null 상태 리스트는 빈 리스트로 처리된다")
        void nullStatusListBecomesEmpty() {
            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.defaultKey()));

            assertThat(criteria.status()).isEmpty();
            assertThat(criteria.sellerIds()).isNull();
        }

        @Test
        @DisplayName("검색어와 검색 필드를 포함한 조건을 생성한다")
        void createWithSearchWordAndField() {
            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            null,
                            SellerAdminSearchField.LOGIN_ID,
                            "admin@test.com",
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.defaultKey()));

            assertThat(criteria.searchField()).isEqualTo(SellerAdminSearchField.LOGIN_ID);
            assertThat(criteria.searchWord()).isEqualTo("admin@test.com");
        }
    }

    @Nested
    @DisplayName("hasSellerIds() 테스트")
    class HasSellerIdsTest {

        @Test
        @DisplayName("sellerId 필터가 있으면 true를 반환한다")
        void returnsTrueWhenSellerIdsExist() {
            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            List.of(1L, 2L),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.defaultKey()));

            assertThat(criteria.hasSellerIds()).isTrue();
        }

        @Test
        @DisplayName("sellerId 필터가 null이면 false를 반환한다")
        void returnsFalseWhenSellerIdsNull() {
            SellerAdminSearchCriteria criteria = SellerAdminSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSellerIds()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasStatusFilter() 테스트")
    class HasStatusFilterTest {

        @Test
        @DisplayName("상태 필터가 있으면 true를 반환한다")
        void returnsTrueWhenStatusFilterExists() {
            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            List.of(SellerAdminStatus.ACTIVE),
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.defaultKey()));

            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 없으면 false를 반환한다")
        void returnsFalseWhenStatusFilterIsEmpty() {
            SellerAdminSearchCriteria criteria = SellerAdminSearchCriteria.defaultCriteria();

            assertThat(criteria.hasStatusFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() 테스트")
    class HasSearchConditionTest {

        @Test
        @DisplayName("검색어가 있으면 true를 반환한다")
        void returnsTrueWhenSearchWordExists() {
            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            null,
                            SellerAdminSearchField.NAME,
                            "홍길동",
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsNull() {
            SellerAdminSearchCriteria criteria = SellerAdminSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("검색어가 공백이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsBlank() {
            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            null,
                            null,
                            "   ",
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchField() 테스트")
    class HasSearchFieldTest {

        @Test
        @DisplayName("검색 필드가 있으면 true를 반환한다")
        void returnsTrueWhenSearchFieldExists() {
            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            null,
                            SellerAdminSearchField.LOGIN_ID,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.defaultKey()));

            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("검색 필드가 null이면 false를 반환한다")
        void returnsFalseWhenSearchFieldIsNull() {
            SellerAdminSearchCriteria criteria = SellerAdminSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSearchField()).isFalse();
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("status 리스트는 외부 변경에 영향을 받지 않는다")
        void statusListIsImmutable() {
            List<SellerAdminStatus> mutableStatuses =
                    new ArrayList<>(List.of(SellerAdminStatus.ACTIVE));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            mutableStatuses,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.defaultKey()));

            mutableStatuses.add(SellerAdminStatus.INACTIVE);

            assertThat(criteria.status()).hasSize(1);
            assertThat(criteria.status()).containsOnly(SellerAdminStatus.ACTIVE);
        }

        @Test
        @DisplayName("sellerIds 리스트는 외부 변경에 영향을 받지 않는다")
        void sellerIdsListIsImmutable() {
            List<Long> mutableSellerIds = new ArrayList<>(List.of(1L));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            mutableSellerIds,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.defaultKey()));

            mutableSellerIds.add(999L);

            assertThat(criteria.sellerIds()).hasSize(1);
            assertThat(criteria.sellerIds()).containsOnly(1L);
        }
    }

    @Nested
    @DisplayName("페이징 편의 메서드 테스트")
    class PagingTest {

        @Test
        @DisplayName("size, offset, page 편의 메서드가 올바른 값을 반환한다")
        void pagingMethodsReturnCorrectValues() {
            SellerAdminSearchCriteria criteria = SellerAdminSearchCriteria.defaultCriteria();

            assertThat(criteria.size()).isPositive();
            assertThat(criteria.offset()).isGreaterThanOrEqualTo(0L);
            assertThat(criteria.page()).isGreaterThanOrEqualTo(0);
        }
    }
}
