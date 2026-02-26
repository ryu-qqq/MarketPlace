package com.ryuqq.marketplace.application.productgroup.service.command;

import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupCommandFactory;
import com.ryuqq.marketplace.application.productgroup.internal.ProductGroupCommandCoordinator;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroup.port.in.command.UpdateProductGroupBasicInfoUseCase;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import org.springframework.stereotype.Service;

/**
 * UpdateProductGroupBasicInfoService - 상품 그룹 기본 정보 수정 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 *
 * <p>APP-TRX-001: @Transactional은 Coordinator에서 처리
 */
@Service
public class UpdateProductGroupBasicInfoService implements UpdateProductGroupBasicInfoUseCase {

    private final ProductGroupCommandFactory commandFactory;
    private final ProductGroupCommandCoordinator coordinator;
    private final ProductGroupReadManager productGroupReadManager;

    public UpdateProductGroupBasicInfoService(
            ProductGroupCommandFactory commandFactory,
            ProductGroupCommandCoordinator coordinator,
            ProductGroupReadManager productGroupReadManager) {
        this.commandFactory = commandFactory;
        this.coordinator = coordinator;
        this.productGroupReadManager = productGroupReadManager;
    }

    @Override
    public void execute(UpdateProductGroupBasicInfoCommand command) {
        ProductGroup productGroup =
                productGroupReadManager.getById(ProductGroupId.of(command.productGroupId()));
        ProductGroupUpdateData updateData =
                commandFactory.createUpdateData(command, productGroup.optionType());
        coordinator.update(updateData);
    }
}
