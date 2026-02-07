package com.ryuqq.marketplace.application.shippingpolicy.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.shippingpolicy.ShippingPolicyQueryFixtures;
import com.ryuqq.marketplace.application.shippingpolicy.dto.query.ShippingPolicySearchParams;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySearchCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShippingPolicyQueryFactory 단위 테스트")
class ShippingPolicyQueryFactoryTest {

    private final ShippingPolicyQueryFactory sut =
            new ShippingPolicyQueryFactory(new CommonVoFactory());

    @Nested
    @DisplayName("createCriteria() - SearchParams → SearchCriteria 변환")
    class CreateCriteriaTest {

        @Test
        @DisplayName("ShippingPolicySearchParams로부터 ShippingPolicySearchCriteria를 생성한다")
        void createCriteria_CreatesSearchCriteria() {
            // given
            Long sellerId = 1L;
            ShippingPolicySearchParams params =
                    ShippingPolicyQueryFixtures.searchParams(sellerId, 0, 20);

            // when
            ShippingPolicySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sellerId().value()).isEqualTo(sellerId);
            assertThat(result.queryContext()).isNotNull();
        }

        @Test
        @DisplayName("기본 검색 파라미터로 Criteria를 생성한다")
        void createCriteria_DefaultParams_CreatesWithDefaults() {
            // given
            Long sellerId = 1L;
            ShippingPolicySearchParams params = ShippingPolicyQueryFixtures.searchParams(sellerId);

            // when
            ShippingPolicySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.queryContext().pageRequest().page()).isZero();
            assertThat(result.queryContext().pageRequest().size()).isEqualTo(20);
        }
    }
}
