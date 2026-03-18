package com.ryuqq.marketplace.application.cancel.service.command;

import com.ryuqq.marketplace.application.cancel.dto.CancelBatchResult;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand.SellerCancelItem;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory;
import com.ryuqq.marketplace.application.cancel.factory.CancelCommandFactory.CancelWithOutbox;
import com.ryuqq.marketplace.application.cancel.internal.CancelPersistenceFacade;
import com.ryuqq.marketplace.application.cancel.port.in.command.SellerCancelBatchUseCase;
import com.ryuqq.marketplace.application.claimhistory.factory.ClaimHistoryFactory;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 판매자 취소 일괄 처리 서비스.
 *
 * <p>판매자 취소는 새 Cancel을 생성하므로 기존 Cancel 조회/검증이 불필요합니다.
 * sellerId는 Cancel 생성 시 함께 저장됩니다.
 */
@Service
public class SellerCancelBatchService implements SellerCancelBatchUseCase {

    private static final Logger log = LoggerFactory.getLogger(SellerCancelBatchService.class);

    private final CancelCommandFactory commandFactory;
    private final CancelPersistenceFacade persistenceFacade;
    private final ClaimHistoryFactory historyFactory;

    public SellerCancelBatchService(
            CancelCommandFactory commandFactory,
            CancelPersistenceFacade persistenceFacade,
            ClaimHistoryFactory historyFactory) {
        this.commandFactory = commandFactory;
        this.persistenceFacade = persistenceFacade;
        this.historyFactory = historyFactory;
    }

    @Override
    public BatchProcessingResult<String> execute(SellerCancelBatchCommand command) {
        CancelBatchResult batchResult = CancelBatchResult.create();

        for (SellerCancelItem item : command.items()) {
            try {
                CancelWithOutbox bundle =
                        commandFactory.createSellerCancel(
                                item, command.requestedBy(), command.sellerId());
                ClaimHistory history = historyFactory.createStatusChange(
                        ClaimType.CANCEL,
                        bundle.cancel().idValue(),
                        null,
                        "REQUESTED",
                        command.requestedBy(),
                        command.requestedBy());
                batchResult.addSuccess(bundle.cancel(), bundle.outbox(), history);
            } catch (Exception e) {
                log.warn(
                        "판매자 취소 생성 실패: orderItemId={}, error={}",
                        item.orderItemId(),
                        e.getMessage());
                batchResult.addFailure(
                        String.valueOf(item.orderItemId()),
                        "CANCEL_CREATION_FAILED",
                        e.getMessage());
            }
        }

        if (batchResult.hasSuccessItems()) {
            persistenceFacade.persistAllWithOutboxesAndHistories(
                    batchResult.cancels(), batchResult.outboxes(), batchResult.histories());
        }

        return batchResult.toBatchProcessingResult();
    }
}
