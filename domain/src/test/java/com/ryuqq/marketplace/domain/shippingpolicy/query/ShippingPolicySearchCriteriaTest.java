package com.ryuqq.marketplace.domain.shippingpolicy.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShippingPolicySearchCriteria 테스트")
class ShippingPolicySearchCriteriaTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("of()로 검색 조건을 생성한다")
        void createWithOf() {
            // given
            SellerId sellerId = SellerId.of(1L);
            QueryContext<ShippingPolicySortKey> queryContext =
                    QueryContext.of(
                            ShippingPolicySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(0, 20));

            // when
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.of(sellerId, queryContext);

            // then
            assertThat(criteria.sellerId()).isEqualTo(sellerId);
            assertThat(criteria.queryContext()).isEqualTo(queryContext);
        }

        @Test
        @DisplayName("defaultCriteria()로 기본 검색 조건을 생성한다")
        void createDefaultCriteria() {
            // given
            SellerId sellerId = SellerId.of(1L);

            // when
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.defaultCriteria(sellerId);

            // then
            assertThat(criteria.sellerId()).isEqualTo(sellerId);
            assertThat(criteria.queryContext().sortKey())
                    .isEqualTo(ShippingPolicySortKey.CREATED_AT);
        }
    }

    @Nested
    @DisplayName("편의 메서드 테스트")
    class ConvenienceMethodTest {

        @Test
        @DisplayName("size()는 페이지 크기를 반환한다")
        void returnsSize() {
            // given
            SellerId sellerId = SellerId.of(1L);
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.defaultCriteria(sellerId);

            // then
            assertThat(criteria.size()).isEqualTo(PageRequest.DEFAULT_SIZE);
        }

        @Test
        @DisplayName("offset()은 오프셋을 반환한다")
        void returnsOffset() {
            // given
            SellerId sellerId = SellerId.of(1L);
            QueryContext<ShippingPolicySortKey> queryContext =
                    QueryContext.of(
                            ShippingPolicySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(2, 20));
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.of(sellerId, queryContext);

            // then
            assertThat(criteria.offset()).isEqualTo(40);
        }

        @Test
        @DisplayName("page()는 현재 페이지를 반환한다")
        void returnsPage() {
            // given
            SellerId sellerId = SellerId.of(1L);
            QueryContext<ShippingPolicySortKey> queryContext =
                    QueryContext.of(
                            ShippingPolicySortKey.CREATED_AT,
                            SortDirection.DESC,
                            PageRequest.of(3, 20));
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.of(sellerId, queryContext);

            // then
            assertThat(criteria.page()).isEqualTo(3);
        }

        @Test
        @DisplayName("sellerIdValue()는 셀러 ID 값을 반환한다")
        void returnsSellerIdValue() {
            // given
            SellerId sellerId = SellerId.of(123L);
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.defaultCriteria(sellerId);

            // then
            assertThat(criteria.sellerIdValue()).isEqualTo(123L);
        }
    }
}
