package com.ryuqq.marketplace.application.inboundproduct.port.out.query;

import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductStatus;
import java.util.List;
import java.util.Optional;

public interface InboundProductQueryPort {
    Optional<InboundProduct> findByInboundSourceIdAndProductCode(
            Long inboundSourceId, String externalProductCode);

    List<InboundProduct> findByStatus(InboundProductStatus status, int limit);

    List<InboundProduct> findByStatusAndRetryCountLessThan(
            InboundProductStatus status, int maxRetryCount, int limit);
}
