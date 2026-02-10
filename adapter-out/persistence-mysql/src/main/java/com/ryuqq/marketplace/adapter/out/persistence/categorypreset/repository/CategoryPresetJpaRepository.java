package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository;

import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.CategoryPresetJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** CategoryPreset JPA Repository (save/delete 용). */
public interface CategoryPresetJpaRepository extends JpaRepository<CategoryPresetJpaEntity, Long> {}
