package com.ryuqq.marketplace.application.legacyconversion.port.out.command;

import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import java.util.List;

/** 레거시 주문 ID 매핑 명령 포트. */
public interface LegacyOrderIdMappingCommandPort {

    /**
     * LegacyOrderIdMapping 영속화.
     *
     * @param mapping 영속화할 매핑
     * @return 영속화된 매핑 ID
     */
    Long persist(LegacyOrderIdMapping mapping);

    /**
     * LegacyOrderIdMapping 목록 일괄 영속화.
     *
     * @param mappings 영속화할 매핑 목록
     * @return 영속화된 매핑 ID 목록
     */
    List<Long> persistAll(List<LegacyOrderIdMapping> mappings);
}
