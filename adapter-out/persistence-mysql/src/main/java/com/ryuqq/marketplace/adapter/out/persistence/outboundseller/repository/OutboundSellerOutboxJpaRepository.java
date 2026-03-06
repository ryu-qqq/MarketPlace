package com.ryuqq.marketplace.adapter.out.persistence.outboundseller.repository;

import com.ryuqq.marketplace.adapter.out.persistence.outboundseller.entity.OutboundSellerOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundSellerOutboxJpaRepository
        extends JpaRepository<OutboundSellerOutboxJpaEntity, Long> {}
