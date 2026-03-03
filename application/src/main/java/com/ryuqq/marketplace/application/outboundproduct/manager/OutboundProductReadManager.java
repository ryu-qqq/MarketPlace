package com.ryuqq.marketplace.application.outboundproduct.manager;

import com.ryuqq.marketplace.application.outboundproduct.port.out.query.OutboundProductQueryPort;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboundProductReadManager {

    private final OutboundProductQueryPort queryPort;

    public OutboundProductReadManager(OutboundProductQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public boolean existsByProductGroupIdAndSalesChannelId(
            Long productGroupId, Long salesChannelId) {
        return queryPort.existsByProductGroupIdAndSalesChannelId(productGroupId, salesChannelId);
    }

    /**
     * 상품그룹 ID + 판매채널 ID로 OutboundProduct 단건 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @param salesChannelId 판매채널 ID
     * @return OutboundProduct
     * @throws IllegalStateException 조회 결과 없음
     */
    @Transactional(readOnly = true)
    public OutboundProduct getByProductGroupIdAndSalesChannelId(
            Long productGroupId, Long salesChannelId) {
        return queryPort
                .findByProductGroupIdAndSalesChannelId(productGroupId, salesChannelId)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "OutboundProduct not found: productGroupId="
                                                + productGroupId
                                                + ", salesChannelId="
                                                + salesChannelId));
    }

    @Transactional(readOnly = true)
    public List<OutboundProduct> findByProductGroupIds(List<Long> productGroupIds) {
        return queryPort.findByProductGroupIds(productGroupIds);
    }

    @Transactional(readOnly = true)
    public List<OutboundProduct> findRegisteredByProductGroupId(Long productGroupId) {
        return queryPort.findRegisteredByProductGroupId(productGroupId);
    }

    @Transactional(readOnly = true)
    public List<OutboundProduct> findDeregisteredByProductGroupId(Long productGroupId) {
        return queryPort.findDeregisteredByProductGroupId(productGroupId);
    }
}
