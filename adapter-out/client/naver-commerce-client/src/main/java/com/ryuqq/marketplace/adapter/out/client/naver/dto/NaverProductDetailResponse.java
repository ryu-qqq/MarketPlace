package com.ryuqq.marketplace.adapter.out.client.naver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * 네이버 커머스 원상품 조회 응답 DTO.
 *
 * <p>GET /v2/products/origin-products/{originProductNo} 응답에서 옵션 조합 정보(combination id)를 확보하기 위한 최소
 * 구조.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverProductDetailResponse(OriginProduct originProduct) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OriginProduct(
            String statusType,
            String name,
            int salePrice,
            int stockQuantity,
            DetailAttribute detailAttribute) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DetailAttribute(OptionInfo optionInfo) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OptionInfo(
            String optionCombinationSortType,
            OptionCombinationGroupNames optionCombinationGroupNames,
            List<OptionCombination> optionCombinations) {

        /** 방어적 복사. */
        public OptionInfo {
            optionCombinations =
                    optionCombinations == null ? null : List.copyOf(optionCombinations);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OptionCombinationGroupNames(
            String optionGroupName1,
            String optionGroupName2,
            String optionGroupName3,
            String optionGroupName4) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OptionCombination(
            Long id,
            String optionName1,
            String optionName2,
            String optionName3,
            int stockQuantity,
            int price,
            String sellerManagerCode,
            boolean usable) {}
}
