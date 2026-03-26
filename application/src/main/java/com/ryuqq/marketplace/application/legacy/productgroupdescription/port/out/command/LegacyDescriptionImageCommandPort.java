package com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;

/** 레거시 상세설명 이미지 Command Port. */
public interface LegacyDescriptionImageCommandPort {

    Long persist(DescriptionImage image);
}
