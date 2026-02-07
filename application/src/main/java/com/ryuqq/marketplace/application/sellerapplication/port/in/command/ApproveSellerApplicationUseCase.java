package com.ryuqq.marketplace.application.sellerapplication.port.in.command;

import com.ryuqq.marketplace.application.sellerapplication.dto.command.ApproveSellerApplicationCommand;

/**
 * 셀러 입점 신청 승인 UseCase.
 *
 * <p>대기 상태의 입점 신청을 승인하고 Seller를 생성합니다.
 *
 * @author ryu-qqq
 */
public interface ApproveSellerApplicationUseCase {

    /**
     * 입점 신청을 승인합니다.
     *
     * @param command 승인 커맨드
     * @return 생성된 Seller ID
     */
    Long execute(ApproveSellerApplicationCommand command);
}
