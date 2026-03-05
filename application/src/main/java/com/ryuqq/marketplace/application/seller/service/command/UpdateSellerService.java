package com.ryuqq.marketplace.application.seller.service.command;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCommand;
import com.ryuqq.marketplace.application.seller.factory.SellerCommandFactory;
import com.ryuqq.marketplace.application.seller.manager.SellerBusinessInfoCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerBusinessInfoReadManager;
import com.ryuqq.marketplace.application.seller.manager.SellerCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerCsCommandManager;
import com.ryuqq.marketplace.application.seller.manager.SellerCsReadManager;
import com.ryuqq.marketplace.application.seller.port.in.command.UpdateSellerUseCase;
import com.ryuqq.marketplace.application.seller.validator.SellerValidator;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxCommandManager;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfoUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCsUpdateData;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerUpdateData;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.setofsync.aggregate.SetofSyncOutbox;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncEntityType;
import com.ryuqq.marketplace.domain.setofsync.vo.SetofSyncOperationType;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UpdateSellerService - 셀러 정보 수정 Service.
 *
 * <p>Seller 기본정보 + CS + BusinessInfo를 한번에 수정합니다.
 *
 * <p>APP-TIM-001: TimeProvider 직접 사용 금지 - Factory에서 처리
 *
 * <p>APP-VAL-001: 검증은 Validator에 위임
 *
 * @author ryu-qqq
 */
@Service
public class UpdateSellerService implements UpdateSellerUseCase {

    private final SellerCommandFactory commandFactory;
    private final SellerCommandManager commandManager;
    private final SellerValidator validator;
    private final SellerCsReadManager csReadManager;
    private final SellerCsCommandManager csCommandManager;
    private final SellerBusinessInfoReadManager businessInfoReadManager;
    private final SellerBusinessInfoCommandManager businessInfoCommandManager;
    private final SetofSyncOutboxCommandManager setofSyncOutboxCommandManager;

    public UpdateSellerService(
            SellerCommandFactory commandFactory,
            SellerCommandManager commandManager,
            SellerValidator validator,
            SellerCsReadManager csReadManager,
            SellerCsCommandManager csCommandManager,
            SellerBusinessInfoReadManager businessInfoReadManager,
            SellerBusinessInfoCommandManager businessInfoCommandManager,
            Optional<SetofSyncOutboxCommandManager> setofSyncOutboxCommandManager) {
        this.commandFactory = commandFactory;
        this.commandManager = commandManager;
        this.validator = validator;
        this.csReadManager = csReadManager;
        this.csCommandManager = csCommandManager;
        this.businessInfoReadManager = businessInfoReadManager;
        this.businessInfoCommandManager = businessInfoCommandManager;
        this.setofSyncOutboxCommandManager = setofSyncOutboxCommandManager.orElse(null);
    }

    @Override
    @Transactional
    public void execute(UpdateSellerCommand command) {
        UpdateContext<SellerId, SellerUpdateData> context =
                commandFactory.createUpdateContext(command);
        SellerId sellerId = context.id();
        Instant changedAt = context.changedAt();

        Seller seller = validator.findExistingOrThrow(sellerId);
        seller.update(context.updateData(), changedAt);
        commandManager.persist(seller);

        updateCsIfPresent(command, sellerId, changedAt);
        updateBusinessInfoIfPresent(command, sellerId, changedAt);
        createSetofSyncOutbox(
                sellerId,
                sellerId.value(),
                SetofSyncEntityType.SELLER,
                SetofSyncOperationType.UPDATE,
                changedAt);
    }

    private void updateCsIfPresent(
            UpdateSellerCommand command, SellerId sellerId, Instant changedAt) {
        if (command.csInfo() == null) {
            return;
        }
        SellerCsUpdateData csUpdateData = commandFactory.createCsUpdateData(command.csInfo());
        SellerCs cs = csReadManager.getBySellerId(sellerId);
        cs.update(
                csUpdateData.csContact(),
                csUpdateData.operatingHours(),
                csUpdateData.operatingDays(),
                csUpdateData.kakaoChannelUrl(),
                changedAt);
        csCommandManager.persist(cs);
    }

    private void updateBusinessInfoIfPresent(
            UpdateSellerCommand command, SellerId sellerId, Instant changedAt) {
        if (command.businessInfo() == null) {
            return;
        }
        SellerBusinessInfoUpdateData businessInfoUpdateData =
                commandFactory.createBusinessInfoUpdateData(command.businessInfo());
        SellerBusinessInfo businessInfo = businessInfoReadManager.getBySellerId(sellerId);
        businessInfo.update(businessInfoUpdateData, changedAt);
        businessInfoCommandManager.persist(businessInfo);
    }

    private void createSetofSyncOutbox(
            SellerId sellerId,
            Long entityId,
            SetofSyncEntityType entityType,
            SetofSyncOperationType operationType,
            Instant now) {
        if (setofSyncOutboxCommandManager != null) {
            SetofSyncOutbox outbox =
                    SetofSyncOutbox.forNew(sellerId, entityId, entityType, operationType, now);
            setofSyncOutboxCommandManager.persist(outbox);
        }
    }
}
