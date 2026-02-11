package com.ryuqq.marketplace.application.categorypreset.validator;

import com.ryuqq.marketplace.application.category.manager.CategoryReadManager;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetReadManager;
import com.ryuqq.marketplace.application.saleschannelcategory.manager.SalesChannelCategoryReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetChannelMismatchException;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetInternalCategoryNotFoundException;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetNotFoundException;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetSalesChannelCategoryNotFoundException;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final CategoryReadManager categoryReadManager;

    public CategoryPresetValidator(
            CategoryPresetReadManager readManager,
            ShopReadManager shopReadManager,
            SalesChannelCategoryReadManager salesChannelCategoryReadManager,
            CategoryReadManager categoryReadManager) {
        this.readManager = readManager;
        this.shopReadManager = shopReadManager;
        this.salesChannelCategoryReadManager = salesChannelCategoryReadManager;
        this.categoryReadManager = categoryReadManager;
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
     * Shop의 판매채널에서 카테고리 코드로 SalesChannelCategoryId를 조회 및 검증.
     *
     * @param shopId Shop ID
     * @param categoryCode 카테고리 코드
     * @return SalesChannelCategory ID
     * @throws CategoryPresetSalesChannelCategoryNotFoundException 카테고리 코드를 찾을 수 없는 경우
     * @throws CategoryPresetChannelMismatchException 판매채널이 불일치하는 경우
     */
    public Long resolveSalesChannelCategoryId(Long shopId, String categoryCode) {
        Shop shop = shopReadManager.getById(ShopId.of(shopId));
        Long salesChannelCategoryId =
                readManager
                        .findSalesChannelCategoryIdByCode(shop.salesChannelId(), categoryCode)
                        .orElseThrow(
                                () ->
                                        new CategoryPresetSalesChannelCategoryNotFoundException(
                                                categoryCode));
        validateSameChannel(shop, salesChannelCategoryId);
        return salesChannelCategoryId;
    }

    /**
     * Shop과 SalesChannelCategory의 판매채널이 동일한지 검증.
     *
     * @param shopId Shop ID
     * @param salesChannelCategoryId SalesChannelCategory ID
     * @throws CategoryPresetChannelMismatchException 판매채널이 일치하지 않는 경우
     */
    public void validateSameChannel(Long shopId, Long salesChannelCategoryId) {
        Shop shop = shopReadManager.getById(ShopId.of(shopId));
        validateSameChannel(shop, salesChannelCategoryId);
    }

    /**
     * 요청한 내부 카테고리 ID 목록이 모두 존재하는지 검증.
     *
     * @param internalCategoryIds 내부 카테고리 ID 목록
     * @throws CategoryPresetInternalCategoryNotFoundException 존재하지 않는 카테고리가 있는 경우
     */
    public void validateInternalCategoriesExist(List<Long> internalCategoryIds) {
        if (internalCategoryIds == null || internalCategoryIds.isEmpty()) {
            return;
        }
        List<Category> foundCategories = categoryReadManager.findAllByIds(internalCategoryIds);
        if (foundCategories.size() != internalCategoryIds.size()) {
            Set<Long> foundIds =
                    foundCategories.stream().map(Category::idValue).collect(Collectors.toSet());
            List<Long> missingIds =
                    internalCategoryIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new CategoryPresetInternalCategoryNotFoundException(missingIds);
        }
    }

    private void validateSameChannel(Shop shop, Long salesChannelCategoryId) {
        SalesChannelCategory salesChannelCategory =
                salesChannelCategoryReadManager.getById(
                        SalesChannelCategoryId.of(salesChannelCategoryId));

        if (!shop.salesChannelId().equals(salesChannelCategory.salesChannelId())) {
            throw new CategoryPresetChannelMismatchException(
                    shop.salesChannelId(), salesChannelCategory.salesChannelId());
        }
    }
}
