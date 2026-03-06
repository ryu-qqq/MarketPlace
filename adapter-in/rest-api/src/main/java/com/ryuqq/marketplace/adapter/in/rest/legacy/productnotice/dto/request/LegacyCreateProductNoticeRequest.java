package com.ryuqq.marketplace.adapter.in.rest.legacy.productnotice.dto.request;

import org.hibernate.validator.constraints.Length;

/** 세토프 CreateProductNotice 호환 요청 DTO. */
public record LegacyCreateProductNoticeRequest(
        @Length(max = 500, message = "상품 고시 정보 값은 500자를 초과할 수 없습니다.") String material,
        @Length(max = 500, message = "상품 고시 정보 값은 500자를 초과할 수 없습니다.") String color,
        @Length(max = 500, message = "상품 고시 정보 값은 500자를 초과할 수 없습니다.") String size,
        @Length(max = 500, message = "상품 고시 정보 값은 500자를 초과할 수 없습니다.") String maker,
        @Length(max = 500, message = "상품 고시 정보 값은 500자를 초과할 수 없습니다.") String origin,
        @Length(max = 500, message = "상품 고시 정보 값은 500자를 초과할 수 없습니다.") String washingMethod,
        @Length(max = 500, message = "상품 고시 정보 값은 500자를 초과할 수 없습니다.") String yearMonth,
        @Length(max = 500, message = "상품 고시 정보 값은 500자를 초과할 수 없습니다.") String assuranceStandard,
        @Length(max = 500, message = "상품 고시 정보 값은 500자를 초과할 수 없습니다.") String asPhone) {}
