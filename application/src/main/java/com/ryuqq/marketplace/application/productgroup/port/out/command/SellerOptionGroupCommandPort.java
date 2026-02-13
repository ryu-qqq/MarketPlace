package com.ryuqq.marketplace.application.productgroup.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import java.util.List;

/** SellerOptionGroup Command Port. */
public interface SellerOptionGroupCommandPort {

    List<Long> findGroupIdsByProductGroupId(Long productGroupId);

    void deleteByProductGroupId(Long productGroupId);

    Long persist(Long productGroupId, SellerOptionGroup group);
}
