package com.ryuqq.marketplace.application.cancel.service.command;

import com.ryuqq.marketplace.application.cancel.dto.command.RejectCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory.OutboxWithHistory;
import com.ryuqq.marketplace.application.cancel.internal.CancelBatchResult;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceBundle;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceFacade;
import com.ryuqq.marketplace.application.cancel.port.in.command.RejectCancelBatchUseCase;
import com.ryuqq.marketplace.application.cancel.validator.CancelBatchValidator;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 취소 거절 일괄 처리 서비스. */
@Service
public class RejectCancelBatchService implements RejectCancelBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(RejectCancelBatchService.class);

    private final CancelBatchValidator validator;
    private final CancelCommandFactory commandFactory;
    private final CancelPersistenceFacade persistenceFacade;

    public RejectCancelBatchService(
            CancelBatchValidator validator,
            CancelCommandFactory commandFactory,
            CancelPersistenceFacade persistenceFacade) {
        this.validator = validator;
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
    }

    @Override
    public BatchProcessingResult<String> execute(RejectCancelBatchCommand command) {
        List<Cancel> cancels = validator.validateAndGet(command.cancelIds(), command.sellerId());

        CancelBatchResult batchResult = CancelBatchResult.create("REJECT");
        for (Cancel cancel : cancels) {
            try {
                OutboxWithHistory bundle =
                        commandFactory.createRejectBundle(cancel, command.processedBy());
                cancel.reject(command.processedBy(), bundle.changedAt());
                batchResult.addSuccess(cancel, bundle.outbox(), bundle.history());
            } catch (Exception e) {
                log.warn("취소 거절 실패: cancelId={}, error={}", cancel.idValue(), e.getMessage());
                batchResult.addFailure(cancel.idValue(), e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistAll(
                    CancelPersistenceBundle.of(
                            batchResult.cancels(),
                            batchResult.outboxes(),
                            batchResult.histories()));
        }

        return batchResult.toBatchProcessingResult();
    }
}
