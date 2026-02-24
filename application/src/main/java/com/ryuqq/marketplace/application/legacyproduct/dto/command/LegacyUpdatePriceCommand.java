package com.ryuqq.marketplace.application.legacyproduct.dto.command;

/** 레거시 상품 가격 수정 Command. */
public record LegacyUpdatePriceCommand(
        long setofProductGroupId, int regularPrice, int currentPrice) {}
