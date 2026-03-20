package com.ryuqq.marketplace.application.legacy.productgroupdescription.service.command;

import com.ryuqq.marketplace.application.legacy.productgroupdescription.manager.LegacyProductDescriptionCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.port.in.command.LegacyProductUpdateDescriptionUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 상세설명 수정 서비스.
 *
 * <p>표준 커맨드를 Manager/Port에 위임하여 luxurydb에 저장합니다.
 */
@Service
public class LegacyProductUpdateDescriptionService
        implements LegacyProductUpdateDescriptionUseCase {

    private final LegacyProductDescriptionCommandManager descriptionCommandManager;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductUpdateDescriptionService(
            LegacyProductDescriptionCommandManager descriptionCommandManager,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.descriptionCommandManager = descriptionCommandManager;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(UpdateProductGroupDescriptionCommand command) {
        descriptionCommandManager.update(command);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
