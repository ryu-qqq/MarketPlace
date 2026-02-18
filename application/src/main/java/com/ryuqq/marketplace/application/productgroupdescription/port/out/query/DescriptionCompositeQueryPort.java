package com.ryuqq.marketplace.application.productgroupdescription.port.out.query;

import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPublishStatusResult;

/**
 * Description Composite Query Port.
 *
 * <p>상세설명 + 이미지 + 업로드 아웃박스 크로스 도메인 Composite Query Port.
 *
 * <p>Persistence 레이어에서 product_group_descriptions, description_images, image_upload_outboxes를
 * JOIN하여 조회합니다.
 */
public interface DescriptionCompositeQueryPort {

    /**
     * 상품 그룹 상세설명의 발행 상태를 조회합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @return 발행 상태 + 이미지별 업로드 상태 집계 결과
     */
    DescriptionPublishStatusResult findPublishStatus(Long productGroupId);
}
