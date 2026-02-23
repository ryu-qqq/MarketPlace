package com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

/** 세토프 CreateDeliveryNotice 호환 요청 DTO. */
public record LegacyCreateDeliveryNoticeRequest(
        @NotBlank(message = "배송 가능 지역은 필수입니다.")
                @Length(max = 200, message = "배송 가능 지역은 200자를 초과할 수 없습니다.")
                String deliveryArea,
        @Min(value = 0, message = "배송비는 0 이상이어야 합니다.")
                @Max(value = 100000, message = "배송비는 100000 이하여야 합니다.")
                long deliveryFee,
        @Min(value = 0, message = "평균 배송 소요일은 0일 이상이어야 합니다.")
                @Max(value = 30, message = "평균 배송 소요일은 30일 이하여야 합니다.")
                int deliveryPeriodAverage) {}
