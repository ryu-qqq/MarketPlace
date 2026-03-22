package com.ryuqq.marketplace.application.legacy.productgroupimage.port.out.command;

import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;

/** 레거시 상품 이미지 저장 Port. */
public interface LegacyProductImageCommandPort {

    Long persist(ProductGroupImage image);
}
