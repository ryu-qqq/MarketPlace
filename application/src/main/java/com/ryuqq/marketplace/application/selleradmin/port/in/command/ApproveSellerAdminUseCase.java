package com.ryuqq.marketplace.application.selleradmin.port.in.command;

import com.ryuqq.marketplace.application.selleradmin.dto.command.ApproveSellerAdminCommand;

/**
 * 셀러 관리자 가입 신청 승인 UseCase.
 *
 * <p>PENDING_APPROVAL 상태의 신청을 승인합니다. 승인 시 인증 서버에 사용자를 생성하고 authUserId를 할당합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface ApproveSellerAdminUseCase {

    /**
     * 관리자 가입 신청을 승인합니다.
     *
     * @param command 승인 커맨드
     * @return 승인 결과 (authUserId 포함)
     */
    String execute(ApproveSellerAdminCommand command);
}
