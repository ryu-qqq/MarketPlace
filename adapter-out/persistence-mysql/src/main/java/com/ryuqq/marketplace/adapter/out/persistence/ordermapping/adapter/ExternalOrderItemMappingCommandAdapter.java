package com.ryuqq.marketplace.adapter.out.persistence.ordermapping.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.ordermapping.mapper.ExternalOrderItemMappingPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.ordermapping.repository.ExternalOrderItemMappingJpaRepository;
import com.ryuqq.marketplace.application.claimsync.port.out.command.ExternalOrderItemMappingCommandPort;
import com.ryuqq.marketplace.domain.ordermapping.aggregate.ExternalOrderItemMapping;
import java.util.List;
import org.springframework.stereotype.Component;

/** 외부 주문상품 매핑 Command Adapter. */
@Component
public class ExternalOrderItemMappingCommandAdapter implements ExternalOrderItemMappingCommandPort {

    private final ExternalOrderItemMappingJpaRepository jpaRepository;
    private final ExternalOrderItemMappingPersistenceMapper mapper;

    public ExternalOrderItemMappingCommandAdapter(
            ExternalOrderItemMappingJpaRepository jpaRepository,
            ExternalOrderItemMappingPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void persist(ExternalOrderItemMapping mapping) {
        jpaRepository.save(mapper.toEntity(mapping));
    }

    @Override
    public void persistAll(List<ExternalOrderItemMapping> mappings) {
        jpaRepository.saveAll(mappings.stream().map(mapper::toEntity).toList());
    }
}
