package com.ryuqq.marketplace.application.productgroup.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupCommandFactory;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroup.port.in.command.UpdateProductGroupImagesUseCase;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupImages;
import org.springframework.stereotype.Service;

/**
 * UpdateProductGroupImagesService - 상품 그룹 이미지 수정 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 *
 * <p>APP-TRX-001: @Transactional은 Port-Out (Adapter)에서 처리
 */
@Service
public class UpdateProductGroupImagesService implements UpdateProductGroupImagesUseCase {

    private final ProductGroupCommandFactory commandFactory;
    private final ProductGroupReadManager readManager;
    private final ProductGroupCommandManager commandManager;

    public UpdateProductGroupImagesService(
            ProductGroupCommandFactory commandFactory,
            ProductGroupReadManager readManager,
            ProductGroupCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateProductGroupImagesCommand command) {
        UpdateContext<ProductGroupId, ProductGroupImages> context =
                commandFactory.createImagesUpdateContext(command);

        ProductGroup productGroup = readManager.getById(context.id());

        productGroup.replaceImages(context.updateData());

        commandManager.persist(productGroup);
    }
}
