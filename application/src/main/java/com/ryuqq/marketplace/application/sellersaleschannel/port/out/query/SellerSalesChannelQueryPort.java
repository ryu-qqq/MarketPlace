package com.ryuqq.marketplace.application.sellersaleschannel.port.out.query;

import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.List;

/** 셀러 판매채널 조회 포트. */
public interface SellerSalesChannelQueryPort {

    /**
     * 셀러의 CONNECTED 상태 판매채널 목록 조회.
     *
     * @param sellerId 셀러 ID
     * @return CONNECTED 상태의 판매채널 목록
     */
    List<SellerSalesChannel> findConnectedBySellerId(SellerId sellerId);
}
