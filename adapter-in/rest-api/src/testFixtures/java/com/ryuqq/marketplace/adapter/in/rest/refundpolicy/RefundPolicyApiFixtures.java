package com.ryuqq.marketplace.adapter.in.rest.refundpolicy;

import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.ChangeRefundPolicyStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.RegisterRefundPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.UpdateRefundPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.query.SearchRefundPoliciesPageApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.NonReturnableConditionApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.RefundPolicyApiResponse;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.NonReturnableConditionResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyPageResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

/**
 * RefundPolicy API 테스트 Fixtures.
 *
 * <p>RefundPolicy REST API 테스트에서 사용하는 요청/응답 객체를 생성합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public final class RefundPolicyApiFixtures {

    private RefundPolicyApiFixtures() {}

    // ===== 상수 =====
    public static final Long DEFAULT_SELLER_ID = 1L;
    public static final Long DEFAULT_POLICY_ID = 1L;
    public static final String DEFAULT_POLICY_NAME = "기본 환불정책";
    public static final boolean DEFAULT_IS_DEFAULT = true;
    public static final int DEFAULT_RETURN_PERIOD_DAYS = 7;
    public static final int DEFAULT_EXCHANGE_PERIOD_DAYS = 7;
    public static final boolean DEFAULT_PARTIAL_REFUND_ENABLED = true;
    public static final boolean DEFAULT_INSPECTION_REQUIRED = true;
    public static final int DEFAULT_INSPECTION_PERIOD_DAYS = 3;
    public static final String DEFAULT_ADDITIONAL_INFO = "교환/반품 시 상품 택이 제거되지 않은 상태여야 합니다.";
    public static final List<String> DEFAULT_CONDITIONS =
            List.of("OPENED_PACKAGING", "USED_PRODUCT", "MISSING_TAG");
    public static final Instant DEFAULT_INSTANT = Instant.parse("2025-01-23T01:30:00Z");
    public static final String DEFAULT_ISO_DATE = "2025-01-23T10:30:00+09:00";

    // ===== RegisterRefundPolicyApiRequest =====

    public static RegisterRefundPolicyApiRequest registerRequest() {
        return new RegisterRefundPolicyApiRequest(
                DEFAULT_POLICY_NAME,
                DEFAULT_IS_DEFAULT,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                DEFAULT_CONDITIONS,
                DEFAULT_PARTIAL_REFUND_ENABLED,
                DEFAULT_INSPECTION_REQUIRED,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                DEFAULT_ADDITIONAL_INFO);
    }

    public static RegisterRefundPolicyApiRequest registerRequestWithNullConditions() {
        return new RegisterRefundPolicyApiRequest(
                DEFAULT_POLICY_NAME,
                DEFAULT_IS_DEFAULT,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                null,
                DEFAULT_PARTIAL_REFUND_ENABLED,
                DEFAULT_INSPECTION_REQUIRED,
                DEFAULT_INSPECTION_PERIOD_DAYS,
                DEFAULT_ADDITIONAL_INFO);
    }

    // ===== UpdateRefundPolicyApiRequest =====

    public static UpdateRefundPolicyApiRequest updateRequest() {
        return new UpdateRefundPolicyApiRequest(
                "수정된 환불정책",
                false,
                14,
                14,
                List.of("OPENED_PACKAGING", "TIME_EXPIRED"),
                false,
                false,
                0,
                "수정된 안내 문구입니다.");
    }

    public static UpdateRefundPolicyApiRequest updateRequestWithNullConditions() {
        return new UpdateRefundPolicyApiRequest(
                "수정된 환불정책", false, 14, 14, null, false, false, 0, "수정된 안내 문구입니다.");
    }

    // ===== ChangeRefundPolicyStatusApiRequest =====

    public static ChangeRefundPolicyStatusApiRequest changeStatusRequest() {
        return new ChangeRefundPolicyStatusApiRequest(List.of(1L, 2L, 3L), false);
    }

    // ===== SearchRefundPoliciesPageApiRequest =====

    public static SearchRefundPoliciesPageApiRequest searchRequest() {
        return new SearchRefundPoliciesPageApiRequest(null, null, null, null);
    }

    public static SearchRefundPoliciesPageApiRequest searchRequest(
            String sortKey, String sortDirection, int page, int size) {
        return new SearchRefundPoliciesPageApiRequest(sortKey, sortDirection, page, size);
    }

    // ===== RefundPolicyResult (Application) =====

    public static RefundPolicyResult policyResult(Long policyId) {
        return new RefundPolicyResult(
                policyId,
                DEFAULT_POLICY_NAME,
                DEFAULT_IS_DEFAULT,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                defaultConditionResults(),
                DEFAULT_INSTANT);
    }

    public static RefundPolicyResult policyResult(
            Long policyId, String policyName, boolean active) {
        return new RefundPolicyResult(
                policyId,
                policyName,
                DEFAULT_IS_DEFAULT,
                active,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                defaultConditionResults(),
                DEFAULT_INSTANT);
    }

    public static RefundPolicyResult policyResultWithNullConditions(Long policyId) {
        return new RefundPolicyResult(
                policyId,
                DEFAULT_POLICY_NAME,
                DEFAULT_IS_DEFAULT,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                null,
                DEFAULT_INSTANT);
    }

    public static RefundPolicyResult policyResultWithEmptyConditions(Long policyId) {
        return new RefundPolicyResult(
                policyId,
                DEFAULT_POLICY_NAME,
                DEFAULT_IS_DEFAULT,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                List.of(),
                DEFAULT_INSTANT);
    }

    public static List<NonReturnableConditionResult> defaultConditionResults() {
        return List.of(
                new NonReturnableConditionResult("OPENED_PACKAGING", "포장 개봉"),
                new NonReturnableConditionResult("USED_PRODUCT", "사용 흔적"),
                new NonReturnableConditionResult("MISSING_TAG", "택 분리"));
    }

    public static List<RefundPolicyResult> policyResults(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> policyResult((long) i, "정책_" + i, true))
                .toList();
    }

    public static RefundPolicyPageResult pageResult(int count, int page, int size) {
        List<RefundPolicyResult> results = policyResults(count);
        return RefundPolicyPageResult.of(results, page, size, count);
    }

    public static RefundPolicyPageResult emptyPageResult() {
        return RefundPolicyPageResult.empty(20);
    }

    // ===== RefundPolicyApiResponse =====

    public static RefundPolicyApiResponse apiResponse(Long policyId) {
        return new RefundPolicyApiResponse(
                policyId,
                DEFAULT_POLICY_NAME,
                DEFAULT_IS_DEFAULT,
                true,
                DEFAULT_RETURN_PERIOD_DAYS,
                DEFAULT_EXCHANGE_PERIOD_DAYS,
                List.of(
                        new NonReturnableConditionApiResponse("OPENED_PACKAGING", "포장 개봉"),
                        new NonReturnableConditionApiResponse("USED_PRODUCT", "사용 흔적"),
                        new NonReturnableConditionApiResponse("MISSING_TAG", "택 분리")),
                DEFAULT_ISO_DATE);
    }

    public static List<RefundPolicyApiResponse> apiResponses(int count) {
        return IntStream.rangeClosed(1, count).mapToObj(i -> apiResponse((long) i)).toList();
    }
}
