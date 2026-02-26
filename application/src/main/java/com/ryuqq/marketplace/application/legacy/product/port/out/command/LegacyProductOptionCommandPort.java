package com.ryuqq.marketplace.application.legacy.product.port.out.command;

import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import java.util.List;

/** 세토프 DB product_option 테이블 커맨드 Port. */
public interface LegacyProductOptionCommandPort {

    void persist(LegacyProductOption productOption);

    void persistAll(List<LegacyProductOption> productOptions);
}
