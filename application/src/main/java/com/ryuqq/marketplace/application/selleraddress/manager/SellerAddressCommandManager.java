package com.ryuqq.marketplace.application.selleraddress.manager;

import com.ryuqq.marketplace.application.selleraddress.port.out.command.SellerAddressCommandPort;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SellerAddress Command Manager. */
@Component
public class SellerAddressCommandManager {

    private final SellerAddressCommandPort commandPort;

    public SellerAddressCommandManager(SellerAddressCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(SellerAddress address) {
        return commandPort.persist(address);
    }

    @Transactional
    public void persistAll(List<SellerAddress> addresses) {
        commandPort.persistAll(addresses);
    }
}
