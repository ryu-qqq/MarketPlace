package com.ryuqq.marketplace.application.imageupload.factory;

import com.ryuqq.marketplace.application.common.dto.command.ExternalDownloadRequest;
import com.ryuqq.marketplace.application.imageupload.internal.ImageUploadProcessBundle;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * 이미지 업로드 처리 Bundle Factory.
 *
 * <p>Outbox에서 다운로드 요청에 필요한 정보를 추출하여 Bundle을 생성합니다.
 */
@Component
public class ImageUploadProcessBundleFactory {

    private static final String UPLOAD_CATEGORY = "product-images";

    public ImageUploadProcessBundle create(ImageUploadOutbox outbox, Instant now) {
        String filename = outbox.generateFilename(now);
        ExternalDownloadRequest request =
                new ExternalDownloadRequest(outbox.originUrlValue(), UPLOAD_CATEGORY, filename);
        return new ImageUploadProcessBundle(outbox, request, now);
    }
}
