package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.repository;

import com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity.InboundProductJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InboundProductJpaRepository extends JpaRepository<InboundProductJpaEntity, Long> {

    Optional<InboundProductJpaEntity> findByInboundSourceIdAndExternalProductCode(
            Long inboundSourceId, String externalProductCode);

    List<InboundProductJpaEntity> findByStatusOrderByCreatedAtAsc(String status, Pageable pageable);
}
