package com.ryuqq.marketplace.application.legacy.productgroupdescription.internal;

import com.ryuqq.marketplace.application.legacy.productgroupdescription.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.manager.LegacyDescriptionImageCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.manager.LegacyProductDescriptionCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.manager.LegacyProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.application.legacy.shared.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.domain.legacy.productdescription.aggregate.LegacyDescriptionImage;
import com.ryuqq.marketplace.domain.legacy.productdescription.aggregate.LegacyProductGroupDescription;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productdescription.vo.LegacyDescriptionImageDiff;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * 레거시 상세설명 Coordinator.
 *
 * <p>내부 DescriptionCommandCoordinator와 동일한 패턴으로 상세설명 등록/수정 라이프사이클을 관리합니다. HTML에서 이미지를 추출하고, 수정 시
 * originUrl 기반 image diff를 계산합니다.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory.now()로 시각을 얻습니다.
 */
@Component
public class LegacyDescriptionCommandCoordinator {

    private final LegacyProductDescriptionCommandManager descriptionCommandManager;
    private final LegacyDescriptionImageCommandManager imageCommandManager;
    private final LegacyProductGroupDescriptionReadManager descriptionReadManager;
    private final LegacyProductGroupCommandFactory commandFactory;

    public LegacyDescriptionCommandCoordinator(
            LegacyProductDescriptionCommandManager descriptionCommandManager,
            LegacyDescriptionImageCommandManager imageCommandManager,
            LegacyProductGroupDescriptionReadManager descriptionReadManager,
            LegacyProductGroupCommandFactory commandFactory) {
        this.descriptionCommandManager = descriptionCommandManager;
        this.imageCommandManager = imageCommandManager;
        this.descriptionReadManager = descriptionReadManager;
        this.commandFactory = commandFactory;
    }

    /** 상세설명 등록 (상품그룹 등록 시 사용). HTML에서 이미지를 추출하여 함께 저장합니다. */
    public void register(LegacyProductGroupId groupId, String content) {
        LegacyProductGroupDescription description =
                LegacyProductGroupDescription.forNew(groupId.value(), content);
        List<LegacyDescriptionImage> images =
                commandFactory.extractDescriptionImages(groupId.value(), content);

        descriptionCommandManager.persistDescription(description);
        if (!images.isEmpty()) {
            imageCommandManager.persistAll(images);
        }
    }

    /**
     * Command 기반 상세설명 수정. Factory로 이미지 추출 및 시각 생성, 도메인 diff 계산 후 persist합니다.
     *
     * @param command 상세설명 수정 Command
     * @return 텍스트 콘텐츠가 실제 변경되었으면 true (AI 재검수 판단용)
     */
    public boolean update(LegacyUpdateDescriptionCommand command) {
        LegacyProductGroupId groupId = LegacyProductGroupId.of(command.productGroupId());
        String content = command.detailDescription();
        Instant changedAt = commandFactory.now();

        LegacyProductGroupDescription existing =
                descriptionReadManager.getByProductGroupId(groupId.value());

        boolean contentChanged = !Objects.equals(existing.content(), content);

        List<LegacyDescriptionImage> newImages =
                commandFactory.extractDescriptionImages(groupId.value(), content);

        LegacyDescriptionImageDiff diff = existing.update(content, newImages, changedAt);

        descriptionCommandManager.persistDescription(existing);

        if (!diff.removed().isEmpty()) {
            imageCommandManager.softDeleteAll(diff.removed());
        }
        if (!diff.added().isEmpty()) {
            imageCommandManager.persistAll(diff.added());
        }

        return contentChanged;
    }
}
