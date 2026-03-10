package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.util.Optional;

/**
 * 레거시 변환 사전 해소 결과.
 *
 * <p>레거시 brandId/categoryId를 내부 ID로 매핑하고, 셀러의 기본 배송/환불 정책과 고시정보 카테고리를 해소한 결과를 담습니다.
 */
public record LegacyConversionResolvedContext(
        SellerId sellerId,
        BrandId brandId,
        CategoryId categoryId,
        ShippingPolicyId shippingPolicyId,
        RefundPolicyId refundPolicyId,
        Optional<NoticeCategory> noticeCategory) {}
