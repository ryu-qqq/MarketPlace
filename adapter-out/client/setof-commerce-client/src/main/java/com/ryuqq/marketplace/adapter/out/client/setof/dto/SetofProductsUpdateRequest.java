package com.ryuqq.marketplace.adapter.out.client.setof.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 세토프 커머스 상품 + 옵션 일괄 수정 요청 DTO.
 *
 * <p>PATCH /api/v2/admin/products/product-groups/{productGroupId} 요청 본문.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SetofProductsUpdateRequest(
        List<OptionGroupRequest> optionGroups, List<ProductRequest> products) {

    /** 방어적 복사. */
    public SetofProductsUpdateRequest {
        optionGroups = optionGroups == null ? null : List.copyOf(optionGroups);
        products = products == null ? null : List.copyOf(products);
    }

    /** 옵션 그룹 요청. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OptionGroupRequest(
            Long sellerOptionGroupId,
            String optionGroupName,
            Integer sortOrder,
            List<OptionValueRequest> optionValues) {

        /** 방어적 복사. */
        public OptionGroupRequest {
            optionValues = optionValues == null ? null : List.copyOf(optionValues);
        }
    }

    /** 옵션 값 요청. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OptionValueRequest(
            Long sellerOptionValueId, String optionValueName, Integer sortOrder) {}

    /** 상품 요청. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProductRequest(
            Long productId,
            String skuCode,
            Integer regularPrice,
            Integer currentPrice,
            Integer stockQuantity,
            Integer sortOrder,
            List<SelectedOptionRequest> selectedOptions) {

        /** 방어적 복사. */
        public ProductRequest {
            selectedOptions = selectedOptions == null ? null : List.copyOf(selectedOptions);
        }
    }

    /** 선택된 옵션 요청. */
    public record SelectedOptionRequest(String optionGroupName, String optionValueName) {}
}
