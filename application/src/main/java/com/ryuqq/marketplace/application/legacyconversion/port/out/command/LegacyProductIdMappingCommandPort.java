package com.ryuqq.marketplace.application.legacyconversion.port.out.command;

import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import java.util.List;

/** 레거시 상품(SKU) ID 매핑 명령 포트. */
public interface LegacyProductIdMappingCommandPort {

    /**
     * LegacyProductIdMapping 영속화 (생성).
     *
     * @param mapping 영속화할 매핑
     * @return 영속화된 매핑 ID
     */
    Long persist(LegacyProductIdMapping mapping);

    /**
     * 다건 LegacyProductIdMapping 일괄 영속화.
     *
     * @param mappings 영속화할 매핑 목록
     */
    void persistAll(List<LegacyProductIdMapping> mappings);
}
