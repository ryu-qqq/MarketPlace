package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.repository;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegacyOptionGroupJpaRepository
        extends JpaRepository<LegacyOptionGroupEntity, Long> {

    Optional<LegacyOptionGroupEntity> findByOptionName(String optionName);
}
