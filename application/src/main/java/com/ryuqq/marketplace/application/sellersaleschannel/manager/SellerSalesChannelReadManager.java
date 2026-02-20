package com.ryuqq.marketplace.application.sellersaleschannel.manager;

import com.ryuqq.marketplace.application.sellersaleschannel.port.out.query.SellerSalesChannelQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.List;
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
}
