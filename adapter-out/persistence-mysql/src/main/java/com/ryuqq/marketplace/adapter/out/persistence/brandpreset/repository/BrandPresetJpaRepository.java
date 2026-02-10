package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository;

import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.BrandPresetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** BrandPreset JPA Repository (save/delete 용). */
public interface BrandPresetJpaRepository extends JpaRepository<BrandPresetJpaEntity, Long> {}
