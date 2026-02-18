package com.ryuqq.marketplace.application.productgroupimage.internal;

import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.factory.ProductGroupImageFactory;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageCommandManager;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageReadManager;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImageDiff;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImageUpdateData;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Image Command Coordinator.
 *
 * <p>ProductGroupImageCommandManager를 통해 이미지 저장 후, ImageUploadOutbox를 생성하여 이미지 업로드 아웃박스까지 처리합니다.
 */
@Component
public class ImageCommandCoordinator {

    private final ProductGroupImageCommandManager imageCommandManager;
    private final ProductGroupImageReadManager imageReadManager;
    private final ProductGroupImageFactory imageFactory;
    private final ImageUploadOutboxCommandManager outboxCommandManager;

    public ImageCommandCoordinator(
            ProductGroupImageCommandManager imageCommandManager,
            ProductGroupImageReadManager imageReadManager,
            ProductGroupImageFactory imageFactory,
            ImageUploadOutboxCommandManager outboxCommandManager) {
        this.imageCommandManager = imageCommandManager;
        this.imageReadManager = imageReadManager;
        this.imageFactory = imageFactory;
        this.outboxCommandManager = outboxCommandManager;
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
     * 이미지 저장 + ImageUploadOutbox 생성.
     *
     * @param images ProductGroupImages 도메인 객체
     * @return 저장된 이미지 ID 목록
     */
    @Transactional
    public List<Long> register(ProductGroupImages images) {
        List<Long> imageIds = imageCommandManager.persistAll(images.toList());

        List<ImageUploadOutbox> outboxes =
                imageFactory.createProductGroupImageOutboxes(imageIds, images.toList());
        outboxCommandManager.persistAll(outboxes);

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
     * 이미지 diff 기반 수정: 삭제/유지는 더티체킹, 신규만 persist + 아웃박스 생성.
     *
     * <p>removed 이미지는 도메인에서 이미 soft delete 처리된 상태이므로 persist 호출 시 더티체킹으로 DB에 반영됩니다.
     *
     * @param diff 이미지 변경 비교 결과 (도메인에서 상태 변경 완료)
     */
    @Transactional
    public void update(ProductGroupImageDiff diff) {
        imageCommandManager.persistAll(diff.removed());

        List<Long> addedIds = imageCommandManager.persistAll(diff.added());

        if (!addedIds.isEmpty()) {
            List<ImageUploadOutbox> outboxes =
                    imageFactory.createProductGroupImageOutboxes(addedIds, diff.added());
            outboxCommandManager.persistAll(outboxes);
        }
    }
}
