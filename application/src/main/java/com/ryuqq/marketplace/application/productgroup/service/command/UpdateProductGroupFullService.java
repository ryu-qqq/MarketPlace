package com.ryuqq.marketplace.application.productgroup.service.command;

import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupUpdateCoordinator;
import com.ryuqq.marketplace.application.productgroup.port.in.command.UpdateProductGroupFullUseCase;
import org.springframework.stereotype.Service;

/**
 * 상품 그룹 전체 수정 Service.
 *
 * <p>APP-SER-001: Service 레이어는 UseCase를 구현하고 Factory + Coordinator로 위임합니다.
 */
@Service
public class UpdateProductGroupFullService implements UpdateProductGroupFullUseCase {

    private final ProductGroupBundleFactory bundleFactory;
    private final FullProductGroupUpdateCoordinator coordinator;

    public UpdateProductGroupFullService(
            ProductGroupBundleFactory bundleFactory,
            FullProductGroupUpdateCoordinator coordinator) {
        this.bundleFactory = bundleFactory;
        this.coordinator = coordinator;
    }

    /**
     * 상품 그룹 전체를 수정합니다.
     *
     * <p>1. Factory를 통해 수정 번들 생성
     *
     * <p>2. Coordinator를 통한 검증 및 저장
     *
     * <p>기존 Product를 soft delete하고 새로운 Product를 생성하는 전략을 사용합니다.
     *
     * @param command 수정 Command
     */
    @Override
    public void execute(UpdateProductGroupFullCommand command) {
        ProductGroupUpdateBundle bundle = bundleFactory.createUpdateBundle(command);
        coordinator.update(bundle);
    }
}
