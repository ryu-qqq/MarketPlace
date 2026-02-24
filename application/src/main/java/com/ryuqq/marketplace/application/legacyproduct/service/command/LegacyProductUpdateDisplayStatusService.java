package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyDisplayStatusUpdateCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdateDisplayStatusUseCase;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 전시 상태 변경 서비스.
 *
 * <p>LegacyDisplayStatusUpdateCoordinator에 위임합니다.
 */
@Service
public class LegacyProductUpdateDisplayStatusService
        implements LegacyProductUpdateDisplayStatusUseCase {

    private final LegacyDisplayStatusUpdateCoordinator displayStatusUpdateCoordinator;

    public LegacyProductUpdateDisplayStatusService(
            LegacyDisplayStatusUpdateCoordinator displayStatusUpdateCoordinator) {
        this.displayStatusUpdateCoordinator = displayStatusUpdateCoordinator;
    }

    @Override
    public void execute(LegacyUpdateDisplayStatusCommand command) {
        displayStatusUpdateCoordinator.execute(command);
    }
}
