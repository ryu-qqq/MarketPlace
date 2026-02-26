package com.ryuqq.marketplace.application.productgroupdescription.internal;

import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPersistResult;
import com.ryuqq.marketplace.application.productgroupdescription.factory.ProductGroupDescriptionCommandFactory;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionImageDiff;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionUpdateData;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description Command Coordinator.
 *
 * <p>DescriptionCommandFacade를 통해 Description + Image 저장 후, ImageUploadOutbox를 생성하여 이미지 업로드 아웃박스까지
 * 처리합니다.
 */
@Component
public class DescriptionCommandCoordinator {

    private final DescriptionCommandFacade descriptionCommandFacade;
    private final ProductGroupDescriptionCommandFactory descriptionCommandFactory;
    private final ProductGroupDescriptionReadManager descriptionReadManager;
    private final ImageUploadOutboxCommandManager outboxCommandManager;

    public DescriptionCommandCoordinator(
            DescriptionCommandFacade descriptionCommandFacade,
            ProductGroupDescriptionCommandFactory descriptionCommandFactory,
            ProductGroupDescriptionReadManager descriptionReadManager,
            ImageUploadOutboxCommandManager outboxCommandManager) {
        this.descriptionCommandFacade = descriptionCommandFacade;
        this.descriptionCommandFactory = descriptionCommandFactory;
        this.descriptionReadManager = descriptionReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    /**
     * 등록 Command로 Description 생성 + 저장 + 아웃박스 생성.
     *
     * @param command 상세 설명 등록 Command
     * @return 저장된 descriptionId
     */
    @Transactional
    public Long register(RegisterProductGroupDescriptionCommand command) {
        ProductGroupDescription description = descriptionCommandFactory.create(command);
        return persist(description);
    }

    /**
     * 수정 Command 기반: 기존 Description 조회 → 수정 → 저장 + 아웃박스 생성.
     *
     * @param command 상세 설명 수정 Command
     * @return 텍스트 콘텐츠가 실제 변경되었으면 true (AI 재검수 판단용)
     */
    @Transactional
    public boolean update(UpdateProductGroupDescriptionCommand command) {
        ProductGroupId pgId = ProductGroupId.of(command.productGroupId());
        ProductGroupDescription existing = descriptionReadManager.getByProductGroupId(pgId);

        boolean contentChanged = !existing.contentValue().equals(command.content());

        DescriptionUpdateData updateData = descriptionCommandFactory.createUpdateData(command);
        DescriptionImageDiff diff = existing.update(updateData);
        update(existing, diff);

        return contentChanged;
    }

    /**
     * Description + Image 저장 + ImageUploadOutbox 생성.
     *
     * <p>이미지가 없는 경우 즉시 PUBLISH_READY로 마킹합니다.
     *
     * @param description ProductGroupDescription 도메인 객체
     * @return 저장된 descriptionId
     */
    @Transactional
    public Long persist(ProductGroupDescription description) {
        if (description.isAllImagesUploaded()) {
            description.markPublishReady();
        }

        DescriptionPersistResult result = descriptionCommandFacade.persist(description);

        List<ImageUploadOutbox> outboxes =
                descriptionCommandFactory.createDescriptionImageOutboxes(
                        result.imageIds(), description.images(), description.createdAt());
        outboxCommandManager.persistAll(outboxes);

        return result.descriptionId();
    }

    /**
     * Description 수정 + 이미지 diff 기반 저장/삭제 + 신규 이미지 아웃박스 생성.
     *
     * <p>수정 후 신규 이미지가 없고 기존 이미지가 모두 업로드된 경우 즉시 PUBLISH_READY로 마킹합니다.
     *
     * @param description 수정된 ProductGroupDescription 도메인 객체
     * @param diff 이미지 변경 비교 결과
     */
    @Transactional
    public void update(ProductGroupDescription description, DescriptionImageDiff diff) {
        DescriptionPersistResult result = descriptionCommandFacade.update(description, diff);

        if (!result.imageIds().isEmpty()) {
            List<ImageUploadOutbox> outboxes =
                    descriptionCommandFactory.createDescriptionImageOutboxes(
                            result.imageIds(), diff.added(), description.updatedAt());
            outboxCommandManager.persistAll(outboxes);
        }

        if (description.isAllImagesUploaded()) {
            description.markPublishReady();
            descriptionCommandFacade.persistDescription(description);
        }
    }
}
