package com.ryuqq.marketplace.adapter.out.persistence.category.repository;

import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Category JPA Repository (save 용). */
public interface CategoryJpaRepository extends JpaRepository<CategoryJpaEntity, Long> {}
