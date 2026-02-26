package com.ryuqq.marketplace.application.productintelligence.manager;

import com.ryuqq.marketplace.application.productintelligence.port.out.command.ProductProfileCommandPort;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 상품 프로파일 Command Manager. */
@Component
public class ProductProfileCommandManager {

    private final ProductProfileCommandPort commandPort;

    public ProductProfileCommandManager(ProductProfileCommandPort commandPort) {
        this.commandPort = commandPort;
    }

    @Transactional
    public Long persist(ProductProfile profile) {
        return commandPort.persist(profile);
    }
}
