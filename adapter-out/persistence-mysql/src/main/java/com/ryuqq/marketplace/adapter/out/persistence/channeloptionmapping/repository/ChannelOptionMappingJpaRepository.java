package com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.repository;

import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.entity.ChannelOptionMappingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** ChannelOptionMapping JPA Repository (save 용). */
public interface ChannelOptionMappingJpaRepository
        extends JpaRepository<ChannelOptionMappingJpaEntity, Long> {}
