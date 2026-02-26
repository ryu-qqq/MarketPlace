package com.ryuqq.marketplace.application.refundpolicy.port.in.query;

import com.ryuqq.marketplace.application.refundpolicy.dto.query.RefundPolicySearchParams;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyPageResult;

/**
 * 환불정책 검색 UseCase.
 *
 * <p>APP-ASM-001: RefundPolicyPageResult로 페이징 결과 반환
 */
public interface SearchRefundPolicyUseCase {

    RefundPolicyPageResult execute(RefundPolicySearchParams params);
}
