package com.ryuqq.marketplace.adapter.out.persistence.productgroup.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository.ProductGroupQueryDslRepository;
import com.ryuqq.marketplace.application.productgroup.port.out.query.ProductGroupQueryPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ProductGroupQueryAdapter - 상품 그룹 Query 어댑터.
 *
 * <p>ProductGroupQueryPort를 구현하여 영속성 계층과 연결합니다.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ProductGroupQueryAdapter implements ProductGroupQueryPort {

    private final ProductGroupQueryDslRepository queryDslRepository;
    private final ProductGroupJpaEntityMapper mapper;

    public ProductGroupQueryAdapter(
            ProductGroupQueryDslRepository queryDslRepository, ProductGroupJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProductGroup> findById(ProductGroupId id) {
        return queryDslRepository
                .findById(id.value())
                .map(
                        entity -> {
                            Long productGroupId = entity.getId();

                            List<ProductGroupImageJpaEntity> images =
                                    queryDslRepository.findImagesByProductGroupId(productGroupId);

                            List<SellerOptionGroupJpaEntity> groups =
                                    queryDslRepository.findOptionGroupsByProductGroupId(
                                            productGroupId);

                            List<Long> groupIds =
                                    groups.stream().map(SellerOptionGroupJpaEntity::getId).toList();

                            List<SellerOptionValueJpaEntity> values =
                                    queryDslRepository.findOptionValuesByOptionGroupIds(groupIds);

                            return mapper.toDomain(entity, images, groups, values);
                        });
    }

    @Override
    public List<ProductGroup> findByCriteria(ProductGroupSearchCriteria criteria) {
        List<ProductGroupJpaEntity> parentEntities = queryDslRepository.findByCriteria(criteria);

        if (parentEntities.isEmpty()) {
            return List.of();
        }

        List<Long> productGroupIds =
                parentEntities.stream().map(ProductGroupJpaEntity::getId).toList();

        List<ProductGroupImageJpaEntity> allImages =
                queryDslRepository.findImagesByProductGroupIds(productGroupIds);

        List<SellerOptionGroupJpaEntity> allGroups =
                queryDslRepository.findOptionGroupsByProductGroupIds(productGroupIds);

        List<Long> allGroupIds = allGroups.stream().map(SellerOptionGroupJpaEntity::getId).toList();

        List<SellerOptionValueJpaEntity> allValues =
                queryDslRepository.findOptionValuesByOptionGroupIds(allGroupIds);

        Map<Long, List<ProductGroupImageJpaEntity>> imagesByProductGroupId =
                allImages.stream()
                        .collect(
                                Collectors.groupingBy(
                                        ProductGroupImageJpaEntity::getProductGroupId));

        Map<Long, List<SellerOptionGroupJpaEntity>> groupsByProductGroupId =
                allGroups.stream()
                        .collect(
                                Collectors.groupingBy(
                                        SellerOptionGroupJpaEntity::getProductGroupId));

        Map<Long, List<SellerOptionValueJpaEntity>> valuesByGroupId =
                allValues.stream()
                        .collect(
                                Collectors.groupingBy(
                                        SellerOptionValueJpaEntity::getSellerOptionGroupId));

        List<ProductGroup> results = new ArrayList<>();
        for (ProductGroupJpaEntity parentEntity : parentEntities) {
            Long pgId = parentEntity.getId();

            List<ProductGroupImageJpaEntity> images =
                    imagesByProductGroupId.getOrDefault(pgId, List.of());

            List<SellerOptionGroupJpaEntity> groups =
                    groupsByProductGroupId.getOrDefault(pgId, List.of());

            List<Long> groupIds = groups.stream().map(SellerOptionGroupJpaEntity::getId).toList();

            List<SellerOptionValueJpaEntity> values = new ArrayList<>();
            for (Long groupId : groupIds) {
                values.addAll(valuesByGroupId.getOrDefault(groupId, List.of()));
            }

            results.add(mapper.toDomain(parentEntity, images, groups, values));
        }

        return results;
    }

    @Override
    public long countByCriteria(ProductGroupSearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }
}
