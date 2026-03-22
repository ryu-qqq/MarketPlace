package com.ryuqq.marketplace.application.legacy.productgroupdescription.internal;

import com.ryuqq.marketplace.application.legacy.productgroupdescription.manager.LegacyDescriptionImageCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.manager.LegacyProductDescriptionCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.manager.LegacyProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.factory.ProductGroupDescriptionCommandFactory;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionImageDiff;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionUpdateData;
import java.time.Instant;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 상세설명 Command Coordinator.
 *
 * <p>표준 도메인 객체 기반으로 레거시 DB(luxurydb)에 저장합니다.
 * 이미지 업로드 Outbox는 생성하지 않습니다 — 레거시 컨버전 과정에서 처리됩니다.
 */
@Component
public class LegacyDescriptionCommandCoordinator {

    private final ProductGroupDescriptionCommandFactory descriptionCommandFactory;
    private final LegacyProductGroupDescriptionReadManager descriptionReadManager;
    private final LegacyProductDescriptionCommandManager descriptionCommandManager;
    private final LegacyDescriptionImageCommandManager imageCommandManager;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyDescriptionCommandCoordinator(
            ProductGroupDescriptionCommandFactory descriptionCommandFactory,
            LegacyProductGroupDescriptionReadManager descriptionReadManager,
            LegacyProductDescriptionCommandManager descriptionCommandManager,
            LegacyDescriptionImageCommandManager imageCommandManager,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.descriptionCommandFactory = descriptionCommandFactory;
        this.descriptionReadManager = descriptionReadManager;
        this.descriptionCommandManager = descriptionCommandManager;
        this.imageCommandManager = imageCommandManager;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    /** 상세설명 등록 (상품그룹 등록 시 사용). Command → 표준 도메인 생성 → persist. */
    @Transactional
    public void register(RegisterProductGroupDescriptionCommand command) {
        ProductGroupDescription description = descriptionCommandFactory.create(command);
        descriptionCommandManager.persist(description);

        for (DescriptionImage image : description.images()) {
            imageCommandManager.persist(image);
        }
    }

    /**
     * 수정 Command 기반: 기존 Description 조회 → diff → 저장 + ConversionOutbox.
     *
     * @param command 상세 설명 수정 Command
     * @return 텍스트 콘텐츠가 실제 변경되었으면 true (AI 재검수 판단용)
     */
    @Transactional
    public boolean update(UpdateProductGroupDescriptionCommand command) {
        ProductGroupDescription existing =
                descriptionReadManager.getByProductGroupId(command.productGroupId());

        boolean contentChanged = !command.content().equals(existing.contentValue());

        DescriptionUpdateData updateData = descriptionCommandFactory.createUpdateData(command);
        DescriptionImageDiff diff = existing.update(updateData);

        descriptionCommandManager.persist(existing);

        for (DescriptionImage image : diff.removed()) {
            imageCommandManager.persist(image);
        }
        for (DescriptionImage image : diff.added()) {
            imageCommandManager.persist(image);
        }

        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());

        return contentChanged;
    }
}
