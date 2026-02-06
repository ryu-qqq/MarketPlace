package com.ryuqq.marketplace.application.selleradmin.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkApproveSellerAdminCommand;

/**
 * 셀러 관리자 가입 신청 일괄 승인 UseCase.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface BulkApproveSellerAdminUseCase {

    /**
     * 가입 신청을 일괄 승인합니다.
     *
     * @param command 일괄 승인 Command
     * @return 일괄 처리 결과 (성공/실패 건수 및 개별 결과)
     */
    BatchProcessingResult<String> execute(BulkApproveSellerAdminCommand command);
}
