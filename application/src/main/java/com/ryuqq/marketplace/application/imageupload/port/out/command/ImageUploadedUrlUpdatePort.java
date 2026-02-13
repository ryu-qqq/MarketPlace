package com.ryuqq.marketplace.application.imageupload.port.out.command;

import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;

/**
 * ImageUploadedUrl Update Port.
 *
 * <p>이미지 S3 업로드 완료 후 uploaded_url을 업데이트하기 위한 포트입니다. sourceType별로 대상 테이블을 라우팅합니다.
 */
public interface ImageUploadedUrlUpdatePort {

    /**
     * 이미지의 uploaded_url을 업데이트합니다.
     *
     * @param sourceType 이미지 소스 타입 (PRODUCT_GROUP_IMAGE or DESCRIPTION_IMAGE)
     * @param sourceId 이미지 DB ID
     * @param uploadedUrl 업로드된 CDN URL
     */
    void updateUploadedUrl(ImageSourceType sourceType, Long sourceId, String uploadedUrl);
}
