package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.BrandPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.mapper.BrandPresetJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository.BrandPresetJpaRepository;
import com.ryuqq.marketplace.application.brandpreset.port.out.command.BrandPresetCommandPort;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import java.util.List;
import org.springframework.stereotype.Component;

/** BrandPreset Command Adapter. */
@Component
public class BrandPresetCommandAdapter implements BrandPresetCommandPort {

    private final BrandPresetJpaRepository repository;
    private final BrandPresetJpaEntityMapper mapper;

    public BrandPresetCommandAdapter(
            BrandPresetJpaRepository repository, BrandPresetJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(BrandPreset brandPreset) {
        BrandPresetJpaEntity entity = mapper.toEntity(brandPreset);
        BrandPresetJpaEntity saved = repository.save(entity);
        return saved.getId();
    }

    @Override
    public List<Long> persistAll(List<BrandPreset> brandPresets) {
        List<BrandPresetJpaEntity> entities = brandPresets.stream().map(mapper::toEntity).toList();
        List<BrandPresetJpaEntity> saved = repository.saveAll(entities);
        return saved.stream().map(BrandPresetJpaEntity::getId).toList();
    }
}
