package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 상품 이미지 수정 Coordinator. */
@Component
public class LegacyImageUpdateCoordinator extends LegacyProductUpdateCoordinator {

    private final LegacyCommandResolver commandResolver;
    private final ImageCommandCoordinator imageCommandCoordinator;

    public LegacyImageUpdateCoordinator(
            LegacyProductIdResolver idResolver,
            LegacyCommandResolver commandResolver,
            ImageCommandCoordinator imageCommandCoordinator) {
        super(idResolver);
        this.commandResolver = commandResolver;
        this.imageCommandCoordinator = imageCommandCoordinator;
    }

    @Transactional
    public void execute(LegacyUpdateImagesCommand command) {
        long internalId = resolveInternalId(command.setofProductGroupId());
        var resolvedCommand = commandResolver.resolveImagesCommand(internalId, command.command());
        imageCommandCoordinator.update(resolvedCommand);
    }
}
