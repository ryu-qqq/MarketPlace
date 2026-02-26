package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductStockEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductGroupQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.product.port.out.query.LegacyProductQueryPort;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product 테이블 조회 Adapter.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository를 사용합니다.
 */
@Component
public class LegacyProductQueryAdapter implements LegacyProductQueryPort {

    private final LegacyProductGroupQueryDslRepository queryDslRepository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyProductQueryAdapter(
            LegacyProductGroupQueryDslRepository queryDslRepository,
            LegacyProductCommandEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public List<LegacyProduct> findByProductGroupId(LegacyProductGroupId productGroupId) {
        List<LegacyProductEntity> productEntities =
                queryDslRepository.findProductsByProductGroupId(productGroupId.value());

        if (productEntities.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = productEntities.stream().map(LegacyProductEntity::getId).toList();

        List<LegacyProductStockEntity> stockEntities =
                queryDslRepository.findStocksByProductIds(productIds);
        Map<Long, Integer> stockByProductId = new LinkedHashMap<>();
        for (LegacyProductStockEntity stock : stockEntities) {
            stockByProductId.put(stock.getProductId(), stock.getStockQuantity());
        }

        List<LegacyProductOptionEntity> optionEntities =
                queryDslRepository.findProductOptionsByProductIds(productIds);
        Map<Long, List<LegacyProductOption>> optionsByProductId = new LinkedHashMap<>();
        for (LegacyProductOptionEntity optionEntity : optionEntities) {
            optionsByProductId
                    .computeIfAbsent(optionEntity.getProductId(), k -> new ArrayList<>())
                    .add(mapper.toOptionDomain(optionEntity));
        }

        return productEntities.stream()
                .map(
                        entity ->
                                mapper.toProductDomain(
                                        entity,
                                        stockByProductId.getOrDefault(entity.getId(), 0),
                                        optionsByProductId.getOrDefault(entity.getId(), List.of())))
                .toList();
    }
}
