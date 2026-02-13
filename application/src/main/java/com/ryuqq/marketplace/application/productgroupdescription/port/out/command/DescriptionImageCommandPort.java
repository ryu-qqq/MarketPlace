package com.ryuqq.marketplace.application.productgroupdescription.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import java.util.List;

/** DescriptionImage Command Port. */
public interface DescriptionImageCommandPort {

    void deleteByDescriptionId(Long descriptionId);

    List<Long> persistAll(Long descriptionId, List<DescriptionImage> images);
}
