package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.composite.BrandPresetCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.mapper.BrandPresetJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository.BrandPresetQueryDslRepository;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetResult;
import com.ryuqq.marketplace.application.brandpreset.port.out.query.BrandPresetQueryPort;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import com.ryuqq.marketplace.domain.brandpreset.query.BrandPresetSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/** BrandPreset Query Adapter. */
@Component
public class BrandPresetQueryAdapter implements BrandPresetQueryPort {

    private final BrandPresetQueryDslRepository repository;
    private final BrandPresetJpaEntityMapper mapper;

    public BrandPresetQueryAdapter(
            BrandPresetQueryDslRepository repository, BrandPresetJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<BrandPreset> findById(BrandPresetId id) {
        return repository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<BrandPresetResult> findByCriteria(BrandPresetSearchCriteria criteria) {
        return repository.findByCriteria(criteria).stream().map(this::toResult).toList();
    }

    @Override
    public long countByCriteria(BrandPresetSearchCriteria criteria) {
        return repository.countByCriteria(criteria);
    }

    @Override
    public List<BrandPreset> findAllByIds(List<Long> ids) {
        return repository.findAllByIds(ids).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Long> findSalesChannelIdBySalesChannelBrandId(Long salesChannelBrandId) {
        return repository.findSalesChannelIdBySalesChannelBrandId(salesChannelBrandId);
    }

    private BrandPresetResult toResult(BrandPresetCompositeDto dto) {
        return new BrandPresetResult(
                dto.id(),
                dto.shopId(),
                dto.shopName(),
                dto.salesChannelId(),
                dto.salesChannelName(),
                dto.accountId(),
                dto.presetName(),
                dto.externalBrandName(),
                dto.externalBrandCode(),
                dto.createdAt());
    }
}
