package com.ryuqq.marketplace.application.exchange.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.dto.ExchangeBatchResult;
import com.ryuqq.marketplace.application.exchange.dto.command.ConvertToRefundBatchCommand;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.internal.ExchangePersistenceFacade;
import com.ryuqq.marketplace.application.exchange.port.in.command.ConvertToRefundBatchUseCase;
import com.ryuqq.marketplace.application.exchange.validator.ExchangeBatchValidator;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.port.in.command.RequestRefundBatchUseCase;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.refund.vo.RefundReasonType;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 교환 건 환불 전환 일괄 처리 서비스.
 *
 * <p>교환 건을 취소하고 환불 요청을 생성합니다 (크로스 도메인). Exchange는 CANCELLED 처리, Refund는 신규 생성됩니다.
 */
@Service
public class ConvertToRefundBatchService implements ConvertToRefundBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConvertToRefundBatchService.class);

    private final ExchangeBatchValidator validator;
    private final ExchangeCommandFactory commandFactory;
    private final ExchangePersistenceFacade persistenceFacade;
    private final RequestRefundBatchUseCase requestRefundBatchUseCase;

    public ConvertToRefundBatchService(
            ExchangeBatchValidator validator,
            ExchangeCommandFactory commandFactory,
            ExchangePersistenceFacade persistenceFacade,
            RequestRefundBatchUseCase requestRefundBatchUseCase) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.requestRefundBatchUseCase = requestRefundBatchUseCase;
    }

    @Override
    public BatchProcessingResult<String> execute(ConvertToRefundBatchCommand command) {
        List<ExchangeClaim> claims =
                validator.validateAndGet(command.exchangeClaimIds(), command.sellerId());

        ExchangeBatchResult batchResult = ExchangeBatchResult.create("CONVERT");
        List<RequestRefundBatchCommand.RefundRequestItem> refundItems = new ArrayList<>();

        for (ExchangeClaim claim : claims) {
            try {
                String fromStatus = claim.status().name();
                claim.cancel(commandFactory.now());
                ClaimHistory history =
                        commandFactory.createConvertToRefundHistory(
                                claim, fromStatus, command.processedBy());
                batchResult.addSuccess(claim, history);

                refundItems.add(
                        new RequestRefundBatchCommand.RefundRequestItem(
                                claim.orderItemIdValue(),
                                claim.exchangeQty(),
                                RefundReasonType.OTHER,
                                "교환 건 환불 전환 (교환번호: " + claim.claimNumberValue() + ")"));
            } catch (Exception e) {
                log.warn(
                        "교환→환불 전환 실패: exchangeClaimId={}, error={}",
                        claim.idValue(),
                        e.getMessage());
                batchResult.addFailure(claim.idValue(), e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistClaimsWithHistories(
                    batchResult.claims(), batchResult.histories());
        }

        if (!refundItems.isEmpty()) {
            long sellerId = claims.get(0).sellerId();
            RequestRefundBatchCommand refundCommand =
                    new RequestRefundBatchCommand(refundItems, command.processedBy(), sellerId);
            requestRefundBatchUseCase.execute(refundCommand);
        }

        return batchResult.toBatchProcessingResult();
    }
}
