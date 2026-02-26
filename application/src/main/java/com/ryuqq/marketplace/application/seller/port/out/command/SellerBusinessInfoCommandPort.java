package com.ryuqq.marketplace.application.seller.port.out.command;

import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import java.util.List;

/** SellerBusinessInfo Command Port. */
public interface SellerBusinessInfoCommandPort {

    Long persist(SellerBusinessInfo businessInfo);

    void persistAll(List<SellerBusinessInfo> businessInfos);
}
