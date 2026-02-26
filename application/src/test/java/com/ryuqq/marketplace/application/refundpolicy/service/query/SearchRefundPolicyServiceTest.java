package com.ryuqq.marketplace.application.refundpolicy.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.refundpolicy.RefundPolicyQueryFixtures;
import com.ryuqq.marketplace.application.refundpolicy.assembler.RefundPolicyAssembler;
import com.ryuqq.marketplace.application.refundpolicy.dto.query.RefundPolicySearchParams;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyPageResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import com.ryuqq.marketplace.application.refundpolicy.factory.RefundPolicyQueryFactory;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.RefundPolicyFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria;
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
@DisplayName("SearchRefundPolicyService 단위 테스트")
class SearchRefundPolicyServiceTest {

    @InjectMocks private SearchRefundPolicyService sut;

    @Mock private RefundPolicyReadManager readManager;
    @Mock private RefundPolicyQueryFactory queryFactory;
    @Mock private RefundPolicyAssembler assembler;

    @Nested
    @DisplayName("execute() - 환불 정책 검색")
    class ExecuteTest {

        @Test
        @DisplayName("검색 조건으로 환불 정책 목록을 페이징하여 반환한다")
        void execute_ReturnsPagedResult() {
            // given
            Long sellerId = 1L;
            RefundPolicySearchParams params =
                    RefundPolicyQueryFixtures.searchParams(sellerId, 0, 20);
            RefundPolicySearchCriteria criteria =
                    RefundPolicySearchCriteria.defaultCriteria(CommonVoFixtures.defaultSellerId());

            List<RefundPolicy> policies =
                    List.of(
                            RefundPolicyFixtures.activeRefundPolicy(),
                            RefundPolicyFixtures.newRefundPolicy(7, 14));
            long totalElements = 2L;

            List<RefundPolicyResult> results =
                    policies.stream().map(RefundPolicyResult::from).toList();
            RefundPolicyPageResult expected =
                    RefundPolicyPageResult.of(results, params.page(), params.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(policies);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(assembler.toPageResult(policies, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            RefundPolicyPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.size()).isEqualTo(2);
            then(queryFactory).should().createCriteria(params);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환한다")
        void execute_NoResults_ReturnsEmptyPage() {
            // given
            Long sellerId = 1L;
            RefundPolicySearchParams params = RefundPolicyQueryFixtures.searchParams(sellerId);
            RefundPolicySearchCriteria criteria =
                    RefundPolicySearchCriteria.defaultCriteria(CommonVoFixtures.defaultSellerId());
            List<RefundPolicy> emptyPolicies = Collections.emptyList();
            long totalElements = 0L;

            RefundPolicyPageResult expected = RefundPolicyPageResult.empty(params.size());

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(readManager.findByCriteria(criteria)).willReturn(emptyPolicies);
            given(readManager.countByCriteria(criteria)).willReturn(totalElements);
            given(
                            assembler.toPageResult(
                                    emptyPolicies, params.page(), params.size(), totalElements))
                    .willReturn(expected);

            // when
            RefundPolicyPageResult result = sut.execute(params);

            // then
            assertThat(result.isEmpty()).isTrue();
        }
    }
}
