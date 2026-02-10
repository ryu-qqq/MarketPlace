package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** SalesChannel JPA Repository (save 용). */
public interface SalesChannelJpaRepository extends JpaRepository<SalesChannelJpaEntity, Long> {}
