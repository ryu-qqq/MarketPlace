package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdatePriceCommand;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyPriceUpdateCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductUpdatePriceUseCase;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 가격 수정 서비스.
 *
 * <p>LegacyPriceUpdateCoordinator에 위임합니다.
 */
@Service
public class LegacyProductUpdatePriceService implements LegacyProductUpdatePriceUseCase {

    private final LegacyPriceUpdateCoordinator priceUpdateCoordinator;

    public LegacyProductUpdatePriceService(LegacyPriceUpdateCoordinator priceUpdateCoordinator) {
        this.priceUpdateCoordinator = priceUpdateCoordinator;
    }

    @Override
    public void execute(LegacyUpdatePriceCommand command) {
        priceUpdateCoordinator.execute(command);
    }
}
