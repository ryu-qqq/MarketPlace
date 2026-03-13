package com.ryuqq.marketplace.application.outboundproductimage.dto;

import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;

/**
 * 외부 채널에 업로드 완료된 단건 이미지 결과.
 *
 * @param externalUrl 외부 채널 CDN URL (예: shop-phinf.pstatic.net)
 * @param imageType 이미지 타입 (THUMBNAIL/DETAIL)
 * @param sortOrder 정렬 순서
 */
public record ResolvedExternalImage(String externalUrl, ImageType imageType, int sortOrder) {

    public boolean isThumbnail() {
        return imageType == ImageType.THUMBNAIL;
    }
}
