package com.ryuqq.marketplace.application.productgroupdescription.internal;

import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionPersistResult;
import com.ryuqq.marketplace.application.productgroupdescription.manager.DescriptionImageCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionCommandManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupDescriptionId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionImageDiff;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description Command Facade.
 *
 * <p>ProductGroupDescription 저장 → ID 할당 → DescriptionImage 저장을 조율합니다.
 *
 * <p>아웃박스는 Coordinator에서 Factory + ImageUploadOutboxCommandManager로 처리합니다.
 */
@Component
public class DescriptionCommandFacade {

    private final ProductGroupDescriptionCommandManager descriptionCommandManager;
    private final DescriptionImageCommandManager imageCommandManager;

    public DescriptionCommandFacade(
            ProductGroupDescriptionCommandManager descriptionCommandManager,
            DescriptionImageCommandManager imageCommandManager) {
        this.descriptionCommandManager = descriptionCommandManager;
        this.imageCommandManager = imageCommandManager;
    }

    /**
     * Description + Image 저장 후 descriptionId + imageIds 반환.
     *
     * @param description ProductGroupDescription 도메인 객체
     * @return 저장 결과 (descriptionId, imageIds)
     */
    @Transactional
    public DescriptionPersistResult persist(ProductGroupDescription description) {
        Long descriptionId = descriptionCommandManager.persist(description);
        description.assignId(ProductGroupDescriptionId.of(descriptionId));
        List<Long> imageIds = imageCommandManager.persistAll(description.images());
        return new DescriptionPersistResult(descriptionId, imageIds);
    }

    /**
     * Description 수정 + 이미지 diff 기반 저장/삭제.
     *
     * @param description 수정된 ProductGroupDescription 도메인 객체
     * @param diff 이미지 변경 비교 결과
     * @return 저장 결과 (descriptionId, 신규 imageIds)
     */
    @Transactional
    public DescriptionPersistResult update(
            ProductGroupDescription description, DescriptionImageDiff diff) {
        Long descriptionId = descriptionCommandManager.persist(description);
        imageCommandManager.persistAll(diff.removed());
        List<Long> newImageIds = imageCommandManager.persistAll(diff.added());
        return new DescriptionPersistResult(descriptionId, newImageIds);
    }

    /**
     * Description만 저장 (이미지 변경 없이 상태 업데이트 시 사용).
     *
     * @param description ProductGroupDescription 도메인 객체
     */
    @Transactional
    public void persistDescription(ProductGroupDescription description) {
        descriptionCommandManager.persist(description);
    }
}
