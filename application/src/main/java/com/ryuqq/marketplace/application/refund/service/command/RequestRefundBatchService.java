package com.ryuqq.marketplace.application.refund.service.command;

import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.RefundBatchResult;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand.RefundRequestItem;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory.RefundWithOutbox;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceFacade;
import com.ryuqq.marketplace.application.refund.port.in.command.RequestRefundBatchUseCase;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 환불 요청 일괄 처리 서비스.
 *
 * <p>환불 요청은 새 RefundClaim을 생성하므로 기존 RefundClaim 조회/검증이 불필요합니다.
 */
@Service
public class RequestRefundBatchService implements RequestRefundBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestRefundBatchService.class);

    private final RefundCommandFactory commandFactory;
    private final RefundPersistenceFacade persistenceFacade;
    private final ClaimHistoryFactory historyFactory;

    public RequestRefundBatchService(
            RefundCommandFactory commandFactory,
            RefundPersistenceFacade persistenceFacade,
            ClaimHistoryFactory historyFactory) {
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.historyFactory = historyFactory;
    }

    @Override
    public BatchProcessingResult<String> execute(RequestRefundBatchCommand command) {
        RefundBatchResult batchResult = RefundBatchResult.create();

        for (RefundRequestItem item : command.items()) {
            try {
                RefundWithOutbox bundle =
                        commandFactory.createRefundRequest(
                                item, command.requestedBy(), command.sellerId());
                ClaimHistory history = historyFactory.createStatusChange(
                        ClaimType.REFUND,
                        bundle.claim().idValue(),
                        null,
                        "REQUESTED",
                        command.requestedBy(),
                        command.requestedBy());
                batchResult.addSuccess(bundle.claim(), bundle.outbox(), history);
            } catch (Exception e) {
                log.warn(
                        "환불 요청 생성 실패: orderItemId={}, error={}",
                        item.orderItemId(),
                        e.getMessage());
                batchResult.addFailure(
                        item.orderItemId(),
                        "REFUND_CREATION_FAILED",
                        e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistAllWithOutboxesAndHistories(
                    batchResult.claims(), batchResult.outboxes(), batchResult.histories());
        }

        return batchResult.toBatchProcessingResult();
    }
}
