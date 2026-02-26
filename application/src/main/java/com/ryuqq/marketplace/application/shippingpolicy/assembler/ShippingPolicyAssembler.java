package com.ryuqq.marketplace.application.shippingpolicy.assembler;

import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyPageResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ShippingPolicyAssembler - 배송 정책 Assembler.
 *
 * <p>Domain → Result 변환 및 PageResult 생성을 담당합니다.
 *
 * <p>APP-ASM-001: 도메인별 구체 Result 클래스 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ShippingPolicyAssembler {

    /**
     * Domain → Result 변환.
     *
     * @param domain ShippingPolicy 도메인 객체
     * @return ShippingPolicyResult
     */
    public ShippingPolicyResult toResult(ShippingPolicy domain) {
        return ShippingPolicyResult.from(domain);
    }

    /**
     * Domain List → Result List 변환.
     *
     * @param domains ShippingPolicy 도메인 객체 목록
     * @return ShippingPolicyResult 목록
     */
    public List<ShippingPolicyResult> toResults(List<ShippingPolicy> domains) {
        return domains.stream().map(this::toResult).toList();
    }

    /**
     * Domain List → ShippingPolicyPageResult 생성.
     *
     * <p>Domain 객체를 Result로 변환하여 PageResult를 생성합니다.
     *
     * @param domains 도메인 객체 목록
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return ShippingPolicyPageResult
     */
    public ShippingPolicyPageResult toPageResult(
            List<ShippingPolicy> domains, int page, int size, long totalElements) {
        List<ShippingPolicyResult> results = toResults(domains);
        return ShippingPolicyPageResult.of(results, page, size, totalElements);
    }
}
