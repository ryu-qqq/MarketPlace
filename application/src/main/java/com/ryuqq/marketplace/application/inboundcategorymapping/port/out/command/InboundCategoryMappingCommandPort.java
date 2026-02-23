package com.ryuqq.marketplace.application.inboundcategorymapping.port.out.command;

import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import java.util.List;

/** InboundCategoryMapping 저장/수정 포트. */
public interface InboundCategoryMappingCommandPort {

    Long persist(InboundCategoryMapping mapping);

    List<Long> persistAll(List<InboundCategoryMapping> mappings);
}
