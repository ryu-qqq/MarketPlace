package com.ryuqq.marketplace.application.refundpolicy;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.refundpolicy.dto.query.RefundPolicySearchParams;

/**
 * RefundPolicy Query 테스트 Fixtures.
 *
 * <p>RefundPolicy 관련 Query 객체들을 생성하는 테스트 유틸리티입니다.
 */
public final class RefundPolicyQueryFixtures {

    private RefundPolicyQueryFixtures() {}

    // ===== RefundPolicySearchParams =====

    public static RefundPolicySearchParams searchParams(Long sellerId) {
        return RefundPolicySearchParams.of(sellerId, defaultCommonSearchParams());
    }

    public static RefundPolicySearchParams searchParams(Long sellerId, int page, int size) {
        return RefundPolicySearchParams.of(sellerId, commonSearchParams(page, size));
    }

    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }

    public static CommonSearchParams commonSearchParams(int page, int size) {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
    }

    public static CommonSearchParams commonSearchParamsIncludeDeleted() {
        return CommonSearchParams.of(true, null, null, "createdAt", "DESC", 0, 20);
    }
}
