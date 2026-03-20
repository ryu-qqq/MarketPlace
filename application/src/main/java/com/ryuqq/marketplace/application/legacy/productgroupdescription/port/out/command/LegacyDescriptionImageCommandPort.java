package com.ryuqq.marketplace.application.legacy.productgroupdescription.port.out.command;

import com.ryuqq.marketplace.domain.legacy.productdescription.aggregate.LegacyDescriptionImage;
import java.util.List;

/**
 * 레거시 상세설명 이미지 Command Port.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface LegacyDescriptionImageCommandPort {

    void persistAll(List<LegacyDescriptionImage> images);

    void softDeleteAll(List<LegacyDescriptionImage> images);
}
