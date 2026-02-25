package com.ryuqq.marketplace.domain.legacy.productgroup.vo;

/** 레거시(세토프) 상품 고시정보 Value Object. */
public record LegacyProductNotice(
        String material,
        String color,
        String size,
        String maker,
        String origin,
        String washingMethod,
        String yearMonthDay,
        String assuranceStandard,
        String asPhone) {}
