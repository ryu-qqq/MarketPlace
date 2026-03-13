package com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.repository;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.entity.OutboundProductImageJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundProductImageJpaRepository
        extends JpaRepository<OutboundProductImageJpaEntity, Long> {

    List<OutboundProductImageJpaEntity> findByOutboundProductIdAndDeletedFalse(
            Long outboundProductId);
}
