package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.mapper.CanonicalOptionGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.repository.CanonicalOptionGroupQueryDslRepository;
import com.ryuqq.marketplace.application.canonicaloption.port.out.query.CanonicalOptionGroupQueryPort;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** CanonicalOptionGroup Query Adapter. */
@Component
public class CanonicalOptionGroupQueryAdapter implements CanonicalOptionGroupQueryPort {

    private final CanonicalOptionGroupQueryDslRepository queryDslRepository;
    private final CanonicalOptionGroupJpaEntityMapper mapper;

    public CanonicalOptionGroupQueryAdapter(
            CanonicalOptionGroupQueryDslRepository queryDslRepository,
            CanonicalOptionGroupJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<CanonicalOptionGroup> findById(CanonicalOptionGroupId id) {
        return queryDslRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<CanonicalOptionGroup> findByCriteria(CanonicalOptionGroupSearchCriteria criteria) {
        return queryDslRepository.findByCriteria(criteria).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByCriteria(CanonicalOptionGroupSearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }
}
