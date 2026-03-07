package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductMappingResolver;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductGroupCompositeResult;
import com.ryuqq.marketplace.application.notice.resolver.CategoryNoticeResolver;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.exception.DefaultRefundPolicyNotFoundException;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.DefaultShippingPolicyNotFoundException;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 레거시 변환 사전 해소기.
 *
 * <p>레거시 brandId/categoryId를 SETOF 인바운드 소스 매핑을 통해 내부 ID로 변환하고, 셀러의 기본 배송/환불 정책과 고시정보 카테고리를 조회합니다.
 */
@Component
public class LegacyConversionPreResolver {

    private static final long SETOF_SOURCE_ID = 2L;

    private final InboundProductMappingResolver mappingResolver;
    private final ShippingPolicyReadManager shippingPolicyReadManager;
    private final RefundPolicyReadManager refundPolicyReadManager;
    private final CategoryNoticeResolver categoryNoticeResolver;

    public LegacyConversionPreResolver(
            InboundProductMappingResolver mappingResolver,
            ShippingPolicyReadManager shippingPolicyReadManager,
            RefundPolicyReadManager refundPolicyReadManager,
            CategoryNoticeResolver categoryNoticeResolver) {
        this.mappingResolver = mappingResolver;
        this.shippingPolicyReadManager = shippingPolicyReadManager;
        this.refundPolicyReadManager = refundPolicyReadManager;
        this.categoryNoticeResolver = categoryNoticeResolver;
    }

    /**
     * 레거시 상품 그룹 데이터에서 내부 시스템 ID를 해소합니다.
     *
     * @param composite 레거시 상품 그룹 composite
     * @return 해소된 내부 ID 컨텍스트
     * @throws IllegalStateException 브랜드 또는 카테고리 매핑 미발견 시
     * @throws DefaultShippingPolicyNotFoundException 셀러 기본 배송정책 미존재 시
     * @throws DefaultRefundPolicyNotFoundException 셀러 기본 환불정책 미존재 시
     */
    public LegacyConversionResolvedContext resolve(LegacyProductGroupCompositeResult composite) {
        BrandId brandId = resolveBrand(composite.brandId());
        CategoryId categoryId = resolveCategory(composite.categoryId());
        SellerId sellerId = SellerId.of(composite.sellerId());
        ShippingPolicyId shippingPolicyId = resolveShippingPolicy(sellerId);
        RefundPolicyId refundPolicyId = resolveRefundPolicy(sellerId);
        Optional<NoticeCategory> noticeCategory =
                categoryNoticeResolver.resolve(categoryId.value());

        return new LegacyConversionResolvedContext(
                brandId, categoryId, shippingPolicyId, refundPolicyId, noticeCategory);
    }

    private BrandId resolveBrand(long legacyBrandId) {
        return mappingResolver
                .resolveInternalBrandId(SETOF_SOURCE_ID, String.valueOf(legacyBrandId))
                .map(BrandId::of)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "SETOF 브랜드 매핑 미발견: legacyBrandId=" + legacyBrandId));
    }

    private CategoryId resolveCategory(long legacyCategoryId) {
        return mappingResolver
                .resolveInternalCategoryId(SETOF_SOURCE_ID, String.valueOf(legacyCategoryId))
                .map(CategoryId::of)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "SETOF 카테고리 매핑 미발견: legacyCategoryId=" + legacyCategoryId));
    }

    private ShippingPolicyId resolveShippingPolicy(SellerId sellerId) {
        return shippingPolicyReadManager
                .findDefaultBySellerId(sellerId)
                .map(ShippingPolicy::id)
                .orElseThrow(() -> new DefaultShippingPolicyNotFoundException(sellerId.value()));
    }

    private RefundPolicyId resolveRefundPolicy(SellerId sellerId) {
        return refundPolicyReadManager
                .findDefaultBySellerId(sellerId)
                .map(RefundPolicy::id)
                .orElseThrow(() -> new DefaultRefundPolicyNotFoundException(sellerId.value()));
    }
}
