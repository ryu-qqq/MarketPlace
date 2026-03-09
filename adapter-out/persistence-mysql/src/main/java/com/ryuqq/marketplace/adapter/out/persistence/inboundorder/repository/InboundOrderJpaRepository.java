package com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository;

import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** InboundOrder JPA Repository — save/delete만 담당. */
public interface InboundOrderJpaRepository extends JpaRepository<InboundOrderJpaEntity, Long> {}
