package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.repository;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionGroupJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** CanonicalOptionGroup JPA Repository. */
public interface CanonicalOptionGroupJpaRepository
        extends JpaRepository<CanonicalOptionGroupJpaEntity, Long> {}
