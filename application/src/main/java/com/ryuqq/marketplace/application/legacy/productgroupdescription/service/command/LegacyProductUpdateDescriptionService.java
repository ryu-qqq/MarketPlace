package com.ryuqq.marketplace.application.legacy.productgroupdescription.service.command;

import com.ryuqq.marketplace.application.legacy.productgroupdescription.internal.LegacyDescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.port.in.command.LegacyProductUpdateDescriptionUseCase;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 상세설명 수정 서비스.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 */
@Service
public class LegacyProductUpdateDescriptionService
        implements LegacyProductUpdateDescriptionUseCase {

    private final LegacyDescriptionCommandCoordinator descriptionCommandCoordinator;

    public LegacyProductUpdateDescriptionService(
            LegacyDescriptionCommandCoordinator descriptionCommandCoordinator) {
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
    }

    @Override
    public void execute(UpdateProductGroupDescriptionCommand command) {
        descriptionCommandCoordinator.update(command);
    }
}
