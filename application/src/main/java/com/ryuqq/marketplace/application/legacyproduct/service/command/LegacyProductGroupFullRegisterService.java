package com.ryuqq.marketplace.application.legacyproduct.service.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.bundle.LegacyProductRegistrationBundle;
import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyRegisterProductGroupCommand;
import com.ryuqq.marketplace.application.legacyproduct.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.legacyproduct.dto.result.LegacyProductGroupSaveResult;
import com.ryuqq.marketplace.application.legacyproduct.factory.LegacyProductBundleFactory;
import com.ryuqq.marketplace.application.legacyproduct.internal.LegacyProductRegistrationCoordinator;
import com.ryuqq.marketplace.application.legacyproduct.port.in.command.LegacyProductGroupFullRegisterUseCase;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 등록 서비스.
 *
 * <p>Factory로 Bundle 생성 → Coordinator로 luxurydb INSERT → 결과 반환.
 */
@Service
public class LegacyProductGroupFullRegisterService
        implements LegacyProductGroupFullRegisterUseCase {

    private final LegacyProductBundleFactory bundleFactory;
    private final LegacyProductRegistrationCoordinator registrationCoordinator;

    public LegacyProductGroupFullRegisterService(
            LegacyProductBundleFactory bundleFactory,
            LegacyProductRegistrationCoordinator registrationCoordinator) {
        this.bundleFactory = bundleFactory;
        this.registrationCoordinator = registrationCoordinator;
    }

    @Override
    public LegacyProductRegistrationResult execute(LegacyRegisterProductGroupCommand command) {
        LegacyProductRegistrationBundle bundle = bundleFactory.create(command);
        LegacyProductGroupSaveResult saveResult = registrationCoordinator.register(bundle);

        return new LegacyProductRegistrationResult(
                saveResult.productGroupId(), command.sellerId(), saveResult.productIds());
    }
}
