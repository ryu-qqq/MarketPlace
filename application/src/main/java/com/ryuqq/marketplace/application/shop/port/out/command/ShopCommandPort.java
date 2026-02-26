package com.ryuqq.marketplace.application.shop.port.out.command;

import com.ryuqq.marketplace.domain.shop.aggregate.Shop;

/** Shop Command Port. */
public interface ShopCommandPort {
    Long persist(Shop shop);
}
