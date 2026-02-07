package com.ryuqq.marketplace.application.shippingpolicy.port.in.query;

import com.ryuqq.marketplace.application.shippingpolicy.dto.query.ShippingPolicySearchParams;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyPageResult;

/**
 * 배송정책 검색 UseCase.
 *
 * <p>APP-ASM-001: ShippingPolicyPageResult로 페이징 결과 반환
 */
public interface SearchShippingPolicyUseCase {

    ShippingPolicyPageResult execute(ShippingPolicySearchParams params);
}
