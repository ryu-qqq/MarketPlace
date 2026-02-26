package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.repository;

import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.BrandMappingJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** BrandMapping JPA Repository (save/delete 용). */
public interface BrandMappingJpaRepository extends JpaRepository<BrandMappingJpaEntity, Long> {

    @Modifying
    @Query("DELETE FROM BrandMappingJpaEntity b WHERE b.presetId = :presetId")
    void deleteAllByPresetId(@Param("presetId") Long presetId);

    @Modifying
    @Query("DELETE FROM BrandMappingJpaEntity b WHERE b.presetId IN :presetIds")
    void deleteAllByPresetIdIn(@Param("presetIds") List<Long> presetIds);
}
