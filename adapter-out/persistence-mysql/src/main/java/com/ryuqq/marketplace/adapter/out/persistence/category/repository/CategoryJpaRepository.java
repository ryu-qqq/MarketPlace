package com.ryuqq.marketplace.adapter.out.persistence.category.repository;

import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * CategoryJpaRepository - Category JPA Repository
 */
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {

    boolean existsByCode(String code);

    Optional<CategoryJpaEntity> findByCode(String code);
}
