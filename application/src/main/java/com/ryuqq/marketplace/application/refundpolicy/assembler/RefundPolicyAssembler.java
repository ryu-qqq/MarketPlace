package com.ryuqq.marketplace.application.refundpolicy.assembler;

import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyPageResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * RefundPolicyAssembler - 환불 정책 Assembler.
 *
 * <p>Domain → Result 변환 및 PageResult 생성을 담당합니다.
 *
 * <p>APP-ASM-001: 도메인별 구체 Result 클래스 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class RefundPolicyAssembler {

    /**
     * Domain → Result 변환.
     *
     * @param domain RefundPolicy 도메인 객체
     * @return RefundPolicyResult
     */
    public RefundPolicyResult toResult(RefundPolicy domain) {
        return RefundPolicyResult.from(domain);
    }

    /**
     * Domain List → Result List 변환.
     *
     * @param domains RefundPolicy 도메인 객체 목록
     * @return RefundPolicyResult 목록
     */
    public List<RefundPolicyResult> toResults(List<RefundPolicy> domains) {
        return domains.stream().map(this::toResult).toList();
    }

    /**
     * Domain List → RefundPolicyPageResult 생성.
     *
     * <p>Domain 객체를 Result로 변환하여 PageResult를 생성합니다.
     *
     * @param domains 도메인 객체 목록
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return RefundPolicyPageResult
     */
    public RefundPolicyPageResult toPageResult(
            List<RefundPolicy> domains, int page, int size, long totalElements) {
        List<RefundPolicyResult> results = toResults(domains);
        return RefundPolicyPageResult.of(results, page, size, totalElements);
    }
}
