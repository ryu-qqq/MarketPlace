package com.ryuqq.marketplace.adapter.out.persistence.refund.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.refund.mapper.RefundPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.refund.repository.RefundClaimJpaRepository;
import com.ryuqq.marketplace.application.refund.port.out.command.RefundCommandPort;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import org.springframework.stereotype.Component;

/** 환불 클레임 Command Adapter. */
@Component
public class RefundCommandAdapter implements RefundCommandPort {

    private final RefundClaimJpaRepository claimRepository;
    private final RefundPersistenceMapper mapper;

    public RefundCommandAdapter(
            RefundClaimJpaRepository claimRepository,
            RefundPersistenceMapper mapper) {
        this.claimRepository = claimRepository;
        this.mapper = mapper;
    }

    @Override
    public void persist(RefundClaim refundClaim) {
        claimRepository.save(mapper.toEntity(refundClaim));
    }
}
