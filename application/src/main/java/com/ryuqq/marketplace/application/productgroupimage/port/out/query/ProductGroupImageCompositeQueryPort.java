package com.ryuqq.marketplace.application.productgroupimage.port.out.query;

import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;

/**
 * ProductGroupImage Composite Query Port.
 *
 * <p>이미지 + 업로드 아웃박스 크로스 도메인 Composite Query Port.
 *
 * <p>Persistence 레이어에서 product_group_images와 image_upload_outboxes를 JOIN하여 조회합니다.
 */
public interface ProductGroupImageCompositeQueryPort {

    /**
     * 상품 그룹 이미지의 업로드 상태를 조회합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @return 이미지별 업로드 상태 집계 결과
     */
    ProductGroupImageUploadStatusResult findImageUploadStatus(Long productGroupId);
}
