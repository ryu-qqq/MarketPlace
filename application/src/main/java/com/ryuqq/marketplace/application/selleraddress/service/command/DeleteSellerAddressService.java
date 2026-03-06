package com.ryuqq.marketplace.application.selleraddress.service.command;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.selleraddress.dto.command.DeleteSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.factory.SellerAddressCommandFactory;
import com.ryuqq.marketplace.application.selleraddress.internal.SellerAddressOutboundFacade;
import com.ryuqq.marketplace.application.selleraddress.port.in.command.DeleteSellerAddressUseCase;
import com.ryuqq.marketplace.application.selleraddress.validator.SellerAddressValidator;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 셀러 주소 삭제 Service. 기본 주소는 동일 타입에 다른 주소가 있을 때만 삭제 불가(마지막 하나는 삭제 허용). */
@Service
public class DeleteSellerAddressService implements DeleteSellerAddressUseCase {

    private final SellerAddressCommandFactory commandFactory;
    private final SellerAddressValidator validator;
    private final SellerAddressOutboundFacade outboundFacade;

    public DeleteSellerAddressService(
            SellerAddressCommandFactory commandFactory,
            SellerAddressValidator validator,
            SellerAddressOutboundFacade outboundFacade) {
        this.commandFactory = commandFactory;
        this.validator = validator;
        this.outboundFacade = outboundFacade;
    }

    @Transactional
    @Override
    public void execute(DeleteSellerAddressCommand command) {
        StatusChangeContext<SellerAddressId> context = commandFactory.createDeleteContext(command);
        SellerAddress address = validator.findExistingOrThrow(context.id());
        validator.validateNotDefaultAddress(address);

        address.delete(context.changedAt());
        outboundFacade.persistDeleteWithSync(address.sellerId(), address, context.changedAt());
    }
}
