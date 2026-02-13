package com.ryuqq.marketplace.application.imageupload.internal;

import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupImage;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ImageUploadOutbox Creator.
 *
 * <p>이미지 저장 직후 Outbox 엔트리를 배치 생성합니다. Facade에서 같은 트랜잭션 내에서 호출됩니다.
 */
@Component
public class ImageUploadOutboxCreator {

    private final ImageUploadOutboxCommandManager outboxCommandManager;

    public ImageUploadOutboxCreator(ImageUploadOutboxCommandManager outboxCommandManager) {
        this.outboxCommandManager = outboxCommandManager;
    }

    /**
     * ProductGroup 이미지에 대한 Outbox 엔트리 생성.
     *
     * @param imageIds 저장된 이미지 ID 목록
     * @param images 이미지 도메인 객체 목록
     * @param now 현재 시각
     */
    public void createForProductGroupImages(
            List<Long> imageIds, List<ProductGroupImage> images, Instant now) {
        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            String originUrl = images.get(i).originUrlValue();
            ImageUploadOutbox outbox =
                    ImageUploadOutbox.forNew(
                            imageId, ImageSourceType.PRODUCT_GROUP_IMAGE, originUrl, now);
            outboxCommandManager.persist(outbox);
        }
    }

    /**
     * Description 이미지에 대한 Outbox 엔트리 생성.
     *
     * @param imageIds 저장된 이미지 ID 목록
     * @param images 이미지 도메인 객체 목록
     * @param now 현재 시각
     */
    public void createForDescriptionImages(
            List<Long> imageIds, List<DescriptionImage> images, Instant now) {
        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            String originUrl = images.get(i).originUrlValue();
            ImageUploadOutbox outbox =
                    ImageUploadOutbox.forNew(
                            imageId, ImageSourceType.DESCRIPTION_IMAGE, originUrl, now);
            outboxCommandManager.persist(outbox);
        }
    }
}
