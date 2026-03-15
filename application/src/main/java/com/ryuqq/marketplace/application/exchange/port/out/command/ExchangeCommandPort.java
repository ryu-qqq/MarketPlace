package com.ryuqq.marketplace.application.exchange.port.out.command;

import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;

/** 교환 클레임 Command Port. */
public interface ExchangeCommandPort {

    void persist(ExchangeClaim exchangeClaim);
}
