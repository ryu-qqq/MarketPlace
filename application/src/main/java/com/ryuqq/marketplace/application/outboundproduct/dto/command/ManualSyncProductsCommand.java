package com.ryuqq.marketplace.application.outboundproduct.dto.command;

import java.util.List;

/**
 * 수동 상품 외부몰 전송 커맨드.
 *
 * @param productGroupIds 전송 대상 상품그룹 ID 목록
 * @param shopIds shop ID 목록)
 */
public record ManualSyncProductsCommand(List<Long> productGroupIds, List<Long> shopIds) {

    public ManualSyncProductsCommand {
        productGroupIds = productGroupIds != null ? List.copyOf(productGroupIds) : List.of();
        shopIds = shopIds != null ? List.copyOf(shopIds) : List.of();
    }
}
