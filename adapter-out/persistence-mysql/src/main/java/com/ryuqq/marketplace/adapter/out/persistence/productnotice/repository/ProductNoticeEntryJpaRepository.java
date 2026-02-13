package com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeEntryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProductNoticeEntry JPA Repository.
 *
 * <p>PER-REP-001: 자식 엔티티 레포지토리는 deleteByXxx 파생 메서드 허용.
 */
public interface ProductNoticeEntryJpaRepository
        extends JpaRepository<ProductNoticeEntryJpaEntity, Long> {

    void deleteByProductNoticeId(Long productNoticeId);
}
