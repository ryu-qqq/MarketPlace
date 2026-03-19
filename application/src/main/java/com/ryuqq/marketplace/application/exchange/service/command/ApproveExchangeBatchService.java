package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.ExchangeBatchResult;
import com.ryuqq.marketplace.application.exchange.dto.command.ApproveExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.port.in.command.ApproveExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 교환 승인 일괄 처리 서비스 (수거 시작).
 *
 * <p>네이버에 approveExchange() 호출 불필요 — 수거 시작은 내부 상태 변경만 수행합니다.
 */
@Service
public class ApproveExchangeBatchService implements ApproveExchangeBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(ApproveExchangeBatchService.class);

    private final ExchangeBatchValidator validator;
    private final ExchangeCommandFactory commandFactory;
    private final ExchangePersistenceFacade persistenceFacade;

    public ApproveExchangeBatchService(
            ExchangeBatchValidator validator,
            ExchangeCommandFactory commandFactory,
            ExchangePersistenceFacade persistenceFacade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public BatchProcessingResult<String> execute(ApproveExchangeBatchCommand command) {
        List<ExchangeClaim> claims =
                validator.validateAndGet(command.exchangeClaimIds(), command.sellerId());

        ExchangeBatchResult batchResult = ExchangeBatchResult.create("APPROVE");
        for (ExchangeClaim claim : claims) {
            try {
                claim.startCollecting(command.processedBy(), commandFactory.now());
                ClaimHistory history =
                        commandFactory.createApproveHistory(claim, command.processedBy());
                batchResult.addSuccess(claim, history);
            } catch (Exception e) {
                log.warn("교환 승인 실패: exchangeClaimId={}, error={}", claim.idValue(), e.getMessage());
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
