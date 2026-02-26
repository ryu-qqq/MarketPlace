package com.ryuqq.marketplace.application.selleraddress.service.command;

import com.ryuqq.marketplace.application.common.dto.command.RegisterContext;
import com.ryuqq.marketplace.application.selleraddress.dto.command.RegisterSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.factory.SellerAddressCommandFactory;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressCommandManager;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressReadManager;
import com.ryuqq.marketplace.application.selleraddress.port.in.command.RegisterSellerAddressUseCase;
import com.ryuqq.marketplace.application.selleraddress.validator.SellerAddressValidator;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** 셀러 주소 등록 Service. */
@Service
public class RegisterSellerAddressService implements RegisterSellerAddressUseCase {

    private final SellerAddressCommandFactory commandFactory;
    private final SellerAddressCommandManager commandManager;
    private final SellerAddressReadManager readManager;
    private final SellerAddressValidator validator;

    public RegisterSellerAddressService(
            SellerAddressCommandFactory commandFactory,
            SellerAddressCommandManager commandManager,
            SellerAddressReadManager readManager,
            SellerAddressValidator validator) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.readManager = readManager;
        this.validator = validator;
    }

    @Override
    public Long execute(RegisterSellerAddressCommand command) {
        SellerId sellerId = SellerId.of(command.sellerId());
        AddressType addressType = AddressType.valueOf(command.addressType());

        validator.validateNoDuplicateAddressName(sellerId, addressType, command.addressName());

        RegisterContext<SellerAddress> context = commandFactory.createRegisterContext(command);

        if (command.defaultAddress()) {
            unmarkExistingDefaults(sellerId, addressType, context.changedAt());
        }

        return commandManager.persist(context.newEntity());
    }

    private void unmarkExistingDefaults(
            SellerId sellerId, AddressType addressType, Instant changedAt) {
        readManager
                .findDefaultBySellerId(sellerId, addressType)
                .ifPresent(
                        existingDefault -> {
                            existingDefault.unmarkDefault(changedAt);
                            commandManager.persist(existingDefault);
                        });
    }
}
