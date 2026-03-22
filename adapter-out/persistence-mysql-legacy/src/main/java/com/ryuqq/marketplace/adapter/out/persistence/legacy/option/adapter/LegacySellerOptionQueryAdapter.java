package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository.LegacyProductGroupQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.product.port.out.query.LegacySellerOptionQueryPort;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionInputType;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionValueName;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB option_group + option_detail 조회 Adapter.
 *
 * <p>레거시 Entity → 표준 SellerOptionGroup/SellerOptionValue 도메인 변환.
 */
@Component
public class LegacySellerOptionQueryAdapter implements LegacySellerOptionQueryPort {

    private final LegacyProductGroupQueryDslRepository queryDslRepository;

    public LegacySellerOptionQueryAdapter(
            LegacyProductGroupQueryDslRepository queryDslRepository) {
        this.queryDslRepository = queryDslRepository;
    }

    @Override
    public List<SellerOptionGroup> findByProductGroupId(long productGroupId) {
        List<LegacyOptionGroupEntity> groupEntities =
                queryDslRepository.findOptionGroupsByProductGroupId(productGroupId);

        if (groupEntities.isEmpty()) {
            return List.of();
        }

        List<Long> groupIds = groupEntities.stream().map(LegacyOptionGroupEntity::getId).toList();
        List<LegacyOptionDetailEntity> detailEntities =
                queryDslRepository.findOptionDetailsByGroupIds(groupIds);

        Map<Long, List<SellerOptionValue>> valuesByGroupId = new LinkedHashMap<>();
        int sortOrder = 0;
        for (LegacyOptionDetailEntity detail : detailEntities) {
            valuesByGroupId
                    .computeIfAbsent(detail.getOptionGroupId(), k -> new ArrayList<>())
                    .add(SellerOptionValue.reconstitute(
                            SellerOptionValueId.of(detail.getId()),
                            SellerOptionGroupId.of(detail.getOptionGroupId()),
                            OptionValueName.of(detail.getOptionValue()),
                            null,
                            sortOrder++,
                            DeletionStatus.active()));
        }

        int groupSortOrder = 0;
        return groupEntities.stream()
                .map(group -> SellerOptionGroup.reconstitute(
                        SellerOptionGroupId.of(group.getId()),
                        ProductGroupId.of(group.getProductGroupId()),
                        OptionGroupName.of(group.getOptionName()),
                        null,
                        OptionInputType.PREDEFINED,
                        groupSortOrder,
                        valuesByGroupId.getOrDefault(group.getId(), List.of()),
                        DeletionStatus.active()))
                .toList();
    }
}
