package com.ryuqq.marketplace.application.productgroup.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import java.util.List;

/** SellerOptionValue Command Port. */
public interface SellerOptionValueCommandPort {

    void deleteByGroupIdIn(List<Long> groupIds);

    void persistAll(Long groupId, List<SellerOptionValue> values);
}
