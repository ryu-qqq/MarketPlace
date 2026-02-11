package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.mapper.CanonicalOptionValueJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.repository.CanonicalOptionValueJpaRepository;
import com.ryuqq.marketplace.application.canonicaloption.port.out.query.CanonicalOptionValueQueryPort;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/** CanonicalOptionValue Query Adapter. */
@Component
public class CanonicalOptionValueQueryAdapter implements CanonicalOptionValueQueryPort {

    private final CanonicalOptionValueJpaRepository valueJpaRepository;
    private final CanonicalOptionValueJpaEntityMapper mapper;

    public CanonicalOptionValueQueryAdapter(
            CanonicalOptionValueJpaRepository valueJpaRepository,
            CanonicalOptionValueJpaEntityMapper mapper) {
        this.valueJpaRepository = valueJpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<CanonicalOptionValue> findByCanonicalOptionGroupId(
            Long canonicalOptionGroupId) {
        return valueJpaRepository
                .findByCanonicalOptionGroupIdOrderBySortOrder(canonicalOptionGroupId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Map<Long, List<CanonicalOptionValue>> findGroupedByCanonicalOptionGroupIds(
            List<Long> canonicalOptionGroupIds) {
        return valueJpaRepository
                .findByCanonicalOptionGroupIdInOrderBySortOrder(canonicalOptionGroupIds)
                .stream()
                .collect(Collectors.groupingBy(
                        CanonicalOptionValueJpaEntity::getCanonicalOptionGroupId,
                        Collectors.mapping(mapper::toDomain, Collectors.toList())));
    }
}
