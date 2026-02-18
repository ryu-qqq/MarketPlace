package com.ryuqq.marketplace.application.selleroption.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import java.util.List;

/** SellerOptionValue Command Port. */
public interface SellerOptionValueCommandPort {

    Long persist(SellerOptionValue value);

    List<Long> persistAll(List<SellerOptionValue> values);
}
