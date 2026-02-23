package com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.repository;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproduct.entity.OutboundProductJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboundProductJpaRepository
        extends JpaRepository<OutboundProductJpaEntity, Long> {

    boolean existsByProductGroupIdAndSalesChannelId(Long productGroupId, Long salesChannelId);

    Optional<OutboundProductJpaEntity> findByProductGroupIdAndSalesChannelId(
            Long productGroupId, Long salesChannelId);
}
