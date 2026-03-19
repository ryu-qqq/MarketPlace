package com.ryuqq.marketplace.application.claimsync.manager;

import com.ryuqq.marketplace.application.claimsync.port.out.query.ExternalOrderItemMappingQueryPort;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 외부 주문상품 매핑 조회 Manager. */
@Component
public class ExternalOrderItemMappingReadManager {

    private final ExternalOrderItemMappingQueryPort queryPort;

    public ExternalOrderItemMappingReadManager(ExternalOrderItemMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 판매채널 ID와 외부 주문상품번호로 매핑을 조회합니다.
     *
     * <p>매핑이 없으면 null을 반환합니다. null은 "매핑 없음 → SKIP" 을 의미하며 예외가 아닙니다.
     *
     * @param salesChannelId 판매채널 ID
     * @param externalProductOrderId 외부 주문상품번호
     * @return 매핑 정보, 없으면 null
     */
    @Transactional(readOnly = true)
    public ExternalOrderItemMapping getMapping(long salesChannelId, String externalProductOrderId) {
        return queryPort
                .findBySalesChannelIdAndExternalProductOrderId(
                        salesChannelId, externalProductOrderId)
                .orElse(null);
    }
}
