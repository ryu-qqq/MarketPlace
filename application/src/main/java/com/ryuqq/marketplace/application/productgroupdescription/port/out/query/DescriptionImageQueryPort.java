package com.ryuqq.marketplace.application.productgroupdescription.port.out.query;

import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import java.util.Optional;

/** DescriptionImage Query Port. */
public interface DescriptionImageQueryPort {

    Optional<DescriptionImage> findById(Long id);
}
