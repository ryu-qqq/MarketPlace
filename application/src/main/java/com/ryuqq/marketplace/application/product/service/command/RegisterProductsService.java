package com.ryuqq.marketplace.application.product.service.command;

import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.internal.ProductCommandCoordinator;
import com.ryuqq.marketplace.application.product.port.in.command.RegisterProductsUseCase;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * RegisterProductsService - 상품(SKU) 일괄 등록 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 */
@Service
public class RegisterProductsService implements RegisterProductsUseCase {

    private final ProductCommandCoordinator coordinator;

    public RegisterProductsService(ProductCommandCoordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public List<Long> execute(RegisterProductsCommand command) {
        return coordinator.register(command);
    }
}
