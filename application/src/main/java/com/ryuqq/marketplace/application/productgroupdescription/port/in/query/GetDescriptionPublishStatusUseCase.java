package com.ryuqq.marketplace.application.productgroupdescription.port.in.query;

import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;

public interface GetDescriptionPublishStatusUseCase {
    DescriptionPublishStatusResult execute(Long productGroupId);
}
