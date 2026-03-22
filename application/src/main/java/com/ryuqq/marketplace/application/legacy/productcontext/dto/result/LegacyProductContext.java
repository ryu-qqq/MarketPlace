package com.ryuqq.marketplace.application.legacy.productcontext.dto.result;

import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;

/**
 * 레거시 상품 등록/수정에 필요한 컨텍스트 정보.
 *
 * <p>레거시 PK → 표준 ID 리졸빙 결과 + 부가 정보를 담습니다. 지금은 pass-through(legacyId = internalId), 새 스키마 전환 시 매핑
 * 테이블 조회로 교체됩니다.
 *
 * @param internalSellerId 표준 셀러 ID
 * @param internalBrandId 표준 브랜드 ID
 * @param internalCategoryId 표준 카테고리 ID
 * @param shippingPolicyId 디폴트 배송정책 ID (레거시에서는 0L)
 * @param refundPolicyId 디폴트 환불정책 ID (레거시에서는 0L)
 * @param noticeCategory 고시정보 카테고리 (nullable)
 */
public record LegacyProductContext(
        long internalSellerId,
        long internalBrandId,
        long internalCategoryId,
        long shippingPolicyId,
        long refundPolicyId,
        NoticeCategory noticeCategory) {}
