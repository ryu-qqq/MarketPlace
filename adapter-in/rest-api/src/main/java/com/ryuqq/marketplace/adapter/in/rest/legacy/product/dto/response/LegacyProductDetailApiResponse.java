package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response;

import com.ryuqq.marketplace.application.product.dto.response.ProductDetailResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeEntryResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import java.util.List;
import java.util.Map;

/**
 * 세토프 레거시 상품 상세 조회 응답 DTO.
 *
 * <p>세토프 OMS가 기대하는 응답 구조를 유지하며, productGroupId는 세토프 PK를 반환합니다.
 */
public record LegacyProductDetailApiResponse(
        long productGroupId,
        String productGroupName,
        long sellerId,
        String optionType,
        long categoryId,
        long brandId,
        String status,
        LegacyPriceResponse price,
        LegacyProductNoticeResponse productNotice,
        List<LegacyProductImageResponse> productImageList,
        String detailDescription,
        List<LegacyOptionResponse> productOptions) {

    /**
     * ProductGroupDetailCompositeResult → 세토프 레거시 응답 변환.
     *
     * @param result 상품 그룹 상세 조회 결과
     * @param setofProductGroupId 세토프 PK (응답에 반환)
     * @param noticeFieldIdToCode notice field ID → field code 매핑 (고시정보 역매핑용)
     */
    public static LegacyProductDetailApiResponse from(
            ProductGroupDetailCompositeResult result,
            long setofProductGroupId,
            Map<Long, String> noticeFieldIdToCode) {

        LegacyPriceResponse priceResponse = extractPrice(result);
        LegacyProductNoticeResponse noticeResponse =
                extractNotice(result.productNotice(), noticeFieldIdToCode);
        List<LegacyProductImageResponse> images = extractImages(result.images());
        String description = result.description() != null ? result.description().content() : null;
        List<LegacyOptionResponse> options = extractOptions(result);

        return new LegacyProductDetailApiResponse(
                setofProductGroupId,
                result.productGroupName(),
                result.sellerId(),
                result.optionType(),
                result.categoryId(),
                result.brandId(),
                result.status(),
                priceResponse,
                noticeResponse,
                images,
                description,
                options);
    }

    private static LegacyPriceResponse extractPrice(ProductGroupDetailCompositeResult result) {
        if (result.optionProductMatrix() == null
                || result.optionProductMatrix().products().isEmpty()) {
            return new LegacyPriceResponse(0, 0);
        }
        ProductDetailResult firstProduct = result.optionProductMatrix().products().get(0);
        return new LegacyPriceResponse(firstProduct.regularPrice(), firstProduct.currentPrice());
    }

    private static LegacyProductNoticeResponse extractNotice(
            ProductNoticeResult notice, Map<Long, String> fieldIdToCode) {
        if (notice == null || notice.entries() == null) {
            return new LegacyProductNoticeResponse(
                    null, null, null, null, null, null, null, null, null);
        }

        String material = null, color = null, size = null, maker = null, origin = null;
        String washingMethod = null, yearMonth = null, assuranceStandard = null;

        for (ProductNoticeEntryResult entry : notice.entries()) {
            String fieldCode = fieldIdToCode.get(entry.noticeFieldId());
            String value = entry.fieldValue();
            if (fieldCode == null) {
                continue;
            }

            switch (fieldCode) {
                case "material" -> material = value;
                case "color" -> color = value;
                case "size" -> size = value;
                case "manufacturer" -> maker = value;
                case "made_in" -> origin = value;
                case "wash_care" -> washingMethod = value;
                case "release_date" -> yearMonth = value;
                case "quality_assurance" -> assuranceStandard = value;
                default -> {
                    // unknown field code — skip
                }
            }
        }

        return new LegacyProductNoticeResponse(
                material,
                color,
                size,
                maker,
                origin,
                washingMethod,
                yearMonth,
                assuranceStandard,
                null);
    }

    private static List<LegacyProductImageResponse> extractImages(
            List<ProductGroupImageResult> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream()
                .map(
                        img ->
                                new LegacyProductImageResponse(
                                        img.imageType(),
                                        img.uploadedUrl() != null
                                                ? img.uploadedUrl()
                                                : img.originUrl(),
                                        img.originUrl()))
                .toList();
    }

    private static List<LegacyOptionResponse> extractOptions(
            ProductGroupDetailCompositeResult result) {
        if (result.optionProductMatrix() == null
                || result.optionProductMatrix().products() == null) {
            return List.of();
        }

        return result.optionProductMatrix().products().stream()
                .map(
                        product -> {
                            List<LegacyOptionDetailResponse> optionDetails =
                                    product.options().stream()
                                            .map(
                                                    opt ->
                                                            new LegacyOptionDetailResponse(
                                                                    opt.sellerOptionGroupId(),
                                                                    opt.sellerOptionValueId(),
                                                                    opt.optionGroupName(),
                                                                    opt.optionValueName()))
                                            .toList();

                            return new LegacyOptionResponse(
                                    product.id(), product.stockQuantity(), 0, optionDetails);
                        })
                .toList();
    }

    public record LegacyPriceResponse(int regularPrice, int currentPrice) {}

    public record LegacyProductNoticeResponse(
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonth,
            String assuranceStandard,
            String asPhone) {}

    public record LegacyProductImageResponse(
            String type, String productImageUrl, String originUrl) {}

    public record LegacyOptionResponse(
            Long productId,
            int stockQuantity,
            int additionalPrice,
            List<LegacyOptionDetailResponse> options) {}

    public record LegacyOptionDetailResponse(
            Long optionGroupId, Long optionDetailId, String optionName, String optionValue) {}
}
