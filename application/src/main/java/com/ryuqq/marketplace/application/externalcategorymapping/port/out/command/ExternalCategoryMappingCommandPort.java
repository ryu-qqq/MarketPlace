package com.ryuqq.marketplace.application.externalcategorymapping.port.out.command;

import com.ryuqq.marketplace.domain.externalcategorymapping.aggregate.ExternalCategoryMapping;
import java.util.List;

/** ExternalCategoryMapping 저장/수정 포트. */
public interface ExternalCategoryMappingCommandPort {

    Long persist(ExternalCategoryMapping mapping);

    List<Long> persistAll(List<ExternalCategoryMapping> mappings);
}
