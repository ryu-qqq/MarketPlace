package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductGroupFullRegisterUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.response.LegacyProductRegistrationResult;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.result.ProductGroupRegistrationResult;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import org.springframework.stereotype.Service;

/**
 * 레거시 상품 등록 서비스.
 *
 * <p>레거시 API 요청을 표준 RegisterProductGroupCommand로 변환 후, 표준 FullProductGroupRegistrationCoordinator를
 * 통해 market 스키마에 저장합니다.
 *
 * <p>신규 등록이므로 PK 매핑이 불필요하며, market auto_increment PK가 그대로 반환됩니다.
 */
@Service
public class LegacyProductGroupFullRegisterService
        implements LegacyProductGroupFullRegisterUseCase {

    private final ProductGroupBundleFactory bundleFactory;
    private final FullProductGroupRegistrationCoordinator registrationCoordinator;

    public LegacyProductGroupFullRegisterService(
            ProductGroupBundleFactory bundleFactory,
            FullProductGroupRegistrationCoordinator registrationCoordinator) {
        this.bundleFactory = bundleFactory;
        this.registrationCoordinator = registrationCoordinator;
    }

    @Override
    public LegacyProductRegistrationResult execute(RegisterProductGroupCommand command) {
        ProductGroupRegistrationBundle bundle = bundleFactory.createProductGroupBundle(command);
        ProductGroupRegistrationResult result = registrationCoordinator.register(bundle);

        return new LegacyProductRegistrationResult(
                result.productGroupId(), command.sellerId(), result.productIds());
    }
}
