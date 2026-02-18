package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.SellerOptionGroupQueryDslRepository;
import com.ryuqq.marketplace.application.selleroption.port.out.query.SellerOptionGroupQueryPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * SellerOptionGroup Query Adapter.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class SellerOptionGroupQueryAdapter implements SellerOptionGroupQueryPort {

    private final SellerOptionGroupQueryDslRepository queryDslRepository;
    private final ProductGroupJpaEntityMapper mapper;

    public SellerOptionGroupQueryAdapter(
            SellerOptionGroupQueryDslRepository queryDslRepository,
            ProductGroupJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<SellerOptionGroup> findByProductGroupId(ProductGroupId productGroupId) {
        List<SellerOptionGroupJpaEntity> groupEntities =
                queryDslRepository.findByProductGroupId(productGroupId.value());

        List<Long> groupIds =
                groupEntities.stream().map(SellerOptionGroupJpaEntity::getId).toList();

        List<SellerOptionValueJpaEntity> valueEntities =
                queryDslRepository.findValuesByGroupIds(groupIds);

        Map<Long, List<SellerOptionValueJpaEntity>> valuesByGroupId =
                valueEntities.stream()
                        .collect(
                                Collectors.groupingBy(
                                        SellerOptionValueJpaEntity::getSellerOptionGroupId));

        return groupEntities.stream()
                .map(
                        groupEntity -> {
                            List<SellerOptionValue> values =
                                    valuesByGroupId
                                            .getOrDefault(groupEntity.getId(), List.of())
                                            .stream()
                                            .map(mapper::toOptionValueDomain)
                                            .toList();
                            return mapper.toOptionGroupDomain(groupEntity, values);
                        })
                .toList();
    }
}
