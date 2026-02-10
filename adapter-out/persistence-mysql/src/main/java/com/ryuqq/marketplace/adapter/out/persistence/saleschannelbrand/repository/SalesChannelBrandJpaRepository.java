package com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.repository;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.SalesChannelBrandJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** SalesChannelBrand JPA Repository (save 용). */
public interface SalesChannelBrandJpaRepository
        extends JpaRepository<SalesChannelBrandJpaEntity, Long> {}
