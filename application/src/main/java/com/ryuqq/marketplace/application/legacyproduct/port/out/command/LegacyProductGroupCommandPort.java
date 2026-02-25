package com.ryuqq.marketplace.application.legacyproduct.port.out.command;

import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;

/** 세토프 DB product_group 테이블 커맨드 Port. */
public interface LegacyProductGroupCommandPort {

    Long persist(LegacyProductGroup productGroup);
}
