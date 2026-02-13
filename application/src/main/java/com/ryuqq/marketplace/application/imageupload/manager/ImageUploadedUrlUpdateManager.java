package com.ryuqq.marketplace.application.imageupload.manager;

import com.ryuqq.marketplace.application.imageupload.port.out.command.ImageUploadedUrlUpdatePort;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import org.springframework.stereotype.Component;

/**
 * ImageUploadedUrl Update Manager.
 *
 * <p>ImageUploadedUrlUpdatePort를 래핑하여 이미지 uploaded_url 업데이트를 위임합니다.
 */
@Component
public class ImageUploadedUrlUpdateManager {

    private final ImageUploadedUrlUpdatePort updatePort;

    public ImageUploadedUrlUpdateManager(ImageUploadedUrlUpdatePort updatePort) {
        this.updatePort = updatePort;
    }

    public void updateUploadedUrl(ImageSourceType sourceType, Long sourceId, String uploadedUrl) {
        updatePort.updateUploadedUrl(sourceType, sourceId, uploadedUrl);
    }
}
