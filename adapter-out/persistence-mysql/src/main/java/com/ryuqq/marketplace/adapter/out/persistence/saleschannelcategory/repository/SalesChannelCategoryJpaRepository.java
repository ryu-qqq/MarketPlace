package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.repository;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.SalesChannelCategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** SalesChannelCategory JPA Repository (save 용). */
public interface SalesChannelCategoryJpaRepository
        extends JpaRepository<SalesChannelCategoryJpaEntity, Long> {}
