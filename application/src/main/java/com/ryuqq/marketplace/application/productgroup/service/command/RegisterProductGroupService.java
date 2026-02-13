package com.ryuqq.marketplace.application.productgroup.service.command;

import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupCommandFactory;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupRegistrationCoordinator;
import com.ryuqq.marketplace.application.productgroup.port.in.command.RegisterProductGroupUseCase;
import org.springframework.stereotype.Service;

/**
 * 상품 그룹 등록 Service.
 *
 * <p>APP-SER-001: Service 레이어는 UseCase를 구현하고 Factory + Coordinator로 위임합니다.
 */
@Service
public class RegisterProductGroupService implements RegisterProductGroupUseCase {

    private final ProductGroupCommandFactory factory;
    private final ProductGroupRegistrationCoordinator coordinator;

    public RegisterProductGroupService(
            ProductGroupCommandFactory factory, ProductGroupRegistrationCoordinator coordinator) {
        this.factory = factory;
        this.coordinator = coordinator;
    }

    /**
     * 상품 그룹을 등록합니다.
     *
     * <p>1. Factory를 통해 등록 번들 생성
     *
     * <p>2. Coordinator를 통한 검증 및 저장
     *
     * @param command 등록 Command
     * @return 생성된 상품 그룹 ID
     */
    @Override
    public Long execute(RegisterProductGroupCommand command) {
        ProductGroupRegistrationBundle bundle = factory.createRegistrationBundle(command);
        return coordinator.register(bundle);
    }
}
