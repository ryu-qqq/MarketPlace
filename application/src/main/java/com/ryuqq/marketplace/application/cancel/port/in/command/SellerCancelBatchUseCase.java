package com.ryuqq.marketplace.application.cancel.port.in.command;

import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;

/** 판매자 취소 일괄 처리 UseCase. */
public interface SellerCancelBatchUseCase {
    BatchProcessingResult<String> execute(SellerCancelBatchCommand command);
}
