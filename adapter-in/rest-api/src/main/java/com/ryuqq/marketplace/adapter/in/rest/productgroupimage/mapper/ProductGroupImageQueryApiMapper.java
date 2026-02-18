package com.ryuqq.marketplace.adapter.in.rest.productgroupimage.mapper;

import com.ryuqq.marketplace.adapter.in.rest.productgroupimage.dto.response.ProductGroupImageUploadStatusApiResponse;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import org.springframework.stereotype.Component;

@Component
public class ProductGroupImageQueryApiMapper {

    public ProductGroupImageUploadStatusApiResponse toResponse(
            ProductGroupImageUploadStatusResult result) {
        return ProductGroupImageUploadStatusApiResponse.from(result);
    }
}
