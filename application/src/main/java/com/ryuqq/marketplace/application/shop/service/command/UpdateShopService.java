package com.ryuqq.marketplace.application.shop.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.shop.dto.command.UpdateShopCommand;
import com.ryuqq.marketplace.application.shop.factory.ShopCommandFactory;
import com.ryuqq.marketplace.application.shop.manager.ShopWriteManager;
import com.ryuqq.marketplace.application.shop.port.in.command.UpdateShopUseCase;
import com.ryuqq.marketplace.application.shop.validator.ShopValidator;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.aggregate.ShopUpdateData;
import com.ryuqq.marketplace.domain.shop.id.ShopId;
import org.springframework.stereotype.Service;

/**
 * Shop 수정 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리.
 *
 * <p>FAC-008: createUpdateContext()로 ID, UpdateData, changedAt 한 번에 생성.
 *
 * <p>APP-VAL-001: 검증 + Domain 조회는 Validator.findExistingOrThrow()로 처리.
 */
@Service
public class UpdateShopService implements UpdateShopUseCase {

    private final ShopCommandFactory commandFactory;
    private final ShopWriteManager writeManager;
    private final ShopValidator validator;

    public UpdateShopService(
            ShopCommandFactory commandFactory,
            ShopWriteManager writeManager,
            ShopValidator validator) {
        this.commandFactory = commandFactory;
        this.writeManager = writeManager;
        this.validator = validator;
    }

    @Override
    public void execute(UpdateShopCommand command) {
        UpdateContext<ShopId, ShopUpdateData> context = commandFactory.createUpdateContext(command);
        ShopId shopId = context.id();

        Shop shop = validator.findExistingOrThrow(shopId);

        validator.validateShopNameNotDuplicateExcluding(context.updateData().shopName(), shopId);
        validator.validateAccountIdNotDuplicateExcluding(context.updateData().accountId(), shopId);

        shop.update(context.updateData(), context.changedAt());

        writeManager.persist(shop);
    }
}
