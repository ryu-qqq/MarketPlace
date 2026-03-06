package com.ryuqq.marketplace.application.sellersaleschannel.manager;

import com.ryuqq.marketplace.application.sellersaleschannel.port.out.query.SellerSalesChannelQueryPort;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 셀러 판매채널 조회 Manager. */
@Component
public class SellerSalesChannelReadManager {

    private final SellerSalesChannelQueryPort queryPort;

    public SellerSalesChannelReadManager(SellerSalesChannelQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<SellerSalesChannel> findConnectedBySellerId(SellerId sellerId) {
        return queryPort.findConnectedBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public List<SellerSalesChannel> findConnectedBySellerIds(Set<SellerId> sellerIds) {
        return queryPort.findConnectedBySellerIds(sellerIds);
    }

    @Transactional(readOnly = true)
    public List<SellerSalesChannel> findConnectedByChannelCode(String channelCode) {
        return queryPort.findConnectedByChannelCode(channelCode);
    }

    /**
     * 셀러 ID + 판매채널 ID로 단건 조회.
     *
     * @param sellerId 셀러 ID
     * @param salesChannelId 판매채널 ID
     * @return 셀러 판매채널
     * @throws IllegalStateException 조회 결과 없음
     */
    @Transactional(readOnly = true)
    public SellerSalesChannel getBySellerIdAndSalesChannelId(
            SellerId sellerId, SalesChannelId salesChannelId) {
        return queryPort
                .findBySellerIdAndSalesChannelId(sellerId, salesChannelId)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "SellerSalesChannel not found: sellerId="
                                                + sellerId.value()
                                                + ", salesChannelId="
                                                + salesChannelId.value()));
    }
}
