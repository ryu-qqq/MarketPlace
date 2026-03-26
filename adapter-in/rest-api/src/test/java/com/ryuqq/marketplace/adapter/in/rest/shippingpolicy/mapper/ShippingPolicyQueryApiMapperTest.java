package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.ShippingPolicyApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.query.SearchShippingPoliciesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.response.ShippingPolicyApiResponse;
import com.ryuqq.marketplace.application.shippingpolicy.dto.query.ShippingPolicySearchParams;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyPageResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShippingPolicyQueryApiMapper 단위 테스트")
class ShippingPolicyQueryApiMapperTest {

    private ShippingPolicyQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShippingPolicyQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchShippingPoliciesPageApiRequest를 ShippingPolicySearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            Long sellerId = 1L;
            SearchShippingPoliciesPageApiRequest request =
                    ShippingPolicyApiFixtures.searchRequest(0, 20);

            // when
            ShippingPolicySearchParams result = mapper.toSearchParams(sellerId, request);

            // then
            assertThat(result.sellerId()).isEqualTo(1L);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("정렬 조건이 포함된 요청을 변환한다")
        void toSearchParams_WithSort_ReturnsSearchParamsWithSort() {
            // given
            Long sellerId = 1L;
            SearchShippingPoliciesPageApiRequest request =
                    ShippingPolicyApiFixtures.searchRequestWithSort("CREATED_AT", "DESC");

            // when
            ShippingPolicySearchParams result = mapper.toSearchParams(sellerId, request);

            // then
            assertThat(result.sellerId()).isEqualTo(1L);
            assertThat(result.sortKey()).isEqualTo("CREATED_AT");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("page/size가 null이면 기본값으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            Long sellerId = 1L;
            SearchShippingPoliciesPageApiRequest request =
                    new SearchShippingPoliciesPageApiRequest(null, null, null, null, null);

            // when
            ShippingPolicySearchParams result = mapper.toSearchParams(sellerId, request);

            // then
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("ShippingPolicyResult를 ShippingPolicyApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            ShippingPolicyResult result = ShippingPolicyApiFixtures.policyResult(1L);

            // when
            ShippingPolicyApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.policyId()).isEqualTo(1L);
            assertThat(response.policyName()).isEqualTo("기본 배송정책");
            assertThat(response.defaultPolicy()).isTrue();
            assertThat(response.active()).isTrue();
            assertThat(response.shippingFeeType()).isEqualTo("CONDITIONAL_FREE");
            assertThat(response.shippingFeeTypeDisplayName()).isEqualTo("조건부 무료배송");
            assertThat(response.baseFee()).isEqualTo(3000L);
            assertThat(response.freeThreshold()).isEqualTo(50000L);
            assertThat(response.createdAt()).isNotNull();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            ShippingPolicyResult result = ShippingPolicyApiFixtures.policyResult(1L);

            // when
            ShippingPolicyApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("ShippingPolicyResult 목록을 ShippingPolicyApiResponse 목록으로 변환한다")
        void toResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<ShippingPolicyResult> results = ShippingPolicyApiFixtures.policyResults(3);

            // when
            List<ShippingPolicyApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).policyName()).isEqualTo("배송정책_1");
            assertThat(responses.get(1).policyName()).isEqualTo("배송정책_2");
            assertThat(responses.get(2).policyName()).isEqualTo("배송정책_3");
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<ShippingPolicyResult> results = List.of();

            // when
            List<ShippingPolicyApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("ShippingPolicyPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            ShippingPolicyPageResult pageResult = ShippingPolicyApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<ShippingPolicyApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            ShippingPolicyPageResult pageResult = ShippingPolicyApiFixtures.emptyPageResult();

            // when
            PageApiResponse<ShippingPolicyApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
