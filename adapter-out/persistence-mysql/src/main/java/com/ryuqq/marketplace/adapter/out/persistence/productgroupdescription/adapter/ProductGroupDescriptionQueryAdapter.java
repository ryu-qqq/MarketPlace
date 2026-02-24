package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper.ProductGroupDescriptionJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.ProductGroupDescriptionQueryDslRepository;
import com.ryuqq.marketplace.application.productgroupdescription.port.out.query.ProductGroupDescriptionQueryPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * ProductGroupDescriptionQueryAdapter - 상품 그룹 상세설명 조회 어댑터.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 */
@Component
public class ProductGroupDescriptionQueryAdapter implements ProductGroupDescriptionQueryPort {

    private final ProductGroupDescriptionQueryDslRepository queryDslRepository;
    private final ProductGroupDescriptionJpaEntityMapper mapper;

    public ProductGroupDescriptionQueryAdapter(
            ProductGroupDescriptionQueryDslRepository queryDslRepository,
            ProductGroupDescriptionJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProductGroupDescription> findById(Long id) {
        return queryDslRepository
                .findById(id)
                .map(
                        entity -> {
                            var imageEntities =
                                    queryDslRepository.findImagesByDescriptionId(entity.getId());
                            return mapper.toDomain(entity, imageEntities);
                        });
    }

    @Override
    public Optional<ProductGroupDescription> findByProductGroupId(ProductGroupId productGroupId) {
        return queryDslRepository
                .findByProductGroupId(productGroupId.value())
                .map(
                        entity -> {
                            var imageEntities =
                                    queryDslRepository.findImagesByDescriptionId(entity.getId());
                            return mapper.toDomain(entity, imageEntities);
                        });
    }

    @Override
    public List<ProductGroupDescription> findByPublishStatus(
            DescriptionPublishStatus status, int limit) {
        return queryDslRepository.findByPublishStatus(status.name(), limit).stream()
                .map(
                        entity -> {
                            var imageEntities =
                                    queryDslRepository.findImagesByDescriptionId(entity.getId());
                            return mapper.toDomain(entity, imageEntities);
                        })
                .toList();
    }

    @Override
    public List<ProductGroupDescription> findByProductGroupIdIn(
            List<ProductGroupId> productGroupIds) {
        List<Long> rawIds = productGroupIds.stream().map(ProductGroupId::value).toList();
        List<ProductGroupDescriptionJpaEntity> entities =
                queryDslRepository.findByProductGroupIdIn(rawIds);

        if (entities.isEmpty()) {
            return List.of();
        }

        List<Long> descriptionIds =
                entities.stream().map(ProductGroupDescriptionJpaEntity::getId).toList();
        List<DescriptionImageJpaEntity> allImages =
                queryDslRepository.findImagesByDescriptionIds(descriptionIds);
        Map<Long, List<DescriptionImageJpaEntity>> imagesByDescriptionId =
                allImages.stream()
                        .collect(
                                Collectors.groupingBy(
                                        DescriptionImageJpaEntity::getProductGroupDescriptionId));

        return entities.stream()
                .map(
                        entity -> {
                            List<DescriptionImageJpaEntity> images =
                                    imagesByDescriptionId.getOrDefault(entity.getId(), List.of());
                            return mapper.toDomain(entity, images);
                        })
                .toList();
    }
}
