package com.ryuqq.marketplace.application.selleraddress.factory;

import com.ryuqq.marketplace.application.common.dto.command.RegisterContext;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.selleraddress.dto.command.DeleteSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.RegisterSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.UpdateSellerAddressCommand;
import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddressUpdateData;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressName;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * SellerAddress Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 */
@Component
public class SellerAddressCommandFactory {

    private final TimeProvider timeProvider;

    public SellerAddressCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 등록 Command로부터 RegisterContext 생성. (새 Aggregate + changedAt)
     *
     * <p>Service는 context.newEntity()로 등록 대상, context.changedAt()으로 부가 작업(기본 배송지 해제 등) 시각 사용.
     *
     * @param command 등록 Command
     * @return RegisterContext (newEntity, changedAt)
     */
    public RegisterContext<SellerAddress> createRegisterContext(
            RegisterSellerAddressCommand command) {
        Instant changedAt = timeProvider.now();
        SellerAddress newAddress =
                SellerAddress.forNew(
                        SellerId.of(command.sellerId()),
                        AddressType.valueOf(command.addressType()),
                        AddressName.of(command.addressName()),
                        toAddress(command.address()),
                        command.defaultAddress(),
                        changedAt);
        return new RegisterContext<>(newAddress, changedAt);
    }

    /**
     * 수정 Command로부터 UpdateContext 생성.
     *
     * @param command 수정 Command
     * @return UpdateContext
     */
    public UpdateContext<SellerAddressId, SellerAddressUpdateData> createUpdateContext(
            UpdateSellerAddressCommand command) {
        SellerAddressId addressId = SellerAddressId.of(command.addressId());
        SellerAddressUpdateData updateData =
                SellerAddressUpdateData.of(
                        AddressName.of(command.addressName()), toAddress(command.address()));
        return new UpdateContext<>(addressId, updateData, timeProvider.now());
    }

    /**
     * 삭제 Command로부터 StatusChangeContext 생성. (삭제 시각 = changedAt)
     *
     * <p>Service는 context.id()로 삭제 대상, context.changedAt()으로 deletedAt 사용.
     *
     * @param command 삭제 Command
     * @return StatusChangeContext (addressId, changedAt)
     */
    public StatusChangeContext<SellerAddressId> createDeleteContext(
            DeleteSellerAddressCommand command) {
        return new StatusChangeContext<>(
                SellerAddressId.of(command.addressId()), timeProvider.now());
    }

    private Address toAddress(RegisterSellerAddressCommand.AddressCommand cmd) {
        if (cmd == null) {
            return null;
        }
        return Address.of(cmd.zipCode(), cmd.line1(), cmd.line2());
    }

    private Address toAddress(UpdateSellerAddressCommand.AddressCommand cmd) {
        if (cmd == null) {
            return null;
        }
        return Address.of(cmd.zipCode(), cmd.line1(), cmd.line2());
    }
}
