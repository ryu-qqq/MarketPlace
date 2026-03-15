package com.ryuqq.marketplace.application.imagevariantsync.port.out.client;

import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import java.util.List;

/**
 * 이미지 Variant Sync Client Port.
 *
 * <p>세토프 Sync API를 호출하여 이미지 Variant 정보를 동기화합니다.
 */
public interface ImageVariantSyncClient {

    /**
     * 해당 sourceImageId의 Variant 목록을 세토프로 동기화합니다.
     *
     * @param sourceImageId 소스 이미지 ID
     * @param sourceType 이미지 소스 타입
     * @param variants 동기화할 Variant 목록
     */
    void syncVariants(long sourceImageId, String sourceType, List<ImageVariantResult> variants);
}
