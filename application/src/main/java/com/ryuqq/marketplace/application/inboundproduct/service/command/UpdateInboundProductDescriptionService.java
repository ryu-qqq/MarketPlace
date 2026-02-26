package com.ryuqq.marketplace.application.inboundproduct.service.command;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductIdResolver;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductDescriptionUseCase;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 인바운드 상품 상세설명 수정 서비스. */
@Service
public class UpdateInboundProductDescriptionService
        implements UpdateInboundProductDescriptionUseCase {

    private final InboundProductIdResolver idResolver;
    private final DescriptionCommandCoordinator descriptionCommandCoordinator;

    public UpdateInboundProductDescriptionService(
            InboundProductIdResolver idResolver,
            DescriptionCommandCoordinator descriptionCommandCoordinator) {
        this.idResolver = idResolver;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
    }

    @Override
    @Transactional
    public void execute(long inboundSourceId, String externalProductCode, String content) {
        ProductGroupId pgId = idResolver.resolve(inboundSourceId, externalProductCode);
        UpdateProductGroupDescriptionCommand command =
                new UpdateProductGroupDescriptionCommand(pgId.value(), content);
        descriptionCommandCoordinator.update(command);
    }
}
