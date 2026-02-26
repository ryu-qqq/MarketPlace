package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.repository;

import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity.InboundSourceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** InboundSource JPA Repository (save/delete 용). */
public interface InboundSourceJpaRepository extends JpaRepository<InboundSourceJpaEntity, Long> {}
