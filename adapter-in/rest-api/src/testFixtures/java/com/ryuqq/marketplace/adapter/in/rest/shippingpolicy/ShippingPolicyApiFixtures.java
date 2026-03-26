package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy;

import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.ChangeShippingPolicyStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.RegisterShippingPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.UpdateShippingPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.query.SearchShippingPoliciesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.response.ShippingPolicyApiResponse;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyPageResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

/**
 * ShippingPolicy API 테스트 Fixtures.
 *
 * <p>ShippingPolicy REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class ShippingPolicyApiFixtures {

    private ShippingPolicyApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final Long DEFAULT_POLICY_ID = 1L;
    public static final String DEFAULT_POLICY_NAME = "기본 배송정책";
    public static final String DEFAULT_SHIPPING_FEE_TYPE = "CONDITIONAL_FREE";
    public static final String DEFAULT_SHIPPING_FEE_TYPE_DISPLAY_NAME = "조건부 무료배송";
    public static final Long DEFAULT_BASE_FEE = 3000L;
    public static final Long DEFAULT_FREE_THRESHOLD = 50000L;
    public static final Long DEFAULT_JEJU_EXTRA_FEE = 3000L;
    public static final Long DEFAULT_ISLAND_EXTRA_FEE = 5000L;
    public static final Long DEFAULT_RETURN_FEE = 3000L;
    public static final Long DEFAULT_EXCHANGE_FEE = 6000L;

    // ===== RegisterShippingPolicyApiRequest =====

    public static RegisterShippingPolicyApiRequest registerRequest() {
        return new RegisterShippingPolicyApiRequest(
                DEFAULT_POLICY_NAME,
                true,
                DEFAULT_SHIPPING_FEE_TYPE,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_JEJU_EXTRA_FEE,
                DEFAULT_ISLAND_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                defaultLeadTimeRequest());
    }

    public static RegisterShippingPolicyApiRequest registerRequestWithoutLeadTime() {
        return new RegisterShippingPolicyApiRequest(
                DEFAULT_POLICY_NAME,
                true,
                DEFAULT_SHIPPING_FEE_TYPE,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_JEJU_EXTRA_FEE,
                DEFAULT_ISLAND_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                null);
    }

    public static RegisterShippingPolicyApiRequest.LeadTimeApiRequest defaultLeadTimeRequest() {
        return new RegisterShippingPolicyApiRequest.LeadTimeApiRequest(1, 3, "14:00");
    }

    // ===== UpdateShippingPolicyApiRequest =====

    public static UpdateShippingPolicyApiRequest updateRequest() {
        return new UpdateShippingPolicyApiRequest(
                "수정된 배송정책",
                false,
                "PAID",
                3500L,
                null,
                3500L,
                5500L,
                3500L,
                7000L,
                new RegisterShippingPolicyApiRequest.LeadTimeApiRequest(2, 5, "12:00"));
    }

    // ===== ChangeShippingPolicyStatusApiRequest =====

    public static ChangeShippingPolicyStatusApiRequest changeStatusRequest() {
        return new ChangeShippingPolicyStatusApiRequest(List.of(1L, 2L, 3L), false);
    }

    public static ChangeShippingPolicyStatusApiRequest activateRequest() {
        return new ChangeShippingPolicyStatusApiRequest(List.of(1L, 2L), true);
    }

    // ===== SearchShippingPoliciesPageApiRequest =====

    public static SearchShippingPoliciesPageApiRequest searchRequest() {
        return new SearchShippingPoliciesPageApiRequest(null, null, 0, 20, null);
    }

    public static SearchShippingPoliciesPageApiRequest searchRequest(int page, int size) {
        return new SearchShippingPoliciesPageApiRequest(null, null, page, size, null);
    }

    public static SearchShippingPoliciesPageApiRequest searchRequestWithSort(
            String sortKey, String sortDirection) {
        return new SearchShippingPoliciesPageApiRequest(sortKey, sortDirection, 0, 20, null);
    }

    // ===== ShippingPolicyResult (Application) =====

    public static ShippingPolicyResult policyResult(Long id) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new ShippingPolicyResult(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                true,
                true,
                DEFAULT_SHIPPING_FEE_TYPE,
                DEFAULT_SHIPPING_FEE_TYPE_DISPLAY_NAME,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_JEJU_EXTRA_FEE,
                DEFAULT_ISLAND_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                1,
                3,
                LocalTime.of(14, 0),
                now,
                now);
    }

    public static ShippingPolicyResult policyResult(Long id, String policyName, boolean active) {
        Instant now = Instant.parse("2025-01-23T01:30:00Z");
        return new ShippingPolicyResult(
                id,
                DEFAULT_SELLER_ID,
                policyName,
                false,
                active,
                DEFAULT_SHIPPING_FEE_TYPE,
                DEFAULT_SHIPPING_FEE_TYPE_DISPLAY_NAME,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_JEJU_EXTRA_FEE,
                DEFAULT_ISLAND_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                1,
                3,
                LocalTime.of(14, 0),
                now,
                now);
    }

    public static List<ShippingPolicyResult> policyResults(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> policyResult((long) i, "배송정책_" + i, true))
                .toList();
    }

    public static ShippingPolicyPageResult pageResult(int count, int page, int size) {
        List<ShippingPolicyResult> results = policyResults(count);
        return ShippingPolicyPageResult.of(results, page, size, count);
    }

    public static ShippingPolicyPageResult emptyPageResult() {
        return ShippingPolicyPageResult.of(List.of(), 0, 20, 0);
    }

    // ===== ShippingPolicyApiResponse =====

    public static ShippingPolicyApiResponse apiResponse(Long id) {
        return new ShippingPolicyApiResponse(
                id,
                DEFAULT_SELLER_ID,
                DEFAULT_POLICY_NAME,
                true,
                true,
                DEFAULT_SHIPPING_FEE_TYPE,
                DEFAULT_SHIPPING_FEE_TYPE_DISPLAY_NAME,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_JEJU_EXTRA_FEE,
                DEFAULT_ISLAND_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                1,
                3,
                "14:00",
                "2025-01-23 10:30:00",
                "2025-01-23 10:30:00");
    }

    public static ShippingPolicyApiResponse apiResponse(Long id, String policyName) {
        return new ShippingPolicyApiResponse(
                id,
                DEFAULT_SELLER_ID,
                policyName,
                false,
                true,
                DEFAULT_SHIPPING_FEE_TYPE,
                DEFAULT_SHIPPING_FEE_TYPE_DISPLAY_NAME,
                DEFAULT_BASE_FEE,
                DEFAULT_FREE_THRESHOLD,
                DEFAULT_JEJU_EXTRA_FEE,
                DEFAULT_ISLAND_EXTRA_FEE,
                DEFAULT_RETURN_FEE,
                DEFAULT_EXCHANGE_FEE,
                1,
                3,
                "14:00",
                "2025-01-23 10:30:00",
                "2025-01-23 10:30:00");
    }
}
