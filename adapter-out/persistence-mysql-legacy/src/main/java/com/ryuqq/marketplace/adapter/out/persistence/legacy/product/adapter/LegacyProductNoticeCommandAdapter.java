package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductNoticeJpaRepository;
import com.ryuqq.marketplace.application.legacyproduct.port.out.command.LegacyProductNoticeCommandPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductNotice;
import org.springframework.stereotype.Component;

/**
 * 세토프 DB product_notice INSERT Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyProductNoticeCommandAdapter implements LegacyProductNoticeCommandPort {

    private final LegacyProductNoticeJpaRepository repository;
    private final LegacyProductCommandEntityMapper mapper;

    public LegacyProductNoticeCommandAdapter(
            LegacyProductNoticeJpaRepository repository, LegacyProductCommandEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void persist(LegacyProductGroupId productGroupId, LegacyProductNotice notice) {
        repository.save(mapper.toEntity(productGroupId, notice));
    }
}
