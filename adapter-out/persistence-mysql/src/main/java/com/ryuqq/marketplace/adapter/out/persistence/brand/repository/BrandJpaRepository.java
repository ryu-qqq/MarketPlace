package com.ryuqq.marketplace.adapter.out.persistence.brand.repository;

import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Brand JPA Repository
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
public interface BrandJpaRepository extends JpaRepository<BrandJpaEntity, Long> {

    /**
     * 브랜드 코드로 조회
     *
     * @param code 브랜드 코드
     * @return BrandJpaEntity
     */
    Optional<BrandJpaEntity> findByCode(String code);

    /**
     * 브랜드 코드 존재 여부 확인
     *
     * @param code 브랜드 코드
     * @return 존재 여부
     */
    boolean existsByCode(String code);

    /**
     * 정규 이름 존재 여부 확인
     *
     * @param canonicalName 정규 이름
     * @return 존재 여부
     */
    boolean existsByCanonicalName(String canonicalName);
}
