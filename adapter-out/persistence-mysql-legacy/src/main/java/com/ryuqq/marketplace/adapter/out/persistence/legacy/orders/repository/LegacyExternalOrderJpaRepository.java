package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity.LegacyExternalOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** 레거시 외부 주문 JPA Repository. */
public interface LegacyExternalOrderJpaRepository
        extends JpaRepository<LegacyExternalOrderEntity, Long> {

    boolean existsBySiteIdAndExternalIdx(long siteId, long externalIdx);
}
