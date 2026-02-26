package com.ryuqq.marketplace.application.productnotice.service.command;

import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.productnotice.port.in.command.UpdateProductNoticeUseCase;
import org.springframework.stereotype.Service;

/**
 * UpdateProductNoticeService - 상품 그룹 고시정보 수정 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 */
@Service
public class UpdateProductNoticeService implements UpdateProductNoticeUseCase {

    private final ProductNoticeCommandCoordinator productNoticeCommandCoordinator;

    public UpdateProductNoticeService(
            ProductNoticeCommandCoordinator productNoticeCommandCoordinator) {
        this.productNoticeCommandCoordinator = productNoticeCommandCoordinator;
    }

    @Override
    public void execute(UpdateProductNoticeCommand command) {
        productNoticeCommandCoordinator.update(command);
    }
}
