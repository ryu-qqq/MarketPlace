package com.ryuqq.marketplace.application.cancel.manager;

import com.ryuqq.marketplace.application.cancel.port.out.query.CancelQueryPort;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.exception.CancelNotFoundException;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Cancel Read Manager. */
@Component
public class CancelReadManager {

    private final CancelQueryPort queryPort;

    public CancelReadManager(CancelQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public Cancel getById(CancelId id) {
        return queryPort
                .findById(id)
                .orElseThrow(() -> new CancelNotFoundException(id.value()));
    }

    @Transactional(readOnly = true)
    public List<Cancel> findByCriteria(CancelSearchCriteria criteria) {
        return queryPort.findByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public long countByCriteria(CancelSearchCriteria criteria) {
        return queryPort.countByCriteria(criteria);
    }

    @Transactional(readOnly = true)
    public List<Cancel> findByIdIn(List<String> cancelIds, Long sellerId) {
        return queryPort.findByIdIn(cancelIds, sellerId);
    }

    @Transactional(readOnly = true)
    public Optional<Cancel> findByOrderItemId(OrderItemId orderItemId) {
        return queryPort.findByOrderItemId(orderItemId);
    }
}
