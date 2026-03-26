package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreatePriceRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

/** 세토프 ProductGroupDetails 호환 요청 DTO. */
public record LegacyProductGroupDetailsRequest(
        @Length(max = 200, message = "상품 그룹명은 200자를 초과할 수 없습니다.") String productGroupName,
        @NotBlank(message = "옵션 타입은 필수입니다.") String optionType,
        @NotBlank(message = "관리 타입은 필수입니다.") String managementType,
        @Valid @NotNull(message = "가격 정보는 필수입니다.") LegacyCreatePriceRequest price,
        @Valid @NotNull(message = "상품 상태 정보는 필수입니다.")
                LegacyCreateProductStatusRequest productStatus,
        @Valid @NotNull(message = "의류 상세정보는 필수입니다.")
                LegacyCreateClothesDetailRequest clothesDetailInfo,
        @Positive(message = "셀러 ID는 0보다 커야 합니다.") long sellerId,
        @Positive(message = "카테고리 ID는 0보다 커야 합니다.") long categoryId,
        @Positive(message = "브랜드 ID는 0보다 커야 합니다.") long brandId) {}
