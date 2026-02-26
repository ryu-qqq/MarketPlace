package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyDescriptionImageEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * LegacyDescriptionImageJpaRepository - 레거시 상세설명 이미지 JPA Repository.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface LegacyDescriptionImageJpaRepository
        extends JpaRepository<LegacyDescriptionImageEntity, Long> {

    List<LegacyDescriptionImageEntity> findAllByProductGroupIdAndDeletedFalse(long productGroupId);
}
