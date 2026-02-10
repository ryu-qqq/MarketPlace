package com.ryuqq.marketplace.application.saleschannelbrand.dto.command;

/** 외부채널 브랜드 등록 커맨드 DTO. */
public record RegisterSalesChannelBrandCommand(
        Long salesChannelId, String externalBrandCode, String externalBrandName) {}
