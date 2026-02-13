package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.SellerOptionValueJpaRepository;
import com.ryuqq.marketplace.application.productgroup.port.out.command.SellerOptionValueCommandPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerOptionValueCommandAdapter - 셀러 옵션 값 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class SellerOptionValueCommandAdapter implements SellerOptionValueCommandPort {

    private final SellerOptionValueJpaRepository repository;
    private final ProductGroupJpaEntityMapper mapper;

    public SellerOptionValueCommandAdapter(
            SellerOptionValueJpaRepository repository, ProductGroupJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void deleteByGroupIdIn(List<Long> groupIds) {
        if (groupIds.isEmpty()) {
            return;
        }
        repository.deleteBySellerOptionGroupIdIn(groupIds);
    }

    @Override
    public void persistAll(Long groupId, List<SellerOptionValue> values) {
        if (values.isEmpty()) {
            return;
        }
        List<SellerOptionValueJpaEntity> entities =
                values.stream().map(mapper::toOptionValueEntity).toList();
        repository.saveAll(entities);
    }
}
