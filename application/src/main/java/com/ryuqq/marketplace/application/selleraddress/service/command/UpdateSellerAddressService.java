package com.ryuqq.marketplace.application.selleraddress.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.selleraddress.dto.command.UpdateSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.factory.SellerAddressCommandFactory;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressCommandManager;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressReadManager;
import com.ryuqq.marketplace.application.selleraddress.port.in.command.UpdateSellerAddressUseCase;
import com.ryuqq.marketplace.application.selleraddress.validator.SellerAddressValidator;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddressUpdateData;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import java.time.Instant;
import org.springframework.stereotype.Service;

/** 셀러 주소 수정 Service. (기본 주소 전환 포함: defaultAddress=true 시 기존 기본 해제 후 해당 주소를 기본으로 설정) */
@Service
public class UpdateSellerAddressService implements UpdateSellerAddressUseCase {

    private final SellerAddressCommandFactory commandFactory;
    private final SellerAddressCommandManager commandManager;
    private final SellerAddressReadManager readManager;
    private final SellerAddressValidator validator;

    public UpdateSellerAddressService(
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
    public void execute(UpdateSellerAddressCommand command) {
        UpdateContext<SellerAddressId, SellerAddressUpdateData> context =
                commandFactory.createUpdateContext(command);

        SellerAddress address = validator.findExistingOrThrow(context.id());
        address.update(context.updateData(), context.changedAt());

        if (Boolean.TRUE.equals(command.defaultAddress())) {
            unmarkExistingDefaultThenMarkThis(address.sellerId(), address, context.changedAt());
        }

        commandManager.persist(address);
    }

    private void unmarkExistingDefaultThenMarkThis(
            SellerId sellerId, SellerAddress newDefault, Instant changedAt) {
        readManager
                .findDefaultBySellerId(sellerId, newDefault.addressType())
                .filter(current -> !current.id().equals(newDefault.id()))
                .ifPresent(
                        current -> {
                            current.unmarkDefault(changedAt);
                            commandManager.persist(current);
                        });
        newDefault.markAsDefault(changedAt);
    }
}
