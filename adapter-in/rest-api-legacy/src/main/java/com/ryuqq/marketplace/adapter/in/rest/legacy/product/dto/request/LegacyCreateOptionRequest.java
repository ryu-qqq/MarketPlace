package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import org.hibernate.validator.constraints.Length;

/** 세토프 CreateOption 호환 요청 DTO. */
public record LegacyCreateOptionRequest(
        Long productId,
        @NotNull(message = "재고 수량은 필수입니다.")
                @Max(value = 9999, message = "재고 수량은 9999를 초과할 수 없습니다.")
                @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.")
                @JsonProperty("stockQuantity")
                Integer quantity,
        BigDecimal additionalPrice,
        @Valid @Size(max = 2, message = "옵션 항목은 최대 2개까지 허용됩니다.") List<OptionDetail> options) {

    public LegacyCreateOptionRequest {
        options = options == null ? List.of() : List.copyOf(options);
    }

    /** 세토프 CreateOptionDetail 호환 요청 DTO. */
    public record OptionDetail(
            Long optionGroupId,
            Long optionDetailId,
            @NotBlank(message = "옵션명은 필수이며 비어있을 수 없습니다.") String optionName,
            @Length(max = 100, message = "옵션 값은 100자를 초과할 수 없습니다.") String optionValue) {}
}
