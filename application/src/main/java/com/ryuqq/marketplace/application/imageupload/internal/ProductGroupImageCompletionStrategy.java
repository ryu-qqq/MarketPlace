package com.ryuqq.marketplace.application.imageupload.internal;

import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformOutboxFactory;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageReadManager;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ProductGroupImage 업로드 완료 전략.
 *
 * <p>상품 그룹 이미지의 uploaded_url을 업데이트하고, 이미지 변환 Outbox를 자동 생성합니다.
 */
@Component
public class ProductGroupImageCompletionStrategy implements ImageUploadCompletionStrategy {

    private final ProductGroupImageReadManager productGroupImageReadManager;
    private final ProductGroupImageCommandManager productGroupImageCommandManager;
    private final ImageTransformOutboxFactory transformOutboxFactory;
    private final ImageTransformOutboxCommandManager transformOutboxCommandManager;

    public ProductGroupImageCompletionStrategy(
            ProductGroupImageReadManager productGroupImageReadManager,
            ProductGroupImageCommandManager productGroupImageCommandManager,
            ImageTransformOutboxFactory transformOutboxFactory,
            ImageTransformOutboxCommandManager transformOutboxCommandManager) {
        this.productGroupImageReadManager = productGroupImageReadManager;
        this.productGroupImageCommandManager = productGroupImageCommandManager;
        this.transformOutboxFactory = transformOutboxFactory;
        this.transformOutboxCommandManager = transformOutboxCommandManager;
    }

    @Override
    public boolean supports(ImageSourceType sourceType) {
        return sourceType.isProductGroupImage();
    }

    @Override
    public void complete(Long sourceId, ImageUrl uploadedUrl, String fileAssetId) {
        ProductGroupImage image = productGroupImageReadManager.getById(sourceId);
        image.updateUploadedUrl(uploadedUrl);
        productGroupImageCommandManager.persist(image);

        List<ImageTransformOutbox> outboxes =
                transformOutboxFactory.createOutboxes(
                        sourceId, ImageSourceType.PRODUCT_GROUP_IMAGE, uploadedUrl);
        transformOutboxCommandManager.persistAll(outboxes);
    }
}
