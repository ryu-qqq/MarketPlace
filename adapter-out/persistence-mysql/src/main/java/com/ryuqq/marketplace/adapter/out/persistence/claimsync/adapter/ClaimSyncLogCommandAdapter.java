package com.ryuqq.marketplace.adapter.out.persistence.claimsync.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.claimsync.mapper.ClaimSyncLogPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.claimsync.repository.ClaimSyncLogJpaRepository;
import com.ryuqq.marketplace.application.claimsync.port.out.command.ClaimSyncLogCommandPort;
import com.ryuqq.marketplace.domain.claimsync.aggregate.ClaimSyncLog;
import org.springframework.stereotype.Component;

/** 클레임 동기화 로그 Command Adapter. */
@Component
public class ClaimSyncLogCommandAdapter implements ClaimSyncLogCommandPort {

    private final ClaimSyncLogJpaRepository jpaRepository;
    private final ClaimSyncLogPersistenceMapper mapper;

    public ClaimSyncLogCommandAdapter(
            ClaimSyncLogJpaRepository jpaRepository, ClaimSyncLogPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void persist(ClaimSyncLog syncLog) {
        jpaRepository.save(mapper.toEntity(syncLog));
    }
}
