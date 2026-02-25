package com.ryuqq.marketplace.application.inboundproduct.dto.payload;

import java.util.List;

/**
 * MUSTIT 크롤링 rawPayloadJson 역직렬화용 페이로드.
 *
 * <p>MUSTIT 상품 페이지에서 크롤링한 데이터를 표현합니다. 알 수 없는 필드가 포함되면 역직렬화 실패하여 크롤러 데이터 변경을 조기에 감지합니다.
 */
public record MustitInboundPayload(
        String itemName,
        String brandName,
        String categoryCode,
        String categoryName,
        int regularPrice,
        int currentPrice,
        int discountRate,
        String itemStatus,
        String originCountry,
        String descriptionHtml,
        boolean freeShipping,
        ProductImageListPayload images,
        ProductOptionListPayload options,
        ProductShippingPayload shipping) {

    public record ProductImageListPayload(
            List<ProductImagePayload> thumbnails, List<ProductImagePayload> descriptionImages) {

        public ProductImageListPayload {
            thumbnails = thumbnails != null ? List.copyOf(thumbnails) : List.of();
            descriptionImages =
                    descriptionImages != null ? List.copyOf(descriptionImages) : List.of();
        }
    }

    public record ProductImagePayload(String url, String imageType, int displayOrder) {}

    public record ProductOptionListPayload(List<ProductOptionPayload> options, int totalStock) {

        public ProductOptionListPayload {
            options = options != null ? List.copyOf(options) : List.of();
        }
    }

    public record ProductOptionPayload(
            long optionNo, String color, String size, int stock, String sizeGuide) {}

    public record ProductShippingPayload(
            String shippingType,
            int shippingFee,
            String shippingFeeType,
            int averageDeliveryDays,
            boolean freeShipping) {}
}
