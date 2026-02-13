package com.ryuqq.marketplace.application.productgroup.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupCommandFactory;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productgroup.port.in.command.UpdateProductGroupBasicInfoUseCase;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import org.springframework.stereotype.Service;

/**
 * UpdateProductGroupBasicInfoService - 상품 그룹 기본 정보 수정 Service.
 *
 * <p>APP-SVC-001: @Service 어노테이션
 *
 * <p>APP-SVC-002: UseCase 구현
 *
 * <p>APP-TRX-001: @Transactional은 Port-Out (Adapter)에서 처리
 */
@Service
public class UpdateProductGroupBasicInfoService implements UpdateProductGroupBasicInfoUseCase {

    private final ProductGroupCommandFactory commandFactory;
    private final ProductGroupReadManager readManager;
    private final ProductGroupCommandManager commandManager;

    public UpdateProductGroupBasicInfoService(
            ProductGroupCommandFactory commandFactory,
            ProductGroupReadManager readManager,
            ProductGroupCommandManager commandManager) {
        this.commandFactory = commandFactory;
        this.readManager = readManager;
        this.commandManager = commandManager;
    }

    @Override
    public void execute(UpdateProductGroupBasicInfoCommand command) {
        UpdateContext<ProductGroupId, UpdateProductGroupBasicInfoCommand> context =
                commandFactory.createBasicInfoUpdateContext(command);

        ProductGroup productGroup = readManager.getById(context.id());

        productGroup.updateBasicInfo(
                ProductGroupName.of(context.updateData().productGroupName()),
                BrandId.of(context.updateData().brandId()),
                CategoryId.of(context.updateData().categoryId()),
                ShippingPolicyId.of(context.updateData().shippingPolicyId()),
                RefundPolicyId.of(context.updateData().refundPolicyId()),
                context.changedAt());

        commandManager.persist(productGroup);
    }
}
