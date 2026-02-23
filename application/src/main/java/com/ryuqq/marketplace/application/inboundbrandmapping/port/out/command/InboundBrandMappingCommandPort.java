package com.ryuqq.marketplace.application.inboundbrandmapping.port.out.command;

import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import java.util.List;

/** InboundBrandMapping 저장/수정 포트. */
public interface InboundBrandMappingCommandPort {

    Long persist(InboundBrandMapping mapping);

    List<Long> persistAll(List<InboundBrandMapping> mappings);
}
