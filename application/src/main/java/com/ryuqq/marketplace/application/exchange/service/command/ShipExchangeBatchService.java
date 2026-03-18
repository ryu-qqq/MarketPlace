package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.ExchangeBatchResult;
import com.ryuqq.marketplace.application.exchange.dto.command.ShipExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.ShipExchangeBatchCommand.ShipItem;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.port.in.command.ShipExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 교환 재배송 일괄 처리 서비스 (PREPARING → SHIPPING). */
@Service
public class ShipExchangeBatchService implements ShipExchangeBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(ShipExchangeBatchService.class);

    private final ExchangeBatchValidator validator;
    private final ExchangeCommandFactory commandFactory;
    private final ExchangePersistenceFacade persistenceFacade;
    private final ClaimHistoryFactory historyFactory;

    public ShipExchangeBatchService(
            ExchangeBatchValidator validator,
            ExchangeCommandFactory commandFactory,
            ExchangePersistenceFacade persistenceFacade,
            ClaimHistoryFactory historyFactory) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.historyFactory = historyFactory;
    }

    @Override
    public BatchProcessingResult<String> execute(ShipExchangeBatchCommand command) {
        List<String> claimIds =
                command.items().stream().map(ShipItem::exchangeClaimId).toList();
        List<ExchangeClaim> claims = validator.validateAndGet(claimIds, command.sellerId());

        Map<String, ShipItem> itemMap =
                command.items().stream()
                        .collect(Collectors.toMap(ShipItem::exchangeClaimId, Function.identity()));

        ExchangeBatchResult batchResult = ExchangeBatchResult.create();
        for (ExchangeClaim claim : claims) {
            try {
                ShipItem item = itemMap.get(claim.idValue());
                claim.startShipping(item.linkedOrderId(), command.processedBy(), commandFactory.now());
                ExchangeOutbox outbox =
                        commandFactory.createShipOutbox(
                                claim, item.deliveryCompany(), item.trackingNumber());
                ClaimHistory history =
                        historyFactory.createStatusChange(
                                ClaimType.EXCHANGE,
                                claim.idValue(),
                                "PREPARING",
                                "SHIPPING",
                                command.processedBy(),
                                command.processedBy());
                batchResult.addSuccess(claim, outbox, history);
            } catch (Exception e) {
                log.warn(
                        "교환 재배송 실패: exchangeClaimId={}, error={}",
                        claim.idValue(),
                        e.getMessage());
                batchResult.addFailure(claim.idValue(), "SHIP_FAILED", e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistClaimsWithOutboxesAndHistories(
                    batchResult.claims(), batchResult.outboxes(), batchResult.histories());
        }

        return batchResult.toBatchProcessingResult();
    }
}
