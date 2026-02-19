package com.ryuqq.marketplace.application.productgroupimage.service.query;

import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageCompositeReadManager;
import com.ryuqq.marketplace.application.productgroupimage.port.in.query.GetProductGroupImageUploadStatusUseCase;
import org.springframework.stereotype.Service;

/** 상품 그룹 이미지 업로드 상태 조회 서비스. */
@Service
public class GetProductGroupImageUploadStatusService
        implements GetProductGroupImageUploadStatusUseCase {

    private final ProductGroupImageCompositeReadManager compositeReadManager;

    public GetProductGroupImageUploadStatusService(
            ProductGroupImageCompositeReadManager compositeReadManager) {
        this.compositeReadManager = compositeReadManager;
    }

    @Override
    public ProductGroupImageUploadStatusResult execute(Long productGroupId) {
        return compositeReadManager.getImageUploadStatus(productGroupId);
    }
}
