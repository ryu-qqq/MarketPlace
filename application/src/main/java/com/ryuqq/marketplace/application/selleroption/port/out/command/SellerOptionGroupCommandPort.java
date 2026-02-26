package com.ryuqq.marketplace.application.selleroption.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import java.util.List;

/** SellerOptionGroup Command Port. */
public interface SellerOptionGroupCommandPort {

    Long persist(SellerOptionGroup group);

    void persistAll(List<SellerOptionGroup> groups);
}
