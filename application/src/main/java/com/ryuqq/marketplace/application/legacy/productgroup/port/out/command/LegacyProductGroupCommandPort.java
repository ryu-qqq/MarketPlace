package com.ryuqq.marketplace.application.legacy.productgroup.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;

/** 세토프 DB product_group 테이블 커맨드 Port. */
public interface LegacyProductGroupCommandPort {

    Long persist(ProductGroup productGroup, long regularPrice, long currentPrice);

    void persist(ProductGroupUpdateData updateData, long regularPrice, long currentPrice);

    void updateDisplayYn(long productGroupId, String displayYn);

    void markSoldOut(long productGroupId);

    void updatePrice(long productGroupId, long regularPrice, long currentPrice);
}
