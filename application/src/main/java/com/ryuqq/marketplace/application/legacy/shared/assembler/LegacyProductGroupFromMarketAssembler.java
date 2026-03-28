package com.ryuqq.marketplace.application.legacy.shared.assembler;

import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyDeliveryResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyImageResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyNoticeResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyOptionMappingResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult.LegacyProductResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductDetailResult;
import com.ryuqq.marketplace.application.product.dto.response.ResolvedProductOptionResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductOptionMatrixResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.ProductGroupDescriptionResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeEntryResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import com.ryuqq.marketplace.domain.legacy.notice.vo.LegacyNoticeFieldMapping;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Market 스키마 조회 결과 → 레거시 응답 결과 Assembler.
 *
 * <p>표준 ProductGroupDetailCompositeResult를 LegacyProductGroupDetailResult로 변환합니다. PK 역매핑(market →
 * legacy)과 고시정보 field_code 기반 복원을 포함합니다.
 */
@Component
public class LegacyProductGroupFromMarketAssembler {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    /** 표준 상세 조회 결과 → 레거시 상세 결과. */
    public LegacyProductGroupDetailResult toDetailResult(
            ProductGroupDetailCompositeResult composite, ResolvedLegacyProductIds resolved) {

        Map<Long, Long> reverseProductMap = resolved.reverseProductIdMap();

        ProductOptionMatrixResult matrix = composite.optionProductMatrix();
        List<ProductDetailResult> products = matrix != null ? matrix.products() : List.of();

        long regularPrice = 0;
        long currentPrice = 0;
        long salePrice = 0;
        int discountRate = 0;
        if (!products.isEmpty()) {
            ProductDetailResult first = products.getFirst();
            regularPrice = first.regularPrice();
            currentPrice = first.currentPrice();
            salePrice = first.salePrice() != null ? first.salePrice() : currentPrice;
            discountRate = first.discountRate();
        }

        return new LegacyProductGroupDetailResult(
                resolved.resolvedProductGroupId().value(),
                composite.productGroupName(),
                composite.sellerId(),
                composite.sellerName(),
                composite.brandId(),
                composite.brandName(),
                composite.categoryId(),
                safe(composite.categoryDisplayPath()),
                toLegacyOptionType(composite.optionType()),
                "",
                regularPrice,
                currentPrice,
                salePrice,
                0L,
                0,
                discountRate,
                "SOLD_OUT".equals(composite.status()),
                "ACTIVE".equals(composite.status()),
                "",
                "",
                "",
                "",
                "",
                toLocalDateTime(composite.createdAt()),
                toLocalDateTime(composite.updatedAt()),
                toNoticeResult(composite.productNotice()),
                toImageResults(composite.images()),
                toDescriptionContent(composite.description()),
                toDeliveryResult(composite.shippingPolicy(), composite.refundPolicy()),
                toProductResults(products, reverseProductMap));
    }

    private String toLegacyOptionType(String optionType) {
        if (optionType == null) return "OPTION_ONE";
        return switch (optionType) {
            case "NONE" -> "SINGLE";
            case "SINGLE" -> "OPTION_ONE";
            case "COMBINATION" -> "OPTION_TWO";
            default -> optionType;
        };
    }

    private LegacyNoticeResult toNoticeResult(ProductNoticeResult notice) {
        if (notice == null || notice.entries() == null) {
            return emptyNotice();
        }

        Map<String, String> fieldMap = new HashMap<>();
        for (ProductNoticeEntryResult entry : notice.entries()) {
            String fieldCode = LegacyNoticeFieldMapping.ID_TO_FIELD_CODE.get(entry.noticeFieldId());
            if (fieldCode != null) {
                fieldMap.put(fieldCode, safe(entry.fieldValue()));
            }
        }

        return new LegacyNoticeResult(
                fieldMap.getOrDefault("material", ""),
                fieldMap.getOrDefault("color", ""),
                fieldMap.getOrDefault("size", ""),
                fieldMap.getOrDefault("maker", ""),
                fieldMap.getOrDefault("origin", ""),
                fieldMap.getOrDefault("washingMethod", ""),
                fieldMap.getOrDefault("yearMonth", ""),
                fieldMap.getOrDefault("assuranceStandard", ""),
                fieldMap.getOrDefault("asPhone", ""));
    }

    private LegacyNoticeResult emptyNotice() {
        return new LegacyNoticeResult("", "", "", "", "", "", "", "", "");
    }

    private List<LegacyImageResult> toImageResults(List<ProductGroupImageResult> images) {
        if (images == null) return List.of();
        return images.stream()
                .map(img -> new LegacyImageResult(img.imageType(), img.uploadedUrl()))
                .toList();
    }

    private String toDescriptionContent(ProductGroupDescriptionResult description) {
        return description != null ? safe(description.content()) : "";
    }

    private LegacyDeliveryResult toDeliveryResult(
            ShippingPolicyResult shipping, RefundPolicyResult refund) {
        return new LegacyDeliveryResult(
                shipping != null ? safe(shipping.deliveryArea()) : "",
                shipping != null ? shipping.deliveryFee() : 0,
                shipping != null ? shipping.deliveryPeriodAverage() : 0,
                refund != null ? safe(refund.returnMethod()) : "",
                refund != null ? safe(refund.returnCourier()) : "",
                refund != null ? refund.returnCharge() : 0,
                refund != null ? safe(refund.returnExchangeArea()) : "");
    }

    private List<LegacyProductResult> toProductResults(
            List<ProductDetailResult> products, Map<Long, Long> reverseProductMap) {
        return products.stream()
                .map(p -> toLegacyProduct(p, reverseProductMap))
                .toList();
    }

    private LegacyProductResult toLegacyProduct(
            ProductDetailResult product, Map<Long, Long> reverseProductMap) {
        long responseProductId = reverseProductMap.getOrDefault(product.id(), product.id());
        boolean soldOut = "SOLD_OUT".equals(product.status());
        List<LegacyOptionMappingResult> options =
                product.options().stream().map(this::toOptionMapping).toList();
        return new LegacyProductResult(responseProductId, product.stockQuantity(), soldOut, options);
    }

    private LegacyOptionMappingResult toOptionMapping(ResolvedProductOptionResult opt) {
        return new LegacyOptionMappingResult(
                opt.sellerOptionGroupId() != null ? opt.sellerOptionGroupId() : 0L,
                opt.sellerOptionValueId() != null ? opt.sellerOptionValueId() : 0L,
                safe(opt.optionGroupName()),
                safe(opt.optionValueName()));
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, SEOUL) : null;
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
