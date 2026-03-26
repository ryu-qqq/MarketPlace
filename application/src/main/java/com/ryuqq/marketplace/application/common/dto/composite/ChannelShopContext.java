package com.ryuqq.marketplace.application.common.dto.composite;

import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;

/**
 * 판매채널 + 매장 컴포짓 조회 결과.
 *
 * <p>sellerId + salesChannelId로 SellerSalesChannel과 Shop을 한방에 조회하여 반환합니다.
 *
 * @param channel 셀러 판매채널
 * @param shop 셀러 매장
 */
public record ChannelShopContext(SellerSalesChannel channel, Shop shop) {}
