package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper.ProductGroupDescriptionJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.DescriptionImageQueryDslRepository;
import com.ryuqq.marketplace.application.productgroupdescription.port.out.query.DescriptionImageQueryPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * DescriptionImage Query Adapter.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class DescriptionImageQueryAdapter implements DescriptionImageQueryPort {

    private final DescriptionImageQueryDslRepository queryDslRepository;
    private final ProductGroupDescriptionJpaEntityMapper mapper;

    public DescriptionImageQueryAdapter(
            DescriptionImageQueryDslRepository queryDslRepository,
            ProductGroupDescriptionJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<DescriptionImage> findById(Long id) {
        return queryDslRepository.findById(id).map(mapper::toImageDomain);
    }
}
