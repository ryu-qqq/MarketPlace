package com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.repository;

import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.entity.InboundCategoryMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** InboundCategoryMapping JPA Repository (save/delete 용). */
public interface InboundCategoryMappingJpaRepository
        extends JpaRepository<InboundCategoryMappingJpaEntity, Long> {}
