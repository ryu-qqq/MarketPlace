package com.ryuqq.marketplace.adapter.in.rest.selleraddress.mapper;

import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.command.RegisterSellerAddressApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.command.UpdateSellerAddressApiRequest;
import com.ryuqq.marketplace.application.selleraddress.dto.command.DeleteSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.RegisterSellerAddressCommand;
import com.ryuqq.marketplace.application.selleraddress.dto.command.UpdateSellerAddressCommand;
import org.springframework.stereotype.Component;

/**
 * SellerAddressCommandApiMapper - 셀러 주소 Command API 변환 매퍼.
 *
 * <p>API Request와 Application Command 간 변환을 담당합니다.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * <p>CQRS 분리: Command 전용 Mapper (QueryApiMapper와 분리).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class SellerAddressCommandApiMapper {

    /**
     * RegisterSellerAddressApiRequest -> RegisterSellerAddressCommand 변환.
     *
     * @param sellerId 셀러 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public RegisterSellerAddressCommand toCommand(
            Long sellerId, RegisterSellerAddressApiRequest request) {
        return new RegisterSellerAddressCommand(
                sellerId,
                request.addressType(),
                request.addressName(),
                toAddressCommand(request.address()),
                request.defaultAddress());
    }

    /**
     * UpdateSellerAddressApiRequest + PathVariable ID -> UpdateSellerAddressCommand 변환.
     *
     * <p>API-DTO-004: Update Request에 ID 포함 금지 -> PathVariable에서 전달.
     *
     * @param addressId 주소 ID (PathVariable)
     * @param request API 요청 DTO
     * @return Application Command DTO
     */
    public UpdateSellerAddressCommand toCommand(
            Long addressId, UpdateSellerAddressApiRequest request) {
        return new UpdateSellerAddressCommand(
                addressId,
                request.addressName(),
                toUpdateAddressCommand(request.address()),
                request.defaultAddress());
    }

    /**
     * 주소 삭제 Command 생성.
     *
     * @param addressId 주소 ID (PathVariable)
     * @return Application Command DTO
     */
    public DeleteSellerAddressCommand toDeleteCommand(Long addressId) {
        return new DeleteSellerAddressCommand(addressId);
    }

    private RegisterSellerAddressCommand.AddressCommand toAddressCommand(
            RegisterSellerAddressApiRequest.AddressRequest address) {
        return new RegisterSellerAddressCommand.AddressCommand(
                address.zipCode(), address.line1(), address.line2());
    }

    private UpdateSellerAddressCommand.AddressCommand toUpdateAddressCommand(
            UpdateSellerAddressApiRequest.AddressRequest address) {
        return new UpdateSellerAddressCommand.AddressCommand(
                address.zipCode(), address.line1(), address.line2());
    }
}
