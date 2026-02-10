package com.ryuqq.marketplace.application.categorymapping.port.out.command;

import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;

/** CategoryMapping Command Port. */
public interface CategoryMappingCommandPort {
    Long persist(CategoryMapping categoryMapping);
}
