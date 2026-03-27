package com.ryuqq.marketplace.domain.claimhistory.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ClaimHistoryPageCriteria 단위 테스트")
class ClaimHistoryPageCriteriaTest {

    @Nested
    @DisplayName("of() - 정적 팩토리 메서드")
    class OfTest {

        @Test
        @DisplayName("모든 파라미터로 페이지 조회 조건을 생성한다")
        void createWithAllParameters() {
            // given
            String orderItemId = "order-item-001";
            ClaimType claimType = ClaimType.CANCEL;
            QueryContext<ClaimHistorySortKey> queryContext =
                    QueryContext.of(
                            ClaimHistorySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 10));

            // when
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(orderItemId, claimType, queryContext);

            // then
            assertThat(criteria.orderItemId()).isEqualTo(orderItemId);
            assertThat(criteria.claimType()).isEqualTo(ClaimType.CANCEL);
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("claimType이 null이어도 생성에 성공한다")
        void createWithNullClaimType() {
            // given
            String orderItemId = "order-item-001";
            QueryContext<ClaimHistorySortKey> queryContext =
                    QueryContext.defaultOf(ClaimHistorySortKey.defaultKey());

            // when
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(orderItemId, null, queryContext);

            // then
            assertThat(criteria.claimType()).isNull();
            assertThat(criteria.hasClaimTypeFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("defaultOf() - 기본 페이지 조회 조건")
    class DefaultOfTest {

        @Test
        @DisplayName("orderItemId만으로 기본 페이지 조회 조건을 생성한다")
        void createDefaultCriteria() {
            // given
            String orderItemId = "order-item-001";

            // when
            ClaimHistoryPageCriteria criteria = ClaimHistoryPageCriteria.defaultOf(orderItemId);

            // then
            assertThat(criteria.orderItemId()).isEqualTo(orderItemId);
            assertThat(criteria.claimType()).isNull();
            assertThat(criteria.queryContext()).isNotNull();
            assertThat(criteria.queryContext().sortKey()).isEqualTo(ClaimHistorySortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("유효성 검증 테스트")
    class ValidationTest {

        @Test
        @DisplayName("orderItemId가 null이면 예외가 발생한다")
        void nullOrderItemIdThrowsException() {
            // given
            QueryContext<ClaimHistorySortKey> queryContext =
                    QueryContext.defaultOf(ClaimHistorySortKey.defaultKey());

            // when & then
            assertThatThrownBy(() -> ClaimHistoryPageCriteria.of(null, null, queryContext))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("orderItemId");
        }

        @Test
        @DisplayName("orderItemId가 빈 문자열이면 예외가 발생한다")
        void blankOrderItemIdThrowsException() {
            // given
            QueryContext<ClaimHistorySortKey> queryContext =
                    QueryContext.defaultOf(ClaimHistorySortKey.defaultKey());

            // when & then
            assertThatThrownBy(() -> ClaimHistoryPageCriteria.of("", null, queryContext))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("orderItemId");
        }

        @Test
        @DisplayName("orderItemId가 공백만 있으면 예외가 발생한다")
        void whitespaceOrderItemIdThrowsException() {
            // given
            QueryContext<ClaimHistorySortKey> queryContext =
                    QueryContext.defaultOf(ClaimHistorySortKey.defaultKey());

            // when & then
            assertThatThrownBy(() -> ClaimHistoryPageCriteria.of("   ", null, queryContext))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("orderItemId");
        }
    }

    @Nested
    @DisplayName("hasClaimTypeFilter() - 클레임 타입 필터 존재 여부")
    class HasClaimTypeFilterTest {

        @Test
        @DisplayName("claimType이 있으면 true를 반환한다")
        void returnTrueWhenClaimTypeExists() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            "order-item-001",
                            ClaimType.CANCEL,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // then
            assertThat(criteria.hasClaimTypeFilter()).isTrue();
        }

        @Test
        @DisplayName("claimType이 null이면 false를 반환한다")
        void returnFalseWhenClaimTypeIsNull() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.defaultOf("order-item-001");

            // then
            assertThat(criteria.hasClaimTypeFilter()).isFalse();
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodsTest {

        @Test
        @DisplayName("size()는 QueryContext의 size를 반환한다")
        void sizeReturnsQueryContextSize() {
            // given
            QueryContext<ClaimHistorySortKey> queryContext =
                    QueryContext.of(
                            ClaimHistorySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of("order-item-001", null, queryContext);

            // then
            assertThat(criteria.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("offset()은 QueryContext의 offset을 반환한다")
        void offsetReturnsQueryContextOffset() {
            // given
            QueryContext<ClaimHistorySortKey> queryContext =
                    QueryContext.of(
                            ClaimHistorySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 10));
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of("order-item-001", null, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(20L);
        }

        @Test
        @DisplayName("page()는 QueryContext의 page를 반환한다")
        void pageReturnsQueryContextPage() {
            // given
            QueryContext<ClaimHistorySortKey> queryContext =
                    QueryContext.of(
                            ClaimHistorySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(3, 10));
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of("order-item-001", null, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("ClaimType별 필터 테스트")
    class ClaimTypeFilterTest {

        @Test
        @DisplayName("CANCEL 타입으로 필터링한다")
        void filterByCancelClaimType() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            "order-item-001",
                            ClaimType.CANCEL,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // then
            assertThat(criteria.claimType()).isEqualTo(ClaimType.CANCEL);
            assertThat(criteria.hasClaimTypeFilter()).isTrue();
        }

        @Test
        @DisplayName("REFUND 타입으로 필터링한다")
        void filterByRefundClaimType() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            "order-item-001",
                            ClaimType.REFUND,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // then
            assertThat(criteria.claimType()).isEqualTo(ClaimType.REFUND);
            assertThat(criteria.hasClaimTypeFilter()).isTrue();
        }

        @Test
        @DisplayName("EXCHANGE 타입으로 필터링한다")
        void filterByExchangeClaimType() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            "order-item-001",
                            ClaimType.EXCHANGE,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // then
            assertThat(criteria.claimType()).isEqualTo(ClaimType.EXCHANGE);
            assertThat(criteria.hasClaimTypeFilter()).isTrue();
        }

        @Test
        @DisplayName("ORDER 타입으로 필터링한다")
        void filterByOrderClaimType() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            "order-item-001",
                            ClaimType.ORDER,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // then
            assertThat(criteria.claimType()).isEqualTo(ClaimType.ORDER);
            assertThat(criteria.hasClaimTypeFilter()).isTrue();
        }
    }
}
