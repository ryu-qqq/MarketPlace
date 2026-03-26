package com.ryuqq.marketplace.application.legacy.product.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;

/** 세토프 DB option_detail 테이블 커맨드 Port. */
public interface LegacyOptionDetailCommandPort {

    Long persist(SellerOptionValue optionValue);
}
