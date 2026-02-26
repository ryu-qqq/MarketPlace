package com.ryuqq.marketplace.application.selleradmin.port.in.command;

import com.ryuqq.marketplace.application.selleradmin.dto.command.ApplySellerAdminCommand;

/**
 * 셀러 관리자 가입 신청 UseCase.
 *
 * <p>셀러 하위에 새로운 관리자 가입을 신청합니다. 승인 전까지 PENDING_APPROVAL 상태로 유지됩니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface ApplySellerAdminUseCase {

    /**
     * 관리자 가입 신청을 실행합니다.
     *
     * @param command 가입 신청 커맨드
     * @return 생성된 SellerAdmin ID (UUIDv7)
     */
    String execute(ApplySellerAdminCommand command);
}
