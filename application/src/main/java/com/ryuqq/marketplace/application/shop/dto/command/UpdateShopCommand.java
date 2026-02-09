package com.ryuqq.marketplace.application.shop.dto.command;

/** Shop 수정 커맨드 DTO. */
public record UpdateShopCommand(Long shopId, String shopName, String accountId, String status) {}
