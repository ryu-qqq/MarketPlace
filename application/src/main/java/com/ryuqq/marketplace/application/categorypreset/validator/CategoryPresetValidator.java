package com.ryuqq.marketplace.application.categorypreset.validator;

import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetReadManager;
import com.ryuqq.marketplace.application.saleschannelcategory.manager.SalesChannelCategoryReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetErrorCode;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetException;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetNotFoundException;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import org.springframework.stereotype.Component;

/**
 * CategoryPreset Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class CategoryPresetValidator {

    private final CategoryPresetReadManager readManager;
    private final ShopReadManager shopReadManager;
    private final SalesChannelCategoryReadManager salesChannelCategoryReadManager;

    public CategoryPresetValidator(
            CategoryPresetReadManager readManager,
            ShopReadManager shopReadManager,
            SalesChannelCategoryReadManager salesChannelCategoryReadManager) {
        this.readManager = readManager;
        this.shopReadManager = shopReadManager;
        this.salesChannelCategoryReadManager = salesChannelCategoryReadManager;
    }

    /**
     * 카테고리 프리셋 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 카테고리 프리셋 ID
     * @return CategoryPreset 도메인 객체
     * @throws CategoryPresetNotFoundException 존재하지 않는 경우
     */
    public CategoryPreset findExistingOrThrow(CategoryPresetId id) {
        return readManager.getById(id);
    }

    /**
     * Shop과 SalesChannelCategory의 판매채널이 동일한지 검증.
     *
     * @param shopId Shop ID
     * @param salesChannelCategoryId SalesChannelCategory ID
     * @throws CategoryPresetException 판매채널이 일치하지 않는 경우
     */
    public void validateSameChannel(Long shopId, Long salesChannelCategoryId) {
        Shop shop = shopReadManager.getById(ShopId.of(shopId));
        SalesChannelCategory salesChannelCategory =
                salesChannelCategoryReadManager.getById(
                        SalesChannelCategoryId.of(salesChannelCategoryId));

        if (!shop.salesChannelId().equals(salesChannelCategory.salesChannelId())) {
            throw new CategoryPresetException(
                    CategoryPresetErrorCode.CATEGORY_PRESET_CHANNEL_MISMATCH,
                    String.format(
                            "Shop(id=%d)의 salesChannelId(%d)와 SalesChannelCategory(id=%d)의"
                                    + " salesChannelId(%d)가 일치하지 않습니다",
                            shopId,
                            shop.salesChannelId(),
                            salesChannelCategoryId,
                            salesChannelCategory.salesChannelId()));
        }
    }
}
