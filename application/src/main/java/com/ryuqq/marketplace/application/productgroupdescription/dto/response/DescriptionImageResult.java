package com.ryuqq.marketplace.application.productgroupdescription.dto.response;

import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;

/** 상세설명 이미지 조회 결과 DTO. */
public record DescriptionImageResult(Long id, String originUrl, String uploadedUrl, int sortOrder) {

    public static DescriptionImageResult from(DescriptionImage image) {
        return new DescriptionImageResult(
                image.idValue(),
                image.originUrlValue(),
                image.uploadedUrlValue(),
                image.sortOrder());
    }
}
