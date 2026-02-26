package com.ryuqq.marketplace.application.legacy.image.internal;

import com.ryuqq.marketplace.application.legacy.image.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.legacy.image.manager.LegacyProductImageCommandManager;
import com.ryuqq.marketplace.application.legacy.image.manager.LegacyProductImageReadManager;
import com.ryuqq.marketplace.application.legacy.shared.factory.LegacyProductGroupCommandFactory;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImages;
import com.ryuqq.marketplace.domain.legacy.productimage.vo.LegacyImageDiff;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 이미지 Coordinator.
 *
 * <p>이미지 등록 및 diff 기반 업데이트 라이프사이클을 관리합니다. Factory로 도메인 객체를 생성하고, 기존 이미지와 diff 후 persist합니다.
 */
@Component
public class LegacyImageCommandCoordinator {

    private final LegacyProductGroupCommandFactory commandFactory;
    private final LegacyProductImageReadManager imageReadManager;
    private final LegacyProductImageCommandManager imageCommandManager;

    public LegacyImageCommandCoordinator(
            LegacyProductGroupCommandFactory commandFactory,
            LegacyProductImageReadManager imageReadManager,
            LegacyProductImageCommandManager imageCommandManager) {
        this.commandFactory = commandFactory;
        this.imageReadManager = imageReadManager;
        this.imageCommandManager = imageCommandManager;
    }

    /** 이미지 일괄 등록. */
    public void register(List<LegacyProductImage> images) {
        imageCommandManager.persistAll(images);
    }

    /**
     * 이미지 수정 Command 기반: Factory로 도메인 객체 생성 → 기존 이미지 로드 → diff → persist.
     *
     * @param command 이미지 수정 Command
     */
    @Transactional
    public void update(LegacyUpdateImagesCommand command) {
        LegacyProductGroupId groupId = LegacyProductGroupId.of(command.productGroupId());
        List<LegacyProductImage> newImages =
                commandFactory.createImagesForUpdate(groupId, command.images());

        LegacyProductImages existing = imageReadManager.getByProductGroupId(groupId);
        LegacyImageDiff diff = existing.update(newImages, commandFactory.now());
        persist(diff);
    }

    /**
     * diff 결과 persist.
     *
     * <p>added는 신규 INSERT, allDirtyImages(retained + removed)는 dirty check로 DB 반영됩니다.
     */
    private void persist(LegacyImageDiff diff) {
        if (!diff.hasNoChanges()) {
            imageCommandManager.persistAll(diff.added());
            imageCommandManager.persistAll(diff.allDirtyImages());
        }
    }
}
