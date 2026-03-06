package com.ryuqq.marketplace.application.selleraddress.internal;

import com.ryuqq.marketplace.application.outboundseller.manager.OutboundSellerOutboxCommandManager;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressCommandManager;
import com.ryuqq.marketplace.domain.outboundseller.aggregate.OutboundSellerOutbox;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerEntityType;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOperationType;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import java.time.Instant;
import org.springframework.stereotype.Component;

@Component
public class SellerAddressOutboundFacade {

    private final SellerAddressCommandManager commandManager;
    private final OutboundSellerOutboxCommandManager outboxManager;

    public SellerAddressOutboundFacade(
            SellerAddressCommandManager commandManager,
            OutboundSellerOutboxCommandManager outboxManager) {
        this.commandManager = commandManager;
        this.outboxManager = outboxManager;
    }

    public Long persistWithSync(
            SellerAddress address, OutboundSellerOperationType op, Instant now) {
        Long id = commandManager.persist(address);
        outboxManager.persist(
                OutboundSellerOutbox.forNew(
                        address.sellerId(), id, OutboundSellerEntityType.SELLER_ADDRESS, op, now));
        return id;
    }

    public void persistDeleteWithSync(SellerId sellerId, SellerAddress address, Instant now) {
        commandManager.persist(address);
        outboxManager.persist(
                OutboundSellerOutbox.forNew(
                        sellerId,
                        address.idValue(),
                        OutboundSellerEntityType.SELLER_ADDRESS,
                        OutboundSellerOperationType.DELETE,
                        now));
    }
}
