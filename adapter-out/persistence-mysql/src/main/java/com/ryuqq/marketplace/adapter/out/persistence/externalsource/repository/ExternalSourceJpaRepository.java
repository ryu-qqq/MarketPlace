package com.ryuqq.marketplace.adapter.out.persistence.externalsource.repository;

import com.ryuqq.marketplace.adapter.out.persistence.externalsource.entity.ExternalSourceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** ExternalSource JPA Repository (save/delete 용). */
public interface ExternalSourceJpaRepository extends JpaRepository<ExternalSourceJpaEntity, Long> {}
