package com.ryuqq.marketplace.application.inboundproduct.port.in.command;

import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;

/** 인바운드 상품 이미지 수정 UseCase. */
public interface UpdateInboundProductImagesUseCase {

    void execute(
            long inboundSourceId,
            String externalProductCode,
            UpdateProductGroupImagesCommand command);
}
