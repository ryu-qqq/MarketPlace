package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.repository;

import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity.InboundBrandMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** InboundBrandMapping JPA Repository (save/delete 용). */
public interface InboundBrandMappingJpaRepository
        extends JpaRepository<InboundBrandMappingJpaEntity, Long> {}
