package com.ryuqq.marketplace.application.outboundproduct.validator;

import com.ryuqq.marketplace.application.sellersaleschannel.manager.SellerSalesChannelReadManager;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** 수동 전송 시 셀러-채널 연결 검증. */
@Component
public class ManualSyncProductsValidator {

    private final SellerSalesChannelReadManager sellerSalesChannelReadManager;

    public ManualSyncProductsValidator(
            SellerSalesChannelReadManager sellerSalesChannelReadManager) {
        this.sellerSalesChannelReadManager = sellerSalesChannelReadManager;
    }

    /** 여러 셀러의 CONNECTED 채널 일괄 조회 후 셀러별 Map 반환. */
    public Map<Long, Set<Long>> findConnectedChannelIdsBySellerIds(Set<SellerId> sellerIds) {
        List<SellerSalesChannel> allConnected =
                sellerSalesChannelReadManager.findConnectedBySellerIds(sellerIds);

        return allConnected.stream()
                .collect(
                        Collectors.groupingBy(
                                ch -> ch.sellerId().value(),
                                Collectors.mapping(
                                        ch -> ch.salesChannelId().value(), Collectors.toSet())));
    }
}
