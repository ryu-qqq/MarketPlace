package com.ryuqq.marketplace.adapter.out.persistence.inboundqna.repository;

import com.ryuqq.marketplace.adapter.out.persistence.inboundqna.entity.InboundQnaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** InboundQna JPA Repository. */
public interface InboundQnaJpaRepository extends JpaRepository<InboundQnaJpaEntity, Long> {}
