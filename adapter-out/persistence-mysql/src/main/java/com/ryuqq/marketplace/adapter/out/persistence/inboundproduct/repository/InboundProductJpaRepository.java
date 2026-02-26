package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.repository;

import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity.InboundProductJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboundProductJpaRepository extends JpaRepository<InboundProductJpaEntity, Long> {

    Optional<InboundProductJpaEntity> findByInboundSourceIdAndExternalProductCode(
            Long inboundSourceId, String externalProductCode);
}
