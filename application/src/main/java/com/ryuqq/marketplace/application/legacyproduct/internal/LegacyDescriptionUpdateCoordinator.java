package com.ryuqq.marketplace.application.legacyproduct.internal;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 상품 상세설명 수정 Coordinator. */
@Component
public class LegacyDescriptionUpdateCoordinator extends LegacyProductUpdateCoordinator {

    private final LegacyCommandResolver commandResolver;
    private final DescriptionCommandCoordinator descriptionCommandCoordinator;

    public LegacyDescriptionUpdateCoordinator(
            LegacyProductIdResolver idResolver,
            LegacyCommandResolver commandResolver,
            DescriptionCommandCoordinator descriptionCommandCoordinator) {
        super(idResolver);
        this.commandResolver = commandResolver;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
    }

    @Transactional
    public void execute(LegacyUpdateDescriptionCommand command) {
        long internalId = resolveInternalId(command.setofProductGroupId());
        var resolvedCommand =
                commandResolver.resolveDescriptionCommand(internalId, command.command());
        descriptionCommandCoordinator.update(resolvedCommand);
    }
}
