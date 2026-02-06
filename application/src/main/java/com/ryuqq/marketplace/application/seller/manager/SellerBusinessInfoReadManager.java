package com.ryuqq.marketplace.application.seller.manager;

import com.ryuqq.marketplace.application.seller.port.out.query.SellerBusinessInfoQueryPort;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.exception.BusinessInfoNotFoundException;
import com.ryuqq.marketplace.domain.seller.id.SellerBusinessInfoId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** SellerBusinessInfo Read Manager. */
@Component
public class SellerBusinessInfoReadManager {

    private final SellerBusinessInfoQueryPort queryPort;

    public SellerBusinessInfoReadManager(SellerBusinessInfoQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public SellerBusinessInfo getById(SellerBusinessInfoId id) {
        return queryPort.findById(id).orElseThrow(BusinessInfoNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public SellerBusinessInfo getBySellerId(SellerId sellerId) {
        return queryPort.findBySellerId(sellerId).orElseThrow(BusinessInfoNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public boolean existsBySellerId(SellerId sellerId) {
        return queryPort.existsBySellerId(sellerId);
    }

    @Transactional(readOnly = true)
    public boolean existsByRegistrationNumber(String registrationNumber) {
        return queryPort.existsByRegistrationNumber(registrationNumber);
    }

    @Transactional(readOnly = true)
    public boolean existsByRegistrationNumberExcluding(
            String registrationNumber, SellerId excludeId) {
        return queryPort.existsByRegistrationNumberExcluding(registrationNumber, excludeId);
    }
}
