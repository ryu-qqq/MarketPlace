package com.ryuqq.marketplace.application.selleraddress.port.out.command;

import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import java.util.List;

/** SellerAddress Command Port. */
public interface SellerAddressCommandPort {

    Long persist(SellerAddress address);

    void persistAll(List<SellerAddress> addresses);
}
