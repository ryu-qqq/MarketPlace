package com.ryuqq.marketplace.application.seller.port.in.command;

import com.ryuqq.marketplace.application.seller.dto.command.UpdateSellerCsCommand;

/**
 * 셀러 CS 정보 수정 UseCase.
 *
 * <p>셀러의 CS 연락처, 운영시간 등을 수정합니다.
 *
 * @author ryu-qqq
 */
public interface UpdateSellerCsUseCase {

    /**
     * CS 정보를 수정합니다.
     *
     * @param command CS 수정 커맨드
     */
    void execute(UpdateSellerCsCommand command);
}
