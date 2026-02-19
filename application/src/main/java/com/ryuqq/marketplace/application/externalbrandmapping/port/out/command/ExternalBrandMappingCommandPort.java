package com.ryuqq.marketplace.application.externalbrandmapping.port.out.command;

import com.ryuqq.marketplace.domain.externalbrandmapping.aggregate.ExternalBrandMapping;
import java.util.List;

/** ExternalBrandMapping 저장/수정 포트. */
public interface ExternalBrandMappingCommandPort {

    Long persist(ExternalBrandMapping mapping);

    List<Long> persistAll(List<ExternalBrandMapping> mappings);
}
