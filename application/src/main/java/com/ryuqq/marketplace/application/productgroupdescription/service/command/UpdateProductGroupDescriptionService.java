package com.ryuqq.marketplace.application.productgroupdescription.service.command;

import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.factory.ProductGroupDescriptionCommandFactory;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.application.productgroupdescription.port.in.command.UpdateProductGroupDescriptionUseCase;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionImageDiff;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionUpdateData;
import org.springframework.stereotype.Service;

/**
 * UpdateProductGroupDescriptionService - 상품 그룹 상세 설명 수정 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 */
@Service
public class UpdateProductGroupDescriptionService implements UpdateProductGroupDescriptionUseCase {

    private final ProductGroupDescriptionCommandFactory commandFactory;
    private final ProductGroupDescriptionReadManager readManager;
    private final DescriptionCommandCoordinator descriptionCommandCoordinator;

    public UpdateProductGroupDescriptionService(
            ProductGroupDescriptionCommandFactory commandFactory,
            ProductGroupDescriptionReadManager readManager,
            DescriptionCommandCoordinator descriptionCommandCoordinator) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
    }

    @Override
    public void execute(UpdateProductGroupDescriptionCommand command) {
        DescriptionUpdateData updateData = commandFactory.createUpdateData(command);

        ProductGroupDescription description =
                readManager.getByProductGroupId(ProductGroupId.of(command.productGroupId()));

        DescriptionImageDiff diff = description.update(updateData);

        descriptionCommandCoordinator.update(description, diff);
    }
}
