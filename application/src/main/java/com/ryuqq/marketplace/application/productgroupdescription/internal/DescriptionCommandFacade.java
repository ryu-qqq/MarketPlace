package com.ryuqq.marketplace.application.productgroupdescription.internal;

import com.ryuqq.marketplace.application.imageupload.internal.ImageUploadOutboxCreator;
import com.ryuqq.marketplace.application.productgroupdescription.manager.DescriptionImageCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionCommandManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description Command Facade.
 *
 * <p>ProductGroupDescription 저장 → DescriptionImage 교체를 조율합니다.
 */
@Component
public class DescriptionCommandFacade {

    private final ProductGroupDescriptionCommandManager descriptionCommandManager;
    private final DescriptionImageCommandManager imageCommandManager;
    private final ImageUploadOutboxCreator outboxCreator;

    public DescriptionCommandFacade(
            ProductGroupDescriptionCommandManager descriptionCommandManager,
            DescriptionImageCommandManager imageCommandManager,
            ImageUploadOutboxCreator outboxCreator) {
        this.descriptionCommandManager = descriptionCommandManager;
        this.imageCommandManager = imageCommandManager;
        this.outboxCreator = outboxCreator;
    }

    /**
     * Description + Image 저장.
     *
     * <p>1. Description 저장 → descriptionId 획득
     *
     * <p>2. 기존 이미지 삭제
     *
     * <p>3. 새 이미지 저장
     *
     * @param description ProductGroupDescription 도메인 객체
     * @return 저장된 descriptionId
     */
    @Transactional
    public Long persist(ProductGroupDescription description) {
        Long descriptionId = descriptionCommandManager.persist(description);

        imageCommandManager.deleteByDescriptionId(descriptionId);
        List<Long> imageIds = imageCommandManager.persistAll(descriptionId, description.images());

        outboxCreator.createForDescriptionImages(imageIds, description.images(), Instant.now());

        return descriptionId;
    }
}
