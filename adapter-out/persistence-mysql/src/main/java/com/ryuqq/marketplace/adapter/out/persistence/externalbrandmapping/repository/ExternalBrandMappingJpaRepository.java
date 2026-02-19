package com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.repository;

import com.ryuqq.marketplace.adapter.out.persistence.externalbrandmapping.entity.ExternalBrandMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** ExternalBrandMapping JPA Repository (save/delete 용). */
public interface ExternalBrandMappingJpaRepository
        extends JpaRepository<ExternalBrandMappingJpaEntity, Long> {}
