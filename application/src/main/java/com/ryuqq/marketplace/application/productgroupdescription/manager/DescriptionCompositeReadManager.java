package com.ryuqq.marketplace.application.productgroupdescription.manager;

import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import com.ryuqq.marketplace.application.productgroupdescription.port.out.query.DescriptionCompositeQueryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Description Composite Read Manager. */
@Component
public class DescriptionCompositeReadManager {

    private final DescriptionCompositeQueryPort compositeQueryPort;

    public DescriptionCompositeReadManager(DescriptionCompositeQueryPort compositeQueryPort) {
        this.compositeQueryPort = compositeQueryPort;
    }

    @Transactional(readOnly = true)
    public DescriptionPublishStatusResult getPublishStatus(Long productGroupId) {
        return compositeQueryPort.findPublishStatus(productGroupId);
    }
}
