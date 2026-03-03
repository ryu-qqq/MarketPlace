package com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository;

import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderJpaEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** InboundOrder JPA Repository. */
public interface InboundOrderJpaRepository extends JpaRepository<InboundOrderJpaEntity, Long> {

    boolean existsBySalesChannelIdAndExternalOrderNo(long salesChannelId, String externalOrderNo);

    @Query(
            "SELECT MAX(e.externalOrderedAt) FROM InboundOrderJpaEntity e"
                    + " WHERE e.salesChannelId = :salesChannelId")
    Optional<Instant> findMaxExternalOrderedAtBySalesChannelId(long salesChannelId);

    List<InboundOrderJpaEntity> findByStatusOrderByIdAsc(InboundOrderJpaEntity.Status status);
}
