package com.ryuqq.marketplace.application.selleraddress;

import com.ryuqq.marketplace.application.selleraddress.dto.command.DeleteSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.RegisterSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.UpdateSellerAddressCommand;

/**
 * SellerAddress Command 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SellerAddressCommandFixtures {

    private SellerAddressCommandFixtures() {}

    // ===== RegisterSellerAddressCommand =====

    public static RegisterSellerAddressCommand registerShippingCommand(Long sellerId) {
        return new RegisterSellerAddressCommand(
                sellerId, "SHIPPING", "본사 창고", addressCommand(), true);
    }

    public static RegisterSellerAddressCommand registerReturnCommand(Long sellerId) {
        return new RegisterSellerAddressCommand(
                sellerId, "RETURN", "반품 센터", addressCommand(), true);
    }

    public static RegisterSellerAddressCommand registerNonDefaultCommand(Long sellerId) {
        return new RegisterSellerAddressCommand(
                sellerId, "SHIPPING", "추가 배송지", addressCommand(), false);
    }

    public static RegisterSellerAddressCommand.AddressCommand addressCommand() {
        return new RegisterSellerAddressCommand.AddressCommand("06164", "서울 강남구 역삼로 123", "5층");
    }

    public static RegisterSellerAddressCommand.AddressCommand addressCommandWithoutDetail() {
        return new RegisterSellerAddressCommand.AddressCommand("06164", "서울 강남구 역삼로 123", null);
    }

    // ===== UpdateSellerAddressCommand =====

    public static UpdateSellerAddressCommand updateCommand(Long addressId) {
        return new UpdateSellerAddressCommand(addressId, "수정된 주소명", updateAddressCommand(), null);
    }

    public static UpdateSellerAddressCommand updateCommandSetDefault(Long addressId) {
        return new UpdateSellerAddressCommand(addressId, "수정된 주소명", updateAddressCommand(), true);
    }

    public static UpdateSellerAddressCommand.AddressCommand updateAddressCommand() {
        return new UpdateSellerAddressCommand.AddressCommand("06165", "서울 강남구 테헤란로 456", "10층");
    }

    // ===== DeleteSellerAddressCommand =====

    public static DeleteSellerAddressCommand deleteCommand(Long addressId) {
        return new DeleteSellerAddressCommand(addressId);
    }
}
