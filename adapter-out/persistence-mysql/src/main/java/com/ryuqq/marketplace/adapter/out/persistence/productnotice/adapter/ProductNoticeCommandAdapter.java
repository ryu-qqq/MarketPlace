package com.ryuqq.marketplace.adapter.out.persistence.productnotice.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.mapper.ProductNoticeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository.ProductNoticeJpaRepository;
import com.ryuqq.marketplace.application.productnotice.port.out.command.ProductNoticeCommandPort;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import org.springframework.stereotype.Component;

/**
 * ProductNoticeCommandAdapter - 상품 고시정보 명령 어댑터.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class ProductNoticeCommandAdapter implements ProductNoticeCommandPort {

    private final ProductNoticeJpaRepository repository;
    private final ProductNoticeJpaEntityMapper mapper;

    public ProductNoticeCommandAdapter(
            ProductNoticeJpaRepository repository, ProductNoticeJpaEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Long persist(ProductNotice productNotice) {
        ProductNoticeJpaEntity entity = mapper.toEntity(productNotice);
        ProductNoticeJpaEntity saved = repository.save(entity);
        return saved.getId();
    }
}
