package com.ryuqq.marketplace.application.selleradmin.port.in.command;

import com.ryuqq.marketplace.application.selleradmin.dto.command.ResetSellerAdminPasswordCommand;

/**
 * 셀러 관리자 비밀번호 초기화 UseCase.
 *
 * <p>ACTIVE 상태이며 인증 서버에 등록된 셀러 관리자의 비밀번호를 초기화합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface ResetSellerAdminPasswordUseCase {

    /**
     * 관리자 비밀번호를 초기화합니다.
     *
     * @param command 비밀번호 초기화 커맨드
     */
    void execute(ResetSellerAdminPasswordCommand command);
}
