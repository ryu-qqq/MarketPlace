package com.ryuqq.marketplace.application.exchange.manager;

import com.ryuqq.marketplace.application.exchange.port.out.query.ExchangeQueryPort;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.exception.ExchangeNotFoundException;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** ExchangeClaim Read Manager. */
@Component
public class ExchangeReadManager {

    private final ExchangeQueryPort queryPort;

    public ExchangeReadManager(ExchangeQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public ExchangeClaim getById(ExchangeClaimId id) {
        return queryPort.findById(id).orElseThrow(() -> new ExchangeNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public Optional<ExchangeClaim> findByOrderItemId(OrderItemId orderItemId) {
        return queryPort.findByOrderItemId(orderItemId);
    }

    @Transactional(readOnly = true)
    public List<ExchangeClaim> findByIdIn(List<String> exchangeClaimIds, Long sellerId) {
        return queryPort.findByIdIn(exchangeClaimIds, sellerId);
    }

    @Transactional(readOnly = true)
    public List<ExchangeClaim> findByCriteria(ExchangeSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(ExchangeSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }
}
