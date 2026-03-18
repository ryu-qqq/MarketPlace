package com.ryuqq.marketplace.application.cancel.service.command;

import com.ryuqq.marketplace.application.cancel.dto.CancelBatchResult;
import com.ryuqq.marketplace.application.cancel.dto.command.ApproveCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceFacade;
import com.ryuqq.marketplace.application.cancel.port.in.command.ApproveCancelBatchUseCase;
import com.ryuqq.marketplace.application.cancel.validator.CancelBatchValidator;
import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 취소 승인 일괄 처리 서비스. */
@Service
public class ApproveCancelBatchService implements ApproveCancelBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(ApproveCancelBatchService.class);

    private final CancelBatchValidator validator;
    private final CancelCommandFactory commandFactory;
    private final CancelPersistenceFacade persistenceFacade;
    private final ClaimHistoryFactory historyFactory;

    public ApproveCancelBatchService(
            CancelBatchValidator validator,
            CancelCommandFactory commandFactory,
            CancelPersistenceFacade persistenceFacade,
            ClaimHistoryFactory historyFactory) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.historyFactory = historyFactory;
    }

    @Override
    public BatchProcessingResult<String> execute(ApproveCancelBatchCommand command) {
        List<Cancel> cancels = validator.validateAndGet(command.cancelIds(), command.sellerId());

        CancelBatchResult batchResult = CancelBatchResult.create();
        for (Cancel cancel : cancels) {
            try {
                cancel.approve(command.processedBy(), commandFactory.now());
                ClaimHistory history = historyFactory.createStatusChange(
                        ClaimType.CANCEL,
                        cancel.idValue(),
                        "REQUESTED",
                        "APPROVED",
                        command.processedBy(),
                        command.processedBy());
                batchResult.addSuccess(cancel, commandFactory.createApproveOutbox(cancel), history);
            } catch (Exception e) {
                log.warn("취소 승인 실패: cancelId={}, error={}", cancel.idValue(), e.getMessage());
                batchResult.addFailure(cancel.idValue(), "APPROVE_FAILED", e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistCancelsWithOutboxesAndHistories(
                    batchResult.cancels(), batchResult.outboxes(), batchResult.histories());
        }

        return batchResult.toBatchProcessingResult();
    }
}
