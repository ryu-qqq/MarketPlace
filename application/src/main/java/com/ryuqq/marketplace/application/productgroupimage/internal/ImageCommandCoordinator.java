package com.ryuqq.marketplace.application.productgroupimage.internal;

import com.ryuqq.marketplace.application.common.port.out.InternalImageUrlChecker;
import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformOutboxFactory;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.factory.ProductGroupImageFactory;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageReadManager;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImageDiff;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImageUpdateData;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Image Command Coordinator.
 *
 * <p>ProductGroupImageCommandManager를 통해 이미지 저장 후, ImageUploadOutbox를 생성하여 이미지 업로드 아웃박스까지 처리합니다.
 *
 * <p>내부 CDN URL(presigned URL로 이미 업로드된 이미지)은 Outbox를 건너뛰고 즉시 uploadedUrl로 설정합니다.
 */
@Component
public class ImageCommandCoordinator {

    private final ProductGroupImageCommandManager imageCommandManager;
    private final ProductGroupImageReadManager imageReadManager;
    private final ProductGroupImageFactory imageFactory;
    private final ImageUploadOutboxCommandManager uploadOutboxCommandManager;
    private final ImageTransformOutboxFactory transformOutboxFactory;
    private final ImageTransformOutboxCommandManager transformOutboxCommandManager;
    private final InternalImageUrlChecker internalImageUrlChecker;

    public ImageCommandCoordinator(
            ProductGroupImageCommandManager imageCommandManager,
            ProductGroupImageReadManager imageReadManager,
            ProductGroupImageFactory imageFactory,
            ImageUploadOutboxCommandManager uploadOutboxCommandManager,
            ImageTransformOutboxFactory transformOutboxFactory,
            ImageTransformOutboxCommandManager transformOutboxCommandManager,
            InternalImageUrlChecker internalImageUrlChecker) {
        this.imageCommandManager = imageCommandManager;
        this.imageReadManager = imageReadManager;
        this.imageFactory = imageFactory;
        this.uploadOutboxCommandManager = uploadOutboxCommandManager;
        this.transformOutboxFactory = transformOutboxFactory;
        this.transformOutboxCommandManager = transformOutboxCommandManager;
        this.internalImageUrlChecker = internalImageUrlChecker;
    }

    /**
     * 이미지 등록 Command로 ProductGroupImages 생성 + 저장 + 아웃박스 생성.
     *
     * @param command 이미지 등록 Command (productGroupId + 이미지 목록)
     * @return 저장된 이미지 ID 목록
     */
    @Transactional
    public List<Long> register(RegisterProductGroupImagesCommand command) {
        ProductGroupId productGroupId = ProductGroupId.of(command.productGroupId());
        ProductGroupImages images =
                imageFactory.createFromImageRegistration(productGroupId, command.images());
        return register(images);
    }

    /**
     * 이미지 저장 + Outbox 생성.
     *
     * <p>내부 CDN URL 이미지는 uploadedUrl을 즉시 설정하고 ImageUploadOutbox를 건너뛰되, ImageTransformOutbox는 바로
     * 생성합니다. 외부 URL 이미지는 기존대로 ImageUploadOutbox를 생성하여 비동기 업로드 후 콜백에서 ImageTransformOutbox가 생성됩니다.
     *
     * @param images ProductGroupImages 도메인 객체
     * @return 저장된 이미지 ID 목록
     */
    @Transactional
    public List<Long> register(ProductGroupImages images) {
        List<ProductGroupImage> allImages = images.toList();
        markInternalImagesAsUploaded(allImages);

        List<Long> imageIds = imageCommandManager.persistAll(allImages);
        createOutboxesByUrlType(imageIds, allImages);

        return imageIds;
    }

    /**
     * 이미지 수정 Command 기반: 기존 이미지 로드 → diff 계산 → 삭제/추가 처리.
     *
     * @param command 이미지 수정 Command
     */
    @Transactional
    public void update(UpdateProductGroupImagesCommand command) {
        ProductGroupImageUpdateData updateData = imageFactory.createUpdateData(command);
        ProductGroupId pgId = ProductGroupId.of(command.productGroupId());
        ProductGroupImages existing = imageReadManager.getByProductGroupId(pgId);
        ProductGroupImageDiff diff = existing.update(updateData);
        update(diff);
    }

    /**
     * 이미지 diff 기반 수정: 삭제/유지는 더티체킹, 신규만 persist + Outbox 생성.
     *
     * <p>removed 이미지는 도메인에서 이미 soft delete 처리된 상태이므로 persist 호출 시 더티체킹으로 DB에 반영됩니다. 내부 CDN URL 이미지는
     * ImageUploadOutbox를 건너뛰고 ImageTransformOutbox를 바로 생성합니다.
     *
     * @param diff 이미지 변경 비교 결과 (도메인에서 상태 변경 완료)
     */
    @Transactional
    public void update(ProductGroupImageDiff diff) {
        imageCommandManager.persistAll(diff.removed());

        List<ProductGroupImage> addedImages = diff.added();
        markInternalImagesAsUploaded(addedImages);

        List<Long> addedIds = imageCommandManager.persistAll(addedImages);
        createOutboxesByUrlType(addedIds, addedImages);
    }

    private void markInternalImagesAsUploaded(List<ProductGroupImage> images) {
        for (ProductGroupImage image : images) {
            if (internalImageUrlChecker.isInternal(image.originUrlValue())) {
                image.updateUploadedUrl(image.originUrl());
            }
        }
    }

    /**
     * URL 타입에 따라 적절한 Outbox를 생성합니다.
     *
     * <p>내부 URL (isUploaded=true): ImageTransformOutbox 바로 생성 (fileAssetId 없이)
     *
     * <p>외부 URL (isUploaded=false): ImageUploadOutbox 생성 → 콜백에서 ImageTransformOutbox 생성
     */
    private void createOutboxesByUrlType(List<Long> imageIds, List<ProductGroupImage> images) {
        List<Long> externalIds = new ArrayList<>();
        List<ProductGroupImage> externalImages = new ArrayList<>();
        List<ImageTransformOutbox> transformOutboxes = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            ProductGroupImage image = images.get(i);
            Long imageId = imageIds.get(i);

            if (image.isUploaded()) {
                transformOutboxes.addAll(
                        transformOutboxFactory.createOutboxes(
                                imageId,
                                ImageSourceType.PRODUCT_GROUP_IMAGE,
                                image.uploadedUrl(),
                                (String) null));
            } else {
                externalIds.add(imageId);
                externalImages.add(image);
            }
        }

        if (!transformOutboxes.isEmpty()) {
            transformOutboxCommandManager.persistAll(transformOutboxes);
        }

        if (!externalIds.isEmpty()) {
            List<ImageUploadOutbox> uploadOutboxes =
                    imageFactory.createProductGroupImageOutboxes(externalIds, externalImages);
            uploadOutboxCommandManager.persistAll(uploadOutboxes);
        }
    }
}
