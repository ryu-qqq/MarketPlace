package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.repository;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionValueJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** CanonicalOptionValue JPA Repository. */
public interface CanonicalOptionValueJpaRepository
        extends JpaRepository<CanonicalOptionValueJpaEntity, Long> {
    List<CanonicalOptionValueJpaEntity> findByCanonicalOptionGroupIdOrderBySortOrder(
            Long canonicalOptionGroupId);

    List<CanonicalOptionValueJpaEntity> findByCanonicalOptionGroupIdInOrderBySortOrder(
            List<Long> canonicalOptionGroupIds);
}
