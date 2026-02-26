package com.ryuqq.marketplace.application.legacy.image.port.out.command;

import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import java.util.List;

/** 세토프 DB product_group_image 테이블 커맨드 Port. */
public interface LegacyProductImageCommandPort {

    void persistAll(List<LegacyProductImage> images);
}
