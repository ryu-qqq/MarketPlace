package com.ryuqq.marketplace.application.inboundproduct.manager;

import com.ryuqq.marketplace.application.inboundproduct.port.out.query.InboundProductQueryPort;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import com.ryuqq.marketplace.domain.inboundproduct.exception.InboundProductNotFoundException;
import com.ryuqq.marketplace.domain.inboundproduct.vo.InboundProductStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class InboundProductReadManager {

    private final InboundProductQueryPort queryPort;

    public InboundProductReadManager(InboundProductQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Optional<InboundProduct> findByInboundSourceIdAndProductCode(
            Long inboundSourceId, String externalProductCode) {
        return queryPort.findByInboundSourceIdAndProductCode(inboundSourceId, externalProductCode);
    }

    @Transactional(readOnly = true)
    public InboundProduct findByInboundSourceIdAndProductCodeOrThrow(
            Long inboundSourceId, String externalProductCode) {
        return queryPort
                .findByInboundSourceIdAndProductCode(inboundSourceId, externalProductCode)
                .orElseThrow(
                        () ->
                                new InboundProductNotFoundException(
                                        inboundSourceId, externalProductCode));
    }

    @Transactional(readOnly = true)
    public List<InboundProduct> findPendingMapping(int limit) {
        return queryPort.findByStatus(InboundProductStatus.PENDING_MAPPING, limit);
    }
}
