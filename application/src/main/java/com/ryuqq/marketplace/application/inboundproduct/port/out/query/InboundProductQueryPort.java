package com.ryuqq.marketplace.application.inboundproduct.port.out.query;

import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.Optional;

public interface InboundProductQueryPort {
    Optional<InboundProduct> findByInboundSourceIdAndProductCode(
            Long inboundSourceId, String externalProductCode);
}
