package com.ryuqq.marketplace.application.selleradmin.port.in.command;

import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkRejectSellerAdminCommand;

/**
 * 셀러 관리자 가입 신청 일괄 거절 UseCase.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface BulkRejectSellerAdminUseCase {

    /**
     * 가입 신청을 일괄 거절합니다.
     *
     * @param command 일괄 거절 Command
     */
    void execute(BulkRejectSellerAdminCommand command);
}
