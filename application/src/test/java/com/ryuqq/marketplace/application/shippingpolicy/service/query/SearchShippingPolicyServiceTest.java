package com.ryuqq.marketplace.application.shippingpolicy.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shippingpolicy.ShippingPolicyQueryFixtures;
import com.ryuqq.marketplace.application.shippingpolicy.assembler.ShippingPolicyAssembler;
import com.ryuqq.marketplace.application.shippingpolicy.dto.query.ShippingPolicySearchParams;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyPageResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import com.ryuqq.marketplace.application.shippingpolicy.factory.ShippingPolicyQueryFactory;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySearchCriteria;
import java.util.Collections;
import java.util.List;
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
@DisplayName("SearchShippingPolicyService 단위 테스트")
class SearchShippingPolicyServiceTest {

    @InjectMocks private SearchShippingPolicyService sut;

    @Mock private ShippingPolicyReadManager readManager;
    @Mock private ShippingPolicyQueryFactory queryFactory;
    @Mock private ShippingPolicyAssembler assembler;

    @Nested
    @DisplayName("execute() - 배송 정책 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 배송 정책 목록을 페이징하여 반환한다")
        void execute_ReturnsPagedResult() {
            // given
            Long sellerId = 1L;
            ShippingPolicySearchParams params =
                    ShippingPolicyQueryFixtures.searchParams(sellerId, 0, 20);
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.defaultCriteria(
                            CommonVoFixtures.defaultSellerId());

            List<ShippingPolicy> policies =
                    List.of(
                            ShippingPolicyFixtures.activeShippingPolicy(),
                            ShippingPolicyFixtures.newPaidShippingPolicy());
            long totalElements = 2L;

            List<ShippingPolicyResult> results =
                    policies.stream().map(ShippingPolicyResult::from).toList();
            ShippingPolicyPageResult expected =
                    ShippingPolicyPageResult.of(
                            results, params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(policies);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(policies, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            ShippingPolicyPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.size()).isEqualTo(2);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler)
                    .should()
                    .toPageResult(policies, params.page(), params.size(), totalElements);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            Long sellerId = 1L;
            ShippingPolicySearchParams params = ShippingPolicyQueryFixtures.searchParams(sellerId);
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.defaultCriteria(
                            CommonVoFixtures.defaultSellerId());
            List<ShippingPolicy> emptyPolicies = Collections.emptyList();
            long totalElements = 0L;

            ShippingPolicyPageResult expected = ShippingPolicyPageResult.empty(params.size());

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptyPolicies);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(
                            assembler.toPageResult(
                                    emptyPolicies, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            ShippingPolicyPageResult result = sut.execute(params);

            // then
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.size()).isZero();
        }
    }
}
