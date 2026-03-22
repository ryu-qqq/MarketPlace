package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity.LegacyProductGroupImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** 레거시 상품그룹 이미지 JPA Repository. */
public interface LegacyProductGroupImageJpaRepository
        extends JpaRepository<LegacyProductGroupImageEntity, Long> {

    /** 상품그룹의 기존 이미지 전체 soft delete. */
    @Modifying
    @Query(
            "UPDATE LegacyProductGroupImageEntity e SET e.deleteYn = 'Y'"
                    + " WHERE e.productGroupId = :productGroupId AND e.deleteYn = 'N'")
    void softDeleteAllByProductGroupId(@Param("productGroupId") long productGroupId);
}
