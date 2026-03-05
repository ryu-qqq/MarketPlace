package com.ryuqq.marketplace.application.imagetransform.manager;

import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.port.out.client.ImageTransformClient;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import org.springframework.stereotype.Component;

/**
 * ImageTransform Manager.
 *
 * <p>ImageTransformClient를 래핑하여 이미지 변환 작업을 위임합니다.
 */
@Component
public class ImageTransformManager {

    private final ImageTransformClient transformClient;

    public ImageTransformManager(ImageTransformClient transformClient) {
        this.transformClient = transformClient;
    }

    public ImageTransformResponse createTransformRequest(
            String uploadedUrl,
            ImageVariantType variantType,
            String fileAssetId,
            String callbackUrl) {
        return transformClient.createTransformRequest(
                uploadedUrl, variantType, fileAssetId, callbackUrl);
    }

    public ImageTransformResponse getTransformRequest(String transformRequestId) {
        return transformClient.getTransformRequest(transformRequestId);
    }
}
