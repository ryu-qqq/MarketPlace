package com.ryuqq.marketplace.adapter.out.persistence.setofsync.repository;

import com.ryuqq.marketplace.adapter.out.persistence.setofsync.entity.SetofSyncOutboxJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetofSyncOutboxJpaRepository
        extends JpaRepository<SetofSyncOutboxJpaEntity, Long> {}
