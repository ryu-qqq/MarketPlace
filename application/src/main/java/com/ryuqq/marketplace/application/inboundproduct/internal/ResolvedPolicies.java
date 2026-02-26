package com.ryuqq.marketplace.application.inboundproduct.internal;

import java.util.List;

/**
 * 인바운드 상품 해석 결과.
 *
 * <p>배송/환불 정책 ID와 고시정보(카테고리 ID + 해석된 엔트리)를 보관합니다.
 */
public record ResolvedPolicies(
        Long shippingPolicyId,
        Long refundPolicyId,
        Long noticeCategoryId,
        List<ResolvedNoticeEntry> resolvedNoticeEntries) {

    public ResolvedPolicies {
        resolvedNoticeEntries =
                resolvedNoticeEntries != null ? List.copyOf(resolvedNoticeEntries) : List.of();
    }

    /** 해석된 고시정보 항목. fieldCode 기반 매칭 후 noticeFieldId가 결정된 상태. */
    public record ResolvedNoticeEntry(long noticeFieldId, String fieldValue) {}
}
