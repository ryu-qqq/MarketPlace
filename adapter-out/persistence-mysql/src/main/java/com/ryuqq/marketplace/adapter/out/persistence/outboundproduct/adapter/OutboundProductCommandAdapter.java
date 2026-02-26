package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.OutboundProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.mapper.OutboundProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository.OutboundProductJpaRepository;
import com.ryuqq.marketplace.application.outboundproduct.port.out.command.OutboundProductCommandPort;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OutboundProductCommandAdapter implements OutboundProductCommandPort {

    private final OutboundProductJpaRepository repository;
    private final OutboundProductJpaEntityMapper mapper;

    public OutboundProductCommandAdapter(
            OutboundProductJpaRepository repository, OutboundProductJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(OutboundProduct product) {
        OutboundProductJpaEntity entity = mapper.toEntity(product);
        OutboundProductJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<OutboundProduct> products) {
        List<OutboundProductJpaEntity> entities = products.stream().map(mapper::toEntity).toList();
        return repository.saveAll(entities).stream().map(OutboundProductJpaEntity::getId).toList();
    }
}
