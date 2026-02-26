package com.ryuqq.marketplace.application.selleradmin.port.in.command;

import com.ryuqq.marketplace.application.selleradmin.dto.command.ChangeSellerAdminPasswordCommand;

/**
 * 셀러 관리자 비밀번호 변경 UseCase.
 *
 * <p>외부 본인인증 완료 후 호출됩니다. ACTIVE 상태이며 인증 서버에 등록된 셀러 관리자의 비밀번호를 새 비밀번호로 변경합니다.
 */
public interface ChangeSellerAdminPasswordUseCase {

    /**
     * 관리자 비밀번호를 새 비밀번호로 변경합니다.
     *
     * @param command 비밀번호 변경 커맨드 (sellerAdminId, newPassword)
     */
    void execute(ChangeSellerAdminPasswordCommand command);
}
