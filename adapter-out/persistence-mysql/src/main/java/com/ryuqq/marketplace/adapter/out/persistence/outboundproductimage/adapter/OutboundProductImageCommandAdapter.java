package com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.entity.OutboundProductImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.mapper.OutboundProductImageJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.repository.OutboundProductImageJpaRepository;
import com.ryuqq.marketplace.application.outboundproductimage.port.out.command.OutboundProductImageCommandPort;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OutboundProductImageCommandAdapter implements OutboundProductImageCommandPort {

    private final OutboundProductImageJpaRepository repository;
    private final OutboundProductImageJpaEntityMapper mapper;

    public OutboundProductImageCommandAdapter(
            OutboundProductImageJpaRepository repository,
            OutboundProductImageJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(OutboundProductImage image) {
        OutboundProductImageJpaEntity entity = mapper.toEntity(image);
        return repository.save(entity).getId();
    }

    @Override
    public List<Long> persistAll(List<OutboundProductImage> images) {
        List<OutboundProductImageJpaEntity> entities =
                images.stream().map(mapper::toEntity).toList();
        return repository.saveAll(entities).stream()
                .map(OutboundProductImageJpaEntity::getId)
                .toList();
    }
}
