package com.ryuqq.marketplace.application.seller.service.command;

import com.ryuqq.marketplace.application.seller.dto.bundle.SellerUpdateBundle;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerFullCommand;
import com.ryuqq.marketplace.application.seller.factory.SellerCommandFactory;
import com.ryuqq.marketplace.application.seller.internal.SellerUpdateCoordinator;
import com.ryuqq.marketplace.application.seller.port.in.command.UpdateSellerFullUseCase;
import org.springframework.stereotype.Service;

/**
 * UpdateSellerFullService - 셀러 전체 수정 Service.
 *
 * <p>Seller + BusinessInfo + Address 한번에 수정 (모두 1:1 관계)
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>APP-VAL-001: 검증은 Coordinator에 위임
 *
 * @author ryu-qqq
 */
@Service
public class UpdateSellerFullService implements UpdateSellerFullUseCase {

    private final SellerCommandFactory commandFactory;
    private final SellerUpdateCoordinator coordinator;

    public UpdateSellerFullService(
            SellerCommandFactory commandFactory, SellerUpdateCoordinator coordinator) {
        this.commandFactory = commandFactory;
        this.coordinator = coordinator;
    }

    @Override
    public void execute(UpdateSellerFullCommand command) {
        SellerUpdateBundle bundle = commandFactory.createUpdateBundle(command);
        coordinator.update(bundle);
    }
}
