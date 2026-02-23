package com.ryuqq.marketplace.application.outboundproduct.port.out.query;

import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import java.util.Optional;

public interface OutboundProductQueryPort {
    boolean existsByProductGroupIdAndSalesChannelId(Long productGroupId, Long salesChannelId);

    Optional<OutboundProduct> findByProductGroupIdAndSalesChannelId(
            Long productGroupId, Long salesChannelId);
}
