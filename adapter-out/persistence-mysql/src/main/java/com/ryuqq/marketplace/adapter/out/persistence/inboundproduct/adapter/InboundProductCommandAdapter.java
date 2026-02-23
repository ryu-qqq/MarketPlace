package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity.InboundProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.mapper.InboundProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.repository.InboundProductJpaRepository;
import com.ryuqq.marketplace.application.inboundproduct.port.out.command.InboundProductCommandPort;
import com.ryuqq.marketplace.domain.inboundproduct.aggregate.InboundProduct;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class InboundProductCommandAdapter implements InboundProductCommandPort {

    private final InboundProductJpaRepository repository;
    private final InboundProductJpaEntityMapper mapper;

    public InboundProductCommandAdapter(
            InboundProductJpaRepository repository, InboundProductJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(InboundProduct product) {
        InboundProductJpaEntity entity = mapper.toEntity(product);
        InboundProductJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<InboundProduct> products) {
        List<InboundProductJpaEntity> entities = products.stream().map(mapper::toEntity).toList();
        return repository.saveAll(entities).stream().map(InboundProductJpaEntity::getId).toList();
    }
}
