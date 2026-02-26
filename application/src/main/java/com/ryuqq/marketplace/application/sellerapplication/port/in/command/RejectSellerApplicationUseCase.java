package com.ryuqq.marketplace.application.sellerapplication.port.in.command;

import com.ryuqq.marketplace.application.sellerapplication.dto.command.RejectSellerApplicationCommand;

/**
 * 셀러 입점 신청 거절 UseCase.
 *
 * <p>대기 상태의 입점 신청을 거절합니다.
 *
 * @author ryu-qqq
 */
public interface RejectSellerApplicationUseCase {

    /**
     * 입점 신청을 거절합니다.
     *
     * @param command 거절 커맨드
     */
    void execute(RejectSellerApplicationCommand command);
}
