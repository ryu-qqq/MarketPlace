package com.ryuqq.marketplace.application.productgroupdescription.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;

/** DescriptionImage Command Port. */
public interface DescriptionImageCommandPort {

    Long persist(DescriptionImage image);
}
