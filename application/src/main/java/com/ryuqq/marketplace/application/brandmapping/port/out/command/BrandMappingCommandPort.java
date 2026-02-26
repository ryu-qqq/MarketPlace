package com.ryuqq.marketplace.application.brandmapping.port.out.command;

import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import java.util.List;

/** BrandMapping Command Port. */
public interface BrandMappingCommandPort {
    Long persist(BrandMapping brandMapping);

    List<Long> persistAll(List<BrandMapping> brandMappings);

    void deleteAllByPresetId(Long presetId);

    void deleteAllByPresetIds(List<Long> presetIds);
}
