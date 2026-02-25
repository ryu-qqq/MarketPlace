package com.ryuqq.marketplace.application.legacyproduct.port.out.command;

import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;

/** 세토프 DB product_option 테이블 커맨드 Port. */
public interface LegacyProductOptionCommandPort {

    void persist(LegacyProductOption productOption);
}
