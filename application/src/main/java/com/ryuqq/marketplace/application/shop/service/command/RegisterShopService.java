package com.ryuqq.marketplace.application.shop.service.command;

import com.ryuqq.marketplace.application.shop.dto.command.RegisterShopCommand;
import com.ryuqq.marketplace.application.shop.factory.ShopCommandFactory;
import com.ryuqq.marketplace.application.shop.manager.ShopWriteManager;
import com.ryuqq.marketplace.application.shop.port.in.command.RegisterShopUseCase;
import com.ryuqq.marketplace.application.shop.validator.ShopValidator;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import com.ryuqq.marketplace.domain.shop.exception.ShopAccountIdDuplicateException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

/** Shop 등록 Service. */
@Service
public class RegisterShopService implements RegisterShopUseCase {

    private final ShopValidator validator;
    private final ShopCommandFactory commandFactory;
    private final ShopWriteManager writeManager;

    public RegisterShopService(
            ShopValidator validator,
            ShopCommandFactory commandFactory,
            ShopWriteManager writeManager) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.writeManager = writeManager;
    }

    @Override
    public Long execute(RegisterShopCommand command) {
        validator.validateAccountNotDuplicate(command.salesChannelId(), command.accountId());

        Shop shop = commandFactory.create(command);
        try {
            return writeManager.persist(shop);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMessage();
            if (message != null && message.contains("uq_sc_account")) {
                throw new ShopAccountIdDuplicateException(command.accountId());
            }
            throw e;
        }
    }
}
