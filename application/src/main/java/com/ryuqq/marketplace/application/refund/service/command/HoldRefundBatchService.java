package com.ryuqq.marketplace.application.refund.service.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.dto.RefundBatchResult;
import com.ryuqq.marketplace.application.refund.dto.command.HoldRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.refund.internal.RefundPersistenceFacade;
import com.ryuqq.marketplace.application.refund.port.in.command.HoldRefundBatchUseCase;
import com.ryuqq.marketplace.application.refund.validator.RefundBatchValidator;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 환불 보류/보류 해제 일괄 처리 서비스. */
@Service
public class HoldRefundBatchService implements HoldRefundBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(HoldRefundBatchService.class);

    private final RefundBatchValidator validator;
    private final RefundCommandFactory commandFactory;
    private final RefundPersistenceFacade persistenceFacade;

    public HoldRefundBatchService(
            RefundBatchValidator validator,
            RefundCommandFactory commandFactory,
            RefundPersistenceFacade persistenceFacade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public BatchProcessingResult<String> execute(HoldRefundBatchCommand command) {
        List<RefundClaim> claims =
                validator.validateAndGet(command.refundClaimIds(), command.sellerId());

        RefundBatchResult batchResult = RefundBatchResult.create(command.operationName());

        for (RefundClaim claim : claims) {
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
                        "환불 보류 처리 실패: refundClaimId={}, isHold={}, error={}",
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
