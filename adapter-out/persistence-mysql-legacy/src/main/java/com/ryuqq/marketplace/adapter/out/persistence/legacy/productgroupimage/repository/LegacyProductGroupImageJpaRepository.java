package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity.LegacyProductGroupImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 레거시 상품그룹 이미지 JPA Repository.
 *
 * <p>이미지 교체 시 신규 이미지 INSERT에 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface LegacyProductGroupImageJpaRepository
        extends JpaRepository<LegacyProductGroupImageEntity, Long> {}
