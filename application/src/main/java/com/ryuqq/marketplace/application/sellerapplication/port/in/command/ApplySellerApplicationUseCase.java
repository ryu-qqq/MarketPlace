package com.ryuqq.marketplace.application.sellerapplication.port.in.command;

import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApplySellerApplicationCommand;

/**
 * 셀러 입점 신청 UseCase.
 *
 * <p>새로운 셀러 입점 신청을 생성합니다.
 *
 * @author ryu-qqq
 */
public interface ApplySellerApplicationUseCase {

    /**
     * 입점 신청을 실행합니다.
     *
     * @param command 입점 신청 커맨드
     * @return 생성된 신청 ID
     */
    Long execute(ApplySellerApplicationCommand command);
}
