package com.ryuqq.marketplace.adapter.out.persistence.brand.repository;

import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandAliasJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * BrandAlias JPA Repository
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
public interface BrandAliasJpaRepository extends JpaRepository<BrandAliasJpaEntity, Long> {

    /**
     * 브랜드 ID로 별칭 목록 조회
     *
     * @param brandId 브랜드 ID
     * @return 별칭 목록
     */
    List<BrandAliasJpaEntity> findByBrandId(Long brandId);

    /**
     * 브랜드 ID로 별칭 삭제
     *
     * @param brandId 브랜드 ID
     */
    @Modifying
    @Query("DELETE FROM BrandAliasJpaEntity ba WHERE ba.brandId = :brandId")
    void deleteByBrandId(@Param("brandId") Long brandId);
}
