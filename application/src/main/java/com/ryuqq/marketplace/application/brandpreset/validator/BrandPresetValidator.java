package com.ryuqq.marketplace.application.brandpreset.validator;

import com.ryuqq.marketplace.application.brand.manager.BrandReadManager;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetChannelMismatchException;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetInternalBrandNotFoundException;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetNotFoundException;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetSalesChannelBrandNotFoundException;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * BrandPreset Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class BrandPresetValidator {

    private final BrandPresetReadManager readManager;
    private final ShopReadManager shopReadManager;
    private final BrandReadManager brandReadManager;

    public BrandPresetValidator(
            BrandPresetReadManager readManager,
            ShopReadManager shopReadManager,
            BrandReadManager brandReadManager) {
        this.readManager = readManager;
        this.shopReadManager = shopReadManager;
        this.brandReadManager = brandReadManager;
    }

    /**
     * 브랜드 프리셋 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param id 브랜드 프리셋 ID
     * @return BrandPreset 도메인 객체
     * @throws BrandPresetNotFoundException 존재하지 않는 경우
     */
    public BrandPreset findExistingOrThrow(BrandPresetId id) {
        return readManager.getById(id);
    }

    /**
     * Shop과 SalesChannelBrand의 판매채널 일치 여부 검증.
     *
     * @param shopId Shop ID
     * @param salesChannelBrandId SalesChannelBrand ID
     * @throws BrandPresetSalesChannelBrandNotFoundException 판매채널 브랜드를 찾을 수 없는 경우
     * @throws BrandPresetChannelMismatchException 판매채널이 일치하지 않는 경우
     */
    public void validateSameChannel(Long shopId, Long salesChannelBrandId) {
        Shop shop = shopReadManager.getById(ShopId.of(shopId));
        Long brandSalesChannelId =
                readManager
                        .findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId)
                        .orElseThrow(
                                () ->
                                        new BrandPresetSalesChannelBrandNotFoundException(
                                                salesChannelBrandId));

        if (!shop.salesChannelId().equals(brandSalesChannelId)) {
            throw new BrandPresetChannelMismatchException(
                    shop.salesChannelId(), brandSalesChannelId);
        }
    }

    /**
     * 요청한 내부 브랜드 ID 목록이 모두 존재하는지 검증.
     *
     * @param internalBrandIds 내부 브랜드 ID 목록
     * @throws BrandPresetInternalBrandNotFoundException 존재하지 않는 브랜드가 있는 경우
     */
    public void validateInternalBrandsExist(List<Long> internalBrandIds) {
        if (internalBrandIds == null || internalBrandIds.isEmpty()) {
            return;
        }
        Set<Long> uniqueInternalBrandIds = new java.util.HashSet<>(internalBrandIds);
        List<Brand> foundBrands = brandReadManager.findAllByIds(internalBrandIds);
        if (foundBrands.size() != uniqueInternalBrandIds.size()) {
            Set<Long> foundIds =
                    foundBrands.stream().map(Brand::idValue).collect(Collectors.toSet());
            List<Long> missingIds =
                    uniqueInternalBrandIds.stream()
                            .filter(id -> !foundIds.contains(id))
                            .toList();
            throw new BrandPresetInternalBrandNotFoundException(missingIds);
        }
    }
}
