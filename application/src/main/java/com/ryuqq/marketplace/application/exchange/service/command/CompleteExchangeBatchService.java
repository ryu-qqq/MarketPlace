package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.ExchangeBatchResult;
import com.ryuqq.marketplace.application.exchange.dto.command.CompleteExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.port.in.command.CompleteExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 교환 완료 일괄 처리 서비스 (SHIPPING → COMPLETED).
 *
 * <p>네이버에서 자동 완료 처리하므로 Outbox 불필요 — 내부 상태 변경만 수행합니다.
 */
@Service
public class CompleteExchangeBatchService implements CompleteExchangeBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(CompleteExchangeBatchService.class);

    private final ExchangeBatchValidator validator;
    private final ExchangeCommandFactory commandFactory;
    private final ExchangePersistenceFacade persistenceFacade;
    private final ClaimHistoryFactory historyFactory;

    public CompleteExchangeBatchService(
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
    public BatchProcessingResult<String> execute(CompleteExchangeBatchCommand command) {
        List<ExchangeClaim> claims =
                validator.validateAndGet(command.exchangeClaimIds(), command.sellerId());

        ExchangeBatchResult batchResult = ExchangeBatchResult.create();
        for (ExchangeClaim claim : claims) {
            try {
                claim.complete(command.processedBy(), commandFactory.now());
                ClaimHistory history =
                        historyFactory.createStatusChange(
                                ClaimType.EXCHANGE,
                                claim.idValue(),
                                "SHIPPING",
                                "COMPLETED",
                                command.processedBy(),
                                command.processedBy());
                batchResult.addSuccess(claim, history);
            } catch (Exception e) {
                log.warn(
                        "교환 완료 실패: exchangeClaimId={}, error={}",
                        claim.idValue(),
                        e.getMessage());
                batchResult.addFailure(claim.idValue(), "COMPLETE_FAILED", e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistClaimsWithHistories(
                    batchResult.claims(), batchResult.histories());
        }

        return batchResult.toBatchProcessingResult();
    }
}
