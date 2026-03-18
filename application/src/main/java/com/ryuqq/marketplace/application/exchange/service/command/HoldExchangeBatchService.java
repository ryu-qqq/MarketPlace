package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.ExchangeBatchResult;
import com.ryuqq.marketplace.application.exchange.dto.command.HoldExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.port.in.command.HoldExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 교환 보류/보류 해제 일괄 처리 서비스. */
@Service
public class HoldExchangeBatchService implements HoldExchangeBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(HoldExchangeBatchService.class);

    private final ExchangeBatchValidator validator;
    private final ExchangeCommandFactory commandFactory;
    private final ExchangePersistenceFacade persistenceFacade;

    public HoldExchangeBatchService(
            ExchangeBatchValidator validator,
            ExchangeCommandFactory commandFactory,
            ExchangePersistenceFacade persistenceFacade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public BatchProcessingResult<String> execute(HoldExchangeBatchCommand command) {
        List<ExchangeClaim> claims =
                validator.validateAndGet(command.exchangeClaimIds(), command.sellerId());

        ExchangeBatchResult batchResult = ExchangeBatchResult.create(command.operationName());

        for (ExchangeClaim claim : claims) {
            try {
                OutboxWithHistory bundle;
                if (command.isHold()) {
                    claim.hold(command.memo(), commandFactory.now());
                    bundle = commandFactory.createHoldBundle(claim, command.processedBy());
                } else {
                    claim.releaseHold(commandFactory.now());
                    bundle = commandFactory.createReleaseHoldBundle(claim, command.processedBy());
                }
                batchResult.addSuccess(claim, bundle.outbox(), bundle.history());
            } catch (Exception e) {
                log.warn(
                        "교환 보류 처리 실패: exchangeClaimId={}, isHold={}, error={}",
                        claim.idValue(),
                        command.isHold(),
                        e.getMessage());
                batchResult.addFailure(claim.idValue(), e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistClaimsWithOutboxesAndHistories(
                    batchResult.claims(), batchResult.outboxes(), batchResult.histories());
        }

        return batchResult.toBatchProcessingResult();
    }
}
