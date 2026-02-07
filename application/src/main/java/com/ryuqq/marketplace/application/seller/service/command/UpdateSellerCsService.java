package com.ryuqq.marketplace.application.seller.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCsCommand;
import com.ryuqq.marketplace.application.seller.factory.SellerCsCommandFactory;
import com.ryuqq.marketplace.application.seller.manager.SellerCsCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerCsReadManager;
import com.ryuqq.marketplace.application.seller.port.in.command.UpdateSellerCsUseCase;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCsUpdateData;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.springframework.stereotype.Service;

/**
 * UpdateSellerCsService - 셀러 CS 정보 수정 Service.
 *
 * <p>셀러의 CS 연락처, 운영시간 등을 수정합니다.
 *
 * @author ryu-qqq
 */
@Service
public class UpdateSellerCsService implements UpdateSellerCsUseCase {

    private final SellerCsReadManager readManager;
    private final SellerCsCommandManager commandManager;
    private final SellerCsCommandFactory commandFactory;

    public UpdateSellerCsService(
            SellerCsReadManager readManager,
            SellerCsCommandManager commandManager,
            SellerCsCommandFactory commandFactory) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.commandFactory = commandFactory;
    }

    @Override
    public void execute(UpdateSellerCsCommand command) {
        SellerId sellerId = SellerId.of(command.sellerId());
        SellerCs sellerCs = readManager.getBySellerId(sellerId);

        UpdateContext<SellerId, SellerCsUpdateData> updateContext =
                commandFactory.createUpdateContext(command);

        sellerCs.update(
                updateContext.updateData().csContact(),
                updateContext.updateData().operatingHours(),
                updateContext.updateData().operatingDays(),
                updateContext.updateData().kakaoChannelUrl(),
                updateContext.changedAt());

        commandManager.persist(sellerCs);
    }
}
