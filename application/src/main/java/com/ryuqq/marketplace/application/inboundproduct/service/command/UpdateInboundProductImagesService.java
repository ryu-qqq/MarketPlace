package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductIdResolver;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductImagesUseCase;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 인바운드 상품 이미지 수정 서비스. */
@Service
public class UpdateInboundProductImagesService implements UpdateInboundProductImagesUseCase {

    private final InboundProductIdResolver idResolver;
    private final ImageCommandCoordinator imageCommandCoordinator;

    public UpdateInboundProductImagesService(
            InboundProductIdResolver idResolver, ImageCommandCoordinator imageCommandCoordinator) {
        this.idResolver = idResolver;
        this.imageCommandCoordinator = imageCommandCoordinator;
    }

    @Override
    @Transactional
    public void execute(
            long inboundSourceId,
            String externalProductCode,
            UpdateProductGroupImagesCommand command) {
        ProductGroupId pgId = idResolver.resolve(inboundSourceId, externalProductCode);
        UpdateProductGroupImagesCommand boundCommand =
                new UpdateProductGroupImagesCommand(pgId.value(), command.images());
        imageCommandCoordinator.update(boundCommand);
    }
}
