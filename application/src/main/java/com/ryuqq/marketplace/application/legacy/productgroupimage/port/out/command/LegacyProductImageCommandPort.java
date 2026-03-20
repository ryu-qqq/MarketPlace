package com.ryuqq.marketplace.application.legacy.productgroupimage.port.out.command;

import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.util.List;

/** 레거시 상품 이미지 저장 Port. */
public interface LegacyProductImageCommandPort {

    void persistAll(List<LegacyProductImage> images);

    /** 표준 도메인 객체를 받아서 luxurydb에 저장. 기존 이미지 soft delete 후 replace. */
    void replaceAll(long productGroupId, List<ProductGroupImage> images);
}
