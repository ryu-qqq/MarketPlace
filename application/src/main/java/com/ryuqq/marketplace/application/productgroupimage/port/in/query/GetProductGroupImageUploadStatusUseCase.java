package com.ryuqq.marketplace.application.productgroupimage.port.in.query;

import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;

public interface GetProductGroupImageUploadStatusUseCase {
    ProductGroupImageUploadStatusResult execute(Long productGroupId);
}
