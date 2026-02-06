package com.ryuqq.marketplace.application.selleraddress.manager;

import com.ryuqq.marketplace.application.selleraddress.port.out.query.SellerAddressQueryPort;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.exception.SellerAddressNotFoundException;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.selleraddress.query.SellerAddressSearchCriteria;
import com.ryuqq.marketplace.domain.selleraddress.vo.AddressType;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SellerAddress Read Manager. */
@Component
public class SellerAddressReadManager {

    private final SellerAddressQueryPort queryPort;

    public SellerAddressReadManager(SellerAddressQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public SellerAddress getById(SellerAddressId id) {
        return queryPort.findById(id).orElseThrow(SellerAddressNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<SellerAddress> findAllBySellerId(SellerId sellerId) {
        return queryPort.findAllBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public Optional<SellerAddress> findDefaultBySellerId(
            SellerId sellerId, AddressType addressType) {
        return queryPort.findDefaultBySellerId(sellerId, addressType);
    }

    @Transactional(readOnly = true)
    public boolean existsBySellerId(SellerId sellerId) {
        return queryPort.existsBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public boolean existsBySellerIdAndAddressTypeAndAddressName(
            SellerId sellerId, AddressType addressType, String addressName) {
        return queryPort.existsBySellerIdAndAddressTypeAndAddressName(
                sellerId, addressType, addressName);
    }

    @Transactional(readOnly = true)
    public List<SellerAddress> search(SellerAddressSearchCriteria criteria) {
        return queryPort.search(criteria);
    }

    @Transactional(readOnly = true)
    public long count(SellerAddressSearchCriteria criteria) {
        return queryPort.count(criteria);
    }
}
