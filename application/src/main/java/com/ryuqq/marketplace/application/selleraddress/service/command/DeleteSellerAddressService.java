package com.ryuqq.marketplace.application.selleraddress.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.selleraddress.dto.command.DeleteSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.factory.SellerAddressCommandFactory;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressCommandManager;
import com.ryuqq.marketplace.application.selleraddress.port.in.command.DeleteSellerAddressUseCase;
import com.ryuqq.marketplace.application.selleraddress.validator.SellerAddressValidator;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import org.springframework.stereotype.Service;

/** 셀러 주소 삭제 Service. 기본 주소는 동일 타입에 다른 주소가 있을 때만 삭제 불가(마지막 하나는 삭제 허용). */
@Service
public class DeleteSellerAddressService implements DeleteSellerAddressUseCase {

    private final SellerAddressCommandFactory commandFactory;
    private final SellerAddressCommandManager commandManager;
    private final SellerAddressValidator validator;

    public DeleteSellerAddressService(
            SellerAddressCommandFactory commandFactory,
            SellerAddressCommandManager commandManager,
            SellerAddressValidator validator) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
    }

    @Override
    public void execute(DeleteSellerAddressCommand command) {
        StatusChangeContext<SellerAddressId> context = commandFactory.createDeleteContext(command);
        SellerAddress address = validator.findExistingOrThrow(context.id());
        validator.validateNotDefaultAddress(address);

        address.delete(context.changedAt());
        commandManager.persist(address);
    }
}
