package com.ryuqq.marketplace.application.seller.port.out.command;

import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;

/** SellerAuthOutbox Command Port. */
public interface SellerAuthOutboxCommandPort {

    Long persist(SellerAuthOutbox outbox);
}
