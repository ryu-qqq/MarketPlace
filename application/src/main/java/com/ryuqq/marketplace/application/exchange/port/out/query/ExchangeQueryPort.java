package com.ryuqq.marketplace.application.exchange.port.out.query;

import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import java.util.List;
import java.util.Optional;

/** 교환 클레임 Query Port. */
public interface ExchangeQueryPort {

    Optional<ExchangeClaim> findById(ExchangeClaimId id);

    Optional<ExchangeClaim> findByOrderId(String orderId);

    List<ExchangeClaim> findByOrderIds(List<String> orderIds);
}
