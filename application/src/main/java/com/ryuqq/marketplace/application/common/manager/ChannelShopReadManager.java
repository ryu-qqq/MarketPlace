package com.ryuqq.marketplace.application.common.manager;

import com.ryuqq.marketplace.application.common.dto.composite.ChannelShopContext;
import com.ryuqq.marketplace.application.common.port.out.ChannelShopQueryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 판매채널 + 매장 컴포짓 조회 매니저.
 *
 * <p>sellerId + salesChannelId로 SellerSalesChannel과 Shop을 한방에 조회합니다.
 */
@Component
public class ChannelShopReadManager {

    private final ChannelShopQueryPort queryPort;

    public ChannelShopReadManager(ChannelShopQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public ChannelShopContext getBySellerIdAndSalesChannelId(Long sellerId, Long salesChannelId) {
        return queryPort.getBySellerIdAndSalesChannelId(sellerId, salesChannelId);
    }
}
