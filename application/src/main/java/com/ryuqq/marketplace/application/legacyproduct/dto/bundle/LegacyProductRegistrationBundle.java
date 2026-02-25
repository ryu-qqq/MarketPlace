package com.ryuqq.marketplace.application.legacyproduct.dto.bundle;

import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import java.util.List;

/**
 * 레거시 상품그룹 등록 번들.
 *
 * <p>LegacyRegisterProductGroupCommand에서 변환된 도메인 객체와 경량 엔트리를 묶어 코디네이터로 전달합니다. productGroupId가 결정되지
 * 않은 이미지와 SKU는 경량 엔트리로 보관하며, 코디네이터에서 도메인 객체로 변환됩니다.
 */
public record LegacyProductRegistrationBundle(
        LegacyProductGroup productGroup, List<ImageEntry> images, List<SkuEntry> skus) {

    public LegacyProductRegistrationBundle {
        images = List.copyOf(images);
        skus = List.copyOf(skus);
    }

    /** 이미지 경량 엔트리. productGroupId는 코디네이터에서 바인딩. */
    public record ImageEntry(String imageType, String imageUrl, String originUrl) {}

    /** 단일 SKU(상품) 등록 엔트리. */
    public record SkuEntry(
            String soldOutYn,
            String displayYn,
            int stockQuantity,
            List<OptionEntry> optionEntries) {

        public SkuEntry {
            optionEntries = List.copyOf(optionEntries);
        }
    }

    /** 옵션 경량 엔트리. optionGroupId/optionDetailId는 OptionResolver에서 해결. */
    public record OptionEntry(String optionName, String optionValue, long additionalPrice) {}
}
