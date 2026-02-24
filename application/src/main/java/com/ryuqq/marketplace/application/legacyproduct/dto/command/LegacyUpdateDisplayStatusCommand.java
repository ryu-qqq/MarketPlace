package com.ryuqq.marketplace.application.legacyproduct.dto.command;

/** 레거시 상품 전시 상태 변경 Command. */
public record LegacyUpdateDisplayStatusCommand(long setofProductGroupId, String displayYn) {}
