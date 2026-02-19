package com.ryuqq.marketplace.application.productgroup.service.command;

import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.application.productgroup.port.in.command.RegisterProductGroupFullUseCase;
import org.springframework.stereotype.Service;

/**
 * 상품 그룹 등록 Service.
 *
 * <p>APP-SER-001: Service 레이어는 UseCase를 구현하고 Factory + Coordinator로 위임합니다.
 */
@Service
public class RegisterProductGroupFullService implements RegisterProductGroupFullUseCase {

    private final ProductGroupBundleFactory bundleFactory;
    private final FullProductGroupRegistrationCoordinator coordinator;

    public RegisterProductGroupFullService(
            ProductGroupBundleFactory bundleFactory,
            FullProductGroupRegistrationCoordinator coordinator) {
        this.bundleFactory = bundleFactory;
        this.coordinator = coordinator;
    }

    /**
     * 상품 그룹을 등록합니다.
     *
     * <p>1. Factory를 통해 등록 번들 생성 (ProductGroup + ProductCreations)
     *
     * <p>2. Coordinator를 통한 검증 및 저장 (Description, Notice는 Coordinator에서 per-package 처리)
     *
     * @param command 등록 Command
     * @return 생성된 상품 그룹 ID
     */
    @Override
    public Long execute(RegisterProductGroupCommand command) {
        ProductGroupRegistrationBundle bundle = bundleFactory.createProductGroupBundle(command);
        return coordinator.register(bundle);
    }
}
