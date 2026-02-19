package com.ryuqq.marketplace.application.productgroupdescription.manager;

import com.ryuqq.marketplace.application.productgroupdescription.port.out.query.DescriptionImageQueryPort;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.exception.DescriptionImageNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** DescriptionImage Read Manager. */
@Component
public class DescriptionImageReadManager {

    private final DescriptionImageQueryPort queryPort;

    public DescriptionImageReadManager(DescriptionImageQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public DescriptionImage getById(Long id) {
        return queryPort.findById(id).orElseThrow(() -> new DescriptionImageNotFoundException(id));
    }
}
