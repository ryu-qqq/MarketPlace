package com.ryuqq.marketplace.adapter.out.persistence.cancel.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.cancel.mapper.CancelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.cancel.repository.CancelQueryDslRepository;
import com.ryuqq.marketplace.application.cancel.port.out.query.CancelQueryPort;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** 취소 Query Adapter. */
@Component
public class CancelQueryAdapter implements CancelQueryPort {

    private final CancelQueryDslRepository cancelRepository;
    private final CancelJpaEntityMapper mapper;

    public CancelQueryAdapter(
            CancelQueryDslRepository cancelRepository, CancelJpaEntityMapper mapper) {
        this.cancelRepository = cancelRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Cancel> findById(CancelId id) {
        return cancelRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Cancel> findByOrderItemId(OrderItemId orderItemId) {
        return cancelRepository.findByOrderItemId(orderItemId.value()).map(mapper::toDomain);
    }

    @Override
    public List<Cancel> findByOrderItemIds(List<OrderItemId> orderItemIds) {
        List<String> ids = orderItemIds.stream().map(OrderItemId::value).toList();
        return cancelRepository.findByOrderItemIds(ids).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Cancel> findByCriteria(CancelSearchCriteria criteria) {
        return cancelRepository.findByCriteria(criteria).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countByCriteria(CancelSearchCriteria criteria) {
        return cancelRepository.countByCriteria(criteria);
    }

    @Override
    public Map<CancelStatus, Long> countByStatus() {
        return cancelRepository.countByStatus();
    }

    @Override
    public List<Cancel> findByIdIn(List<String> cancelIds, Long sellerId) {
        return cancelRepository.findByIdIn(cancelIds, sellerId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
