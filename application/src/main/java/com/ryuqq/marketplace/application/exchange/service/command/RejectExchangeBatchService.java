package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.ExchangeBatchResult;
import com.ryuqq.marketplace.application.exchange.dto.command.RejectExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.port.in.command.RejectExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 교환 거절 일괄 처리 서비스. */
@Service
public class RejectExchangeBatchService implements RejectExchangeBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(RejectExchangeBatchService.class);

    private final ExchangeBatchValidator validator;
    private final ExchangeCommandFactory commandFactory;
    private final ExchangePersistenceFacade persistenceFacade;

    public RejectExchangeBatchService(
            ExchangeBatchValidator validator,
            ExchangeCommandFactory commandFactory,
            ExchangePersistenceFacade persistenceFacade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public BatchProcessingResult<String> execute(RejectExchangeBatchCommand command) {
        List<ExchangeClaim> claims =
                validator.validateAndGet(command.exchangeClaimIds(), command.sellerId());

        ExchangeBatchResult batchResult = ExchangeBatchResult.create("REJECT");
        for (ExchangeClaim claim : claims) {
            try {
                String fromStatus = claim.status().name();
                claim.reject(command.processedBy(), commandFactory.now());
                OutboxWithHistory bundle =
                        commandFactory.createRejectBundle(claim, fromStatus, command.processedBy());
                batchResult.addSuccess(claim, bundle.outbox(), bundle.history());
            } catch (Exception e) {
                log.warn(
                        "교환 거절 실패: exchangeClaimId={}, error={}",
                        claim.idValue(),
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
