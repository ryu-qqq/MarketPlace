package com.ryuqq.marketplace.application.refund.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.RefundBatchResult;
import com.ryuqq.marketplace.application.refund.dto.command.ApproveRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceBundle;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceFacade;
import com.ryuqq.marketplace.application.refund.port.in.command.ApproveRefundBatchUseCase;
import com.ryuqq.marketplace.application.refund.validator.RefundBatchValidator;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 환불 승인 일괄 처리 서비스 (수거 시작). */
@Service
public class ApproveRefundBatchService implements ApproveRefundBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(ApproveRefundBatchService.class);

    private final RefundBatchValidator validator;
    private final RefundCommandFactory commandFactory;
    private final RefundPersistenceFacade persistenceFacade;

    public ApproveRefundBatchService(
            RefundBatchValidator validator,
            RefundCommandFactory commandFactory,
            RefundPersistenceFacade persistenceFacade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public BatchProcessingResult<String> execute(ApproveRefundBatchCommand command) {
        List<RefundClaim> claims =
                validator.validateAndGet(command.refundClaimIds(), command.sellerId());

        RefundBatchResult batchResult = RefundBatchResult.create("APPROVE");
        for (RefundClaim claim : claims) {
            try {
                OutboxWithHistory bundle =
                        commandFactory.createApproveBundle(claim, command.processedBy());
                batchResult.addSuccess(claim, bundle.outbox(), bundle.history());
            } catch (Exception e) {
                log.warn("환불 승인 실패: refundClaimId={}, error={}", claim.idValue(), e.getMessage());
                batchResult.addFailure(claim.idValue(), e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistAll(
                    RefundPersistenceBundle.of(
                            batchResult.claims(), batchResult.outboxes(), batchResult.histories()));
        }

        return batchResult.toBatchProcessingResult();
    }
}
