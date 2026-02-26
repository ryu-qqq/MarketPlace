package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateProductGroupCommand;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyProductGroupFullUpdateCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductGroupFullUpdateUseCase;
import java.time.Instant;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품그룹 전체 수정 서비스.
 *
 * <p>updateStatus 플래그 기반으로 변경된 섹션만 레거시 DB에 직접 반영합니다.
 */
@Service
public class LegacyProductGroupFullUpdateService implements LegacyProductGroupFullUpdateUseCase {

    private final LegacyProductGroupFullUpdateCoordinator fullUpdateCoordinator;
    private final LegacyConversionOutboxCommandManager conversionOutboxCommandManager;

    public LegacyProductGroupFullUpdateService(
            LegacyProductGroupFullUpdateCoordinator fullUpdateCoordinator,
            LegacyConversionOutboxCommandManager conversionOutboxCommandManager) {
        this.fullUpdateCoordinator = fullUpdateCoordinator;
        this.conversionOutboxCommandManager = conversionOutboxCommandManager;
    }

    @Override
    public void execute(LegacyUpdateProductGroupCommand command) {
        fullUpdateCoordinator.execute(command);
        conversionOutboxCommandManager.createIfNoPending(command.productGroupId(), Instant.now());
    }
}
