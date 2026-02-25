package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.legacyproduct.dto.bundle.LegacyProductRegistrationBundle.OptionEntry;
import com.ryuqq.marketplace.application.legacyproduct.dto.bundle.LegacyProductRegistrationBundle.SkuEntry;
import com.ryuqq.marketplace.application.legacyproduct.facade.LegacyProductRegistrationFacade;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyOptionResolver.ResolvedOptions;
import com.ryuqq.marketplace.domain.legacy.optiondetail.id.LegacyOptionDetailId;
import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 SKU Coordinator.
 *
 * <p>옵션 해석(OptionResolver) + SKU별 상품/재고/옵션매핑 등록을 담당합니다. SkuEntry → 옵션 ID 매핑 → LegacyProduct 생성 →
 * Facade 저장 흐름을 캡슐화합니다.
 */
@Component
public class LegacySkuCoordinator {

    private final LegacyOptionResolver optionResolver;
    private final LegacyProductRegistrationFacade productFacade;

    public LegacySkuCoordinator(
            LegacyOptionResolver optionResolver, LegacyProductRegistrationFacade productFacade) {
        this.optionResolver = optionResolver;
        this.productFacade = productFacade;
    }

    /** SKU 엔트리 목록 → 옵션 해석 + 상품/재고/옵션매핑 일괄 등록 후 productId 목록 반환. */
    public List<Long> registerSkus(LegacyProductGroupId groupId, List<SkuEntry> skus) {
        ResolvedOptions resolved = optionResolver.resolve(skus);

        List<Long> productIds = new ArrayList<>();
        for (SkuEntry sku : skus) {
            List<LegacyProductOption> options = toProductOptions(sku.optionEntries(), resolved);

            LegacyProduct product =
                    LegacyProduct.forNew(
                            groupId,
                            sku.soldOutYn(),
                            sku.displayYn(),
                            sku.stockQuantity(),
                            options);

            Long productId = productFacade.register(product);
            productIds.add(productId);
        }
        return productIds;
    }

    private List<LegacyProductOption> toProductOptions(
            List<OptionEntry> optionEntries, ResolvedOptions resolved) {
        return optionEntries.stream()
                .map(
                        entry -> {
                            Long optionGroupIdVal = resolved.optionGroupId(entry.optionName());
                            Long optionDetailIdVal =
                                    resolved.optionDetailId(optionGroupIdVal, entry.optionValue());
                            return LegacyProductOption.forNew(
                                    LegacyProductId.forNew(),
                                    LegacyOptionGroupId.of(optionGroupIdVal),
                                    LegacyOptionDetailId.of(optionDetailIdVal),
                                    entry.additionalPrice());
                        })
                .toList();
    }
}
