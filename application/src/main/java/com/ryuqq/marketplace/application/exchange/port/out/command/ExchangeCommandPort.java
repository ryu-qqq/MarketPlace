package com.ryuqq.marketplace.application.exchange.port.out.command;

import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.util.List;

/** 교환 클레임 Command Port. */
public interface ExchangeCommandPort {

    void persist(ExchangeClaim exchangeClaim);

    void persistAll(List<ExchangeClaim> exchangeClaims);
}
