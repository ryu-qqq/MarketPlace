package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.ExchangeBatchResult;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand.ExchangeRequestItem;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.port.in.command.RequestExchangeBatchUseCase;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 교환 요청 일괄 처리 서비스.
 *
 * <p>교환 요청은 새 ExchangeClaim을 생성하므로 기존 ExchangeClaim 조회/검증이 불필요합니다.
 * 네이버에 호출할 API가 없으므로 Outbox를 생성하지 않습니다.
 */
@Service
public class RequestExchangeBatchService implements RequestExchangeBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestExchangeBatchService.class);

    private final ExchangeCommandFactory commandFactory;
    private final ExchangePersistenceFacade persistenceFacade;
    private final ClaimHistoryFactory historyFactory;

    public RequestExchangeBatchService(
            ExchangeCommandFactory commandFactory,
            ExchangePersistenceFacade persistenceFacade,
            ClaimHistoryFactory historyFactory) {
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.historyFactory = historyFactory;
    }

    @Override
    public BatchProcessingResult<String> execute(RequestExchangeBatchCommand command) {
        ExchangeBatchResult batchResult = ExchangeBatchResult.create();

        for (ExchangeRequestItem item : command.items()) {
            try {
                ExchangeClaim claim =
                        commandFactory.createExchangeRequest(
                                item, command.requestedBy(), command.sellerId());
                ClaimHistory history =
                        historyFactory.createStatusChange(
                                ClaimType.EXCHANGE,
                                claim.idValue(),
                                null,
                                "REQUESTED",
                                command.requestedBy(),
                                command.requestedBy());
                batchResult.addSuccess(claim, history);
            } catch (Exception e) {
                log.warn(
                        "교환 요청 생성 실패: orderItemId={}, error={}",
                        item.orderItemId(),
                        e.getMessage());
                batchResult.addFailure(
                        item.orderItemId(), "EXCHANGE_CREATION_FAILED", e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistAllWithHistories(
                    batchResult.claims(), batchResult.histories());
        }

        return batchResult.toBatchProcessingResult();
    }
}
