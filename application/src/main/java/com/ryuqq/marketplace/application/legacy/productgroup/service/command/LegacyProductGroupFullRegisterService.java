package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.productgroup.internal.LegacyProductRegistrationCoordinator;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductGroupFullRegisterUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupSaveResult;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 등록 서비스.
 *
 * <p>표준 RegisterProductGroupCommand → BundleFactory로 Bundle 생성 → Coordinator로 luxurydb INSERT.
 */
@Service
public class LegacyProductGroupFullRegisterService
        implements LegacyProductGroupFullRegisterUseCase {

    private final ProductGroupBundleFactory bundleFactory;
    private final LegacyProductRegistrationCoordinator registrationCoordinator;

    public LegacyProductGroupFullRegisterService(
            ProductGroupBundleFactory bundleFactory,
            LegacyProductRegistrationCoordinator registrationCoordinator) {
        this.bundleFactory = bundleFactory;
        this.registrationCoordinator = registrationCoordinator;
    }

    @Override
    public LegacyProductRegistrationResult execute(RegisterProductGroupCommand command) {
        ProductGroupRegistrationBundle bundle = bundleFactory.createProductGroupBundle(command);
        LegacyProductGroupSaveResult saveResult = registrationCoordinator.register(bundle);

        return new LegacyProductRegistrationResult(
                saveResult.productGroupId(), command.sellerId(), saveResult.productIds());
    }
}
