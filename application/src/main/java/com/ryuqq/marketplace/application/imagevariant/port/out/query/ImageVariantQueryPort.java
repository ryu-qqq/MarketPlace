package com.ryuqq.marketplace.application.imagevariant.port.out.query;

import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import java.util.List;

/** ImageVariant Query Port. */
public interface ImageVariantQueryPort {

    /**
     * 소스 이미지 ID와 소스 타입으로 Variant 목록 조회.
     *
     * @param sourceImageId 소스 이미지 ID
     * @param sourceType 이미지 소스 타입
     * @return Variant 목록
     */
    List<ImageVariant> findBySourceImageId(Long sourceImageId, ImageSourceType sourceType);

    /**
     * 소스 이미지 ID 목록과 소스 타입으로 Variant 목록 배치 조회.
     *
     * @param sourceImageIds 소스 이미지 ID 목록
     * @param sourceType 이미지 소스 타입
     * @return Variant 목록
     */
    List<ImageVariant> findBySourceImageIds(List<Long> sourceImageIds, ImageSourceType sourceType);
}
