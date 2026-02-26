package com.ryuqq.marketplace.application.selleraddress.port.out.query;

import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.selleraddress.query.SellerAddressSearchCriteria;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.util.List;
import java.util.Optional;

/** SellerAddress Query Port. */
public interface SellerAddressQueryPort {

    Optional<SellerAddress> findById(SellerAddressId id);

    List<SellerAddress> findAllBySellerId(SellerId sellerId);

    Optional<SellerAddress> findDefaultBySellerId(SellerId sellerId, AddressType addressType);

    boolean existsBySellerId(SellerId sellerId);

    /**
     * 동일 셀러·배송지 타입 내 배송지 이름 중복 여부.
     *
     * @param sellerId 셀러 ID
     * @param addressType 배송지 타입 (SHIPPING/RETURN)
     * @param addressName 배송지 이름
     * @return 중복 시 true
     */
    boolean existsBySellerIdAndAddressTypeAndAddressName(
            SellerId sellerId, AddressType addressType, String addressName);

    List<SellerAddress> search(SellerAddressSearchCriteria criteria);

    long count(SellerAddressSearchCriteria criteria);
}
