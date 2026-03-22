package com.ryuqq.marketplace.application.legacy.product.port.out.query;

import com.ryuqq.marketplace.domain.product.aggregate.Product;
import java.util.List;

/** 세토프 DB product 테이블 조회 Port. */
public interface LegacyProductQueryPort {

    List<Product> findByProductGroupId(long productGroupId);
}
