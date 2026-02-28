package com.ryuqq.marketplace.application.imageupload.internal;

import com.ryuqq.marketplace.application.productgroupdescription.manager.DescriptionImageCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.DescriptionImageReadManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import org.springframework.stereotype.Component;

/**
 * DescriptionImage 업로드 완료 전략.
 *
 * <p>상세설명 이미지의 uploaded_url을 업데이트하고, 모든 이미지가 업로드 완료되면 Description을 PUBLISH_READY로 전환합니다.
 */
@Component
public class DescriptionImageCompletionStrategy implements ImageUploadCompletionStrategy {

    private final DescriptionImageReadManager descriptionImageReadManager;
    private final DescriptionImageCommandManager descriptionImageCommandManager;
    private final ProductGroupDescriptionReadManager descriptionReadManager;
    private final ProductGroupDescriptionCommandManager descriptionCommandManager;

    public DescriptionImageCompletionStrategy(
            DescriptionImageReadManager descriptionImageReadManager,
            DescriptionImageCommandManager descriptionImageCommandManager,
            ProductGroupDescriptionReadManager descriptionReadManager,
            ProductGroupDescriptionCommandManager descriptionCommandManager) {
        this.descriptionImageReadManager = descriptionImageReadManager;
        this.descriptionImageCommandManager = descriptionImageCommandManager;
        this.descriptionReadManager = descriptionReadManager;
        this.descriptionCommandManager = descriptionCommandManager;
    }

    @Override
    public boolean supports(ImageSourceType sourceType) {
        return sourceType != null && sourceType.isDescriptionImage();
    }

    @Override
    public void complete(Long sourceId, ImageUrl uploadedUrl, String fileAssetId) {
        DescriptionImage image = descriptionImageReadManager.getById(sourceId);
        image.updateUploadedUrl(uploadedUrl);
        descriptionImageCommandManager.persist(image);

        ProductGroupDescription description =
                descriptionReadManager.getById(image.productGroupDescriptionIdValue());
        if (description.publishStatus().isPending() && description.isAllImagesUploaded()) {
            description.markPublishReady();
            descriptionCommandManager.persist(description);
        }
    }
}
