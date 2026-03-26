package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.RefundPolicyApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.query.SearchRefundPoliciesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.NonReturnableConditionApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.RefundPolicyApiResponse;
import com.ryuqq.marketplace.application.refundpolicy.dto.query.RefundPolicySearchParams;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.NonReturnableConditionResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyPageResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundPolicyQueryApiMapper 단위 테스트")
class RefundPolicyQueryApiMapperTest {

    private RefundPolicyQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RefundPolicyQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchRefundPoliciesPageApiRequest를 RefundPolicySearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            Long sellerId = 1L;
            SearchRefundPoliciesPageApiRequest request =
                    RefundPolicyApiFixtures.searchRequest("CREATED_AT", "DESC", 0, 20);

            // when
            RefundPolicySearchParams result = mapper.toSearchParams(sellerId, request);

            // then
            assertThat(result.sellerId()).isEqualTo(1L);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("CREATED_AT");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("null 필드에 기본값을 적용한다")
        void toSearchParams_NullFields_AppliesDefaults() {
            // given
            Long sellerId = 1L;
            SearchRefundPoliciesPageApiRequest request = RefundPolicyApiFixtures.searchRequest();

            // when
            RefundPolicySearchParams result = mapper.toSearchParams(sellerId, request);

            // then
            assertThat(result.sellerId()).isEqualTo(1L);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("커스텀 페이지 파라미터를 적용한다")
        void toSearchParams_CustomPageParams_AppliesCorrectly() {
            // given
            Long sellerId = 5L;
            SearchRefundPoliciesPageApiRequest request =
                    RefundPolicyApiFixtures.searchRequest("POLICY_NAME", "ASC", 2, 50);

            // when
            RefundPolicySearchParams result = mapper.toSearchParams(sellerId, request);

            // then
            assertThat(result.sellerId()).isEqualTo(5L);
            assertThat(result.page()).isEqualTo(2);
            assertThat(result.size()).isEqualTo(50);
            assertThat(result.sortKey()).isEqualTo("POLICY_NAME");
            assertThat(result.sortDirection()).isEqualTo("ASC");
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("RefundPolicyResult를 RefundPolicyApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            RefundPolicyResult result = RefundPolicyApiFixtures.policyResult(1L);

            // when
            RefundPolicyApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.policyId()).isEqualTo(1L);
            assertThat(response.policyName())
                    .isEqualTo(RefundPolicyApiFixtures.DEFAULT_POLICY_NAME);
            assertThat(response.defaultPolicy()).isTrue();
            assertThat(response.active()).isTrue();
            assertThat(response.returnPeriodDays())
                    .isEqualTo(RefundPolicyApiFixtures.DEFAULT_RETURN_PERIOD_DAYS);
            assertThat(response.exchangePeriodDays())
                    .isEqualTo(RefundPolicyApiFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS);
        }

        @Test
        @DisplayName("날짜를 ISO 8601 형식으로 변환한다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            RefundPolicyResult result = RefundPolicyApiFixtures.policyResult(1L);

            // when
            RefundPolicyApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.createdAt()).contains("2025-01-23");
            assertThat(response.createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }

        @Test
        @DisplayName("반품 불가 조건을 변환한다")
        void toResponse_ConvertsConditions_ReturnsConditionResponses() {
            // given
            RefundPolicyResult result = RefundPolicyApiFixtures.policyResult(1L);

            // when
            RefundPolicyApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.nonReturnableConditions()).hasSize(3);
            assertThat(response.nonReturnableConditions().get(0).code())
                    .isEqualTo("OPENED_PACKAGING");
            assertThat(response.nonReturnableConditions().get(0).displayName()).isEqualTo("포장 개봉");
            assertThat(response.nonReturnableConditions().get(1).code()).isEqualTo("USED_PRODUCT");
            assertThat(response.nonReturnableConditions().get(2).code()).isEqualTo("MISSING_TAG");
        }

        @Test
        @DisplayName("조건이 null이면 빈 목록을 반환한다")
        void toResponse_NullConditions_ReturnsEmptyList() {
            // given
            RefundPolicyResult result = RefundPolicyApiFixtures.policyResultWithNullConditions(1L);

            // when
            RefundPolicyApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.nonReturnableConditions()).isEmpty();
        }

        @Test
        @DisplayName("조건이 빈 목록이면 빈 목록을 반환한다")
        void toResponse_EmptyConditions_ReturnsEmptyList() {
            // given
            RefundPolicyResult result = RefundPolicyApiFixtures.policyResultWithEmptyConditions(1L);

            // when
            RefundPolicyApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.nonReturnableConditions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toConditionResponses() - 조건 목록 변환")
    class ToConditionResponsesTest {

        @Test
        @DisplayName("NonReturnableConditionResult 목록을 변환한다")
        void toConditionResponses_ConvertsResults_ReturnsApiResponses() {
            // given
            List<NonReturnableConditionResult> conditions =
                    RefundPolicyApiFixtures.defaultConditionResults();

            // when
            List<NonReturnableConditionApiResponse> responses =
                    mapper.toConditionResponses(conditions);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).code()).isEqualTo("OPENED_PACKAGING");
            assertThat(responses.get(0).displayName()).isEqualTo("포장 개봉");
        }

        @Test
        @DisplayName("null이면 빈 목록을 반환한다")
        void toConditionResponses_Null_ReturnsEmptyList() {
            // when
            List<NonReturnableConditionApiResponse> responses = mapper.toConditionResponses(null);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("결과 목록을 응답 목록으로 변환한다")
        void toResponses_ConvertsResults_ReturnsApiResponses() {
            // given
            List<RefundPolicyResult> results = RefundPolicyApiFixtures.policyResults(3);

            // when
            List<RefundPolicyApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).policyId()).isEqualTo(1L);
            assertThat(responses.get(1).policyId()).isEqualTo(2L);
            assertThat(responses.get(2).policyId()).isEqualTo(3L);
        }

        @Test
        @DisplayName("빈 목록은 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // when
            List<RefundPolicyApiResponse> responses = mapper.toResponses(List.of());

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("RefundPolicyPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageApiResponse() {
            // given
            RefundPolicyPageResult pageResult = RefundPolicyApiFixtures.pageResult(5, 0, 20);

            // when
            PageApiResponse<RefundPolicyApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(5);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(5);
        }

        @Test
        @DisplayName("빈 페이지 결과를 변환한다")
        void toPageResponse_EmptyPageResult_ReturnsEmptyPage() {
            // given
            RefundPolicyPageResult pageResult = RefundPolicyApiFixtures.emptyPageResult();

            // when
            PageApiResponse<RefundPolicyApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
