package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductDeliveryEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductNoticeEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductStockEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductGroupEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductGroupQueryDslRepository;
import com.ryuqq.marketplace.application.legacyproduct.dto.setof.SetofProductGroupComposite;
import com.ryuqq.marketplace.application.legacyproduct.port.out.query.SetofProductGroupQueryPort;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * SetofProductGroupQueryAdapter - 세토프 DB 상품그룹 조회 Adapter.
 *
 * <p>배치 로딩 패턴으로 상품그룹 + 관련 데이터를 한번에 조회합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class SetofProductGroupQueryAdapter implements SetofProductGroupQueryPort {

    private final LegacyProductGroupQueryDslRepository queryDslRepository;
    private final LegacyProductGroupEntityMapper mapper;

    public SetofProductGroupQueryAdapter(
            LegacyProductGroupQueryDslRepository queryDslRepository,
            LegacyProductGroupEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SetofProductGroupComposite> findByProductGroupId(long productGroupId) {
        Optional<LegacyProductGroupEntity> productGroupOpt =
                queryDslRepository.findProductGroupById(productGroupId);

        if (productGroupOpt.isEmpty()) {
            return Optional.empty();
        }

        LegacyProductGroupEntity productGroup = productGroupOpt.get();

        List<LegacyProductEntity> products =
                queryDslRepository.findProductsByProductGroupId(productGroupId);

        List<Long> productIds = products.stream().map(LegacyProductEntity::getId).toList();

        List<LegacyProductOptionEntity> productOptions =
                queryDslRepository.findProductOptionsByProductIds(productIds);

        List<LegacyProductStockEntity> stocks =
                queryDslRepository.findStocksByProductIds(productIds);

        List<LegacyProductGroupImageEntity> images =
                queryDslRepository.findImagesByProductGroupId(productGroupId);

        LegacyProductGroupDetailDescriptionEntity detailDescription =
                queryDslRepository
                        .findDetailDescriptionByProductGroupId(productGroupId)
                        .orElse(null);

        LegacyProductNoticeEntity notice =
                queryDslRepository.findNoticeByProductGroupId(productGroupId).orElse(null);

        LegacyProductDeliveryEntity delivery =
                queryDslRepository.findDeliveryByProductGroupId(productGroupId).orElse(null);

        List<Long> optionGroupIds =
                productOptions.stream()
                        .map(LegacyProductOptionEntity::getOptionGroupId)
                        .distinct()
                        .collect(Collectors.toList());

        List<Long> optionDetailIds =
                productOptions.stream()
                        .map(LegacyProductOptionEntity::getOptionDetailId)
                        .distinct()
                        .collect(Collectors.toList());

        List<LegacyOptionGroupEntity> optionGroups =
                queryDslRepository.findOptionGroupsByIds(optionGroupIds);

        List<LegacyOptionDetailEntity> optionDetails =
                queryDslRepository.findOptionDetailsByIds(optionDetailIds);

        return Optional.of(
                mapper.toComposite(
                        productGroup,
                        products,
                        productOptions,
                        stocks,
                        images,
                        detailDescription,
                        notice,
                        delivery,
                        optionGroups,
                        optionDetails));
    }
}
