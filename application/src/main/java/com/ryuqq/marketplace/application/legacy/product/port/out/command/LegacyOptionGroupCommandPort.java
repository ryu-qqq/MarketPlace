package com.ryuqq.marketplace.application.legacy.product.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;

/** 세토프 DB option_group 테이블 커맨드 Port. */
public interface LegacyOptionGroupCommandPort {

    Long persist(SellerOptionGroup optionGroup);
}
