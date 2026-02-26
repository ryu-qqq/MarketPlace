package com.ryuqq.marketplace.application.seller.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerBusinessInfoCommand;
import com.ryuqq.marketplace.application.seller.factory.SellerCommandFactory;
import com.ryuqq.marketplace.application.seller.manager.SellerBusinessInfoCommandManager;
import com.ryuqq.marketplace.application.seller.port.in.command.UpdateSellerBusinessInfoUseCase;
import com.ryuqq.marketplace.application.seller.validator.SellerBusinessInfoValidator;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfoUpdateData;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Service;

/**
 * UpdateSellerBusinessInfoService - 사업자 정보 수정 Service.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>APP-VAL-001: 검증은 Validator에 위임
 *
 * @author ryu-qqq
 */
@Service
public class UpdateSellerBusinessInfoService implements UpdateSellerBusinessInfoUseCase {

    private final SellerCommandFactory commandFactory;
    private final SellerBusinessInfoCommandManager commandManager;
    private final SellerBusinessInfoValidator validator;

    public UpdateSellerBusinessInfoService(
            SellerCommandFactory commandFactory,
            SellerBusinessInfoCommandManager commandManager,
            SellerBusinessInfoValidator validator) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
    }

    @Override
    public void execute(UpdateSellerBusinessInfoCommand command) {
        UpdateContext<SellerId, SellerBusinessInfoUpdateData> context =
                commandFactory.createUpdateContext(command);

        SellerBusinessInfo businessInfo = validator.findExistingOrThrow(context.id());
        businessInfo.update(context.updateData(), context.changedAt());

        commandManager.persist(businessInfo);
    }
}
