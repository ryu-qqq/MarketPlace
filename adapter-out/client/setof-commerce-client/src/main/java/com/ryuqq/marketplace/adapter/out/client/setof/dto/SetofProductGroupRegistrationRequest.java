package com.ryuqq.marketplace.adapter.out.client.setof.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 * 세토프 커머스 상품 그룹 등록 요청 DTO.
 *
 * <p>POST /api/v2/admin/product-groups 요청 본문. 세토프 커머스의 RegisterProductGroupApiRequest 스펙에 맞는 중첩
 * record 구조입니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SetofProductGroupRegistrationRequest(
        Long sellerId,
        Long brandId,
        Long categoryId,
        Long shippingPolicyId,
        Long refundPolicyId,
        String productGroupName,
        String optionType,
        List<ImageRequest> images,
        List<OptionGroupRequest> optionGroups,
        List<ProductRequest> products,
        DescriptionRequest description,
        NoticeRequest notice) {

    /** 방어적 복사. */
    public SetofProductGroupRegistrationRequest {
        images = images == null ? null : List.copyOf(images);
        optionGroups = optionGroups == null ? null : List.copyOf(optionGroups);
        products = products == null ? null : List.copyOf(products);
    }

    /** 이미지 요청. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ImageRequest(String imageType, String originUrl, int sortOrder) {}

    /** 옵션 그룹 요청. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OptionGroupRequest(
            String optionGroupName,
            Long canonicalOptionGroupId,
            String inputType,
            List<OptionValueRequest> optionValues) {

        /** 방어적 복사. */
        public OptionGroupRequest {
            optionValues = optionValues == null ? null : List.copyOf(optionValues);
        }
    }

    /** 옵션 값 요청. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record OptionValueRequest(
            String optionValueName, Long canonicalOptionValueId, int sortOrder) {}

    /** 상품 요청. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ProductRequest(
            String skuCode,
            int regularPrice,
            int currentPrice,
            int stockQuantity,
            int sortOrder,
            List<SelectedOptionRequest> selectedOptions) {

        /** 방어적 복사. */
        public ProductRequest {
            selectedOptions = selectedOptions == null ? null : List.copyOf(selectedOptions);
        }
    }

    /** 선택된 옵션 요청. */
    public record SelectedOptionRequest(String optionGroupName, String optionValueName) {}

    /** 상세 설명 요청. */
    public record DescriptionRequest(String content) {}

    /** 고시정보 요청. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record NoticeRequest(Long noticeCategoryId, List<NoticeEntryRequest> entries) {

        /** 방어적 복사. */
        public NoticeRequest {
            entries = entries == null ? null : List.copyOf(entries);
        }
    }

    /** 고시정보 항목 요청. */
    public record NoticeEntryRequest(Long noticeFieldId, String fieldValue) {}
}
