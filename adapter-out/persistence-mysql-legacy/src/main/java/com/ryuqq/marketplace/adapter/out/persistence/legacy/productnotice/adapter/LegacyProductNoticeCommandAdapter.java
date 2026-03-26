package com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.entity.LegacyProductNoticeEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.repository.LegacyProductNoticeJpaRepository;
import com.ryuqq.marketplace.application.legacy.productnotice.port.out.command.LegacyProductNoticeCommandPort;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품 고시정보 저장 Adapter.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 */
@Component
public class LegacyProductNoticeCommandAdapter implements LegacyProductNoticeCommandPort {

    private final LegacyProductNoticeJpaRepository repository;

    public LegacyProductNoticeCommandAdapter(LegacyProductNoticeJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void persist(long productGroupId, Map<String, String> flatFields) {
        LegacyProductNoticeEntity entity =
                LegacyProductNoticeEntity.create(
                        productGroupId,
                        flatFields.getOrDefault("material", ""),
                        flatFields.getOrDefault("color", ""),
                        flatFields.getOrDefault("size", ""),
                        flatFields.getOrDefault("manufacturer", ""),
                        flatFields.getOrDefault("made_in", ""),
                        flatFields.getOrDefault("wash_care", ""),
                        flatFields.getOrDefault("release_date", ""),
                        flatFields.getOrDefault("quality_assurance", ""),
                        flatFields.getOrDefault("cs_info", ""));

        repository.save(entity);
    }
}
