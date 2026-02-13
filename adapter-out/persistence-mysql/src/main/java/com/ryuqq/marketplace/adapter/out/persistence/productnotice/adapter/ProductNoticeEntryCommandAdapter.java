package com.ryuqq.marketplace.adapter.out.persistence.productnotice.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.mapper.ProductNoticeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository.ProductNoticeEntryJpaRepository;
import com.ryuqq.marketplace.application.productnotice.port.out.command.ProductNoticeEntryCommandPort;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ProductNoticeEntryCommandAdapter - 고시정보 항목 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class ProductNoticeEntryCommandAdapter implements ProductNoticeEntryCommandPort {

    private final ProductNoticeEntryJpaRepository repository;
    private final ProductNoticeJpaEntityMapper mapper;

    public ProductNoticeEntryCommandAdapter(
            ProductNoticeEntryJpaRepository repository, ProductNoticeJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void deleteByNoticeId(Long productNoticeId) {
        repository.deleteByProductNoticeId(productNoticeId);
    }

    @Override
    public void persistAll(Long productNoticeId, List<ProductNoticeEntry> entries) {
        if (entries.isEmpty()) {
            return;
        }
        List<ProductNoticeEntryJpaEntity> entities1 =
                entries.stream()
                        .map(entry -> mapper.toEntryEntity(entry, productNoticeId))
                        .toList();
        repository.saveAll(entities1);
    }
}
