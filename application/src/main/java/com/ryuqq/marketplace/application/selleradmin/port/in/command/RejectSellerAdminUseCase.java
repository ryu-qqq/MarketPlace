package com.ryuqq.marketplace.application.selleradmin.port.in.command;

import com.ryuqq.marketplace.application.selleradmin.dto.command.RejectSellerAdminCommand;

/**
 * 셀러 관리자 가입 신청 거절 UseCase.
 *
 * <p>PENDING_APPROVAL 상태의 신청을 거절합니다. 상태만 REJECTED로 변경하고, 인증 서버에는 요청하지 않습니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface RejectSellerAdminUseCase {

    /**
     * 관리자 가입 신청을 거절합니다.
     *
     * @param command 거절 커맨드
     */
    void execute(RejectSellerAdminCommand command);
}
