package com.ryuqq.marketplace.application.inboundproduct.dto.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.math.BigDecimal;
import java.util.List;

/**
 * 세토프(레거시) rawPayloadJson 역직렬화용 페이로드.
 *
 * <p>adapter-in의 LegacyCreateProductGroupRequest를 직접 참조하면 hexagonal 원칙 위반이므로 application 계층에 파싱 전용
 * record를 정의합니다.
 */
@SuppressFBWarnings(
        value = "EI_EXPOSE_REP",
        justification = "List fields are defensively copied via List.copyOf in compact constructor")
@JsonIgnoreProperties(ignoreUnknown = true)
public record LegacyInboundPayload(
        Long productGroupId,
        String productGroupName,
        long sellerId,
        String optionType,
        LegacyPayloadPrice price,
        LegacyPayloadNotice productNotice,
        List<LegacyPayloadImage> productImageList,
        String detailDescription,
        List<LegacyPayloadOption> productOptions) {

    public LegacyInboundPayload {
        productImageList = productImageList != null ? List.copyOf(productImageList) : List.of();
        productOptions = productOptions != null ? List.copyOf(productOptions) : List.of();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LegacyPayloadPrice(long regularPrice, long currentPrice) {}

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
    public record LegacyPayloadImage(String productImageType, String imageUrl, String originUrl) {}

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
}
