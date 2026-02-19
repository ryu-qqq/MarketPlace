package com.ryuqq.marketplace.application.imageupload.internal;

import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;

/**
 * 이미지 업로드 완료 전략 인터페이스.
 *
 * <p>sourceType별로 도메인 모델 조회 → uploaded_url 업데이트 → 영속화를 수행합니다.
 */
public interface ImageUploadCompletionStrategy {

    /** 이 전략이 처리할 수 있는 소스 타입인지 판별한다. */
    boolean supports(ImageSourceType sourceType);

    /**
     * 업로드 완료 처리.
     *
     * @param sourceId 대상 이미지 ID
     * @param uploadedUrl 업로드된 CDN URL
     * @param fileAssetId FileFlow 에셋 ID (null 가능)
     */
    void complete(Long sourceId, ImageUrl uploadedUrl, String fileAssetId);
}
