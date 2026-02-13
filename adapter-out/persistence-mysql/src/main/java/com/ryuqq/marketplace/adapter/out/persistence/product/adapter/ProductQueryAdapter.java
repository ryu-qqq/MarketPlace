package com.ryuqq.marketplace.adapter.out.persistence.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductOptionMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.mapper.ProductJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.product.repository.ProductQueryDslRepository;
import com.ryuqq.marketplace.application.product.port.out.query.ProductQueryPort;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Product Query Adapter.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-003: Domain 반환 (DTO 반환 금지).
 */
@Component
public class ProductQueryAdapter implements ProductQueryPort {

    private final ProductQueryDslRepository queryDslRepository;
    private final ProductJpaEntityMapper mapper;

    public ProductQueryAdapter(
            ProductQueryDslRepository queryDslRepository, ProductJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Product> findById(ProductId id) {
        return queryDslRepository
                .findById(id.value())
                .map(
                        entity -> {
                            List<ProductOptionMappingJpaEntity> mappings =
                                    queryDslRepository.findOptionMappingsByProductId(
                                            entity.getId());
                            return mapper.toDomain(entity, mappings);
                        });
    }

    @Override
    public List<Product> findByProductGroupId(ProductGroupId productGroupId) {
        List<ProductJpaEntity> entities =
                queryDslRepository.findByProductGroupId(productGroupId.value());

        if (entities.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = entities.stream().map(ProductJpaEntity::getId).toList();

        List<ProductOptionMappingJpaEntity> allMappings =
                queryDslRepository.findOptionMappingsByProductIds(productIds);

        Map<Long, List<ProductOptionMappingJpaEntity>> mappingsByProductId =
                allMappings.stream()
                        .collect(
                                Collectors.groupingBy(ProductOptionMappingJpaEntity::getProductId));

        List<Product> products = new ArrayList<>();
        for (ProductJpaEntity entity : entities) {
            List<ProductOptionMappingJpaEntity> mappings =
                    mappingsByProductId.getOrDefault(entity.getId(), List.of());
            products.add(mapper.toDomain(entity, mappings));
        }

        return products;
    }
}
