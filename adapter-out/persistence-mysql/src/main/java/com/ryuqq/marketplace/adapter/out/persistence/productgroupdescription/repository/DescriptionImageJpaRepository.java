package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DescriptionImageJpaRepository - 상세설명 이미지 JPA Repository.
 *
 * <p>PER-REP-001: Child Repository는 deleteByXxx 메서드 허용.
 */
public interface DescriptionImageJpaRepository
        extends JpaRepository<DescriptionImageJpaEntity, Long> {

    void deleteByProductGroupDescriptionId(Long productGroupDescriptionId);
}
