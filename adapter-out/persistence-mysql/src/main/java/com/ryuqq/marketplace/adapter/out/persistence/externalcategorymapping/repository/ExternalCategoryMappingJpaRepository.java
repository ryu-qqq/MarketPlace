package com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.repository;

import com.ryuqq.marketplace.adapter.out.persistence.externalcategorymapping.entity.ExternalCategoryMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** ExternalCategoryMapping JPA Repository (save/delete 용). */
public interface ExternalCategoryMappingJpaRepository
        extends JpaRepository<ExternalCategoryMappingJpaEntity, Long> {}
