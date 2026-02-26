package com.ryuqq.marketplace.domain.selleraddress.aggregate;

import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressName;

/**
 * 셀러 주소 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 */
public record SellerAddressUpdateData(AddressName addressName, Address address) {

    public static SellerAddressUpdateData of(AddressName addressName, Address address) {
        return new SellerAddressUpdateData(addressName, address);
    }
}
