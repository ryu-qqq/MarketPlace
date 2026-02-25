package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.mapper.LegacyOptionCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.repository.LegacyOptionDetailJpaRepository;
import com.ryuqq.marketplace.application.legacyproduct.port.out.command.LegacyOptionDetailCommandPort;
import com.ryuqq.marketplace.domain.legacy.optiondetail.aggregate.LegacyOptionDetail;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB option_detail INSERT Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyOptionDetailCommandAdapter implements LegacyOptionDetailCommandPort {

    private final LegacyOptionDetailJpaRepository repository;
    private final LegacyOptionCommandEntityMapper mapper;

    public LegacyOptionDetailCommandAdapter(
            LegacyOptionDetailJpaRepository repository, LegacyOptionCommandEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(LegacyOptionDetail optionDetail) {
        LegacyOptionDetailEntity entity = mapper.toEntity(optionDetail);
        LegacyOptionDetailEntity saved = repository.save(entity);
        return saved.getId();
    }
}
