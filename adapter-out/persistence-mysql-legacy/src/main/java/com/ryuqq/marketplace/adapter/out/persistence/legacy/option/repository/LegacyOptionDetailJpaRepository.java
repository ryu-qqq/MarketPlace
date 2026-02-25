package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyOptionDetailJpaRepository
        extends JpaRepository<LegacyOptionDetailEntity, Long> {

    Optional<LegacyOptionDetailEntity> findByOptionGroupIdAndOptionValue(
            Long optionGroupId, String optionValue);
}
