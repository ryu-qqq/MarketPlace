package com.ryuqq.marketplace.application.legacy.productcontext.internal;

import com.ryuqq.marketplace.application.legacy.productcontext.dto.command.ResolveLegacyProductContextCommand;
import com.ryuqq.marketplace.application.legacy.productcontext.dto.result.LegacyProductContext;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyBrandIdResolver;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyCategoryIdResolver;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyDefaultPolicyResolver;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyNoticeCategoryResolver;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacySellerIdResolver;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 컨텍스트 ReadFacade.
 *
 * <p>여러 리졸버를 조합하여 LegacyProductContext를 생성합니다.
 */
@Component
public class LegacyProductContextReadFacade {

    private final LegacySellerIdResolver sellerIdResolver;
    private final LegacyBrandIdResolver brandIdResolver;
    private final LegacyCategoryIdResolver categoryIdResolver;
    private final LegacyDefaultPolicyResolver defaultPolicyResolver;
    private final LegacyNoticeCategoryResolver noticeCategoryResolver;

    public LegacyProductContextReadFacade(
            LegacySellerIdResolver sellerIdResolver,
            LegacyBrandIdResolver brandIdResolver,
            LegacyCategoryIdResolver categoryIdResolver,
            LegacyDefaultPolicyResolver defaultPolicyResolver,
            LegacyNoticeCategoryResolver noticeCategoryResolver) {
        this.sellerIdResolver = sellerIdResolver;
        this.brandIdResolver = brandIdResolver;
        this.categoryIdResolver = categoryIdResolver;
        this.defaultPolicyResolver = defaultPolicyResolver;
        this.noticeCategoryResolver = noticeCategoryResolver;
    }

    public LegacyProductContext resolve(ResolveLegacyProductContextCommand command) {
        long internalSellerId = sellerIdResolver.resolve(command.legacySellerId());
        long internalBrandId = brandIdResolver.resolve(command.legacyBrandId());
        long internalCategoryId = categoryIdResolver.resolve(command.legacyCategoryId());

        long shippingPolicyId =
                defaultPolicyResolver.resolveShippingPolicyId(
                        internalSellerId, command.deliveryData());
        long refundPolicyId =
                defaultPolicyResolver.resolveRefundPolicyId(
                        internalSellerId, command.refundData());

        NoticeCategory noticeCategory = noticeCategoryResolver.resolve(internalCategoryId);

        return new LegacyProductContext(
                internalSellerId,
                internalBrandId,
                internalCategoryId,
                shippingPolicyId,
                refundPolicyId,
                noticeCategory);
    }
}
