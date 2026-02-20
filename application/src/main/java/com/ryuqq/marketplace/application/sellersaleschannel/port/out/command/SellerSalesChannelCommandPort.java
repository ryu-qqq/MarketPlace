package com.ryuqq.marketplace.application.sellersaleschannel.port.out.command;

import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;

/** 셀러 판매채널 커맨드 포트. */
public interface SellerSalesChannelCommandPort {

    /**
     * 셀러 판매채널 저장.
     *
     * @param sellerSalesChannel 저장할 셀러 판매채널
     * @return 저장된 엔티티 ID
     */
    Long persist(SellerSalesChannel sellerSalesChannel);
}
