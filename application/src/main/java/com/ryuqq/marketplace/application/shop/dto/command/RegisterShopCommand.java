package com.ryuqq.marketplace.application.shop.dto.command;

/** Shop 등록 커맨드 DTO. */
public record RegisterShopCommand(Long salesChannelId, String shopName, String accountId) {}
