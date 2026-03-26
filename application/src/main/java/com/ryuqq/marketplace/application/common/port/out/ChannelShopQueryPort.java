package com.ryuqq.marketplace.application.common.port.out;

import com.ryuqq.marketplace.application.common.dto.composite.ChannelShopContext;

/**
 * 판매채널 + 매장 컴포짓 조회 포트.
 *
 * <p>sellerId + salesChannelId로 SellerSalesChannel과 Shop을 한방에 조회합니다.
 */
public interface ChannelShopQueryPort {

    /**
     * 셀러 ID와 판매채널 ID로 ChannelShopContext를 조회합니다.
     *
     * @param sellerId 셀러 ID
     * @param salesChannelId 판매채널 ID
     * @return 판매채널 + 매장 컨텍스트
     */
    ChannelShopContext getBySellerIdAndSalesChannelId(Long sellerId, Long salesChannelId);
}
