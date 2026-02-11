package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository;

import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.CategoryMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** CategoryMapping JPA Repository (save/delete 용). */
public interface CategoryMappingJpaRepository
        extends JpaRepository<CategoryMappingJpaEntity, Long> {

    void deleteAllByPresetId(Long presetId);

    void deleteAllByPresetIdIn(java.util.List<Long> presetIds);
}
