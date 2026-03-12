package com.ryuqq.marketplace.application.outboundproductimage.manager;

import com.ryuqq.marketplace.application.outboundproductimage.port.out.command.OutboundProductImageCommandPort;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboundProductImageCommandManager {

    private final OutboundProductImageCommandPort commandPort;

    public OutboundProductImageCommandManager(OutboundProductImageCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(OutboundProductImage image) {
        return commandPort.persist(image);
    }

    @Transactional
    public List<Long> persistAll(List<OutboundProductImage> images) {
        if (images.isEmpty()) {
            return List.of();
        }
        return commandPort.persistAll(images);
    }
}
