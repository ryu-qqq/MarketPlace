package com.ryuqq.marketplace.application.brandpreset.validator;

import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetErrorCode;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetException;
import com.ryuqq.marketplace.domain.brandpreset.exception.BrandPresetNotFoundException;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
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

    public BrandPresetValidator(
            BrandPresetReadManager readManager, ShopReadManager shopReadManager) {
        this.readManager = readManager;
        this.shopReadManager = shopReadManager;
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
     * @throws BrandPresetException 판매채널이 일치하지 않는 경우
     */
    public void validateSameChannel(Long shopId, Long salesChannelBrandId) {
        Shop shop = shopReadManager.getById(ShopId.of(shopId));
        Long brandSalesChannelId =
                readManager
                        .findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId)
                        .orElseThrow(
                                () ->
                                        new BrandPresetException(
                                                BrandPresetErrorCode.BRAND_PRESET_CHANNEL_MISMATCH,
                                                String.format(
                                                        "SalesChannelBrand를 찾을 수 없습니다 (id: %d)",
                                                        salesChannelBrandId)));

        if (!shop.salesChannelId().equals(brandSalesChannelId)) {
            throw new BrandPresetException(
                    BrandPresetErrorCode.BRAND_PRESET_CHANNEL_MISMATCH,
                    String.format(
                            "Shop(salesChannelId: %d)과 SalesChannelBrand(salesChannelId: %d)의 판매채널이"
                                    + " 일치하지 않습니다",
                            shop.salesChannelId(), brandSalesChannelId));
        }
    }
}
