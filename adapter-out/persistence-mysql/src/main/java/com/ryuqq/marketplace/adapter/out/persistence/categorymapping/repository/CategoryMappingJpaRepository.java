package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository;

import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.CategoryMappingJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** CategoryMapping JPA Repository (save/delete 용). */
public interface CategoryMappingJpaRepository
        extends JpaRepository<CategoryMappingJpaEntity, Long> {

    @Modifying
    @Query("DELETE FROM CategoryMappingJpaEntity c WHERE c.presetId = :presetId")
    void deleteAllByPresetId(@Param("presetId") Long presetId);

    @Modifying
    @Query("DELETE FROM CategoryMappingJpaEntity c WHERE c.presetId IN :presetIds")
    void deleteAllByPresetIdIn(@Param("presetIds") List<Long> presetIds);
}
