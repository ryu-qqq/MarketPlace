package com.ryuqq.marketplace.application.legacy.shared.dto.bundle;

import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand;
import java.util.List;

/**
 * 레거시 상품그룹 등록 번들.
 *
 * <p>Command에서 변환된 raw 데이터를 묶어 Coordinator로 전달합니다. 도메인 객체 생성은 Coordinator 내부에서 처리됩니다.
 *
 * @param sellerId 셀러 ID
 * @param brandId 브랜드 ID
 * @param categoryId 카테고리 ID
 * @param productGroupName 상품그룹명
 * @param optionType 옵션 타입
 * @param regularPrice 정가 (레거시 전용)
 * @param currentPrice 판매가 (레거시 전용)
 * @param notice 고시정보 (레거시 flat)
 * @param delivery 배송정보 (레거시 전용)
 * @param detailDescription 상세설명 HTML
 * @param images 이미지 엔트리 목록
 * @param skus SKU 엔트리 목록
 */
public record LegacyProductRegistrationBundle(
        long sellerId,
        long brandId,
        long categoryId,
        String productGroupName,
        String optionType,
        long regularPrice,
        long currentPrice,
        LegacyRegisterProductGroupCommand.NoticeCommand notice,
        LegacyRegisterProductGroupCommand.DeliveryCommand delivery,
        String detailDescription,
        List<ImageEntry> images,
        List<SkuEntry> skus) {

    public LegacyProductRegistrationBundle {
        images = List.copyOf(images);
        skus = List.copyOf(skus);
    }

    /** 이미지 경량 엔트리. */
    public record ImageEntry(String imageType, String originUrl) {}

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

    /** 옵션 경량 엔트리. */
    public record OptionEntry(String optionName, String optionValue, long additionalPrice) {}
}
