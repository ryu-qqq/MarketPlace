package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

/** 세토프 CreateRefundNotice 호환 요청 DTO. */
public record LegacyCreateRefundNoticeRequest(
        @NotBlank(message = "국내 반품 방법은 필수입니다.") String returnMethodDomestic,
        @NotBlank(message = "국내 반품 택배사는 필수입니다.")
                @Length(max = 30, message = "국내 반품 택배사명은 30자를 초과할 수 없습니다.")
                String returnCourierDomestic,
        @Min(value = 0, message = "국내 반품비는 0 이상이어야 합니다.")
                @Max(value = 100000, message = "국내 반품비는 100000 이하여야 합니다.")
                int returnChargeDomestic,
        @NotBlank(message = "국내 반품/교환지는 필수입니다.")
                @Length(max = 200, message = "국내 반품/교환지는 200자를 초과할 수 없습니다.")
                String returnExchangeAreaDomestic) {}
