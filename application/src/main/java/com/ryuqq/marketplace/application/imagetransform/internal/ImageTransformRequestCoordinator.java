package com.ryuqq.marketplace.application.imagetransform.internal;

import com.ryuqq.marketplace.application.imagetransform.dto.command.RequestImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformOutboxFactory;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxReadManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageReadManager;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * 수동 이미지 변환 요청 Coordinator.
 *
 * <p>업로드 완료된 이미지에 대해 활성 Outbox가 없는 Variant 타입만 필터링하여 Outbox를 일괄 생성합니다.
 */
@Component
public class ImageTransformRequestCoordinator {

    private final ProductGroupImageReadManager imageReadManager;
    private final ImageTransformOutboxReadManager outboxReadManager;
    private final ImageTransformOutboxFactory outboxFactory;
    private final ImageTransformOutboxCommandManager outboxCommandManager;

    public ImageTransformRequestCoordinator(
            ProductGroupImageReadManager imageReadManager,
            ImageTransformOutboxReadManager outboxReadManager,
            ImageTransformOutboxFactory outboxFactory,
            ImageTransformOutboxCommandManager outboxCommandManager) {
        this.imageReadManager = imageReadManager;
        this.outboxReadManager = outboxReadManager;
        this.outboxFactory = outboxFactory;
        this.outboxCommandManager = outboxCommandManager;
    }

    /**
     * 수동 이미지 변환 요청을 처리합니다.
     *
     * @param command 변환 요청 Command (상품 그룹 ID + Variant 타입 목록)
     */
    public void request(RequestImageTransformCommand command) {
        ProductGroupId productGroupId = ProductGroupId.of(command.productGroupId());
        ProductGroupImages images = imageReadManager.getByProductGroupId(productGroupId);
        List<ImageVariantType> requestedTypes = command.resolvedVariantTypes();

        List<ProductGroupImage> uploadedImages =
                images.toList().stream().filter(ProductGroupImage::isUploaded).toList();

        if (uploadedImages.isEmpty()) {
            return;
        }

        List<Long> uploadedImageIds =
                uploadedImages.stream().map(ProductGroupImage::idValue).toList();

        Map<Long, Set<ImageVariantType>> activeMap =
                outboxReadManager.findActiveVariantTypesBySourceImageIds(
                        uploadedImageIds, requestedTypes);

        List<ImageTransformOutbox> allOutboxes = new ArrayList<>();

        for (ProductGroupImage image : uploadedImages) {
            Set<ImageVariantType> activeTypes =
                    activeMap.getOrDefault(image.idValue(), Collections.emptySet());

            List<ImageVariantType> requestableTypes =
                    requestedTypes.stream().filter(type -> !activeTypes.contains(type)).toList();

            if (requestableTypes.isEmpty()) {
                continue;
            }

            allOutboxes.addAll(
                    outboxFactory.createOutboxes(
                            image.idValue(),
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            image.uploadedUrl(),
                            requestableTypes));
        }

        if (!allOutboxes.isEmpty()) {
            outboxCommandManager.persistAll(allOutboxes);
        }
    }
}
