package com.ryuqq.marketplace.application.inboundproduct.dto.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.util.List;

/**
 * 세토프(레거시) UpdateProductGroup rawPayloadJson 역직렬화용 페이로드.
 *
 * <p>adapter-in DTO 의존을 피하기 위해 application 계층에 update 파싱 전용 record를 정의합니다.
 */
@SuppressFBWarnings(
        value = "EI_EXPOSE_REP",
        justification = "List fields are defensively copied via List.copyOf in compact constructor")
@JsonIgnoreProperties(ignoreUnknown = true)
public record LegacyInboundUpdatePayload(
        LegacyPayloadNotice productNotice,
        List<LegacyPayloadImage> productImageList,
        LegacyPayloadDescription detailDescription,
        List<LegacyPayloadOption> productOptions,
        LegacyUpdateStatus updateStatus) {

    public LegacyInboundUpdatePayload {
        productImageList = productImageList != null ? List.copyOf(productImageList) : List.of();
        productOptions = productOptions != null ? List.copyOf(productOptions) : List.of();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LegacyPayloadNotice(
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonth,
            String assuranceStandard,
            String asPhone) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LegacyPayloadImage(String type, String productImageUrl, String originUrl) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LegacyPayloadDescription(String detailDescription) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LegacyPayloadOption(
            Long productId,
            Integer quantity,
            BigDecimal additionalPrice,
            List<LegacyPayloadOptionDetail> options) {

        public LegacyPayloadOption {
            options = options != null ? List.copyOf(options) : List.of();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LegacyPayloadOptionDetail(
            Long optionGroupId, Long optionDetailId, String optionName, String optionValue) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LegacyUpdateStatus(
            boolean productStatus,
            boolean noticeStatus,
            boolean imageStatus,
            boolean descriptionStatus,
            boolean stockOptionStatus,
            boolean deliveryStatus,
            boolean refundStatus) {}
}
