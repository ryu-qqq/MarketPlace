package com.ryuqq.marketplace.application.shippingpolicy.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/**
 * 배송정책 페이징 조회 결과 DTO.
 *
 * <p>페이징 조회 결과와 메타 정보를 함께 반환합니다.
 *
 * @param results 조회 결과 목록
 * @param pageMeta 페이징 메타 정보
 */
public record ShippingPolicyPageResult(List<ShippingPolicyResult> results, PageMeta pageMeta) {

    public static ShippingPolicyPageResult of(
            List<ShippingPolicyResult> results, PageMeta pageMeta) {
        return new ShippingPolicyPageResult(results, pageMeta);
    }

    public static ShippingPolicyPageResult of(
            List<ShippingPolicyResult> results, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new ShippingPolicyPageResult(results, pageMeta);
    }

    public static ShippingPolicyPageResult empty(int size) {
        return new ShippingPolicyPageResult(List.of(), PageMeta.empty(size));
    }

    public boolean isEmpty() {
        return results == null || results.isEmpty();
    }

    public int size() {
        return results != null ? results.size() : 0;
    }
}
