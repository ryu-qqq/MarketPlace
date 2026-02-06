package com.ryuqq.marketplace.application.refundpolicy.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/**
 * 환불정책 페이징 조회 결과 DTO.
 *
 * <p>페이징 조회 결과와 메타 정보를 함께 반환합니다.
 *
 * @param results 조회 결과 목록
 * @param pageMeta 페이징 메타 정보
 */
public record RefundPolicyPageResult(List<RefundPolicyResult> results, PageMeta pageMeta) {

    public static RefundPolicyPageResult of(List<RefundPolicyResult> results, PageMeta pageMeta) {
        return new RefundPolicyPageResult(results, pageMeta);
    }

    public static RefundPolicyPageResult of(
            List<RefundPolicyResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new RefundPolicyPageResult(results, pageMeta);
    }

    public static RefundPolicyPageResult empty(int size) {
        return new RefundPolicyPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
