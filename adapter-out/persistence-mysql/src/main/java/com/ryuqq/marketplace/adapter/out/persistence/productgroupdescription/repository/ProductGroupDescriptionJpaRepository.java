package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProductGroupDescriptionJpaRepository - 상품 그룹 상세설명 JPA Repository.
 *
 * <p>PER-REP-001: MAIN Repository는 save/saveAll만 제공.
 */
public interface ProductGroupDescriptionJpaRepository
        extends JpaRepository<ProductGroupDescriptionJpaEntity, Long> {}
