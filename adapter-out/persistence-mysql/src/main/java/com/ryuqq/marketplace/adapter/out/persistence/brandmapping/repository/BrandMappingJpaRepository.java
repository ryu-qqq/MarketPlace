package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.repository;

import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.BrandMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** BrandMapping JPA Repository (save/delete 용). */
public interface BrandMappingJpaRepository extends JpaRepository<BrandMappingJpaEntity, Long> {}
