package com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.mapper;

import com.ryuqq.marketplace.adapter.in.rest.productgroupdescription.dto.response.DescriptionPublishStatusApiResponse;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;
import org.springframework.stereotype.Component;

@Component
public class ProductGroupDescriptionQueryApiMapper {

    public DescriptionPublishStatusApiResponse toResponse(DescriptionPublishStatusResult result) {
        return DescriptionPublishStatusApiResponse.from(result);
    }
}
