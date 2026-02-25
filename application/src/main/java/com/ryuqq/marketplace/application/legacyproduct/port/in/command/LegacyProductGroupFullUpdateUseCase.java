package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;

/** 레거시 상품 수정 UseCase. */
public interface LegacyProductGroupFullUpdateUseCase {

    /**
     * 레거시 상품(세토프) 수정 요청을 InboundProduct 기준으로 반영합니다.
     *
     * @param command 레거시 수정 요청을 변환한 인바운드 커맨드
     */
    void execute(ReceiveInboundProductCommand command);
}
