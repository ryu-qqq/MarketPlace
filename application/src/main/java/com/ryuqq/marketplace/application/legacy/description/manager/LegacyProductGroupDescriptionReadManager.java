package com.ryuqq.marketplace.application.legacy.description.manager;

import com.ryuqq.marketplace.application.legacy.description.port.out.query.LegacyProductGroupDescriptionReadPort;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroupDescription;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 상품그룹 상세설명 Read Manager. */
@Component
public class LegacyProductGroupDescriptionReadManager {

    private final LegacyProductGroupDescriptionReadPort readPort;

    public LegacyProductGroupDescriptionReadManager(
            LegacyProductGroupDescriptionReadPort readPort) {
        this.readPort = readPort;
    }

    /** productGroupId로 상세설명 조회. 존재하지 않으면 신규 생성 대상. */
    @Transactional(readOnly = true)
    public LegacyProductGroupDescription getByProductGroupId(long productGroupId) {
        return readPort.findByProductGroupId(productGroupId)
                .orElseGet(() -> LegacyProductGroupDescription.forNew(productGroupId, null));
    }
}
