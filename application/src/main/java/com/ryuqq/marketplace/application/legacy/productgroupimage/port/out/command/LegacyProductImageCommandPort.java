package com.ryuqq.marketplace.application.legacy.productgroupimage.port.out.command;

import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import java.util.List;

/** 레거시 상품 이미지 저장 Port. */
public interface LegacyProductImageCommandPort {

    void persistAll(List<LegacyProductImage> images);

    /** 표준 커맨드를 받아서 luxurydb에 저장. */
    void update(UpdateProductGroupImagesCommand command);
}
