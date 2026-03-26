package com.ryuqq.marketplace.application.seller.service.command;

import com.ryuqq.marketplace.application.seller.dto.command.RegisterSellerCommand;
import com.ryuqq.marketplace.application.seller.factory.SellerCommandFactory;
import com.ryuqq.marketplace.application.seller.internal.SellerRegistrationBundle;
import com.ryuqq.marketplace.application.seller.internal.SellerRegistrationCoordinator;
import com.ryuqq.marketplace.application.seller.port.in.command.RegisterSellerUseCase;
import org.springframework.stereotype.Service;

/**
 * RegisterSellerService - 셀러 등록 Service.
 *
 * <p>Seller + BusinessInfo + Addresses 한번에 등록
 *
 * <p>Factory에서 번들 생성 → Coordinator를 통해 검증 및 저장
 *
 * @author ryu-qqq
 */
@Service
public class RegisterSellerService implements RegisterSellerUseCase {

    private final SellerCommandFactory commandFactory;
    private final SellerRegistrationCoordinator coordinator;

    public RegisterSellerService(
            SellerCommandFactory commandFactory, SellerRegistrationCoordinator coordinator) {
        this.commandFactory = commandFactory;
        this.coordinator = coordinator;
    }

    @Override
    public Long execute(RegisterSellerCommand command) {
        SellerRegistrationBundle bundle = commandFactory.createRegistrationBundle(command);
        return coordinator.register(bundle);
    }
}
