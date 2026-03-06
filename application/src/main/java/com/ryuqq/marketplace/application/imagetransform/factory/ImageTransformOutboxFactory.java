package com.ryuqq.marketplace.application.imagetransform.factory;

import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 이미지 변환 Outbox Factory.
 *
 * <p>소스 이미지 ID와 업로드된 URL로 모든 Variant 타입에 대한 Outbox 목록을 생성합니다.
 */
@Component
public class ImageTransformOutboxFactory {

    /**
     * 모든 Variant 타입에 대한 Outbox 목록을 생성합니다.
     *
     * @param sourceImageId 소스 이미지 ID
     * @param sourceType 이미지 소스 타입
     * @param uploadedUrl 업로드된 CDN URL
     * @param fileAssetId FileFlow 에셋 ID (다운로드 완료 시 획득)
     * @return 생성된 Outbox 목록 (Variant 타입별 1개씩)
     */
    public List<ImageTransformOutbox> createOutboxes(
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageUrl uploadedUrl,
            String fileAssetId) {
        return createOutboxes(
                sourceImageId,
                sourceType,
                uploadedUrl,
                fileAssetId,
                List.of(ImageVariantType.values()));
    }

    /**
     * 지정된 Variant 타입에 대한 Outbox 목록을 생성합니다 (fileAssetId 없이).
     *
     * <p>수동 변환 요청 등 fileAssetId를 알 수 없는 경우에 사용합니다.
     *
     * @param sourceImageId 소스 이미지 ID
     * @param sourceType 이미지 소스 타입
     * @param uploadedUrl 업로드된 CDN URL
     * @param variantTypes 변환 대상 Variant 타입 목록
     * @return 생성된 Outbox 목록
     */
    public List<ImageTransformOutbox> createOutboxes(
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageUrl uploadedUrl,
            List<ImageVariantType> variantTypes) {
        return createOutboxes(sourceImageId, sourceType, uploadedUrl, null, variantTypes);
    }

    /**
     * 지정된 Variant 타입에 대한 Outbox 목록을 생성합니다.
     *
     * @param sourceImageId 소스 이미지 ID
     * @param sourceType 이미지 소스 타입
     * @param uploadedUrl 업로드된 CDN URL
     * @param fileAssetId FileFlow 에셋 ID (다운로드 완료 시 획득, nullable)
     * @param variantTypes 변환 대상 Variant 타입 목록
     * @return 생성된 Outbox 목록
     */
    public List<ImageTransformOutbox> createOutboxes(
            Long sourceImageId,
            ImageSourceType sourceType,
            ImageUrl uploadedUrl,
            String fileAssetId,
            List<ImageVariantType> variantTypes) {
        Instant now = Instant.now();
        List<ImageTransformOutbox> outboxes = new ArrayList<>();

        for (ImageVariantType variantType : variantTypes) {
            outboxes.add(
                    ImageTransformOutbox.forNew(
                            sourceImageId, sourceType, uploadedUrl, variantType, fileAssetId, now));
        }

        return outboxes;
    }
}
