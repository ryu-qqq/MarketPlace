package com.ryuqq.marketplace.application.sellersaleschannel.port.out.query;

import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.List;
import java.util.Optional;

/** 셀러 판매채널 조회 포트. */
public interface SellerSalesChannelQueryPort {

    /**
     * 셀러의 CONNECTED 상태 판매채널 목록 조회.
     *
     * @param sellerId 셀러 ID
     * @return CONNECTED 상태의 판매채널 목록
     */
    List<SellerSalesChannel> findConnectedBySellerId(SellerId sellerId);

    /**
     * 셀러 ID + 판매채널 ID로 단건 조회.
     *
     * @param sellerId 셀러 ID
     * @param salesChannelId 판매채널 ID
     * @return 셀러 판매채널 (없으면 empty)
     */
    Optional<SellerSalesChannel> findBySellerIdAndSalesChannelId(
            SellerId sellerId, SalesChannelId salesChannelId);

    /**
     * 채널 코드 기준 CONNECTED 상태 판매채널 목록 조회.
     *
     * @param channelCode 채널 코드
     * @return CONNECTED 상태의 판매채널 목록
     */
    List<SellerSalesChannel> findConnectedByChannelCode(String channelCode);
}
