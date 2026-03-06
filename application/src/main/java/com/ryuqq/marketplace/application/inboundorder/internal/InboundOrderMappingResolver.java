package com.ryuqq.marketplace.application.inboundorder.internal;

import com.ryuqq.marketplace.application.outboundproduct.port.out.query.OutboundProductQueryPort;
import com.ryuqq.marketplace.application.productgroup.port.out.query.ProductGroupQueryPort;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrder;
import com.ryuqq.marketplace.domain.inboundorder.aggregate.InboundOrderItem;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * InboundOrder 상품 매핑 해석기.
 *
 * <p>OutboundProduct 역조회를 통해 외부 상품 ID → 내부 상품 매핑을 수행합니다. ProductGroup에서 sellerId를 역추적합니다.
 */
@Component
public class InboundOrderMappingResolver {

    private final OutboundProductQueryPort outboundProductQueryPort;
    private final ProductGroupQueryPort productGroupQueryPort;

    public InboundOrderMappingResolver(
            OutboundProductQueryPort outboundProductQueryPort,
            ProductGroupQueryPort productGroupQueryPort) {
        this.outboundProductQueryPort = outboundProductQueryPort;
        this.productGroupQueryPort = productGroupQueryPort;
    }

    /**
     * InboundOrder의 각 아이템에 대해 상품 매핑을 수행합니다.
     *
     * @param order 인바운드 주문
     * @param now 현재 시각
     * @return 전체 아이템 매핑 성공 여부
     */
    public boolean resolveAndApply(InboundOrder order, Instant now) {
        boolean allMapped = true;

        for (InboundOrderItem item : order.items()) {
            if (item.isMapped()) {
                continue;
            }

            Optional<OutboundProduct> outbound =
                    outboundProductQueryPort.findByExternalProductIdAndSalesChannelId(
                            item.externalProductId(), order.salesChannelId());

            if (outbound.isEmpty()) {
                allMapped = false;
                continue;
            }

            long productGroupId = outbound.get().productGroupIdValue();
            long sellerId = resolveSellerIdFromProductGroup(productGroupId);
            item.applyMapping(productGroupId, 0L, sellerId, 0L, null);
        }

        if (allMapped) {
            order.applyMapping(now);
        } else {
            order.markPendingMapping(now);
        }
        return allMapped;
    }

    private long resolveSellerIdFromProductGroup(long productGroupId) {
        return productGroupQueryPort
                .findById(new ProductGroupId(productGroupId))
                .map(ProductGroup::sellerIdValue)
                .orElse(0L);
    }
}
