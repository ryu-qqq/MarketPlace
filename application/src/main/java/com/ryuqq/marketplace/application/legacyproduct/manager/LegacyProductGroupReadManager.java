package com.ryuqq.marketplace.application.legacyproduct.manager;

import com.ryuqq.marketplace.application.legacyproduct.port.out.query.LegacyProductGroupQueryPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.exception.LegacyProductGroupNotFoundException;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 세토프 상품그룹 Read Manager. */
@Component
public class LegacyProductGroupReadManager {

    private final LegacyProductGroupQueryPort queryPort;

    public LegacyProductGroupReadManager(LegacyProductGroupQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public LegacyProductGroup getById(LegacyProductGroupId productGroupId) {
        return queryPort
                .findById(productGroupId)
                .orElseThrow(() -> new LegacyProductGroupNotFoundException(productGroupId.value()));
    }
}
