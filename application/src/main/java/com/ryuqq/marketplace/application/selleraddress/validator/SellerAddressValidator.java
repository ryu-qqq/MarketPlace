package com.ryuqq.marketplace.application.selleraddress.validator;

import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressReadManager;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.exception.CannotDeleteDefaultAddressException;
import com.ryuqq.marketplace.domain.selleraddress.exception.DuplicateAddressNameException;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerAddress Validator.
 *
 * <p>APP-VAL-001: 검증 성공 시 Domain 객체를 반환합니다.
 *
 * <p>APP-VAL-002: 도메인 전용 예외를 발생시킵니다.
 */
@Component
public class SellerAddressValidator {

    private final SellerAddressReadManager readManager;

    public SellerAddressValidator(SellerAddressReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 주소 ID로 존재 여부 검증 후 Domain 객체 반환.
     *
     * @param addressId 주소 ID
     * @return SellerAddress 도메인 객체
     */
    public SellerAddress findExistingOrThrow(SellerAddressId addressId) {
        return readManager.getById(addressId);
    }

    /**
     * 기본 주소 삭제 불가 검증. 단, 해당 타입에 이 주소만 있으면 삭제 허용(0개 가능).
     *
     * <p>기본 주소인데 동일 셀러·동일 타입에 다른 주소가 있으면 예외. 마지막 하나(기본) 삭제는 허용.
     *
     * @param address 셀러 주소
     */
    public void validateNotDefaultAddress(SellerAddress address) {
        if (!address.isDefaultAddress()) {
            return;
        }
        List<SellerAddress> sameTypeOthers =
                readManager.findAllBySellerId(address.sellerId()).stream()
                        .filter(
                                a ->
                                        a.addressType() == address.addressType()
                                                && !a.id().equals(address.id()))
                        .toList();
        if (!sameTypeOthers.isEmpty()) {
            throw new CannotDeleteDefaultAddressException();
        }
    }

    /**
     * 동일 셀러·배송지 타입 내 배송지 이름 중복 검증. 중복 시 도메인 예외 발생.
     *
     * @param sellerId 셀러 ID
     * @param addressType 배송지 타입 (SHIPPING/RETURN)
     * @param addressName 배송지 이름
     */
    public void validateNoDuplicateAddressName(
            SellerId sellerId, AddressType addressType, String addressName) {
        if (readManager.existsBySellerIdAndAddressTypeAndAddressName(
                sellerId, addressType, addressName.trim())) {
            throw new DuplicateAddressNameException();
        }
    }
}
