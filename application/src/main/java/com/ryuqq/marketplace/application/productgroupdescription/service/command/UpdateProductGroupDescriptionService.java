package com.ryuqq.marketplace.application.productgroupdescription.service.command;

import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.factory.ProductGroupDescriptionCommandFactory;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionCommandManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.application.productgroupdescription.port.in.command.UpdateProductGroupDescriptionUseCase;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * UpdateProductGroupDescriptionService - 상품 그룹 상세 설명 수정 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 *
 * <p>APP-TRX-001: @Transactional은 Port-Out (Adapter)에서 처리
 */
@Service
public class UpdateProductGroupDescriptionService implements UpdateProductGroupDescriptionUseCase {

    private final ProductGroupDescriptionCommandFactory commandFactory;
    private final ProductGroupDescriptionReadManager readManager;
    private final ProductGroupDescriptionCommandManager commandManager;

    public UpdateProductGroupDescriptionService(
            ProductGroupDescriptionCommandFactory commandFactory,
            ProductGroupDescriptionReadManager readManager,
            ProductGroupDescriptionCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateProductGroupDescriptionCommand command) {
        ProductGroupId productGroupId = ProductGroupId.of(command.productGroupId());
        Optional<ProductGroupDescription> existingOpt =
                readManager.findByProductGroupId(productGroupId);

        ProductGroupDescription description =
                commandFactory.createOrUpdateDescription(command, existingOpt);

        commandManager.persist(description);
    }
}
