package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.ExchangeBatchResult;
import com.ryuqq.marketplace.application.exchange.dto.command.PrepareExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.port.in.command.PrepareExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 교환 준비 완료 일괄 처리 서비스 (COLLECTED → PREPARING).
 *
 * <p>네이버에 별도 API 호출 불필요 — 내부 상태 변경만 수행합니다.
 */
@Service
public class PrepareExchangeBatchService implements PrepareExchangeBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(PrepareExchangeBatchService.class);

    private final ExchangeBatchValidator validator;
    private final ExchangeCommandFactory commandFactory;
    private final ExchangePersistenceFacade persistenceFacade;

    public PrepareExchangeBatchService(
            ExchangeBatchValidator validator,
            ExchangeCommandFactory commandFactory,
            ExchangePersistenceFacade persistenceFacade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public BatchProcessingResult<String> execute(PrepareExchangeBatchCommand command) {
        List<ExchangeClaim> claims =
                validator.validateAndGet(command.exchangeClaimIds(), command.sellerId());

        ExchangeBatchResult batchResult = ExchangeBatchResult.create("PREPARE");
        for (ExchangeClaim claim : claims) {
            try {
                claim.startPreparing(command.processedBy(), commandFactory.now());
                ClaimHistory history =
                        commandFactory.createPrepareHistory(claim, command.processedBy());
                batchResult.addSuccess(claim, history);
            } catch (Exception e) {
                log.warn(
                        "교환 준비 완료 실패: exchangeClaimId={}, error={}",
                        claim.idValue(),
                        e.getMessage());
                batchResult.addFailure(claim.idValue(), e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistClaimsWithHistories(
                    batchResult.claims(), batchResult.histories());
        }

        return batchResult.toBatchProcessingResult();
    }
}
