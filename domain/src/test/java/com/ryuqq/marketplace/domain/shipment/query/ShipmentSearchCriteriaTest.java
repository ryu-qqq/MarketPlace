package com.ryuqq.marketplace.domain.shipment.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentSearchCriteria 단위 테스트")
class ShipmentSearchCriteriaTest {

    @Nested
    @DisplayName("defaultCriteria() - 기본 검색 조건 생성")
    class DefaultCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건은 필터 없는 상태로 생성된다")
        void createDefaultCriteria() {
            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();

            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.sellerIds()).isEmpty();
            assertThat(criteria.shopOrderNos()).isEmpty();
            assertThat(criteria.searchField()).isNull();
            assertThat(criteria.searchWord()).isNull();
            assertThat(criteria.dateRange()).isNull();
            assertThat(criteria.dateField()).isNull();
        }

        @Test
        @DisplayName("기본 검색 조건은 QueryContext를 가진다")
        void defaultCriteriaHasQueryContext() {
            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();

            assertThat(criteria.queryContext()).isNotNull();
        }

        @Test
        @DisplayName("기본 정렬 키는 CREATED_AT이다")
        void defaultCriteriaHasCreatedAtSortKey() {
            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();

            assertThat(criteria.queryContext().sortKey()).isEqualTo(ShipmentSortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("of() - 직접 생성")
    class OfTest {

        @Test
        @DisplayName("상태 필터와 셀러 필터를 포함한 검색 조건을 생성한다")
        void createWithStatusAndSellerFilter() {
            List<ShipmentStatus> statuses =
                    List.of(ShipmentStatus.SHIPPED, ShipmentStatus.IN_TRANSIT);
            List<Long> sellerIds = List.of(1L, 2L);
            QueryContext<ShipmentSortKey> queryContext =
                    QueryContext.defaultOf(ShipmentSortKey.defaultKey());

            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            statuses, sellerIds, null, null, null, null, null, queryContext);

            assertThat(criteria.statuses())
                    .containsExactlyInAnyOrder(ShipmentStatus.SHIPPED, ShipmentStatus.IN_TRANSIT);
            assertThat(criteria.sellerIds()).containsExactlyInAnyOrder(1L, 2L);
        }

        @Test
        @DisplayName("null 상태 리스트는 빈 리스트로 처리된다")
        void nullStatusListBecomesEmpty() {
            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.defaultKey()));

            assertThat(criteria.statuses()).isEmpty();
            assertThat(criteria.sellerIds()).isEmpty();
            assertThat(criteria.shopOrderNos()).isEmpty();
        }

        @Test
        @DisplayName("검색어와 검색 필드를 포함한 조건을 생성한다")
        void createWithSearchWordAndField() {
            QueryContext<ShipmentSortKey> queryContext =
                    QueryContext.defaultOf(ShipmentSortKey.defaultKey());

            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            null,
                            null,
                            null,
                            ShipmentSearchField.TRACKING_NUMBER,
                            "1234567890",
                            null,
                            null,
                            queryContext);

            assertThat(criteria.searchField()).isEqualTo(ShipmentSearchField.TRACKING_NUMBER);
            assertThat(criteria.searchWord()).isEqualTo("1234567890");
        }

        @Test
        @DisplayName("외부 주문번호 필터를 포함한 조건을 생성한다")
        void createWithShopOrderNoFilter() {
            List<String> shopOrderNos = List.of("NAVER-001", "NAVER-002");
            QueryContext<ShipmentSortKey> queryContext =
                    QueryContext.defaultOf(ShipmentSortKey.defaultKey());

            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            null, null, shopOrderNos, null, null, null, null, queryContext);

            assertThat(criteria.shopOrderNos()).containsExactlyInAnyOrder("NAVER-001", "NAVER-002");
        }
    }

    @Nested
    @DisplayName("hasStatusFilter() 테스트")
    class HasStatusFilterTest {

        @Test
        @DisplayName("상태 필터가 있으면 true를 반환한다")
        void returnsTrueWhenStatusFilterExists() {
            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            List.of(ShipmentStatus.SHIPPED),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.defaultKey()));

            assertThat(criteria.hasStatusFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 없으면 false를 반환한다")
        void returnsFalseWhenStatusFilterIsEmpty() {
            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();

            assertThat(criteria.hasStatusFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSellerFilter() 테스트")
    class HasSellerFilterTest {

        @Test
        @DisplayName("셀러 필터가 있으면 true를 반환한다")
        void returnsTrueWhenSellerFilterExists() {
            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            null,
                            List.of(1L, 2L),
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.defaultKey()));

            assertThat(criteria.hasSellerFilter()).isTrue();
        }

        @Test
        @DisplayName("셀러 필터가 없으면 false를 반환한다")
        void returnsFalseWhenSellerFilterIsEmpty() {
            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSellerFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasShopOrderNoFilter() 테스트")
    class HasShopOrderNoFilterTest {

        @Test
        @DisplayName("외부 주문번호 필터가 있으면 true를 반환한다")
        void returnsTrueWhenShopOrderNoFilterExists() {
            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            null,
                            null,
                            List.of("NAVER-001"),
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.defaultKey()));

            assertThat(criteria.hasShopOrderNoFilter()).isTrue();
        }

        @Test
        @DisplayName("외부 주문번호 필터가 없으면 false를 반환한다")
        void returnsFalseWhenShopOrderNoFilterIsEmpty() {
            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();

            assertThat(criteria.hasShopOrderNoFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchCondition() 테스트")
    class HasSearchConditionTest {

        @Test
        @DisplayName("검색어가 있으면 true를 반환한다")
        void returnsTrueWhenSearchWordExists() {
            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            null,
                            null,
                            null,
                            ShipmentSearchField.CUSTOMER_NAME,
                            "홍길동",
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("검색어가 null이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsNull() {
            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSearchCondition()).isFalse();
        }

        @Test
        @DisplayName("검색어가 공백이면 false를 반환한다")
        void returnsFalseWhenSearchWordIsBlank() {
            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            "   ",
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.defaultKey()));

            assertThat(criteria.hasSearchCondition()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasSearchField() 테스트")
    class HasSearchFieldTest {

        @Test
        @DisplayName("검색 필드가 있으면 true를 반환한다")
        void returnsTrueWhenSearchFieldExists() {
            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            null,
                            null,
                            null,
                            ShipmentSearchField.ORDER_ITEM_ID,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.defaultKey()));

            assertThat(criteria.hasSearchField()).isTrue();
        }

        @Test
        @DisplayName("검색 필드가 null이면 false를 반환한다")
        void returnsFalseWhenSearchFieldIsNull() {
            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();

            assertThat(criteria.hasSearchField()).isFalse();
        }
    }

    @Nested
    @DisplayName("페이징 편의 메서드 테스트")
    class PagingTest {

        @Test
        @DisplayName("size(), offset(), page() 편의 메서드가 QueryContext에서 값을 반환한다")
        void pagingConvenienceMethods() {
            ShipmentSearchCriteria criteria = ShipmentSearchCriteria.defaultCriteria();

            assertThat(criteria.size()).isEqualTo(criteria.queryContext().size());
            assertThat(criteria.offset()).isEqualTo(criteria.queryContext().offset());
            assertThat(criteria.page()).isEqualTo(criteria.queryContext().page());
        }
    }

    @Nested
    @DisplayName("불변성 테스트")
    class ImmutabilityTest {

        @Test
        @DisplayName("statuses 리스트는 외부 변경에 영향을 받지 않는다")
        void statusListIsImmutable() {
            List<ShipmentStatus> mutableStatuses =
                    new java.util.ArrayList<>(List.of(ShipmentStatus.SHIPPED));

            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            mutableStatuses,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.defaultKey()));

            mutableStatuses.add(ShipmentStatus.DELIVERED);

            assertThat(criteria.statuses()).hasSize(1);
            assertThat(criteria.statuses()).containsOnly(ShipmentStatus.SHIPPED);
        }

        @Test
        @DisplayName("shopOrderNos 리스트는 외부 변경에 영향을 받지 않는다")
        void shopOrderNoListIsImmutable() {
            List<String> mutableShopOrderNos = new java.util.ArrayList<>(List.of("NAVER-001"));

            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            null,
                            null,
                            mutableShopOrderNos,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.defaultKey()));

            mutableShopOrderNos.add("NAVER-999");

            assertThat(criteria.shopOrderNos()).hasSize(1);
            assertThat(criteria.shopOrderNos()).containsOnly("NAVER-001");
        }

        @Test
        @DisplayName("sellerIds 리스트는 외부 변경에 영향을 받지 않는다")
        void sellerIdListIsImmutable() {
            List<Long> mutableSellerIds = new java.util.ArrayList<>(List.of(1L));

            ShipmentSearchCriteria criteria =
                    ShipmentSearchCriteria.of(
                            null,
                            mutableSellerIds,
                            null,
                            null,
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(ShipmentSortKey.defaultKey()));

            mutableSellerIds.add(999L);

            assertThat(criteria.sellerIds()).hasSize(1);
            assertThat(criteria.sellerIds()).containsOnly(1L);
        }
    }
}
