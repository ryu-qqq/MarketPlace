package com.ryuqq.marketplace.adapter.out.persistence.inboundorder.repository;

import com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity.InboundOrderItemJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** InboundOrderItem JPA Repository. */
public interface InboundOrderItemJpaRepository
        extends JpaRepository<InboundOrderItemJpaEntity, Long> {

    List<InboundOrderItemJpaEntity> findByInboundOrderId(long inboundOrderId);

    List<InboundOrderItemJpaEntity> findByInboundOrderIdIn(List<Long> inboundOrderIds);
}
