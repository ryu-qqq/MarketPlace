package com.ryuqq.marketplace.application.legacyproduct.port.in.command;

import com.ryuqq.marketplace.application.legacyproduct.dto.command.LegacyUpdateNoticeCommand;

/** 레거시 상품 고시정보 수정 UseCase. */
public interface LegacyProductUpdateNoticeUseCase {

    void execute(LegacyUpdateNoticeCommand command);
}
