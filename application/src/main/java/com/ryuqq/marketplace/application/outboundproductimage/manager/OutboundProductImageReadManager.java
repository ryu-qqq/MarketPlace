package com.ryuqq.marketplace.application.outboundproductimage.manager;

import com.ryuqq.marketplace.application.outboundproductimage.port.out.query.OutboundProductImageQueryPort;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import com.ryuqq.marketplace.domain.outboundproductimage.vo.OutboundProductImages;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboundProductImageReadManager {

    private final OutboundProductImageQueryPort queryPort;

    public OutboundProductImageReadManager(OutboundProductImageQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public OutboundProductImages findByOutboundProductId(Long outboundProductId) {
        List<OutboundProductImage> images =
                queryPort.findActiveByOutboundProductId(outboundProductId);
        return OutboundProductImages.of(images);
    }
}
