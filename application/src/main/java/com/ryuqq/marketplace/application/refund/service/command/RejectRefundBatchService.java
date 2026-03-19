package com.ryuqq.marketplace.application.refund.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.RefundBatchResult;
import com.ryuqq.marketplace.application.refund.dto.command.RejectRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceFacade;
import com.ryuqq.marketplace.application.refund.port.in.command.RejectRefundBatchUseCase;
import com.ryuqq.marketplace.application.refund.validator.RefundBatchValidator;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 환불 거절 일괄 처리 서비스. */
@Service
public class RejectRefundBatchService implements RejectRefundBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(RejectRefundBatchService.class);

    private final RefundBatchValidator validator;
    private final RefundCommandFactory commandFactory;
    private final RefundPersistenceFacade persistenceFacade;

    public RejectRefundBatchService(
            RefundBatchValidator validator,
            RefundCommandFactory commandFactory,
            RefundPersistenceFacade persistenceFacade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public BatchProcessingResult<String> execute(RejectRefundBatchCommand command) {
        List<RefundClaim> claims =
                validator.validateAndGet(command.refundClaimIds(), command.sellerId());

        RefundBatchResult batchResult = RefundBatchResult.create("REJECT");
        for (RefundClaim claim : claims) {
            try {
                claim.reject(command.processedBy(), commandFactory.now());
                OutboxWithHistory bundle =
                        commandFactory.createRejectBundle(claim, command.processedBy());
                batchResult.addSuccess(claim, bundle.outbox(), bundle.history());
            } catch (Exception e) {
                log.warn("환불 거절 실패: refundClaimId={}, error={}", claim.idValue(), e.getMessage());
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
