package com.ryuqq.marketplace.application.product.dto.response;

/**
 * 상품에 매핑된 옵션 값의 resolved 결과 DTO.
 *
 * <p>상세 조회에서 상품(SKU)마다 어떤 옵션 그룹의 어떤 옵션 값에 매핑되었는지를 이름까지 포함하여 보여줍니다.
 *
 * @param sellerOptionGroupId 옵션 그룹 ID
 * @param optionGroupName 옵션 그룹명 (예: "색상", "사이즈")
 * @param sellerOptionValueId 옵션 값 ID
 * @param optionValueName 옵션 값명 (예: "블랙", "L")
 */
public record ResolvedProductOptionResult(
        Long sellerOptionGroupId,
        String optionGroupName,
        Long sellerOptionValueId,
        String optionValueName) {}
